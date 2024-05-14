package org.example.project_cinemas_java.service.implement;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.example.project_cinemas_java.configurations.VNPayConfig;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPayService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private BillService billService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private BillTicketRepo billTicketRepo;
    @Autowired
    private TicketRepo ticketRepo;
    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private MovieRepo movieRepo;
    @Autowired
    private BillFoodRepo billFoodRepo;

    public String createOrder(int total, int orderInfor, String urlReturn){
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total*100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", String.valueOf(orderInfor));
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        urlReturn += VNPayConfig.vnp_Returnurl;
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 5);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    public int orderReturn(HttpServletRequest request){
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                int user = Integer.parseInt(request.getParameter("vnp_OrderInfo"));
                System.out.println(user);
                try {
                    //save bill thành đã thanh toán cập nhật lại trang thái ghế thành đã bán
                   String code = billService.saveBillInformation(user);

                    //thông báo bill tơi email
                   sendEmail(user,code);
            } catch (DataNotFoundException ex) {
                    return 0;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return 1;
            } else {

                return 0;
            }
        } else {
            return -1;
        }
    }

    public void     sendEmail( int user, String code) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        User user1 = userRepo.findById(user).orElse(null);
        Bill bill = billRepo.findBillByUserAndBillstatusIdAndTradingCode(user1, 2,code);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String reservationCode = "";
        String movieTitle = "";
        String theater = "";
        String hall = "";
        String sessionTime = "";
        String seats = "";
        String food = "";
        String paymentTime = "";
        String totalAmount = "";
        Set<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
        List<Ticket> tickets = new ArrayList<>();
        for (BillTicket billTicket:billTickets){
            tickets.add(billTicket.getTicket());
        }

        List<String> seatsList = new ArrayList<>();
        for (Ticket ticket:tickets){
            theater = ticket.getSchedule().getMovie().getCinema().getNameOfCinema();
            hall = ticket.getSchedule().getRoom().getName();
            sessionTime = ticket.getSchedule().getStartAt().format(formatter);
            movieTitle = ticket.getSchedule().getMovie().getName();
            paymentTime = LocalDateTime.now().format(formatter);
            totalAmount = String.valueOf(bill.getTotalMoney());
            seatsList.add(ticket.getSeat().getLine() + ticket.getSeat().getNumber());
        }

        seats = seats + String.join(", ", seatsList);
        reservationCode = bill.getTradingCode();

        List<BillFood> billFoods = billFoodRepo.findAllByBill(bill);
        for (BillFood billFood: billFoods){
            food = food + billFood.getQuantity() + " x " + billFood.getFood().getNameOfFood() + ", ";
        }

        String htmlContent = "<html>"
                + "<body>"
                + "<style>"
                + "table { width: 100%; border-collapse: collapse; }"
                + "th, td { text-align: left; padding: 8px; }"
                + "th { background-color: #f2f2f2; min-width: 100px;display:flex; justify-content: flex-start !important }" // Cài đặt chiều rộng tối thiểu cho th
                + "</style>"
                + "<table>"
                + "<tr><th><Mã vé:</th><td>" + reservationCode + "</td></tr>"
                + "<tr><th >Phim:</th><td>" + movieTitle + "</td></tr>"
                + "<tr><th>Rạp:</th><td>" + theater + "</td></tr>"
                + "<tr><th >Phòng chiếu:</th><td>" + hall + "</td></tr>"
                + "<tr><th >Thời gian:</th><td>" + sessionTime + "</td></tr>"
                + "<tr ><th >Ghế:</th><td>" + seats + "</td></tr>"
                + "<tr><th >Đồ ăn:</th><td>" + food + "</td></tr>"
                + "<tr><th >Thời gian thanh toán:</th><td>" + paymentTime + "</td></tr>"
                + "<tr ><th>Tổng tiền:</th><td>" + totalAmount + " VND" + "</td></tr>"
                + "</table>"
                + "</body>";
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            String to = user1.getEmail();
            String subject = "Xin chào " +  user1.getUserName() + "\n" + " Chúc mừng bạn đặt vé thành công";
            helper.setFrom("doan77309@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        javaMailSender.send(mimeMessage);
    }

}

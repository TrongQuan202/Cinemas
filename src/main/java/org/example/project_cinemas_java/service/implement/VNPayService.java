package org.example.project_cinemas_java.service.implement;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.example.project_cinemas_java.configurations.VNPayConfig;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatStatusDTO;
import org.example.project_cinemas_java.payload.request.seat_request.SeatStatusRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    @Autowired
    private PromotionRepo promotionRepo;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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

    public int orderReturn(HttpServletRequest request) throws Exception{
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
                        float finalAmount = Float.parseFloat(request.getParameter("vnp_Amount")) / 100;

                        try {
                            //save bill thành đã thanh toán cập nhật lại trang thái ghế thành đã bán
                            String code = billService.saveBillInformation(user,finalAmount);

                            //thông báo bill tơi email
                            sendEmail(user,code);

                        } catch (DataNotFoundException ex) {
                            return 0;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        //cập nhât lại seatStatus thành đã bán cho toàn bộ người phòng
                        handleSeatAfterPayment(user);
                return 1;
            } else {
                int user = Integer.parseInt(request.getParameter("vnp_OrderInfo"));
                try {
                    resetTicketByUser(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }
        } else {
            return -1;
        }
    }

    public void handleSeatAfterPayment(int userId) {
        User  user = userRepo.findById(userId).orElse(null);
        Bill bill = billRepo.findBillByUserAndBillstatusId(user,3);
        if(bill != null){
            List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
            for (BillTicket billTicket:billTickets){
                SeatStatusRequest seatStatusRequest = new SeatStatusRequest();
                seatStatusRequest.setSeatId(billTicket.getTicket().getSeat().getId());
                seatStatusRequest.setStatus(4);
                seatStatusRequest.setSeatType(billTicket.getTicket().getSeatType());
                seatStatusRequest.setUserId(billTicket.getTicket().getUser().getId());
                seatStatusRequest.setSchedule(billTicket.getTicket().getSchedule().getId());
                simpMessagingTemplate.convertAndSend("/topic/seatStatus/" + seatStatusRequest.getSchedule(), seatStatusRequest );
            }
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
        List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
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
    public void resetTicketByUser(int userId)throws Exception {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        List<SeatStatusDTO> seatStatusDTOS = new ArrayList<>();
        List<Ticket> tickets = ticketRepo.findAllByUserAndSeatStatus(user, 3);
        if (!tickets.isEmpty()) {
            for (Ticket ticket:tickets){
                if(ticket.getSeatStatus() == 3){
                    ticket.setPriceTicket(0);
                    ticket.setCode(null);
                    ticket.setActive(false);
                    ticket.setSeatStatus(1);
                    ticket.setUser(null);
                    ticket.setTicketBookingTime(null);
                    ticketRepo.save(ticket);

                    Bill bill = billRepo.findBillByUserAndBillstatusId(user,3);
                    if(bill == null) {
                        throw new DataNotFoundException("Bill does not exits");
                    }
                    List<BillFood> billFoods = billFoodRepo.findAllByBill(bill);
                    if (!billFoods.isEmpty()){
                        for (BillFood billFood:billFoods){
                            bill.setTotalMoney(bill.getTotalMoney() - billFood.getFood().getPrice());
                            billRepo.save(bill);
                            billFood.setFood(null);
                            billFood.setBill(null);
                            billFood.setQuantity(0);
                            billFoodRepo.delete(billFood);
                        }
                    }

                    bill.setTotalMoney(0);
                    billRepo.save(bill);

                    List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
                    if(!billTickets.isEmpty()){
                        for (BillTicket billTicket:billTickets){
                            billTicket.setTicket(null);
                            billTicket.setBill(null);
                            billTicketRepo.deleteById(billTicket.getId());
                        }
                    }
                    Promotion promotion = bill.getPromotion();
                    if(promotion != null){
                        promotion.setQuantity(promotion.getQuantity() + 1);
                        promotionRepo.save(promotion);
                    }

                    SeatStatusDTO seatStatusDTO = new SeatStatusDTO();
                    seatStatusDTO.setSeatStatus(ticket.getSeatStatus());
                    seatStatusDTO.setId(ticket.getSeat().getId());
                    seatStatusDTO.setUserId(null);
                    seatStatusDTOS.add(seatStatusDTO);
                }

            }
        }
    }
}


package org.example.project_cinemas_java.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Ticket;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.repository.TicketRepo;
import org.example.project_cinemas_java.service.implement.TicketService;
import org.example.project_cinemas_java.service.implement.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class VNPayController {
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private TicketService ticketService;
    @PostMapping("/submitOrder")
    public ResponseEntity<?> submidOrder(@RequestParam("amount") int orderTotal,
                              @RequestParam("user") int user,
                                         @RequestParam("schedule") String schedule,
                              HttpServletRequest request) {
//                System.out.println(schedule);
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                String vnpayUrl = vnPayService.createOrder(orderTotal, user, baseUrl);

                //trước khi vào trang thanh toán của VNPay thì gia hạn giời gian giữ cho cho ghế đã chọn
               try {
                    ticketService.updateSeatHoldExpiration(user,schedule);
                    return ResponseEntity.ok(vnpayUrl);
                } catch (DataNotFoundException ex) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
    }

    @GetMapping("/vnpay-payment")
    public  ResponseEntity<?> confirmPayment(HttpServletRequest request, Model model){
        try {
           int paymentStatus = vnPayService.orderReturn(request);

            String orderInfo = request.getParameter("vnp_OrderInfo");
            String paymentTime = request.getParameter("vnp_PayDate");
            String transactionId = request.getParameter("vnp_TransactionNo");
            String totalPrice = request.getParameter("vnp_Amount");


            model.addAttribute("orderId", orderInfo);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("paymentTime", paymentTime);
            model.addAttribute("transactionId", transactionId);

//        return paymentStatus == 1 ? "ordersuccess" : "orderfail";

            String redirectUrl = "https://spacecinema-wheat.vercel.app/thong-bao";
            if(paymentStatus == 1){
                redirectUrl += "?status=success";
            }else {
                redirectUrl += "?status=failure";
            }

            // Chuyển hướng người dùng về trang trong Nuxt.js
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @GetMapping("/check")
    public String check(){
            return "hello";
    }
}

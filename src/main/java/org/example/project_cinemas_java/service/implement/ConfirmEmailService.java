package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.ConfirmEmailExpired;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.EmailMessage;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.ConfirmCodeRequest;
import org.example.project_cinemas_java.repository.ConfirmEmailRepo;
import org.example.project_cinemas_java.service.iservice.IConfirmEmailService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ConfirmEmailService implements IConfirmEmailService {
    @Autowired
    private ConfirmEmailRepo confirmEmailRepo;

    @Autowired
    private JavaMailSender javaMailSender;
    private String generateConfirmCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }
    private void senEmail(String to, String subject, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("doan77309@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);

    }

    public boolean isExpired(ConfirmEmail confirmEmail) {
        return LocalDateTime.now().isAfter(confirmEmail.getExpiredTime());
    }

    @Override
    public void sendConfirmEmail(User user) {
        ConfirmEmail confirmEmail = ConfirmEmail.builder()
                .user(user)
                .confirmCode(generateConfirmCode())
                .expiredTime(LocalDateTime.now().plusSeconds(60))
                .isConfirm(false)
                .requiredTime(LocalDateTime.now())
                .build();
        confirmEmailRepo.save(confirmEmail);

        //todo Gửi email với mã code và thông tin
        String subject ="Xác nhận email của bạn";
        String content = "Mã xác thực của bạn là: " + confirmEmail.getConfirmCode();
        senEmail(user.getEmail(),subject,content);
    }

    @Override
    public boolean confirmEmail(String confirmCode) throws Exception {
        ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmCode);

        if(confirmEmail == null ){
            throw  new DataNotFoundException(MessageKeys.INCORRECT_VERIFICATION_CODE);
        }

        if (isExpired(confirmEmail)){
            throw new ConfirmEmailExpired(MessageKeys.VERIFICATION_CODE_HAS_EXPIRED);
        }
        confirmEmail.setConfirm(true);

        confirmEmailRepo.save(confirmEmail);

        return true;
    }
}

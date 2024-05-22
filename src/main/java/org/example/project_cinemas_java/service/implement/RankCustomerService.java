package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.RankCustomer;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.converter.RankUserConverter;
import org.example.project_cinemas_java.payload.dto.rankcustomerdtos.RankUserDTO;
import org.example.project_cinemas_java.repository.RankCustomerRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IRankCustomerService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public class RankCustomerService implements IRankCustomerService {
    @Autowired
    private RankCustomerRepo rankCustomerRepo;
    @Autowired
    private UserRepo  userRepo;

    private final RankUserConverter rankUserConverter;

    public RankCustomerService(RankUserConverter rankUserConverter) {
        this.rankUserConverter = rankUserConverter;
    }

    @Override
    public RankUserDTO getRankOfUser(String email) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw  new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        RankCustomer rankCustomer = rankCustomerRepo.findByUsers(user);
        if(rankCustomer == null){
            throw new DataNotFoundException(MessageKeys.RANK_OF_USER_DOES_NOT_EXIST);
        }
        return rankUserConverter.rankCustomerToRankUserDTO(rankCustomer);
    }

}

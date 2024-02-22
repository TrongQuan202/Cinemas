package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.model.Role;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.RegisterRequest;
import org.example.project_cinemas_java.repository.RankCustomerRepo;
import org.example.project_cinemas_java.repository.RoleRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.repository.UserStatusRepo;
import org.example.project_cinemas_java.service.iservice.IAuthService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RankCustomerRepo rankCustomerRepo;
    @Autowired
    private UserStatusRepo userStatusRepo;
    @Override
    public User register(RegisterRequest registerRequest) throws Exception {
        String email = registerRequest.getEmail();

        if(userRepo.existsByEmail(email)){
            throw new DataIntegrityViolationException(MessageKeys.EMAIL_ALREADY_EXISTS);
        }
        Role userRole = roleRepo.findById(1).orElseThrow(()
            -> new IllegalStateException("Role not found with ID 2"));

        User user = User.builder()
                .point(0)
                .name(registerRequest.getName())
                .email(email)
                .userName(registerRequest.getUserName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(userRole)
                .rankcustomer(rankCustomerRepo.findById(1).orElse(null))
                .userStatus(userStatusRepo.findById(2).orElse(null))
                .build();

        return user;
    }

    public User registerRequestToUser(RegisterRequest registerRequest){
        return this.modelMapper.map(registerRequest, User.class);
    }
}

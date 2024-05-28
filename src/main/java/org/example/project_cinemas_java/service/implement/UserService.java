package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.userdto.UserAccountDTO;
import org.example.project_cinemas_java.payload.dto.userdto.UserDTO;
import org.example.project_cinemas_java.payload.request.auth_request.ConfirmAuthorRequest;
import org.example.project_cinemas_java.repository.RoleRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IUserService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void updateInfoUser(User user) {

    }

    @Override
    public String getPhoneNumberById(int id) throws Exception {
        User existingUser = userRepo.findById(id).orElse(null);
        if (existingUser == null) {
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        String phone = null;
        for (User user : userRepo.findAll()) {
            if (user.getId() == id) {
                phone = user.getPhoneNumber();
                break;
            }
        }
        return phone;
    }

    @Override
    public List<UserDTO> getAllUserByAdmin() {
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserName(user.getUserName());
            userDTO.setRole(user.getRole().getId() == 1 ? "USER" : "ADMIN");
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setIsActive(user.isActive() ? "Đã kích hoạt" : "Chưa kích hoạt");
            userDTO.setRank(user.getRankcustomer().getName());
            userDTO.setPoint(user.getPoint());

            userDTOS.add(userDTO);
        }
        return userDTOS;
    }

    @Override
    public List<UserDTO>  confirmAuth(String email, ConfirmAuthorRequest confirmAuthorRequest) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(confirmAuthorRequest.getPassword(), user.getPassword())) {
            throw new DataIntegrityViolationException("Thông tin xác thực không chính xác");

        }
            User newUser = userRepo.findByEmail(confirmAuthorRequest.getNewEmail()).orElse(null);
            if (newUser == null) {
                throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
            }

            newUser.setRole(roleRepo.findById(2).orElse(null));
            userRepo.save(newUser);

            List<UserDTO> userDTOS = new ArrayList<>();
            for (User u : userRepo.findAll()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUserName(u.getUserName());
                userDTO.setRole(u.getRole().getId() == 1 ? "USER" : "ADMIN");
                userDTO.setEmail(u.getEmail());
                userDTO.setPhoneNumber(u.getPhoneNumber());
                userDTO.setIsActive(u.isActive() ? "Đã kích hoạt" : "Chưa kích hoạt");
                userDTO.setRank(u.getRankcustomer().getName());
                userDTO.setPoint(u.getPoint());

                userDTOS.add(userDTO);
            }
            return userDTOS;
            }

    @Override
    public UserAccountDTO getProfileUser(String email) throws Exception{
        User user = userRepo.findByEmail(email).orElse(null);
        if(user ==null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        UserAccountDTO userAccountDTO = userToUserAccountDTO(user);

        return userAccountDTO;
    }

    public UserAccountDTO userToUserAccountDTO(User user){
        UserAccountDTO userAccountDTO = new UserAccountDTO();
        userAccountDTO.setEmail(user.getEmail());
        userAccountDTO.setUserName(user.getUserName());
        userAccountDTO.setPassword(user.getPassword());
        userAccountDTO.setPhoneNumber(user.getPhoneNumber());
        userAccountDTO.setRankcustomer(user.getRankcustomer().getName());
        userAccountDTO.setPoint(user.getPoint());
        return userAccountDTO;
    }

    public float getPoint(String email) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw new DataNotFoundException("Người dùng kooong tồn tại");
        }
        return user.getPoint();
    }


}



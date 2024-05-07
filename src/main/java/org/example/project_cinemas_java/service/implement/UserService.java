package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.userdto.UserDTO;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IUserService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepo userRepo;


    @Override
    public void updateInfoUser(User user) {

    }

    @Override
    public String getPhoneNumberById(int id) throws Exception {
        User existingUser = userRepo.findById(id).orElse(null);
        if(existingUser == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        String phone = null;
        for (User user: userRepo.findAll()){
            if(user.getId() == id){
                phone = user.getPhoneNumber();
                break;
            }
        }
        return phone;
    }

    @Override
    public List<UserDTO> getAllUserByAdmin() {
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user: userRepo.findAll()){
            UserDTO userDTO = new UserDTO();
            userDTO.setUserName(user.getUserName());
            userDTO.setRole(user.getRole().getId() == 1 ? "USER" :"ADMIN");
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setIsActive(user.isActive() ?"Đã kích hoạt" : "Chưa kích hoạt");
            userDTO.setName(user.getName());
            userDTO.setRank(user.getRankcustomer().getName());
            userDTO.setPoint(user.getPoint());

            userDTOS.add(userDTO);
        }
        return userDTOS;
    }
}

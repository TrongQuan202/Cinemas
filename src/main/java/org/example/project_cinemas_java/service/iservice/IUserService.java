package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.userdto.UserDTO;

import java.util.List;

public interface IUserService {
    void updateInfoUser (User user);

    String getPhoneNumberById(int id)throws Exception;

    List<UserDTO> getAllUserByAdmin();
}

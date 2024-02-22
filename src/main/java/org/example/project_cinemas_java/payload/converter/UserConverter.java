package org.example.project_cinemas_java.payload.converter;


import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.userdto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public UserDTO entityToDTO(User user){
        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        return dto;
    }
}

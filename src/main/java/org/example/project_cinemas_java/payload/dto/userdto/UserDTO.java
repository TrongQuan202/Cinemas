package org.example.project_cinemas_java.payload.dto.userdto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class UserDTO {
    private String userName;

    private String email;


    private String role;

    private String phoneNumber;

    private String isActive;

    private String rank;

    private float point;
}

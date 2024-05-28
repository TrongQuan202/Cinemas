package org.example.project_cinemas_java.payload.dto.userdto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.example.project_cinemas_java.model.RankCustomer;
import org.example.project_cinemas_java.model.UserStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountDTO {
    private float point;

    private String userName;

    private String email;

    private String phoneNumber;

    private String password;

    private String rankcustomer;

    private String userStatus;
}

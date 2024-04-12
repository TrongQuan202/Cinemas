package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "confirmEmail")
@Builder
public class ConfirmEmail extends BaseEntity {
    private String emailUser;

    private LocalDateTime requiredTime;

    private LocalDateTime expiredTime;

    private String confirmCode;

    private boolean isConfirm;

}

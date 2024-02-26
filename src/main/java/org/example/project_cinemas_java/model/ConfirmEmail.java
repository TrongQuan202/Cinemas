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
@Table(name = "confirmemail")
@Builder
public class ConfirmEmail extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "fk_ConfirmEmail_User"))
    @JsonManagedReference
    private User user;

    private LocalDateTime requiredTime;

    private LocalDateTime expiredTime;

    private String confirmCode;

    private boolean isConfirm;

}

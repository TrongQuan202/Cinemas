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
@Table(name = "refreshToken")
@Builder
public class RefreshToken extends BaseEntity{
    private String accessToken;

    private LocalDateTime timeExpiredAccess;

    private String refreshToken;

    private LocalDateTime timeExpiredRefresh;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "fk_RefreshToken_User"))
    @JsonManagedReference
    private User user;
}

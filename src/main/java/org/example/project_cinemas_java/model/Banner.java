package org.example.project_cinemas_java.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "banner")
@Builder
public class Banner extends  BaseEntity {
    private String imageUrl;
    private String title;

}

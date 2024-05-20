package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blog")
@Builder
public class Blog extends BaseEntity {
    private String name;

    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String image;

}

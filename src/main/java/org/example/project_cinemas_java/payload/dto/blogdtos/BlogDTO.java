package org.example.project_cinemas_java.payload.dto.blogdtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogDTO {
    private String image;
    private String description;
    private String name;
    private String content;
}

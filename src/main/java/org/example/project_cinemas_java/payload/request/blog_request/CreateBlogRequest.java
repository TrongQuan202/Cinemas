package org.example.project_cinemas_java.payload.request.blog_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateBlogRequest {
    private String image;
    private String name;
    private String description;
    private String content;
}

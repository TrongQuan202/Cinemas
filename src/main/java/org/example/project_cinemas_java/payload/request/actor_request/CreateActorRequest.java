package org.example.project_cinemas_java.payload.request.actor_request;

import lombok.Data;

@Data
public class CreateActorRequest {
    private String name;

    private String slug;

    private String image;

    private String description;

    private String birthDay;
    private String gender;

    private String height;
    private String nationality;
}

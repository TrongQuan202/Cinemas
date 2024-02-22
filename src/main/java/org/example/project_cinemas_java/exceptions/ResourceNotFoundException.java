package org.example.project_cinemas_java.exceptions;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String fileName;
    Long filedValue;

    public ResourceNotFoundException(String resourceName, String fileName, Long filedValue) {
        super(String.format("%s not found with %s : %s ", resourceName, fileName, filedValue));
        this.resourceName = resourceName;
        this.fileName = fileName;
        this.filedValue = filedValue;
    }
}

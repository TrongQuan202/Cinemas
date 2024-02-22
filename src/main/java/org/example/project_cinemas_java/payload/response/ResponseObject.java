package org.example.project_cinemas_java.payload.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;

@Component
@Data
public class ResponseObject <T>{
    private int statusCode;
    private String message;
    private T data;

    public ResponseObject() {
    }

    public ResponseObject(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ResponseObject<T> responseSuccess(String message, T data){
        return new ResponseObject<T>(HttpURLConnection.HTTP_OK, message, data);
    }
    public ResponseObject<T> responseError(int statusCode , String message, T data){
        return new ResponseObject<T>(statusCode, message, data);
    }
}

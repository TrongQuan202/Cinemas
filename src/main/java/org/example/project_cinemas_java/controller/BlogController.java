package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateCinemaRequest;
import org.example.project_cinemas_java.payload.request.blog_request.CreateBlogRequest;
import org.example.project_cinemas_java.service.implement.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/blog")
@RequiredArgsConstructor
public class BlogController {
    @Autowired
    private BlogService blogService;

    @PostMapping("/create-blog")
    public ResponseEntity<?> createBlog(@RequestBody CreateBlogRequest createBlogRequest){
        try {
            blogService.createBlog(createBlogRequest);
            return ResponseEntity.ok().body("Tạo blog thành công");
        } catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

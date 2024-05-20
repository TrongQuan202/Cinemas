package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.payload.request.blog_request.CreateBlogRequest;

public interface IBlogService {
    void createBlog(CreateBlogRequest createBlogRequest)throws Exception;
}

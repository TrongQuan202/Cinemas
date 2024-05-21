package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Blog;
import org.example.project_cinemas_java.payload.request.blog_request.CreateBlogRequest;

import java.util.List;

public interface IBlogService {
    void createBlog(CreateBlogRequest createBlogRequest)throws Exception;

    List<Blog> getAllBlog() throws Exception;

    Blog getBlogDetail(int blogId)throws Exception;
}

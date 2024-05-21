package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Blog;
import org.example.project_cinemas_java.payload.request.blog_request.CreateBlogRequest;
import org.example.project_cinemas_java.repository.BlogRepo;
import org.example.project_cinemas_java.service.iservice.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService implements IBlogService {
    @Autowired
    private BlogRepo blogRepo;


    @Override
    public void createBlog(CreateBlogRequest createBlogRequest) throws Exception {
        Blog blog = blogRepo.findByName(createBlogRequest.getName());
        if(blog != null){
            throw new DataNotFoundException("Blog đã tồn tại");
        }
        Blog blog1 = new Blog();
        blog1.setName(createBlogRequest.getName());
        blog1.setDescription(createBlogRequest.getDescription());
        blog1.setImage(createBlogRequest.getImage());
        blog1.setContent(createBlogRequest.getContent());
        blogRepo.save(blog1);
    }

    @Override
    public List<Blog> getAllBlog() throws Exception {

        return blogRepo.findAll();
    }

    @Override
    public Blog getBlogDetail(int blogId) throws Exception {
        Blog blog = blogRepo.findById(blogId).orElse(null);
        if(blog ==null){
            throw new DataNotFoundException("Blog không tồn tại hoặc đã bị xóa! Vui lòng thử lại");
        }
        return blog;
    }
}

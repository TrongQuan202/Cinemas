package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Blog;
import org.example.project_cinemas_java.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepo  extends JpaRepository<Blog, Integer> {
    Blog findByName(String name);
    Blog findByImage(String image);
}

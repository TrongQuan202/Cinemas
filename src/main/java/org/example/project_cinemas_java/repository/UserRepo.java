package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    User findByConfirmEmails(ConfirmEmail confirmEmail);
}

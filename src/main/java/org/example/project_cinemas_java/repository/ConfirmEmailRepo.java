package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmEmailRepo extends JpaRepository<ConfirmEmail, Integer> {

    boolean existsByEmailUserAndIsConfirm(String email, boolean isConfirm);

    ConfirmEmail findConfirmEmailByConfirmCode(String confirmCode);

    boolean existsByEmailUser (String email);

    ConfirmEmail findConfirmEmailByConfirmCodeAndEmailUser(String confirmCode, String email);


}

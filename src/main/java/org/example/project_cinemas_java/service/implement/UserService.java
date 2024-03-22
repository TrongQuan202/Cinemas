package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IUserService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepo userRepo;


    @Override
    public void updateInfoUser(User user) {

    }

    @Override
    public String getPhoneNumberById(int id) throws Exception {
        User existingUser = userRepo.findById(id).orElse(null);
        if(existingUser == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        String phone = null;
        for (User user: userRepo.findAll()){
            if(user.getId() == id){
                phone = user.getPhoneNumber();
                break;
            }
        }
        return phone;
    }
}

package service;

import dao.UserDAO;  // 
import models.User; // 


public class UserService {
   
    private UserDAO userDAO = new UserDAO();

    public User authenticate(String username, String password) {
        User user = userDAO.login(username, password); // 
        if (user != null) {
            System.out.println("Giriş Başarılı! Hoş geldin " + user.getFullName());
        } else {
            System.out.println("Giriş Başarısız! ❌");
        }
        return user;
    }
}
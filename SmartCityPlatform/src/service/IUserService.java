package service;

import models.User;

public interface IUserService {
    User authenticateUser(String username, String password); // Kullanıcı girişi
}
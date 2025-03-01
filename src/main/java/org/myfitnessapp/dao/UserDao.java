package org.myfitnessapp.dao;


import org.myfitnessapp.models.User;

import java.util.List;

public interface UserDao extends Dao<User> {

    List<User> getAllUsers();

    boolean verifyUser(String username, String password);
}

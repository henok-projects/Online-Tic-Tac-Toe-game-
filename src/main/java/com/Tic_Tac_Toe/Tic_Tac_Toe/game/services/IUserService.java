package com.Tic_Tac_Toe.Tic_Tac_Toe.game.services;

import com.Tic_Tac_Toe.Tic_Tac_Toe.game.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    void createUser(User user);
    List<User> getAllUsers();
    Optional<User> getOneUser(Integer Id);

    Optional<User> findByUsernameAndPassword(String username, String password);
}


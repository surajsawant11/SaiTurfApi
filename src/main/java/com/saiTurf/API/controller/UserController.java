package com.saiTurf.API.controller;

import com.saiTurf.API.model.UserModel;
//import com.saiTurf.API.model.User;
import com.saiTurf.API.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

//    @GetMapping
//    public List<UserModel> getUsers() {
//        return userService.getAllUsers();
//    }

//    @PostMapping
//    public UserModel createUser(@RequestBody UserModel user) {
//        return userService.createUser(user);
//    }
}

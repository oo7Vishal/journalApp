package net.vishal.journalApp.controller;

import net.vishal.journalApp.entity.User;
import net.vishal.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "ok";
    }


    @PostMapping("/create-user")
    public void createUser(@RequestBody User user ) {
        userService.saveNewUser(user);
    }





}

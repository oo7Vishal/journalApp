package net.vishal.journalApp.controller;

import lombok.extern.slf4j.Slf4j;
import net.vishal.journalApp.entity.User;
import net.vishal.journalApp.service.UserDetailsServiceImpl;
import net.vishal.journalApp.service.UserService;
import net.vishal.journalApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "ok";
    }


    @PostMapping("/signup")
    public void signup(@RequestBody User user ) {
        userService.saveNewUser(user);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user ) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName()
                            ,user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return  new ResponseEntity<>(jwt, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Error during login authentication",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }






}

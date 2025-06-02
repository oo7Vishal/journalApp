package net.vishal.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.vishal.journalApp.entity.JournalEntry;
import net.vishal.journalApp.entity.User;
import net.vishal.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder =   new BCryptPasswordEncoder();

   // private final Logger logger = LoggerFactory.getLogger(UserService.class);


    public void saveUser(User user) {
        userRepository.save(user);
    }




    public boolean saveNewUser(User user) {
        try{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return true;
        }catch (Exception e) {
           // logger.error("Error occured for {} : " ,user.getUserName() ,e);
            log.error("Error occured for {} : " ,user.getUserName() ,e);
            log.info("Error occured for {} : " ,user.getUserName() ,e);
            log.debug(  "Error occured for {} : " ,user.getUserName() ,e);
            log.warn("Error occured for {} : " ,user.getUserName() ,e);
            log.trace("Error occured for {} : " ,user.getUserName() ,e);


            return false;
        }
    }

    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(user);
    }

    public List<User> getAll() {
        return  userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id ) {
        return userRepository.findById(id);
    }

    public void deleteById(ObjectId id) {
        userRepository.deleteById(id);
    }

    public User findByUserName(String userName ) {
        return userRepository.findByUserName(userName);
    }



}

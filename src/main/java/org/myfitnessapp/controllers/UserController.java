package org.myfitnessapp.controllers;

import lombok.RequiredArgsConstructor;
import org.myfitnessapp.models.User;
import org.myfitnessapp.dao.JdbcUserDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final JdbcUserDao userDao;

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable("id") long userId) {
        User result = userDao.get(userId);
        return ResponseEntity.ok(result);
    }


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> result = userDao.getAllUsers();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        boolean result = userDao.verifyUser(username, password);
        return ResponseEntity.ok(result);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<String> create(@RequestBody User user) {
        String result = userDao.create(user);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> update(@PathVariable("id") long userId, @RequestBody User user) {
        boolean result = userDao.update(userId, user);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> partialUpdate(@PathVariable("id") long userId, @RequestBody Map<String, Object> updates){
        boolean result = userDao.partialUpdate(userId, updates);
        return ResponseEntity.ok(result);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long userId) {
        userDao.delete(userId);
    }

    //TODO Unit tests
    //TODO see if I can remove try/catch in service methods and throw the exception in method signature
    //TODO implement spring-boot-starter-security (Authentication)
    //TODO customize when a specific status code should be thrown (like invalid id) or username doesn't exist
    //TODO Add Vue3 to the project
    //TODO build a login page that hits the verifyUser endpoint
    //TODO build a User Profile page
}
package com.galvanize;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return this.repository.findAll();
    }

    @PostMapping("/users")
    public User postUser(@RequestBody User user) {
        return this.repository.save(user);
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return this.repository.findById(id).orElseThrow(()-> new NoSuchElementException(
                String.format("This element does not exist")));
    }

    @PatchMapping("/users/{id}")
    public User patchUser(@PathVariable Long id, @RequestBody User user) {
        User oldUser = this.repository.findById(id).orElseThrow(()-> new NoSuchElementException(
                String.format("This element does not exist")));

        if (user.getEmail() != null)
            oldUser.setEmail(user.getEmail());

        if (user.getPassword() != null)
            oldUser.setPassword(user.getPassword());

        return this.repository.save(oldUser);
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Integer> deleteUser(@PathVariable Long id) {
        this.repository.deleteById(id);
        List<User> userList = this.repository.findAll();

        Map<String, Integer> returnMap= new HashMap<>();

        returnMap.put("count", userList.size());

        return returnMap;
    }

    @PostMapping("/users/authenticate")
    public Authenticate postAuthentication(@RequestBody User user) {
        Authenticate resultAuthenticate = new Authenticate();
        User oldUser = this.repository.findByEmail(user.getEmail());

        if(oldUser != null && oldUser.getPassword().equals(user.getPassword())) {
            resultAuthenticate.setAuthenticated(true);
            resultAuthenticate.setUser(oldUser);
        }
        else {
            resultAuthenticate.setAuthenticated(false);
        }

        return resultAuthenticate;
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String handleException(Exception e) {
        return e.getMessage();
    }
}

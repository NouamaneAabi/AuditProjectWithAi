package com.audit.backend.controllers;

import com.audit.backend.entities.User;
import com.audit.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @com.audit.backend.audit.Auditable(action = "READ", entityType = "User")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @com.audit.backend.audit.Auditable(action = "READ", entityType = "User", entityIdArg = "id")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @com.audit.backend.audit.Auditable(action = "CREATE", entityType = "User")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    @com.audit.backend.audit.Auditable(action = "DELETE", entityType = "User", entityIdArg = "id")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
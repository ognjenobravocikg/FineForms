package com.djokic.userserviceff.controller;

import com.djokic.userserviceff.dto.LoginRequestDTO;
import com.djokic.userserviceff.dto.RegisterRequestDTO;
import com.djokic.userserviceff.entity.User;
import com.djokic.userserviceff.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody RegisterRequestDTO registerRequest){
        return userService.createUser(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestBody LoginRequestDTO loginRequest){
        return ResponseEntity.status(HttpStatus.OK).body(userService.loginUser(loginRequest));
    }

    @GetMapping("/{id}/details")
    public Optional<User> getUserDetails(@PathVariable Long id){
        return userService.findById(id);
    }

    @GetMapping("/")
    public List<User> getUserDetails(){
        return userService.getUsers();
    }
}

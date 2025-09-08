package com.djokic.userservice.controller;

import com.djokic.userservice.dto.LoginRequestDTO;
import com.djokic.userservice.dto.RegisterRequestDTO;
import com.djokic.userservice.entity.User;
import com.djokic.userservice.repository.UserRepository;
import com.djokic.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @RequestBody RegisterRequestDTO registerRequest){
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestBody LoginRequestDTO loginRequest){
        return null;
    }

    @GetMapping("/{id}/details")
    public Optional<User> getUserDetails(@PathVariable UUID id){
        return userRepository.findById(id);
    }
}

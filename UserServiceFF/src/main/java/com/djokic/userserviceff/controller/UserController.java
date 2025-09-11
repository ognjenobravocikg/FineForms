package com.djokic.userserviceff.controller;

import com.djokic.userserviceff.dto.LoginRequestDTO;
import com.djokic.userserviceff.dto.RegisterRequestDTO;
import com.djokic.userserviceff.dto.UserDTO;
import com.djokic.userserviceff.entity.User;
import com.djokic.userserviceff.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequestDTO registerRequest){

        Optional<UserDTO> createdUser = userService.createUser(registerRequest);

        if (createdUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", HttpStatus.CONFLICT.value(),
                            "error", "Conflict",
                            "message", "User with this email already exists!"
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {

        Optional<UserDTO> loggedInUser = userService.loginUser(loginRequest);

        if (loggedInUser.isPresent()) {
            return ResponseEntity.ok(loggedInUser.get());
        } else {
            // JSON odgovor za neuspešan login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Unauthorized",
                            "message", "Invalid email or password"
                    ));
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id) {
        Optional<UserDTO> userDTO = userService.findById(id);
        return userDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getUsers());

    }
}

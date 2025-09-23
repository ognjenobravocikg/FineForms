package com.djokic.userserviceff.controller;

import com.djokic.userserviceff.dto.*;
import com.djokic.userserviceff.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequestDTO registerRequest){

        UserDetailsDTO createdUser = userService.createUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {

        UserDTO loggedInUser = userService.loginUser(loginRequest);
        return ResponseEntity.ok(loggedInUser);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editUser(
            @PathVariable Long id,
            @Valid @RequestBody EditRequestDTO editRequest) {

        UserDTO updatedUser = userService.updateUser(id, editRequest);

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getUserDetails(@PathVariable String email) {
        UserDetailsDTO userDetailsDTO = userService.findByEmail(email);

        return ResponseEntity.ok(userDetailsDTO);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @PatchMapping("/change-role/{id}")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id){
        return ResponseEntity.ok(userService.changeUserRole(id));
    }
}

package com.djokic.apigatewayauthserviceff.controller;

import com.djokic.apigatewayauthserviceff.dto.AuthResponseDTO;
import com.djokic.apigatewayauthserviceff.dto.EditRequestDTO;
import com.djokic.apigatewayauthserviceff.dto.LoginRequestDTO;
import com.djokic.apigatewayauthserviceff.dto.RegisterRequestDTO;
import com.djokic.apigatewayauthserviceff.services.AuthService;
import com.djokic.apigatewayauthserviceff.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class AuthContoller {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/users/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO){
        AuthResponseDTO authResponseDTO = authService.register(registerRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authResponseDTO);
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO){
        AuthResponseDTO authResponseDTO = authService.login(loginRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponseDTO);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<?> editUser(
            @PathVariable Long id,
            @RequestBody EditRequestDTO editRequestDTO,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        if (!userIdFromToken.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "You are not allowed to edit this user",
                            "status", 403,
                            "error", "Forbidden"
                    ));
        }

        return ResponseEntity.status(HttpStatus.OK).body(authService.editUser(id, editRequestDTO));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(authService.getAllUsers());
    }
}

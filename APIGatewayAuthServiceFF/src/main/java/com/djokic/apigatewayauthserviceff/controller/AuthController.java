package com.djokic.apigatewayauthserviceff.controller;

import com.djokic.apigatewayauthserviceff.client.FormServiceClient;
import com.djokic.apigatewayauthserviceff.client.ResponseServiceClient;
import com.djokic.apigatewayauthserviceff.client.UserServiceClient;
import com.djokic.apigatewayauthserviceff.dto.formservicedto.AddCollaboratorDTO;
import com.djokic.apigatewayauthserviceff.dto.formservicedto.CreateFormDto;
import com.djokic.apigatewayauthserviceff.dto.responseservicedto.CreateResponseDTO;
import com.djokic.apigatewayauthserviceff.dto.userservicedto.*;
import com.djokic.apigatewayauthserviceff.enumeration.CollaboratorRole;
import com.djokic.apigatewayauthserviceff.services.AuthService;
import com.djokic.apigatewayauthserviceff.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final FormServiceClient formServiceClient;
    private final UserServiceClient userServiceClient;
    private final ResponseServiceClient responseServiceClient;

    @PostMapping("/users/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO){
        AuthResponseDTO authResponseDTO = authService.register(registerRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authResponseDTO);
    }

    @GetMapping("/users/{id}/details")
    public ResponseEntity<?> getUserDetailsById(@PathVariable("id") Long id){
        UserDetailsDTO userDetailsDTO = userServiceClient.getUserDetailsById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDetailsDTO);
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO){
        AuthResponseDTO authResponseDTO = authService.login(loginRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponseDTO);
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<?> getUserDetailsByEmail(@PathVariable("email") String email){
        UserDetailsDTO userDetailsDTO = userServiceClient.getUserDetailsByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(userDetailsDTO);
    }

    @PostMapping("/users/edit/{id}")
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

        return ResponseEntity.ok(authService.editUser(id, editRequestDTO));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(authService.getAllUsers());
    }

    @PostMapping("/form")
    ResponseEntity<?> createForm(@RequestBody CreateFormDto createFormDto, @RequestHeader("Authorization") String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        return formServiceClient.createForm(createFormDto, userIdFromToken);
    }

    @GetMapping("/form/{id}")
    ResponseEntity<?> getFormById(@PathVariable("id") Long id){
        return formServiceClient.getFormById(id);
    }

    @GetMapping("/form")
    ResponseEntity<?> getAllForms(){
        return formServiceClient.getAllForms();
    }

    @PutMapping("/form/{id}")
    ResponseEntity<?> updateForm(@PathVariable Long id, @RequestBody CreateFormDto createFormDto, @RequestHeader("Authorization") String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        return formServiceClient.updateForm(id, createFormDto, userIdFromToken);
    }

    @DeleteMapping("/form/{id}")
    ResponseEntity<?> deleteForm(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        return formServiceClient.deleteForm(id, userIdFromToken);
    }

    @GetMapping("/form/public/{id}")
    ResponseEntity<?> getPublicFormById(@PathVariable Long id){
        return formServiceClient.getPublicFormById(id);
    }

    @PostMapping("/form/{formId}/collab")
    ResponseEntity<?> addCollaborator(@PathVariable("formId") Long formId,
                                      @RequestBody AddCollaboratorDTO addCollaboratorDto,
                                      @RequestHeader("Authorization") String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        Long currentUserId = jwtService.extractAllClaims(token).get("id", Long.class);

        return formServiceClient.addCollaborator(formId, addCollaboratorDto.getUserId(), addCollaboratorDto.getRole(), currentUserId);
    }

    @GetMapping("/form/{formId}/collab")
    ResponseEntity<?> getCollaborators(@PathVariable("formId") Long formId){
        return formServiceClient.getCollaborators(formId);
    }

    @PostMapping("/form/{formId}/collab/{userId}")
    ResponseEntity<?> updateRole(@PathVariable("formId") Long formId,
                                 @PathVariable("userId") Long userId,
                                 @RequestParam CollaboratorRole collaboratorRole,
                                 @RequestHeader("Authorization") String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        return formServiceClient.updateRole(formId, userId, collaboratorRole, userIdFromToken);
    }

    @DeleteMapping("/form/{formId}/collab/{userId}")
    ResponseEntity<?> removeCollaborator(@PathVariable("formId") Long formId,
                                         @PathVariable("userId") Long userId,
                                         @RequestHeader("Authorization") String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        return formServiceClient.removeCollaborator(formId, userId, userIdFromToken);
    }

    @GetMapping("/response/{formId}")
    ResponseEntity<?> getAllResponsesForForm(@PathVariable Long formId, @RequestHeader("Authorization") String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        return responseServiceClient.getAllResponsesForForm(formId, userIdFromToken);
    }

    @GetMapping("/response/{formId}/{id}")
    ResponseEntity<?> getResponseForFormById(@PathVariable Long formId, @PathVariable Long responseId, @RequestHeader("Authorization") String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        return responseServiceClient.getResponseForFormById(formId, responseId, userIdFromToken);
    }

    @PostMapping("/response")
    ResponseEntity<?> createResponse(@RequestBody CreateResponseDTO createResponseDTO){
        return responseServiceClient.createResponse(createResponseDTO);
    }

    @DeleteMapping("/response/{id}")
    ResponseEntity<?> deleteResponse(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        Long userIdFromToken = jwtService.extractAllClaims(token).get("id", Long.class);

        return responseServiceClient.deleteResponse(id, userIdFromToken);
    }
}

package com.djokic.apigatewayauthserviceff.client;

import com.djokic.apigatewayauthserviceff.dto.userservicedto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {

    @PostMapping("/users/register")
    UserDetailsDTO register(@RequestBody RegisterRequestDTO registerRequest);

    @PostMapping("/users/login")
    UserDetailsDTO login(@RequestBody LoginRequestDTO loginRequest);

    @PostMapping("/users/edit/{id}")
    UserDTO editUser(@PathVariable("id") Long id, @RequestBody EditRequestDTO editRequest);

    @GetMapping("/users/{id}/details")
    UserDetailsDTO getUserDetailsById(@PathVariable("id") Long id);

    @GetMapping("/users/{email}")
    UserDetailsDTO getUserDetailsByEmail(@PathVariable("email") String email);

    @GetMapping("/users/")
    List<UserDTO> getAllUsers();

    @PatchMapping("/users/change-role/{id}")
    UserDTO changeUserRole(@PathVariable("id") Long id);
}
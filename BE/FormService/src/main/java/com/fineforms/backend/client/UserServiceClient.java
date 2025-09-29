package com.fineforms.backend.client;

import com.fineforms.backend.DTO.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {

    @GetMapping("/users/{id}/details")
    UserDetailsDTO getUserDetailsById(@PathVariable("id") Long id);

    @GetMapping("/users/{email}")
    UserDetailsDTO getUserDetailsByEmail(@PathVariable("email") String email);

    @GetMapping("/users/")
    List<UserDTO> getAllUsers();

}

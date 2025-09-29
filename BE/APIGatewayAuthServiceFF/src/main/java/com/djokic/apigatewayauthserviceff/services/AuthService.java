package com.djokic.apigatewayauthserviceff.services;

import com.djokic.apigatewayauthserviceff.client.UserServiceClient;
import com.djokic.apigatewayauthserviceff.dto.userservicedto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserServiceClient userClient;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetailsDTO user = userClient.getUserDetailsByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole())
                .build();
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        UserDetailsDTO newUser = userClient.register(request);
        return new AuthResponseDTO(jwtService.generateToken(newUser));
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        UserDetailsDTO user = userClient.login(request);
        return new AuthResponseDTO(jwtService.generateToken(user));
    }

    public UserDTO editUser(Long id, EditRequestDTO request) {
        UserDTO editedUser = userClient.editUser(id, request);

        return editedUser;
    }

    public List<UserDTO> getAllUsers() {
        return userClient.getAllUsers();
    }
}

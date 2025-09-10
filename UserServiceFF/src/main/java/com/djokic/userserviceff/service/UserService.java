package com.djokic.userserviceff.service;

import com.djokic.userserviceff.dto.LoginRequestDTO;
import com.djokic.userserviceff.dto.RegisterRequestDTO;
import com.djokic.userserviceff.entity.User;
import com.djokic.userserviceff.enumeration.Role;
import com.djokic.userserviceff.repository.UserRepository;
import com.djokic.userserviceff.util.HmacSHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final HmacSHA256 hmacSHA256;


    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<Object> createUser(RegisterRequestDTO registerRequest) {
        User user = User
                .builder()
                .email(registerRequest.getEmail())
                .password(hmacSHA256.hashPassword(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(Role.USER)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }

    public Optional<User> loginUser(LoginRequestDTO loginRequestDTO){
        if(loginRequestDTO.getEmail() != null && loginRequestDTO.getPassword() != null){
            return userRepository.findByEmail(loginRequestDTO.getEmail())
                    .filter(user -> hmacSHA256.matches(loginRequestDTO.getPassword(), user.getPassword()));
        } else {
            return Optional.empty();
        }
    }
}

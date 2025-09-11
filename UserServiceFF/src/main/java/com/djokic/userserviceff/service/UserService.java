package com.djokic.userserviceff.service;

import com.djokic.userserviceff.dto.LoginRequestDTO;
import com.djokic.userserviceff.dto.RegisterRequestDTO;
import com.djokic.userserviceff.dto.UserDTO;
import com.djokic.userserviceff.entity.User;
import com.djokic.userserviceff.enumeration.Role;
import com.djokic.userserviceff.mappers.UserMapper;
import com.djokic.userserviceff.repository.UserRepository;
import com.djokic.userserviceff.util.HmacSHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final HmacSHA256 hmacSHA256;
    private final UserMapper userMapper;


    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::userToUserDTO);
    }

    public List<UserDTO> getUsers() {
        return userMapper.userListToUserDTOList(userRepository.findAll());
    }

    public Optional<UserDTO> createUser(RegisterRequestDTO registerRequest) {
        Assert.notNull(registerRequest.getEmail(), "Email must be provided !");
        Assert.notNull(registerRequest.getPassword(), "Password cannot be blank");

        registerRequest.setEmail(registerRequest.getEmail().toLowerCase()); // Transform all letters from email to lowercase

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) return Optional.empty();

        User user = User
                .builder()
                .email(registerRequest.getEmail())
                .password(hmacSHA256.hashPassword(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(Role.USER)
                .build();

        return Optional.of(userMapper.userToUserDTO(userRepository.save(user)));
    }

    public Optional<UserDTO> loginUser(LoginRequestDTO loginRequestDTO){
        if(loginRequestDTO.getEmail() != null && loginRequestDTO.getPassword() != null){
            return userRepository.findByEmail(loginRequestDTO.getEmail())
                    .filter(user -> hmacSHA256.matches(loginRequestDTO.getPassword(), user.getPassword()))
                    .map(userMapper::userToUserDTO);
        } else {
            return Optional.empty();
        }
    }
}

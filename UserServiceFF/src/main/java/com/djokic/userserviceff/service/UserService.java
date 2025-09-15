package com.djokic.userserviceff.service;

import com.djokic.userserviceff.dto.EditRequestDTO;
import com.djokic.userserviceff.dto.LoginRequestDTO;
import com.djokic.userserviceff.dto.RegisterRequestDTO;
import com.djokic.userserviceff.dto.UserDTO;
import com.djokic.userserviceff.entity.User;
import com.djokic.userserviceff.enumeration.Role;
import com.djokic.userserviceff.exception.*;
import com.djokic.userserviceff.mappers.UserMapper;
import com.djokic.userserviceff.repository.UserRepository;
import com.djokic.userserviceff.util.HmacSHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final HmacSHA256 hmacSHA256;
    private final UserMapper userMapper;


    public UserDTO findById(Long id) throws RuntimeException{
        return userMapper.userToUserDTO(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    public List<UserDTO> getUsers() {
        return userMapper.userListToUserDTOList(userRepository.findAll());
    }

    public UserDTO createUser(RegisterRequestDTO registerRequest) throws RuntimeException{
        if(registerRequest.getEmail().isEmpty()) throw new EmailNotProvidedException();
        if(registerRequest.getPassword().length() < 8) throw new PasswordLengthException();

        registerRequest.setEmail(registerRequest.getEmail().toLowerCase());

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent())throw new EmailAlreadyExistsException(registerRequest.getEmail());

        User user = User
                .builder()
                .email(registerRequest.getEmail())
                .password(hmacSHA256.hashPassword(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(Role.USER)
                .build();

        return userMapper.userToUserDTO(userRepository.save(user));
    }

    public UserDTO loginUser(LoginRequestDTO loginRequestDTO) throws RuntimeException{
        if(loginRequestDTO.getEmail().isEmpty()) throw new EmailNotProvidedException();
        if(loginRequestDTO.getPassword().isEmpty()) throw new PasswordNotProvidedException();

        User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(WrongCredentialsException::new);

        if(!hmacSHA256.hashPassword(loginRequestDTO.getPassword()).equals(user.getPassword())) throw new WrongCredentialsException();

        return userMapper.userToUserDTO(user);
    }

    public UserDTO updateUser(Long id, EditRequestDTO editRequestDTO) throws RuntimeException{
        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()) throw new UserNotFoundException(id);

        User user = userOptional.get();

        if(editRequestDTO.getEmail() != null && !editRequestDTO.getEmail().isBlank() && !editRequestDTO.getEmail().equals(user.getEmail())){
            if(userRepository.findByEmail(editRequestDTO.getEmail()).isPresent()){
                throw new EmailAlreadyExistsException(editRequestDTO.getEmail());
            }

            user.setEmail(editRequestDTO.getEmail());
        }
        if(editRequestDTO.getFirstName() != null && !editRequestDTO.getFirstName().isBlank()) user.setFirstName(editRequestDTO.getFirstName());
        if(editRequestDTO.getLastName() != null && !editRequestDTO.getLastName().isBlank()) user.setLastName(editRequestDTO.getLastName());
        if(editRequestDTO.getPassword() != null && !user.getPassword().equals(hmacSHA256.hashPassword(editRequestDTO.getPassword()))){
            if(editRequestDTO.getPassword().length() < 8) throw new PasswordLengthException();

            user.setPassword(hmacSHA256.hashPassword(editRequestDTO.getPassword()));
        }

        return userMapper.userToUserDTO(userRepository.save(user));
    }

    public UserDTO changeUserRole(Long id) throws RuntimeException{
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if(user.getRole() == Role.ADMIN){
            user.setRole(Role.USER);
        }else{
            user.setRole(Role.ADMIN);
        }

        return userMapper.userToUserDTO(userRepository.save(user));
    }
}

package com.djokic.userserviceff.service;

import com.djokic.userserviceff.dto.*;
import com.djokic.userserviceff.entity.User;
import com.djokic.userserviceff.enumeration.Role;
import com.djokic.userserviceff.exception.*;
import com.djokic.userserviceff.mappers.UserDetailsMapper;
import com.djokic.userserviceff.mappers.UserMapper;
import com.djokic.userserviceff.repository.UserRepository;
import com.djokic.userserviceff.util.HmacSHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final HmacSHA256 hmacSHA256;
    private final UserDetailsMapper userDetailsMapper;
    private final UserMapper userMapper;

    public UserDetailsDTO findByEmail(String email) throws RuntimeException{
        if(email == null || email.isEmpty()) throw new EmailNotProvidedException();

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if(!email.matches(emailRegex)) {
            throw new InvalidEmailFormatException();
        }

        return userDetailsMapper.userToUserDetailsDTO(userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email)));
    }

    public UserDTO findById(Long id) throws RuntimeException{
        if(id == null) throw new IdNotProvidedException();

        return userMapper.userToUserDTO(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    public List<UserDTO> getUsers() {
        return userMapper.userListToUserDTOList(userRepository.findAll());
    }

    @Transactional
    public UserDetailsDTO createUser(RegisterRequestDTO registerRequest) throws RuntimeException{
        if(registerRequest.getEmail().isEmpty()) throw new EmailNotProvidedException();
        if(registerRequest.getFirstName().isEmpty()) throw new FirstNameNotProvidedException();
        if(registerRequest.getLastName().isEmpty()) throw new LastNameNotProvidedException();
        if(registerRequest.getPassword().isEmpty()) throw new PasswordNotProvidedException();
        if(registerRequest.getPassword().length() < 8) throw new PasswordLengthException();

        registerRequest.setEmail(registerRequest.getEmail().toLowerCase().trim());

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent())throw new EmailAlreadyExistsException(registerRequest.getEmail());

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if(!registerRequest.getEmail().matches(emailRegex)) {
            throw new InvalidEmailFormatException();
        }

        String cleanFirstName = registerRequest.getFirstName().trim();
        String cleanLastName = registerRequest.getLastName().trim();

        if (!cleanFirstName.matches("^[\\p{L}]+(?:\\s[\\p{L}]+)*$")) {
            throw new InvalidInputFieldFormatException("First name");
        }

        if (!cleanLastName.matches("^[\\p{L}]+(?:\\s[\\p{L}]+)*$")) {
            throw new InvalidInputFieldFormatException("Last name");
        }

        if(cleanFirstName.isEmpty()) throw new InvalidInputFieldFormatException("First name");
        if(cleanLastName.isEmpty()) throw new InvalidInputFieldFormatException("Last name");
        if(cleanFirstName.length() > 50) throw new InputLimitExceededException("First name");
        if(cleanLastName.length() > 50) throw new InputLimitExceededException("Last name");

        User user = User
                .builder()
                .email(registerRequest.getEmail())
                .password(hmacSHA256.hashPassword(registerRequest.getPassword()))
                .firstName(cleanFirstName)
                .lastName(cleanLastName)
                .role(Role.USER)
                .build();

        return userDetailsMapper.userToUserDetailsDTO(userRepository.save(user));
    }

    public UserDTO loginUser(LoginRequestDTO loginRequestDTO) throws RuntimeException{
        if(loginRequestDTO.getEmail().isEmpty()) throw new EmailNotProvidedException();

        String normalizedEmail = loginRequestDTO.getEmail().trim().toLowerCase();

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if(!normalizedEmail.matches(emailRegex)) {
            throw new InvalidEmailFormatException();
        }

        if(loginRequestDTO.getPassword().isEmpty()) throw new PasswordNotProvidedException();

        User user = userRepository.findByEmail(normalizedEmail).orElseThrow(WrongCredentialsException::new);

        if(!hmacSHA256.hashPassword(loginRequestDTO.getPassword()).equals(user.getPassword())) throw new WrongCredentialsException();

        return userMapper.userToUserDTO(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, EditRequestDTO editRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (editRequestDTO.getEmail() != null && !editRequestDTO.getEmail().isEmpty()) {
            String normalizedEmail = editRequestDTO.getEmail().trim().toLowerCase();
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!normalizedEmail.matches(emailRegex)) {
                throw new InvalidEmailFormatException();
            }

            if (!normalizedEmail.equalsIgnoreCase(user.getEmail()) &&
                    userRepository.findByEmail(normalizedEmail).isPresent()) {
                throw new EmailAlreadyExistsException(normalizedEmail);
            }

            user.setEmail(editRequestDTO.getEmail());
        }

        if (editRequestDTO.getFirstName() != null && !editRequestDTO.getFirstName().isEmpty()) {
            String cleanFirstName = editRequestDTO.getFirstName().trim();
            if (!cleanFirstName.matches("^[\\p{L}]+(?:\\s[\\p{L}]+)*$")) {
                throw new InvalidInputFieldFormatException("First name");
            }
            if (cleanFirstName.length() > 50) throw new InputLimitExceededException("First name");
            user.setFirstName(cleanFirstName);
        }

        if (editRequestDTO.getLastName() != null && !editRequestDTO.getLastName().isEmpty()) {
            String cleanLastName = editRequestDTO.getLastName().trim();
            if (!cleanLastName.matches("^[\\p{L}]+(?:\\s[\\p{L}]+)*$")) {
                throw new InvalidInputFieldFormatException("Last name");
            }
            if (cleanLastName.length() > 50) throw new InputLimitExceededException("Last name");
            user.setLastName(cleanLastName);
        }

        if (editRequestDTO.getPassword() != null && !editRequestDTO.getPassword().isEmpty()) {
            if (editRequestDTO.getPassword().length() < 8) {
                throw new PasswordLengthException();
            }
            user.setPassword(hmacSHA256.hashPassword(editRequestDTO.getPassword()));
        }

        return userMapper.userToUserDTO(userRepository.save(user));
    }

    @Transactional
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

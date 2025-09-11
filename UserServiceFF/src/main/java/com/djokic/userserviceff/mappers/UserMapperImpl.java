package com.djokic.userserviceff.mappers;

import com.djokic.userserviceff.dto.UserDTO;
import com.djokic.userserviceff.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO userToUserDTO(User user) {
        if (user == null) return null;

        return UserDTO
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }

    @Override
    public User userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) return null;

        return User
                .builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .role(userDTO.getRole())
                .password(null)
                .build();
    }

    @Override
    public List<UserDTO> userListToUserDTOList(List<User> users) {
        if (users == null) return null;
        return users.stream()
                .map(this::userToUserDTO)
                .collect(Collectors.toList());
    }
}

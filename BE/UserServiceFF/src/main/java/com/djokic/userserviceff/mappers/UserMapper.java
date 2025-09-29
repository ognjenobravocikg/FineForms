package com.djokic.userserviceff.mappers;

import com.djokic.userserviceff.dto.UserDTO;
import com.djokic.userserviceff.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);

    List<UserDTO> userListToUserDTOList(List<User> users);
}

package com.djokic.userserviceff.mappers;

import com.djokic.userserviceff.dto.UserDTO;
import com.djokic.userserviceff.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDTO userToUserDTO(User user);

    @Mapping(target = "password", ignore = true)
    User userDTOToUser(UserDTO userDTO);

    List<UserDTO> userListToUserDTOList(List<User> users);
}

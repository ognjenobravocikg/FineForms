package com.djokic.userserviceff.mappers;

import com.djokic.userserviceff.dto.UserDetailsDTO;
import com.djokic.userserviceff.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDetailsMapper {
    UserDetailsDTO userToUserDetailsDTO(User user);

    User userDetailsDTOToUser(UserDetailsDTO userDTO);
}

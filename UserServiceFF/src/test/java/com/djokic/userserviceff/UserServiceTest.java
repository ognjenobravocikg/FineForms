package com.djokic.userserviceff;

import com.djokic.userserviceff.dto.EditRequestDTO;
import com.djokic.userserviceff.dto.LoginRequestDTO;
import com.djokic.userserviceff.dto.RegisterRequestDTO;
import com.djokic.userserviceff.dto.UserDTO;
import com.djokic.userserviceff.enumeration.Role;
import com.djokic.userserviceff.exception.*;
import com.djokic.userserviceff.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    // REGISTER USE-CASE TESTS

    public RegisterRequestDTO createMockRegisterRequest(String email, String password, String firstName, String lastName){
        return new RegisterRequestDTO(email,password,firstName,lastName);
    }

    // User-Based scenarios

    @Test
    void testCreateUserSuccess(){
        RegisterRequestDTO registerRequest = createMockRegisterRequest("testUser1@gmail.com","passwordTest!","John","Doe");

        UserDTO createdUser = userService.createUser(registerRequest);

        assertNotNull(createdUser);
        assertEquals(registerRequest.getEmail(),createdUser.getEmail());
        assertEquals(registerRequest.getFirstName(),createdUser.getFirstName());
        assertEquals(registerRequest.getLastName(),createdUser.getLastName());
    }

    @Test
    void testCreateDuplicateUserFail(){
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("testUser1@gmail.com","passwordTest!","John","Doe");
        userService.createUser(registerRequestDTO);

        registerRequestDTO.setFirstName("Johanna");
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(registerRequestDTO));
    }

    // Email-Based scenarios

    @Test
    void testCreateUserWithEmptyEmail(){
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("", "passwordTest!","John","Doe");

        assertThrows(EmailNotProvidedException.class, () -> userService.createUser(registerRequestDTO));
    }

    @Test
    void testCreateUserWithNullEmail(){
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest(null, "passwordTest!","John","Doe");

        assertThrows(EmailNotProvidedException.class, () -> userService.createUser(registerRequestDTO));
    }

    @Test
    void testCreateUserWithInvalidEmailFormat() {
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("invalid-email", "passwordTest!", "John", "Doe");
        assertThrows(InvalidEmailFormatException.class, () -> userService.createUser(registerRequestDTO));
    }

    // Password-Based scenarios

    @Test
    void testCreateUserWithShortPassword(){
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("testUser1@gmail.com","short","John","Doe");

        assertThrows(PasswordLengthException.class, () -> userService.createUser(registerRequestDTO));
    }

    @Test
    void testCreateUserWithEmptyPassword() {
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("testUser2@gmail.com", "", "John", "Doe");
        assertThrows(PasswordNotProvidedException.class, () -> userService.createUser(registerRequestDTO));
    }

    @Test
    void testCreateUserWithNullPassword() {
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("testUser2@gmail.com", null, "John", "Doe");
        assertThrows(PasswordNotProvidedException.class, () -> userService.createUser(registerRequestDTO));
    }

    // FirstName-Based scenarios

    @Test
    void testCreateUserWithEmptyFirstName() {
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("testUser1@gmail.com", "passwordTest!", "", "Doe");

        assertThrows(FirstNameNotProvidedException.class, () -> userService.createUser(registerRequestDTO));
    }

    @Test
    void testCreateUserWithNullFirstName() {
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("testUser5@gmail.com", "passwordTest!", null, "Doe");
        assertThrows(FirstNameNotProvidedException.class, () -> userService.createUser(registerRequestDTO));
    }

    // LastName-Based scenarios


    @Test
    void testCreateuserWithEmptyLastName(){
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("testUser1@gmail.com", "passwordTest!", "John", "");

        assertThrows(LastNameNotProvidedException.class, () -> userService.createUser(registerRequestDTO));
    }

    @Test
    void testCreateUserWithNullLastName() {
        RegisterRequestDTO registerRequestDTO = createMockRegisterRequest("testUser6@gmail.com", "passwordTest!", "John", null);
        assertThrows(LastNameNotProvidedException.class, () -> userService.createUser(registerRequestDTO));
    }

    // FIND USER USE-CASE TESTS

    @Test
    void findUserByIdSuccess(){
        RegisterRequestDTO registerRequest = createMockRegisterRequest("testUser1@gmail.com","passwordTest!","John","Doe");

        UserDTO createdUser = userService.createUser(registerRequest);

        assertEquals(createdUser, userService.findById(createdUser.getId()));
    }

    @Test
    void findUserByIdFail(){
        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    // GET USERS USE-CASE TESTS

    @Test
    void getAllUsersSuccess() {
        RegisterRequestDTO user1 = createMockRegisterRequest("user1@test.com", "password123!", "John", "Doe");
        RegisterRequestDTO user2 = createMockRegisterRequest("user2@test.com", "password123!", "Jane", "Smith");

        userService.createUser(user1);
        userService.createUser(user2);

        List<UserDTO> users = userService.getUsers();

        assertEquals(2, users.size());
    }

    // LOGIN USE-CASE TESTS

    @Test
    void loginUserSuccess() {
        RegisterRequestDTO registerRequest = createMockRegisterRequest("test@test.com", "password123!", "John", "Doe");
        userService.createUser(registerRequest);

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@test.com", "password123!");
        UserDTO loggedInUser = userService.loginUser(loginRequest);

        assertNotNull(loggedInUser);
        assertEquals("test@test.com", loggedInUser.getEmail());
    }

    @Test
    void loginUserWithWrongCredentials() {
        RegisterRequestDTO registerRequest = createMockRegisterRequest("test@test.com", "password123!", "John", "Doe");
        userService.createUser(registerRequest);

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@test.com", "wrongpassword");
        assertThrows(WrongCredentialsException.class, () -> userService.loginUser(loginRequest));
    }

    @Test
    void loginUserWithInvalidEmail() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("invalid-email", "password123!");
        assertThrows(InvalidEmailFormatException.class, () -> userService.loginUser(loginRequest));
    }

    // UPDATE USER USE-CASE TESTS

    @Test
    void updateUserSuccess() {
        RegisterRequestDTO registerRequest = createMockRegisterRequest("test@test.com", "password123!", "John", "Doe");
        UserDTO createdUser = userService.createUser(registerRequest);

        EditRequestDTO editRequest = new EditRequestDTO("updated@test.com", "newpassword123!", "Jane", "Smith");
        UserDTO updatedUser = userService.updateUser(createdUser.getId(), editRequest);

        assertEquals("updated@test.com", updatedUser.getEmail());
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
    }

    @Test
    void updateUserWithExistingEmail() {
        RegisterRequestDTO user1 = createMockRegisterRequest("test1@test.com", "password123!", "John", "Doe");
        RegisterRequestDTO user2 = createMockRegisterRequest("test2@test.com", "password123!", "Jane", "Smith");

        UserDTO createdUser1 = userService.createUser(user1);
        userService.createUser(user2);

        EditRequestDTO editRequest = new EditRequestDTO("test2@test.com", "password123!", "John", "Doe");
        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(createdUser1.getId(), editRequest));
    }

    // CHANGE USER ROLE USE-CASE TESTS

    @Test
    void changeUserRoleSuccess() {
        RegisterRequestDTO registerRequest = createMockRegisterRequest("test@test.com", "password123!", "John", "Doe");
        UserDTO createdUser = userService.createUser(registerRequest);

        UserDTO updatedUser = userService.changeUserRole(createdUser.getId());
        assertEquals(Role.ADMIN, updatedUser.getRole());

        updatedUser = userService.changeUserRole(createdUser.getId());
        assertEquals(Role.USER, updatedUser.getRole());
    }

    @Test
    void changeUserRoleNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> userService.changeUserRole(1L));
    }
}
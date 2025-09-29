package com.djokic.apigatewayauthserviceff;

import com.djokic.apigatewayauthserviceff.client.UserServiceClient;
import com.djokic.apigatewayauthserviceff.dto.userservicedto.AuthResponseDTO;
import com.djokic.apigatewayauthserviceff.dto.userservicedto.LoginRequestDTO;
import com.djokic.apigatewayauthserviceff.dto.userservicedto.RegisterRequestDTO;
import com.djokic.apigatewayauthserviceff.dto.userservicedto.UserDetailsDTO;
import com.djokic.apigatewayauthserviceff.enumeration.Role;
import com.djokic.apigatewayauthserviceff.services.AuthService;
import com.djokic.apigatewayauthserviceff.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AuthServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    // REGISTER USE-CASE TESTS -> We are going to test only cases that are related to AuthService function in register.
    // As the UserService has test cases that are written for its functionalities.

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterReturnsValidJwt(){
        RegisterRequestDTO registerRequestDTO = RegisterRequestDTO
                .builder()
                .email("testmail@gmail.com")
                .password("testpassword")
                .firstName("John")
                .lastName("Doe")
                .build();

        UserDetailsDTO response = UserDetailsDTO
                .builder()
                .id(1L)
                .email("testmail@gmail.com")
                .password("testpassword")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();

        when(userServiceClient.register(registerRequestDTO)).thenReturn(response);

        AuthResponseDTO user = authService.register(registerRequestDTO);

        assertNotNull(user);

        String receivedToken = user.getToken();

        jwtService.generateToken(response);
        assertEquals(user.getToken(), receivedToken);
    }

    @Test
    void testLoginReturnsValidJwt(){
        LoginRequestDTO loginRequestDTO = LoginRequestDTO
                .builder()
                .email("testmail@gmail.com")
                .password("testpassword")
                .build();

        UserDetailsDTO response = UserDetailsDTO
                .builder()
                .id(1L)
                .email("testmail@gmail.com")
                .password("testpassword")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();

        when(userServiceClient.login(loginRequestDTO)).thenReturn(response);

        AuthResponseDTO responseDto = authService.login(loginRequestDTO);

        String receivedToken = responseDto.getToken();

        UserDetailsDTO mockUserToTest = UserDetailsDTO
                .builder()
                .id(1L)
                .email("testmail@gmail.com")
                .password("testpassword")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();

        assertEquals(responseDto.getToken(), receivedToken);
    }
}
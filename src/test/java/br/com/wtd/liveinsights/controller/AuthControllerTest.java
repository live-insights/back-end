package br.com.wtd.liveinsights.controller;

import br.com.wtd.liveinsights.dto.AuthRequest;
import br.com.wtd.liveinsights.model.User;
import br.com.wtd.liveinsights.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -----------------------------------------------------
    //  TESTE DO /register
    // -----------------------------------------------------
    @Test
    void register_UserAlreadyExists_ShouldReturnConflict() {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("123");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = controller.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Usuário já existe", response.getBody());
    }

    @Test
    void register_NewUser_ShouldSaveAndReturnOk() {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("encoded-pass");

        ResponseEntity<String> response = controller.register(request);

        verify(userRepository).save(any(User.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário registrado com sucesso", response.getBody());
    }

    // -----------------------------------------------------
    //  TESTES DO /login
    // -----------------------------------------------------
    @Test
    void login_ValidCredentials_ShouldReturnOk() {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("123");

        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        ResponseEntity<String> response = controller.login(request, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login efetuado com sucesso", response.getBody());
    }

    @Test
    void login_InvalidCredentials_ShouldReturnUnauthorized() {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        ResponseEntity<String> response = controller.login(request, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário ou senha inválidos", response.getBody());
    }
}

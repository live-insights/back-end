package br.com.wtd.liveinsights.controller;

import br.com.wtd.liveinsights.model.User;
import br.com.wtd.liveinsights.repository.UserRepository;
import br.com.wtd.liveinsights.service.LiveAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LiveAnalysisControllerTest {

    @Mock
    private LiveAnalysisService manager;

    @Mock
    private LiveAnalysisService analysisManager; // repetido no controller

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private LiveAnalysisController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------------------------------
    // START
    // ------------------------------------------

    @Test
    void start_ok() {
        String liveId = "live123";
        User user = new User();
        user.setId(1L);

        when(userDetails.getUsername()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        doNothing().when(manager).configureAnalysis(1L, liveId);
        when(manager.startAnalysis(liveId)).thenReturn(true);

        ResponseEntity<String> response = controller.start(liveId, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Análise iniciada para a live: live123", response.getBody());
    }

    @Test
    void start_liveInativa() {
        String liveId = "live123";
        User user = new User();
        user.setId(1L);

        when(userDetails.getUsername()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        doNothing().when(manager).configureAnalysis(1L, liveId);
        when(manager.startAnalysis(liveId)).thenReturn(false);

        ResponseEntity<String> response = controller.start(liveId, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao iniciar análise. Live inativa.", response.getBody());
    }

    @Test
    void start_erro() {
        String liveId = "live123";

        when(userDetails.getUsername()).thenReturn("user");
        when(userRepository.findByUsername("user"))
                .thenThrow(new RuntimeException("Falha"));

        ResponseEntity<String> response = controller.start(liveId, userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Erro ao configurar análise"));
    }

    // ------------------------------------------
    // STOP
    // ------------------------------------------

    @Test
    void stop_ok() {
        doNothing().when(manager).stopAnalysis();

        ResponseEntity<String> response = controller.stop();

        verify(manager).stopAnalysis();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Análise interrompida.", response.getBody());
    }

    // ------------------------------------------
    // STATUS
    // ------------------------------------------

    @Test
    void status_running() {
        when(manager.isRunning()).thenReturn(true);

        ResponseEntity<String> response = controller.status();

        assertEquals("Análise em execução.", response.getBody());
    }

    @Test
    void status_stopped() {
        when(manager.isRunning()).thenReturn(false);

        ResponseEntity<String> response = controller.status();

        assertEquals("Análise parada.", response.getBody());
    }
}

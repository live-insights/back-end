package br.com.wtd.liveinsights.controller;

import br.com.wtd.liveinsights.model.CommentsInfo;
import br.com.wtd.liveinsights.model.User;
import br.com.wtd.liveinsights.repository.CommentsRepository;
import br.com.wtd.liveinsights.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentsControllerTest {

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CommentsController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -----------------------------------------------------
    // TESTE: retornar comentários com usuário autenticado
    // -----------------------------------------------------
    @Test
    void getCommentsForLive_ShouldReturnComments() {
        String liveId = "12345";

        User user = new User();
        user.setId(1L);

        CommentsInfo c1 = new CommentsInfo();
        CommentsInfo c2 = new CommentsInfo();
        List<CommentsInfo> commentsList = List.of(c1, c2);

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(commentsRepository.findByLive_LiveIdAndUser_Id(liveId, 1L))
                .thenReturn(commentsList);

        ResponseEntity<List<CommentsInfo>> response =
                controller.getCommentsForLive(liveId, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    // -----------------------------------------------------
    // TESTE: erro ao buscar usuário → retorna 403
    // -----------------------------------------------------
    @Test
    void getCommentsForLive_WhenUserNotFound_ShouldReturnForbidden() {
        String liveId = "12345";

        when(userDetails.getUsername()).thenReturn("notfound");
        when(userRepository.findByUsername("notfound"))
                .thenReturn(Optional.empty()); // dispara o orElseThrow()

        ResponseEntity<List<CommentsInfo>> response =
                controller.getCommentsForLive(liveId, userDetails);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

}

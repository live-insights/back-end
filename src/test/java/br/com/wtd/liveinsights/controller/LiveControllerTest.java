package br.com.wtd.liveinsights.controller;

import br.com.wtd.liveinsights.model.Live;
import br.com.wtd.liveinsights.model.Tag;
import br.com.wtd.liveinsights.model.User;
import br.com.wtd.liveinsights.repository.LiveRepository;
import br.com.wtd.liveinsights.repository.TagRepository;
import br.com.wtd.liveinsights.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LiveControllerTest {

    @Mock private LiveRepository liveRepo;
    @Mock private LiveRepository liveRepository;
    @Mock private UserRepository userRepo;
    @Mock private UserRepository userRepository;
    @Mock private TagRepository tagRepository;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private LiveController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -----------------------------------------------------------
    // GET /lives
    // -----------------------------------------------------------
    @Test
    void listLives_ok() {
        User user = new User();
        user.setId(1L);

        when(userDetails.getUsername()).thenReturn("john");
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));

        when(liveRepo.findByUserId(1L)).thenReturn(List.of(new Live(), new Live()));

        List<Live> result = controller.listLives(userDetails);

        assertEquals(2, result.size());
    }

    // -----------------------------------------------------------
    // POST /lives
    // -----------------------------------------------------------
    @Test
    void createLive_ok() {
        Map<String, String> body = Map.of(
                "liveId", "123",
                "title", "Live Test",
                "tagName", "Games"
        );

        User user = new User();
        user.setId(1L);

        when(userDetails.getUsername()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(liveRepository.findByLiveIdAndUserId("123", 1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Tag tag = new Tag("Games");
        when(tagRepository.findByName("Games")).thenReturn(Optional.of(tag));

        ResponseEntity<?> response = controller.createLive(body, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(liveRepository).save(any(Live.class));
    }

    @Test
    void createLive_conflict() {
        Map<String, String> body = Map.of(
                "liveId", "123",
                "title", "Live Test",
                "tagName", "Games"
        );

        User user = new User();
        user.setId(1L);

        when(userDetails.getUsername()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        when(liveRepository.findByLiveIdAndUserId("123", 1L))
                .thenReturn(Optional.of(new Live()));

        ResponseEntity<?> response = controller.createLive(body, userDetails);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    // -----------------------------------------------------------
    // DELETE /lives/{liveId}
    // -----------------------------------------------------------
    @Test
    void deleteLive_ok() {
        Live live = new Live();
        Tag tag = new Tag("Music");
        live.setTag(tag);

        when(liveRepository.findByLiveId("AAA")).thenReturn(Optional.of(live));
        // countByTag returns Long in repository — return a Long (0L)
        when(liveRepository.countByTag(tag)).thenReturn(0L);

        ResponseEntity<Void> response = controller.deleteLive("AAA");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(liveRepository).delete(live);
        verify(tagRepository).delete(tag);
    }

    @Test
    void deleteLive_notFound() {
        when(liveRepository.findByLiveId("AAA")).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.deleteLive("AAA");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // -----------------------------------------------------------
    // PUT /lives/{liveId}
    // -----------------------------------------------------------
    @Test
    void updateLiveTitle_ok() {
        Map<String, String> body = Map.of("title", "New Title");

        User user = new User();
        user.setId(1L);

        Live live = new Live();
        live.setTitle("Old Title");

        when(userDetails.getUsername()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(liveRepository.findByLiveIdAndUserId("ABC", 1L))
                .thenReturn(Optional.of(live));

        ResponseEntity<Void> response = controller.updateLiveTitle("ABC", body, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Title", live.getTitle());
    }

    @Test
    void updateLiveTitle_notFound() {
        User user = new User();
        user.setId(1L);

        when(userDetails.getUsername()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(liveRepository.findByLiveIdAndUserId("ABC", 1L))
                .thenReturn(Optional.empty());

        Map<String, String> body = Map.of("title", "New Title");

        ResponseEntity<Void> response = controller.updateLiveTitle("ABC", body, userDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // -----------------------------------------------------------
    // PUT /lives/{liveId}/tag
    // -----------------------------------------------------------
    @Test
    void updateTag_ok() {
        Live live = new Live();
        Tag oldTag = new Tag("OldTag");
        live.setTag(oldTag);

        when(liveRepository.findByLiveId("XYZ")).thenReturn(Optional.of(live));

        Tag newTag = new Tag("NewTag");
        when(tagRepository.findByNameIgnoreCase("NewTag")).thenReturn(Optional.of(newTag));

        // countByTag returns Long -> return 0L here
        when(liveRepository.countByTag(oldTag)).thenReturn(0L);

        Map<String, String> body = Map.of("tagName", "NewTag");

        ResponseEntity<?> response = controller.updateTag("XYZ", body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newTag, live.getTag());
        verify(tagRepository).delete(oldTag);
    }

    @Test
    void updateTag_notFound() {
        when(liveRepository.findByLiveId("XYZ")).thenReturn(Optional.empty());

        Map<String, String> body = Map.of("tagName", "NewTag");

        ResponseEntity<?> response = controller.updateTag("XYZ", body);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // -----------------------------------------------------------
    // TEST OF INTERNAL TagController
    // -----------------------------------------------------------
    @Test
    void tagController_listTags_ok() throws Exception {
        LiveController.TagController tagController = controller.new TagController();

        Field f = tagController.getClass().getDeclaredField("tagRepository");
        f.setAccessible(true);
        f.set(tagController, tagRepository);

        when(tagRepository.findAll()).thenReturn(List.of(
                new Tag("Games"),
                new Tag("Music")
        ));

        List<String> tags = tagController.getTags();

        assertEquals(2, tags.size());
        assertTrue(tags.contains("Games"));
    }
}

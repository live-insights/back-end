package br.com.wtd.liveinsights.controller;

import br.com.wtd.liveinsights.model.Live;
import br.com.wtd.liveinsights.model.Tag;
import br.com.wtd.liveinsights.model.User;
import br.com.wtd.liveinsights.repository.LiveRepository;
import br.com.wtd.liveinsights.repository.TagRepository;
import br.com.wtd.liveinsights.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lives")
public class LiveController {
    @Autowired
    private LiveRepository liveRepo;
    @Autowired private UserRepository userRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private LiveRepository liveRepository;

    @GetMapping
    public List<Live> listLives(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(" Usuário autenticado: " + userDetails.getUsername());
        var user = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        return liveRepo.findByUserId(user.getId());
    }

    @PostMapping
    public ResponseEntity<?> createLive(@RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails userDetails) {
        String liveId = body.get("liveId");
        String title = body.get("title");
        String tagName = body.get("tagName");

        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (liveRepository.findByLiveIdAndUserId(liveId, userId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Você já criou uma live com esse ID.");
        }

        User user = userRepository.findById(userId).orElseThrow();

        Tag tag = tagRepository.findByName(tagName)
                .orElseGet(() -> tagRepository.save(new Tag(tagName)));

        Live live = new Live();
        live.setLiveId(liveId);
        live.setTitle(title);
        live.setUser(user);
        live.setTag(tag);

        liveRepository.save(live);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{liveId}")
    public ResponseEntity<Void> deleteLive(@PathVariable String liveId) {
        Optional<Live> optionalLive = liveRepository.findByLiveId(liveId);

        if (optionalLive.isPresent()) {
            Live live = optionalLive.get();
            Tag tag = live.getTag();

            liveRepository.delete(live);

            boolean isTagUnused = liveRepository.countByTag(tag) == 0;
            if (isTagUnused) {
                tagRepository.delete(tag);
            }

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{liveId}")
    public ResponseEntity<Void> updateLiveTitle(@PathVariable String liveId, @RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Live> liveOpt = liveRepository.findByLiveIdAndUserId(liveId, getUserId(userDetails));
        if (liveOpt.isPresent()) {
            Live live = liveOpt.get();
            live.setTitle(body.get("title"));
            liveRepository.save(live);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{liveId}/tag")
    public ResponseEntity<?> updateTag(
            @PathVariable String liveId,
            @RequestBody Map<String, String> payload) {

        String newTagName = payload.get("tagName");

        Optional<Live> optionalLive = liveRepository.findByLiveId(liveId);
        if (optionalLive.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Live live = optionalLive.get();
        Tag oldTag = live.getTag();

        Tag newTag = tagRepository.findByNameIgnoreCase(newTagName)
                .orElseGet(() -> tagRepository.save(new Tag(newTagName)));

        live.setTag(newTag);
        liveRepository.save(live);

        if (liveRepository.countByTag(oldTag) == 0) {
            tagRepository.delete(oldTag);
        }
        return ResponseEntity.ok("Tag atualizada com sucesso.");
    }


    @RestController
    public class TagController {

        @Autowired
        private TagRepository tagRepository;

        @GetMapping("/tags")
        public List<String> getTags() {
            return tagRepository.findAll()
                    .stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
        }
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
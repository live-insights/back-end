package br.com.wtd.liveinsights.controller;

import br.com.wtd.liveinsights.model.CommentsInfo;
import br.com.wtd.liveinsights.model.User;
import br.com.wtd.liveinsights.repository.CommentsRepository;
import br.com.wtd.liveinsights.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentsController {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{liveId}")
    public ResponseEntity<List<CommentsInfo>> getCommentsForLive(@PathVariable String liveId,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            List<CommentsInfo> comments = commentsRepository.findByLive_LiveIdAndUser_Id(liveId, user.getId());
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}


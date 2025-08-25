package br.com.wtd.liveinsights.controller;

import br.com.wtd.liveinsights.repository.UserRepository;
import br.com.wtd.liveinsights.service.LiveAnalysisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/live")
public class LiveAnalysisController {

    @Autowired
    private LiveAnalysisManager manager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private LiveAnalysisManager analysisManager;

    @PostMapping("/start/{liveId}")
    public ResponseEntity<String> start(@PathVariable String liveId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow()
                    .getId();

            manager.configureAnalysis(userId, liveId);
            boolean started = manager.startAnalysis(liveId);

            return started
                    ? ResponseEntity.ok("Análise iniciada para a live: " + liveId)
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao iniciar análise. Live inativa.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao configurar análise: " + e.getMessage());
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stop() {
        manager.stopAnalysis();
        return ResponseEntity.ok("Análise interrompida.");
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return manager.isRunning()
                ? ResponseEntity.ok("Análise em execução.")
                : ResponseEntity.ok("Análise parada.");
    }
}


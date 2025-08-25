package br.com.wtd.liveinsights.controller;

import br.com.wtd.liveinsights.dto.AuthRequest;
import br.com.wtd.liveinsights.model.User;
import br.com.wtd.liveinsights.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já existe");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Usuário registrado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request, HttpSession session) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

            Authentication auth = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            return ResponseEntity.ok("Login efetuado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos");
        }
    }
}

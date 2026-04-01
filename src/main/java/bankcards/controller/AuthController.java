package bankcards.controller;

import bankcards.dto.LoginRequest;
import bankcards.dto.LoginResponse;
import bankcards.dto.RegisterRequest;
import bankcards.entity.Role;
import bankcards.entity.User;
import bankcards.security.JwtUtils;
import bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {throw new RuntimeException("Username is already taken");}
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = userService.getRoleByName("ROLE_USER");
        user.setRoles(Set.of(role));
        userService.createUser(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtUtils.generateToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/admin-register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {throw new RuntimeException("Username is already taken");}
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = userService.getRoleByName("ROLE_ADMIN");
        user.setRoles(Set.of(role));
        userService.createUser(user);
        return ResponseEntity.ok("Admin registered");
    }
}
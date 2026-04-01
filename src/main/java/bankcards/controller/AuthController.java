package bankcards.controller;

import bankcards.dto.LoginRequest;
import bankcards.dto.LoginResponse;
import bankcards.entity.Role;
import bankcards.entity.User;
import bankcards.security.JwtUtils;
import bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // ✅ РЕГИСТРАЦИЯ (всегда USER)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {

        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = userService.getRoleByName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userService.saveUser(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // ✅ ЛОГИН
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(request.getUsername());

            String token = jwtUtils.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse("Invalid username or password"));
        }
    }
}
package bankcards.controller;

import bankcards.dto.UserDTO;
import bankcards.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService; // Интерфейс не имеет методов DTO

    // ---------------- Получение всех пользователей — только ADMIN ----------------
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllDTO();
        return ResponseEntity.ok(users);
    }

    // ---------------- Получение конкретного пользователя — ADMIN или сам пользователь ----------------
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        UserDTO user = userService.findByUsernameDTO(username);
        return ResponseEntity.ok(user);
    }
}
package bankcards.controller;

import bankcards.dto.UserDTO;
import bankcards.dto.UserCreateDTO;
import bankcards.entity.User;
import bankcards.service.UserServiceImpl; // Обрати внимание: используем реализацию для DTO
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService; // Интерфейс не имеет методов DTO

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // Используем метод из реализации, который возвращает DTO
        List<UserDTO> users = userService.findAllDTO();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        UserDTO user = userService.findByUsernameDTO(username);
        return ResponseEntity.ok(user);
    }

//    @PostMapping
//    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO dto) {
//        User user = new User();
//        user.setUsername(dto.getUsername());
//        user.setPassword(dto.getPassword()); // пароль нужно будет закодировать в сервисе безопасности
//
//        User saved = userService.saveUser(user);
//
//        // Преобразуем в DTO перед возвратом
//        UserDTO response = UserDTO.fromEntity(saved);
//        return ResponseEntity.ok(response);
//    }
}
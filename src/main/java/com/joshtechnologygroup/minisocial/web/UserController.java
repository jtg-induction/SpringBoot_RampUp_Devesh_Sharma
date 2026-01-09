package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.dto.user.ActiveUserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<ActiveUserDTO>> getActiveUsers() {
        return new ResponseEntity<>(userService.getActiveUsers(), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateRequest req) {
        return new ResponseEntity<>(userService.createUser(req), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserUpdateRequest req) {
        return new ResponseEntity<>(userService.updateUser(req), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        Optional<UserDTO> dto = userService.getUser(id);
        if (dto.isEmpty()) throw new UserDoesNotExistException();
        return new ResponseEntity<>(dto.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
}

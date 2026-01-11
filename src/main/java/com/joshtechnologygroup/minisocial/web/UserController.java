package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.annotation.StandardSecurityResponse;
import com.joshtechnologygroup.minisocial.dto.user.ActiveUserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@StandardSecurityResponse
@Tag(name = "User Management", description = "APIs for managing user accounts and profiles")
class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @Operation(description = "Get a list of all active users", summary = "Retrieve Active Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active users",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActiveUserDTO.class))),
    })
    public ResponseEntity<List<ActiveUserDTO>> getActiveUsers() {
        return new ResponseEntity<>(userService.getActiveUsers(), HttpStatus.OK);
    }

    @PostMapping("/user")
    @Operation(description = "Create a new user account", summary = "Create User")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
    })
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateRequest req) {
        return new ResponseEntity<>(userService.createUser(req), HttpStatus.CREATED);
    }

    @PutMapping("/user")
    @Operation(description = "Update an existing user account", summary = "Update User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
    })
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserUpdateRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(userService.updateUser(req, userDetails.getUsername()), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    @Operation(description = "Get user details by ID", summary = "Retrieve User by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
    })
    public ResponseEntity<UserDTO> getUser(@PositiveOrZero @PathVariable Long id) {
        Optional<UserDTO> dto = userService.getUser(id);
        if (dto.isEmpty()) throw new UserDoesNotExistException();
        return new ResponseEntity<>(dto.get(), HttpStatus.OK);
    }

    @DeleteMapping("/user/{id}")
    @Operation(description = "Delete a user account by ID", summary = "Delete User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
    })
    public ResponseEntity<UserDTO> deleteUser(@PositiveOrZero @PathVariable Long id) {
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
}

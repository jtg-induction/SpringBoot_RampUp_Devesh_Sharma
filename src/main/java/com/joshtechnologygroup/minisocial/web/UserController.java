package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.annotation.BadDeserializationResponse;
import com.joshtechnologygroup.minisocial.annotation.StandardSecurityResponse;
import com.joshtechnologygroup.minisocial.annotation.ValidationErrorResponse;
import com.joshtechnologygroup.minisocial.dto.user.ActiveUserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserCreateRequest;
import com.joshtechnologygroup.minisocial.dto.user.UserDTO;
import com.joshtechnologygroup.minisocial.dto.user.UserUpdateRequest;
import com.joshtechnologygroup.minisocial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "User Management", description = "APIs for managing user accounts and profiles")
class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get a list of all active users", summary = "Retrieve Active Users")
    @StandardSecurityResponse
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active users")
    })
    public ResponseEntity<PagedModel<ActiveUserDTO>> getActiveUsers(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<ActiveUserDTO> users = userService.getActiveUsers(pageable);
        return new ResponseEntity<>(new PagedModel<>(users), HttpStatus.OK);
    }

    @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get details of the currently authenticated user", summary = "Retrieve Current User")
    @StandardSecurityResponse
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current user details")
    })
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO dto = userService.getUserByEmail(userDetails.getUsername());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create a new user account", summary = "Create User")
    @BadDeserializationResponse
    @ValidationErrorResponse
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
    })
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateRequest req) {
        return new ResponseEntity<>(userService.createUser(req), HttpStatus.CREATED);
    }

    @PutMapping(value = "/users/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update an existing user account", summary = "Update User")
    @StandardSecurityResponse
    @BadDeserializationResponse
    @ValidationErrorResponse
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully")
    })
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserUpdateRequest req, @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(userService.updateUser(req, userDetails.getUsername()), HttpStatus.OK);
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @StandardSecurityResponse
    @Operation(description = "Get user details by ID", summary = "Retrieve User by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
    })
    public ResponseEntity<UserDTO> getUser(@PositiveOrZero @PathVariable Long id) {
        UserDTO dto = userService.getUser(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/users/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @StandardSecurityResponse
    @Operation(description = "Delete a user account by ID", summary = "Delete User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully")
    })
    public ResponseEntity<UserDTO> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(userService.deleteUser(userDetails.getUsername()), HttpStatus.OK);
    }
}

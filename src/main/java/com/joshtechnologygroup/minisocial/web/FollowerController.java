package com.joshtechnologygroup.minisocial.web;

import com.joshtechnologygroup.minisocial.annotation.StandardSecurityResponse;
import com.joshtechnologygroup.minisocial.service.FollowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@StandardSecurityResponse
@Validated
@Tag(name = "Followers Management", description = "APIs for managing user followers")
class FollowerController {
    private final FollowerService followerService;

    FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @PostMapping("/following")
    @Operation(summary = "Update User's Following List", description = "Updates the list of users that the authenticated user is following.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the following list", content = @Content),
    })
    public ResponseEntity<Void> updateUserFollowingList(@RequestBody
            List<
                @NotNull(message = "ID cannot be null")
                @PositiveOrZero(message = "ID must be unsigned") Long
            > request, @AuthenticationPrincipal UserDetails userDetails) {
        followerService.updateFollowed(request, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers")
    @Operation(summary = "Get User Followers", description = "Retrieves the list of user IDs that are following the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the followers list",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Long.class)))
            ),
    })
    public ResponseEntity<List<Long>> getUserFollowers(@AuthenticationPrincipal UserDetails userDetails) {
        List<Long> followers = followerService.getUserFollowers(userDetails.getUsername());
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }

    @GetMapping("/following")
    @Operation(summary = "Get Users Followed By", description = "Retrieves the list of user IDs that the authenticated user is following.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the followed users list",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Long.class)))
            ),
    })
    public ResponseEntity<List<Long>> getUsersFollowedBy(@AuthenticationPrincipal UserDetails userDetails) {
                List<Long> followed = followerService.getUsersFollowedBy(userDetails.getUsername());
                return new ResponseEntity<>(followed, HttpStatus.OK);
    }
}

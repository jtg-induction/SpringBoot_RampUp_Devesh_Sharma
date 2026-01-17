package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.follower.UpdateFollowingRequest;
import com.joshtechnologygroup.minisocial.exception.IllegalActionException;
import com.joshtechnologygroup.minisocial.exception.NoEffectException;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import com.joshtechnologygroup.minisocial.exception.InvalidIdException;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowerServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowerService followerService;

    @Test
    void updateFollowed_shouldUpdate_whenValid() {
        User user = UserFactory.defaultUser();
        User user1 = UserFactory.defaultUser();
        User user2 = UserFactory.defaultUser();
        UpdateFollowingRequest updateRequest = new UpdateFollowingRequest(
                List.of(user1.getId(), user2.getId()));

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findExistingUserIds(updateRequest.userIds())).thenReturn(updateRequest.userIds());
        when(userRepository.getReferenceById(user1.getId())).thenReturn(user1);
        when(userRepository.getReferenceById(user2.getId())).thenReturn(user2);

        followerService.updateFollowed(updateRequest, user.getEmail());
        assertEquals(2, user.getFollowed()
                .size());
        assertTrue(user.getFollowed()
                .contains(user1));
        assertTrue(user.getFollowed()
                .contains(user2));
        assertTrue(user1.getFollowers()
                .contains(user));
        assertEquals(1, user1.getFollowers()
                .size());
        assertTrue(user2.getFollowers()
                .contains(user));
        assertEquals(1, user2.getFollowers()
                .size());

        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findExistingUserIds(updateRequest.userIds());
        verify(userRepository, times(2)).getReferenceById(any());
    }

    @Test
    void updateFollowed_shouldThrow_whenInvalidId() {
        User user = UserFactory.defaultUser();
        User user1 = UserFactory.defaultUser();
        UpdateFollowingRequest updateRequest = new UpdateFollowingRequest(List.of(user1.getId(), 999L));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findExistingUserIds(updateRequest.userIds())).thenReturn(List.of(user1.getId()));

        assertThrows(InvalidIdException.class, () ->
                followerService.updateFollowed(updateRequest, user.getEmail())
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findExistingUserIds(updateRequest.userIds());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).getReferenceById(any());
    }

    @Test
    void updateFollowed_shouldThrow_whenUserDoesNotExist() {
        UpdateFollowingRequest updateRequest = new UpdateFollowingRequest(List.of(1L, 2L));

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
                followerService.updateFollowed(updateRequest, "test@gmail.com")
        );

        verify(userRepository, times(1)).findByEmail(any());
        verify(userRepository, never()).findExistingUserIds(any());
    }

    @Test
    void getFollowers_shouldReturnFollowers_whenValid() {
        User user = UserFactory.defaultUser();
        User follower1 = UserFactory.defaultUser();
        User follower2 = UserFactory.defaultUser();
        follower1.addFollowed(user);
        follower2.addFollowed(user);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        List<Long> followers = followerService.getUserFollowers(user.getEmail());
        assertEquals(2, followers.size());
        assertTrue(followers.contains(follower1.getId()));
        assertTrue(followers.contains(follower2.getId()));

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void getFollowers_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
                followerService.getUserFollowers("test@gmail.com"));
    }

    @Test
    void getUsersFollowedBy_shouldReturnFollowedUsers_whenValid() {
        User user = UserFactory.defaultUser();
        User followed1 = UserFactory.defaultUser();
        User followed2 = UserFactory.defaultUser();
        user.addFollowed(followed1);
        user.addFollowed(followed2);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        List<Long> followedUsers = followerService.getUsersFollowedBy(user.getEmail());
        assertEquals(2, followedUsers.size());
        assertTrue(followedUsers.contains(followed1.getId()));
        assertTrue(followedUsers.contains(followed2.getId()));

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void getUsersFollowedBy_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
                followerService.getUsersFollowedBy("test@gmail.com"));
    }

    @Test
    void addFollowed_shouldAddFollowed_whenValid() {
        User user = UserFactory.defaultUser();
        User followedUser = UserFactory.defaultUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(followedUser.getId())).thenReturn(Optional.of(followedUser));
        when(userRepository.save(user)).thenReturn(user);

        followerService.addFollowed(user.getEmail(), followedUser.getId());

        assertTrue(user.getFollowed()
                .contains(followedUser));
        assertTrue(followedUser.getFollowers()
                .contains(user));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addFollowed_shouldNotAdd_whenAlreadyFollowing() {
        User user = UserFactory.defaultUser();
        User followedUser = UserFactory.defaultUser();
        user.addFollowed(followedUser); // Already following

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(followedUser.getId())).thenReturn(Optional.of(followedUser));

        assertThrows(NoEffectException.class, () ->
                followerService.addFollowed(user.getEmail(), followedUser.getId())
        );

        assertEquals(1, user.getFollowed()
                .size());
        verify(userRepository, never()).save(user);
    }

    @Test
    void addFollowed_shouldNotAdd_whenTryingToFollowSelf() {
        User user = UserFactory.defaultUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(IllegalActionException.class, () ->
                followerService.addFollowed(user.getEmail(), user.getId())
        );

        assertEquals(0, user.getFollowed()
                .size());
        verify(userRepository, never()).save(user);
    }

    @Test
    void addFollowed_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
                followerService.addFollowed("test@gmail.com", 1L));

        verify(userRepository, never()).save(any());
    }

    @Test
    void addFollowed_shouldThrow_whenFollowedUserDoesNotExist() {
        User user = UserFactory.defaultUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(InvalidIdException.class, () ->
                followerService.addFollowed(user.getEmail(), 999L));

        verify(userRepository, never()).save(any());
    }

    @Test
    void removeFollowed_shouldRemoveFollowed_whenValid() {
        User user = UserFactory.defaultUser();
        User followedUser = UserFactory.defaultUser();
        user.addFollowed(followedUser); // Setup existing relationship

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(followedUser.getId())).thenReturn(Optional.of(followedUser));
        when(userRepository.save(user)).thenReturn(user);

        followerService.removeFollowed(user.getEmail(), followedUser.getId());

        assertFalse(user.getFollowed()
                .contains(followedUser));
        assertFalse(followedUser.getFollowers()
                .contains(user));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void removeFollowed_shouldNotRemove_whenNotFollowing() {
        User user = UserFactory.defaultUser();
        User followedUser = UserFactory.defaultUser();
        // Not following initially

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(followedUser.getId())).thenReturn(Optional.of(followedUser));

        assertThrows(NoEffectException.class, () ->
                followerService.removeFollowed(user.getEmail(), followedUser.getId()));

        assertEquals(0, user.getFollowed()
                .size());
        verify(userRepository, never()).save(user);
    }

    @Test
    void removeFollowed_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
                followerService.removeFollowed("test@gmail.com", 1L));

        verify(userRepository, never()).save(any());
    }

    @Test
    void removeFollowed_shouldThrow_whenFollowedUserDoesNotExist() {
        User user = UserFactory.defaultUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(InvalidIdException.class, () ->
                followerService.removeFollowed(user.getEmail(), 999L));

        verify(userRepository, never()).save(any());
    }
}


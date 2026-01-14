package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import com.joshtechnologygroup.minisocial.exception.InvalidValueException;
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
        List<Long> updateRequest = List.of(user1.getId(), user2.getId());

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findExistingUserIds(updateRequest)).thenReturn(updateRequest);
        when(userRepository.getReferenceById(user1.getId())).thenReturn(user1);
        when(userRepository.getReferenceById(user2.getId())).thenReturn(user2);

        followerService.updateFollowed(updateRequest, user.getEmail());
        assertEquals(2, user.getFollowed().size());
        assertTrue(user.getFollowed().contains(user1));
        assertTrue(user.getFollowed().contains(user2));
        assertTrue(user1.getFollowers().contains(user));
        assertEquals(1, user1.getFollowers()
                .size());
        assertTrue(user2.getFollowers().contains(user));
        assertEquals(1, user2.getFollowers()
                .size());

        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findExistingUserIds(updateRequest);
        verify(userRepository, times(2)).getReferenceById(any());
    }

    @Test
    void updateFollowed_shouldThrow_whenInvalidId() {
        User user = UserFactory.defaultUser();
        User user1 = UserFactory.defaultUser();
        List<Long> updateRequest = List.of(user1.getId(), 999L); // 999L does not exist

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findExistingUserIds(updateRequest)).thenReturn(List.of(user1.getId()));

        assertThrows(InvalidValueException.class, () ->
            followerService.updateFollowed(updateRequest, user.getEmail())
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findExistingUserIds(updateRequest);
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).getReferenceById(any());
    }

    @Test
    void updateFollowed_shouldThrow_whenUserDoesNotExist() {
        List<Long> updateRequest = List.of(1L, 2L);

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
}


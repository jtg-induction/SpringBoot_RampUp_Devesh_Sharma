package com.joshtechnologygroup.minisocial.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserFollow;
import com.joshtechnologygroup.minisocial.dao.UserFollowDao;
import com.joshtechnologygroup.minisocial.dto.follower.UpdateFollowingRequest;
import com.joshtechnologygroup.minisocial.exception.IllegalActionException;
import com.joshtechnologygroup.minisocial.exception.InvalidIdException;
import com.joshtechnologygroup.minisocial.exception.NoEffectException;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.repository.UserFollowRepository;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FollowerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFollowRepository userFollowRepository;

    @Mock
    private UserFollowDao userFollowDao;

    @InjectMocks
    private FollowerService followerService;

    @Test
    void updateFollowed_shouldUpdate_whenValid() {
        User user = UserFactory.defaultUser();
        UpdateFollowingRequest updateRequest = new UpdateFollowingRequest(
            List.of(2L, 3L)
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );
        when(
            userRepository.findExistingUserIds(updateRequest.userIds())
        ).thenReturn(updateRequest.userIds());

        followerService.updateFollowed(updateRequest, user.getEmail());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findExistingUserIds(
            updateRequest.userIds()
        );
        verify(userFollowRepository, times(1)).deleteByFollowerId(user.getId());
        verify(userFollowDao, times(1)).insertBatch(
            user.getId(),
            updateRequest.userIds()
        );
    }

    @Test
    void updateFollowed_shouldThrow_whenInvalidId() {
        User user = UserFactory.defaultUser();
        UpdateFollowingRequest updateRequest = new UpdateFollowingRequest(
            List.of(2L, 999L)
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );
        when(
            userRepository.findExistingUserIds(updateRequest.userIds())
        ).thenReturn(List.of(2L));

        assertThrows(InvalidIdException.class, () ->
            followerService.updateFollowed(updateRequest, user.getEmail())
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findExistingUserIds(
            updateRequest.userIds()
        );
        verify(userFollowRepository, never()).deleteByFollowerId(anyLong());
        verify(userFollowDao, never()).insertBatch(anyLong(), anyList());
    }

    @Test
    void updateFollowed_shouldThrow_whenUserTriesToFollowSelf() {
        User user = UserFactory.defaultUser();
        UpdateFollowingRequest updateRequest = new UpdateFollowingRequest(
            List.of(user.getId(), 2L)
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );

        assertThrows(IllegalActionException.class, () ->
            followerService.updateFollowed(updateRequest, user.getEmail())
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, never()).findExistingUserIds(anyList());
        verify(userFollowRepository, never()).deleteByFollowerId(anyLong());
        verify(userFollowDao, never()).insertBatch(anyLong(), anyList());
    }

    @Test
    void updateFollowed_shouldThrow_whenUserDoesNotExist() {
        UpdateFollowingRequest updateRequest = new UpdateFollowingRequest(
            List.of(1L, 2L)
        );

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
            followerService.updateFollowed(updateRequest, "test@gmail.com")
        );

        verify(userRepository, times(1)).findByEmail(any());
        verify(userRepository, never()).findExistingUserIds(anyList());
    }

    @Test
    void getUserFollowers_shouldReturnFollowers_whenValid() {
        String userEmail = "test@example.com";
        List<Long> expectedFollowers = List.of(1L, 2L, 3L);

        when(userFollowRepository.findFollowerIdsByEmail(userEmail)).thenReturn(
            expectedFollowers
        );

        List<Long> followers = followerService.getUserFollowers(userEmail);

        assertEquals(expectedFollowers, followers);
        verify(userFollowRepository, times(1)).findFollowerIdsByEmail(
            userEmail
        );
    }

    @Test
    void getUsersFollowedBy_shouldReturnFollowedUsers_whenValid() {
        String userEmail = "test@example.com";
        List<Long> expectedFollowed = List.of(4L, 5L, 6L);

        when(userFollowRepository.findFollowedIdsByEmail(userEmail)).thenReturn(
            expectedFollowed
        );

        List<Long> followedUsers = followerService.getUsersFollowedBy(
            userEmail
        );

        assertEquals(expectedFollowed, followedUsers);
        verify(userFollowRepository, times(1)).findFollowedIdsByEmail(
            userEmail
        );
    }

    @Test
    void addFollowed_shouldAddFollowed_whenValid() {
        User user = UserFactory.defaultUser();
        User followedUser = UserFactory.defaultUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );
        when(userRepository.findById(followedUser.getId())).thenReturn(
            Optional.of(followedUser)
        );
        when(
            userFollowRepository.existsByFollowerIdAndFollowedId(
                user.getId(),
                followedUser.getId()
            )
        ).thenReturn(false);

        followerService.addFollowed(user.getEmail(), followedUser.getId());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findById(followedUser.getId());
        verify(userFollowRepository, times(1)).existsByFollowerIdAndFollowedId(
            user.getId(),
            followedUser.getId()
        );
        verify(userFollowRepository, times(1)).save(any(UserFollow.class));
    }

    @Test
    void addFollowed_shouldThrow_whenAlreadyFollowing() {
        User user = UserFactory.defaultUser();
        Long followedId = 2L;

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );
        when(
            userFollowRepository.existsByFollowerIdAndFollowedId(
                user.getId(),
                followedId
            )
        ).thenReturn(true);

        assertThrows(NoEffectException.class, () ->
            followerService.addFollowed(user.getEmail(), followedId)
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userFollowRepository, times(1)).existsByFollowerIdAndFollowedId(
            user.getId(),
            followedId
        );
        verify(userFollowRepository, never()).save(any(UserFollow.class));
    }

    @Test
    void addFollowed_shouldThrow_whenTryingToFollowSelf() {
        User user = UserFactory.defaultUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );

        assertThrows(IllegalActionException.class, () ->
            followerService.addFollowed(user.getEmail(), user.getId())
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userFollowRepository, never()).existsByFollowerIdAndFollowedId(
            anyLong(),
            anyLong()
        );
        verify(userFollowRepository, never()).save(any(UserFollow.class));
    }

    @Test
    void addFollowed_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
            followerService.addFollowed("test@gmail.com", 1L)
        );

        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(userFollowRepository, never()).save(any(UserFollow.class));
    }

    @Test
    void addFollowed_shouldThrow_whenFollowedUserDoesNotExist() {
        User user = UserFactory.defaultUser();
        Long nonExistentUserId = 999L;

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );
        when(
            userFollowRepository.existsByFollowerIdAndFollowedId(
                user.getId(),
                nonExistentUserId
            )
        ).thenReturn(false);
        when(userRepository.findById(nonExistentUserId)).thenReturn(
            Optional.empty()
        );

        assertThrows(InvalidIdException.class, () ->
            followerService.addFollowed(user.getEmail(), nonExistentUserId)
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findById(nonExistentUserId);
        verify(userFollowRepository, never()).save(any(UserFollow.class));
    }

    @Test
    void removeFollowed_shouldRemoveFollowed_whenValid() {
        User user = UserFactory.defaultUser();
        Long followedId = 2L;

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );
        when(
            userFollowRepository.deleteByFollowerIdAndFollowedId(
                user.getId(),
                followedId
            )
        ).thenReturn(1L);

        followerService.removeFollowed(user.getEmail(), followedId);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userFollowRepository, times(1)).deleteByFollowerIdAndFollowedId(
            user.getId(),
            followedId
        );
    }

    @Test
    void removeFollowed_shouldThrow_whenNotFollowing() {
        User user = UserFactory.defaultUser();
        Long followedId = 2L;

        when(userRepository.findByEmail(user.getEmail())).thenReturn(
            Optional.of(user)
        );
        when(
            userFollowRepository.deleteByFollowerIdAndFollowedId(
                user.getId(),
                followedId
            )
        ).thenReturn(0L);

        assertThrows(NoEffectException.class, () ->
            followerService.removeFollowed(user.getEmail(), followedId)
        );

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userFollowRepository, times(1)).deleteByFollowerIdAndFollowedId(
            user.getId(),
            followedId
        );
    }

    @Test
    void removeFollowed_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () ->
            followerService.removeFollowed("test@gmail.com", 1L)
        );

        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(userFollowRepository, never()).deleteByFollowerIdAndFollowedId(
            anyLong(),
            anyLong()
        );
    }
}

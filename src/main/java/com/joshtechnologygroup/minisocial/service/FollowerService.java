package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import com.joshtechnologygroup.minisocial.exception.InvalidValueException;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FollowerService {
    private final UserRepository userRepository;

    public FollowerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void updateFollowed(List<Long> followedIds, String userEmail) {
        // Find user
        log.debug("Updating followed list for user {}: {}", userEmail, followedIds);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserDoesNotExistException::new);
        List<Long> validIds = userRepository.findExistingUserIds(followedIds);
        if (validIds.size() != followedIds.size()) {
            log.warn("Some follower IDs do not exist. Requested: {}, Valid: {}", followedIds, validIds);
            throw new InvalidValueException("One or more of the provided follower IDs do not exist.");
        }

        // Find new followers
        Set<User> currentFollowed = user.getFollowed();
        Set<User> newFollowedReferences = new HashSet<>();

        for (Long id : validIds) {
            User followed = userRepository.getReferenceById(id);
            newFollowedReferences.add(followed);
        }

        // Remove followers that are no longer in the list
        for (User followed : currentFollowed) {
            if (!newFollowedReferences.contains(followed)) {
                user.removeFollowed(followed);
            }
        }

        // Add new followers
        for (User followed : newFollowedReferences) {
            if (!currentFollowed.contains(followed)) {
                user.addFollowed(followed);
            }
        }

        log.info("Updated followed list for user {}: {}", userEmail, validIds);

        userRepository.save(user);
    }

    public List<Long> getUserFollowers(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserDoesNotExistException::new);
        return user.getFollowers().stream()
                .map(User::getId)
                .toList();
    }

    public List<Long> getUsersFollowedBy(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserDoesNotExistException::new);
        return user.getFollowed().stream()
                .map(User::getId)
                .toList();
    }
}

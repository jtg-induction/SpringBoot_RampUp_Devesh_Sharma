package com.joshtechnologygroup.minisocial.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.bean.UserFollow;
import com.joshtechnologygroup.minisocial.dao.UserFollowDao;
import com.joshtechnologygroup.minisocial.dto.follower.UpdateFollowingRequest;
import com.joshtechnologygroup.minisocial.exception.IllegalActionException;
import com.joshtechnologygroup.minisocial.exception.InvalidIdException;
import com.joshtechnologygroup.minisocial.exception.NoEffectException;
import com.joshtechnologygroup.minisocial.exception.UserDoesNotExistException;
import com.joshtechnologygroup.minisocial.repository.UserFollowRepository;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FollowerService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final UserFollowDao userFollowDao;

    public FollowerService(
        UserRepository userRepository,
        UserFollowRepository userFollowRepository,
        UserFollowDao userFollowDao) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
        this.userFollowDao = userFollowDao;
    }

    @Transactional
    public void updateFollowed(UpdateFollowingRequest req, String userEmail) {
        log.info("Updating followed list for user {}", userEmail);

        User user = userRepository
            .findByEmail(userEmail)
            .orElseThrow(UserDoesNotExistException::new);

        List<Long> requestedIds = req.userIds();

        if (requestedIds.contains(user.getId())) {
            throw new IllegalActionException("Users cannot follow themselves");
        }

        List<Long> validIds = userRepository.findExistingUserIds(requestedIds);
        if (validIds.size() != requestedIds.size()) {
            throw new InvalidIdException("One or more user IDs do not exist");
        }

        log.debug(
            "Replacing followed list for user {} → {}",
            userEmail,
            validIds
        );

        // derived bulk delete
        int deleted = userFollowRepository.deleteByFollowerId(user.getId());
        log.debug("Deleted {} existing followed entries", deleted);

        // batch insert
        userFollowDao.insertBatch(user.getId(),validIds);

        log.info("Updated followed list for user {}", userEmail);
    }

    @Transactional
    public void addFollowed(String userEmail, Long followedId) {
        User follower = userRepository
            .findByEmail(userEmail)
            .orElseThrow(UserDoesNotExistException::new);

        if (follower.getId().equals(followedId)) {
            throw new IllegalActionException("Users cannot follow themselves");
        }

        if (
            userFollowRepository.existsByFollowerIdAndFollowedId(
                follower.getId(),
                followedId
            )
        ) {
            throw new NoEffectException();
        }

        User followed = userRepository
            .findById(followedId)
            .orElseThrow(() -> new InvalidIdException("Invalid UserID"));

        userFollowRepository.save(new UserFollow(follower, followed));
    }

    @Transactional
    public void removeFollowed(String userEmail, Long followedId) {
        User follower = userRepository
            .findByEmail(userEmail)
            .orElseThrow(UserDoesNotExistException::new);

        long affected = userFollowRepository.deleteByFollowerIdAndFollowedId(
            follower.getId(),
            followedId
        );

        if (affected == 0) {
            throw new NoEffectException();
        }
    }

    @Transactional
    public List<Long> getUserFollowers(String userEmail) {
        return userFollowRepository.findFollowerIdsByEmail(userEmail);
    }

    @Transactional
    public List<Long> getUsersFollowedBy(String userEmail) {
        return userFollowRepository.findFollowedIdsByEmail(userEmail);
    }
}

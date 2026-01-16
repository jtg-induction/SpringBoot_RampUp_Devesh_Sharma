package com.joshtechnologygroup.minisocial.tool.service;

import com.joshtechnologygroup.minisocial.bean.*;
import com.joshtechnologygroup.minisocial.repository.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import com.joshtechnologygroup.minisocial.tool.bean.UserDetailRow;
import com.joshtechnologygroup.minisocial.tool.bean.UserFollowingDetailRow;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserDetailImportService {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final ResidentialDetailRepository residentialDetailRepository;
    private final OfficialDetailRepository officialDetailRepository;

    public UserDetailImportService(UserRepository userRepository, UserDetailRepository userDetailRepository, ResidentialDetailRepository residentialDetailRepository, OfficialDetailRepository officialDetailRepository) {
        this.userRepository = userRepository;
        this.userDetailRepository = userDetailRepository;
        this.residentialDetailRepository = residentialDetailRepository;
        this.officialDetailRepository = officialDetailRepository;
    }

    @Transactional
    public void importUserDetails(List<UserDetailRow> userDetailRows) {
        // Prepare batch lists
        List<User> users = new ArrayList<>();
        List<UserDetail> userDetails = new ArrayList<>();
        List<ResidentialDetail> residentialDetails = new ArrayList<>();
        List<OfficialDetail> officialDetails = new ArrayList<>();

        // Build entities for batch processing
        for (UserDetailRow row : userDetailRows) {
            User user = new User();
            user.setEmail(row.getEmailId());
            user.setPassword(row.getPassword());
            user.setActive(true);

            UserDetail userDetail = new UserDetail();
            userDetail.setFirstName(row.getFirstName());
            userDetail.setLastName(row.getLastName());
            userDetail.setGender(row.getGender());
            userDetail.setAge(row.getAge());
            userDetail.setMaritalStatus(row.getMaritalStatus());
            userDetail.setUser(user);

            ResidentialDetail residentialDetail = new ResidentialDetail();
            residentialDetail.setCity(row.getResidentialCity());
            residentialDetail.setState(row.getResidentialState());
            residentialDetail.setCountry(row.getResidentialCountry());
            residentialDetail.setAddress(row.getResidentialAddress());
            residentialDetail.setContactNo1(row.getPrimaryContactNumber());
            residentialDetail.setContactNo2(row.getSecondaryContactNumber());
            residentialDetail.setUser(user);

            OfficialDetail officialDetail = new OfficialDetail();
            officialDetail.setAddress(row.getOfficeAddress());
            officialDetail.setCity(row.getOfficeCity());
            officialDetail.setState(row.getOfficeState());
            officialDetail.setCountry(row.getOfficeCountry());
            officialDetail.setEmployeeCode(row.getEmployeeCode());
            officialDetail.setCompanyName(row.getCompanyName());
            officialDetail.setCompanyContactEmail(row.getCompanyContactEmail());
            officialDetail.setCompanyContactNo(row.getCompanyContactNumber());
            officialDetail.setUser(user);

            users.add(user);
            userDetails.add(userDetail);
            residentialDetails.add(residentialDetail);
            officialDetails.add(officialDetail);
        }

        // Perform batch saves
        log.debug("Saving {} users in batch", users.size());
        userRepository.saveAll(users);

        log.debug("Saving {} user details in batch", userDetails.size());
        userDetailRepository.saveAll(userDetails);

        log.debug("Saving {} residential details in batch", residentialDetails.size());
        residentialDetailRepository.saveAll(residentialDetails);

        log.debug("Saving {} official details in batch", officialDetails.size());
        officialDetailRepository.saveAll(officialDetails);

        log.debug("Batch import completed for {} user records", users.size());
    }

    @Transactional
    public void importFollowingDetails(List<UserFollowingDetailRow> rows) {
        // Import User Following Details with batch processing
        List<User> usersToUpdate = new ArrayList<>();

        for (UserFollowingDetailRow row : rows) {
            User user = userRepository.findByEmail(row.getUserEmail())
                    .orElse(null);
            if (user == null) {
                log.warn("User with email {} not found. Skipping following details.", row.getUserEmail());
                continue;
            }

            boolean userModified = false;
            for (String followedEmail : row.getFollowing()) {
                User followedUser = userRepository.findByEmail(followedEmail)
                        .orElse(null);
                if (followedUser == null) {
                    log.warn("Followed user with email {} not found. Skipping.", followedEmail);
                    continue;
                }
                user.addFollowed(followedUser);
                userModified = true;
            }

            if (userModified) {
                usersToUpdate.add(user);
            }
        }

        // Perform batch save of all modified users
        if (!usersToUpdate.isEmpty()) {
            log.debug("Saving {} users with following relationships in batch", usersToUpdate.size());
            userRepository.saveAll(usersToUpdate);
            log.debug("Batch save completed for following relationships");
        }
    }

    @Transactional
    public void clearAllData() {
        officialDetailRepository.deleteAllInBatch();
        residentialDetailRepository.deleteAllInBatch();
        userDetailRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        log.info("Cleared all data from the database.");
    }
}

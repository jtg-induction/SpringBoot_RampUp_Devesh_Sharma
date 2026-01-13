package com.joshtechnologygroup.minisocial.tool.service;

import com.joshtechnologygroup.minisocial.bean.*;
import com.joshtechnologygroup.minisocial.dao.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.tool.bean.UserDetailRow;
import com.joshtechnologygroup.minisocial.tool.bean.UserFollowingDetailRow;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        // Import User Details
        for (UserDetailRow row : userDetailRows) {
            OfficialDetail officialDetail = new OfficialDetail();
            officialDetail.setAddress(row.getOfficeAddress());
            officialDetail.setCity(row.getOfficeCity());
            officialDetail.setState(row.getOfficeState());
            officialDetail.setCountry(row.getOfficeCountry());
            officialDetail.setEmployeeCode(row.getEmployeeCode());
            officialDetail.setCompanyName(row.getCompanyName());
            officialDetail.setCompanyContactEmail(row.getCompanyContactEmail());
            officialDetail.setCompanyContactNo(row.getCompanyContactNumber());

            ResidentialDetail residentialDetail = new ResidentialDetail();
            residentialDetail.setCity(row.getResidentialCity());
            residentialDetail.setState(row.getResidentialState());
            residentialDetail.setCountry(row.getResidentialCountry());
            residentialDetail.setAddress(row.getResidentialAddress());
            residentialDetail.setContactNo1(row.getPrimaryContactNumber());
            residentialDetail.setContactNo2(row.getSecondaryContactNumber());

            UserDetail userDetail = new UserDetail();
            userDetail.setFirstName(row.getFirstName());
            userDetail.setLastName(row.getLastName());
            userDetail.setGender(row.getGender());
            userDetail.setAge(row.getAge());
            userDetail.setMaritalStatus(row.getMaritalStatus());

            User user = new User();
            user.setEmail(row.getEmailId());
            user.setPassword(row.getPassword());
            user.setActive(true);
            userDetail.setUser(user);
            officialDetail.setUser(user);
            residentialDetail.setUser(user);

            userRepository.save(user);
            userDetailRepository.save(userDetail);
            residentialDetailRepository.save(residentialDetail);
            officialDetailRepository.save(officialDetail);
        }
    }

    @Transactional
    public void importFollowingDetails(List<UserFollowingDetailRow> rows) {
        // Import User Following Details
        for (UserFollowingDetailRow row : rows) {
            User user = userRepository.findByEmail(row.getUserEmail())
                    .orElse(null);
            if (user == null) {
                log.warn("User with email {} not found. Skipping following details.", row.getUserEmail());
                continue;
            }

            for (String followedEmail : row.getFollowing()) {
                User followedUser = userRepository.findByEmail(followedEmail)
                        .orElse(null);
                if (followedUser == null) {
                    log.warn("Followed user with email {} not found. Skipping.", followedEmail);
                    continue;
                }
                user.addFollowed(followedUser);
            }
            userRepository.save(user);
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

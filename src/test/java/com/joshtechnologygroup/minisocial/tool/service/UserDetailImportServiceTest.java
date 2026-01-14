package com.joshtechnologygroup.minisocial.tool.service;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dao.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserDetailRepository;
import com.joshtechnologygroup.minisocial.dao.UserRepository;
import com.joshtechnologygroup.minisocial.factory.UserDetailRowFactory;
import com.joshtechnologygroup.minisocial.factory.UserFactory;
import com.joshtechnologygroup.minisocial.factory.UserFollowingDetailRowFactory;
import com.joshtechnologygroup.minisocial.tool.bean.UserDetailRow;
import com.joshtechnologygroup.minisocial.tool.bean.UserFollowingDetailRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailImportServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserDetailRepository userDetailRepository;

    @Mock
    ResidentialDetailRepository residentialDetailRepository;

    @Mock
    OfficialDetailRepository officialDetailRepository;

    @InjectMocks
    UserDetailImportService userDetailImportService;

    @Test
    void importUserDetails_shouldImport_whenValid() {
        List<UserDetailRow> userDetailList = List.of(
                UserDetailRowFactory.defaultUserDetailRow(),
                UserDetailRowFactory.defaultUserDetailRow(),
                UserDetailRowFactory.defaultUserDetailRow(),
                UserDetailRowFactory.defaultUserDetailRow()
        );

        when(userRepository.saveAll(any())).thenReturn(List.of());
        when(userDetailRepository.saveAll(any())).thenReturn(List.of());
        when(residentialDetailRepository.saveAll(any())).thenReturn(List.of());
        when(officialDetailRepository.saveAll(any())).thenReturn(List.of());

        userDetailImportService.importUserDetails(userDetailList);

        verify(userRepository, times(1)).saveAll(any());
        verify(userDetailRepository, times(1)).saveAll(any());
        verify(residentialDetailRepository, times(1)).saveAll(any());
        verify(officialDetailRepository, times(1)).saveAll(any());
    }

    @Test
    void importFollowingDetails_shouldImport_whenValid() {
        User user1 = UserFactory.defaultUser();
        User user2 = UserFactory.defaultUser();
        User user3 = UserFactory.defaultUser();
        List<UserFollowingDetailRow> userFollowingDetailRowList = List.of(
                UserFollowingDetailRowFactory.userFollowingDetailRowWithEmails(user1.getEmail(), List.of(user2.getEmail(), user3.getEmail())),
                UserFollowingDetailRowFactory.userFollowingDetailRowWithEmails(user2.getEmail(), List.of(user3.getEmail()))
        );

        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(userRepository.findByEmail(user3.getEmail())).thenReturn(Optional.of(user3));
        when(userRepository.saveAll(any())).thenReturn(List.of());

        userDetailImportService.importFollowingDetails(userFollowingDetailRowList);

        verify(userRepository, times(1)).findByEmail(user1.getEmail());
        verify(userRepository, times(2)).findByEmail(user2.getEmail());
        verify(userRepository, times(2)).findByEmail(user3.getEmail());
        verify(userRepository, times(1)).saveAll(any());
    }
}

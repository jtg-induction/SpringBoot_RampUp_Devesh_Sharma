package com.joshtechnologygroup.minisocial.tool;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.repository.OfficialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.ResidentialDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserDetailRepository;
import com.joshtechnologygroup.minisocial.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserDetailImporterToolTest {
    @Autowired
    private UserDetailImporterTool userDetailImporterTool;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailRepository userDetailRepository;
    @Autowired
    private ResidentialDetailRepository residentialDetailRepository;
    @Autowired
    private OfficialDetailRepository officialDetailRepository;

    @Test
    void userDetailImporter_shouldImportData_whenValid() {
        String userDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("user-details.csv"))
                .getPath();
        String followingDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("following-details.csv"))
                .getPath();

        try {
            userDetailImporterTool.importUserDetails(userDetailsCsv, followingDetailsCsv, "50");
            assertEquals(10, userRepository.findAll()
                    .size());
            assertEquals(10, userDetailRepository.findAll()
                    .size());
            assertEquals(10, residentialDetailRepository.findAll()
                    .size());
            assertEquals(10, officialDetailRepository.findAll()
                    .size());
            Optional<User> arjun = userRepository.findByEmail("arjun.nair@zenithcorp.in");
            assertTrue(arjun.isPresent());
            assertEquals(4, arjun.get()
                    .getFollowed()
                    .size());
        } catch (Exception e) {
            // Fail the test if any exception occurs
            fail("Importing valid data should not throw an exception: " + e.getMessage());
        }
    }

    @Test
    void userDetailImporter_shouldHandleFileNotFound_whenUserDetailsFileDoesNotExist() {
        String nonExistentUserDetailsCsv = "non-existent-user-details.csv";
        String followingDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("following-details.csv"))
                .getPath();

        // This should not throw an exception but should handle the error gracefully
        assertDoesNotThrow(() -> {
            userDetailImporterTool.importUserDetails(nonExistentUserDetailsCsv, followingDetailsCsv, "50");
        });

        // Verify no data was imported
        assertEquals(0, userRepository.findAll()
                .size());
        assertEquals(0, userDetailRepository.findAll()
                .size());
        assertEquals(0, residentialDetailRepository.findAll()
                .size());
        assertEquals(0, officialDetailRepository.findAll()
                .size());
    }

    @Test
    void userDetailImporter_shouldHandleFileNotFound_whenFollowingDetailsFileDoesNotExist() {
        String userDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("user-details.csv"))
                .getPath();
        String nonExistentFollowingDetailsCsv = "non-existent-following-details.csv";

        // This should not throw an exception but should handle the error gracefully
        assertDoesNotThrow(() -> {
            userDetailImporterTool.importUserDetails(userDetailsCsv, nonExistentFollowingDetailsCsv, "50");
        });

        // Verify no data was imported
        assertEquals(0, userRepository.findAll()
                .size());
        assertEquals(0, userDetailRepository.findAll()
                .size());
        assertEquals(0, residentialDetailRepository.findAll()
                .size());
        assertEquals(0, officialDetailRepository.findAll()
                .size());
    }

    @Test
    void userDetailImporter_shouldHandleEmptyFile_whenUserDetailsFileIsEmpty() {
        String emptyUserDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("user-details-empty.csv"))
                .getPath();
        String followingDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("following-details.csv"))
                .getPath();

        // This should not throw an exception but should handle the error gracefully
        assertDoesNotThrow(() -> {
            userDetailImporterTool.importUserDetails(emptyUserDetailsCsv, followingDetailsCsv, "50");
        });

        // Verify no data was imported
        assertEquals(0, userRepository.findAll()
                .size());
        assertEquals(0, userDetailRepository.findAll()
                .size());
        assertEquals(0, residentialDetailRepository.findAll()
                .size());
        assertEquals(0, officialDetailRepository.findAll()
                .size());
    }

    @Test
    void userDetailImporter_shouldHandleInvalidData_whenCsvContainsInvalidValues() {
        String invalidUserDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("user-details-invalid.csv"))
                .getPath();
        String followingDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("following-details.csv"))
                .getPath();

        // This should not throw an exception but should handle validation errors gracefully
        assertDoesNotThrow(() -> {
            userDetailImporterTool.importUserDetails(invalidUserDetailsCsv, followingDetailsCsv, "50");
        });

        // Verify no data was imported due to validation failures
        assertEquals(0, userRepository.findAll()
                .size());
        assertEquals(0, userDetailRepository.findAll()
                .size());
        assertEquals(0, residentialDetailRepository.findAll()
                .size());
        assertEquals(0, officialDetailRepository.findAll()
                .size());
    }

    @Test
    void userDetailImporter_shouldHandleMalformedCsv_whenCsvHasMissingColumns() {
        String malformedUserDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("user-details-bad.csv"))
                .getPath();
        String followingDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("following-details.csv"))
                .getPath();

        // This should not throw an exception but should handle parsing errors gracefully
        assertDoesNotThrow(() -> {
            userDetailImporterTool.importUserDetails(malformedUserDetailsCsv, followingDetailsCsv, "50");
        });

        // Verify no data was imported due to parsing/validation failures
        assertEquals(0, userRepository.findAll()
                .size());
        assertEquals(0, userDetailRepository.findAll()
                .size());
        assertEquals(0, residentialDetailRepository.findAll()
                .size());
        assertEquals(0, officialDetailRepository.findAll()
                .size());
    }

    @Test
    void userDetailImporter_shouldHandleInvalidBatchSize_whenBatchSizeIsNotNumeric() {
        String userDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("user-details.csv"))
                .getPath();
        String followingDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("following-details.csv"))
                .getPath();

        assertDoesNotThrow(() -> {
            userDetailImporterTool.importUserDetails(userDetailsCsv, followingDetailsCsv, "invalid-batch-size");
        });

        // Verify data was imported successfully
        assertEquals(10, userRepository.findAll()
                .size());
        assertEquals(10, userDetailRepository.findAll()
                .size());
        assertEquals(10, residentialDetailRepository.findAll()
                .size());
        assertEquals(10, officialDetailRepository.findAll()
                .size());
    }

    @Test
    void userDetailImporter_shouldHandleZeroBatchSize_whenBatchSizeIsZero() {
        String userDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("user-details.csv"))
                .getPath();
        String followingDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("following-details.csv"))
                .getPath();

        // Should work with zero batch size (though it may cause issues, the method should handle it)
        assertDoesNotThrow(() -> {
            userDetailImporterTool.importUserDetails(userDetailsCsv, followingDetailsCsv, "0");
        });
    }

    @Test
    void userDetailImporter_shouldHandleNegativeBatchSize_whenBatchSizeIsNegative() {
        String userDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("user-details.csv"))
                .getPath();
        String followingDetailsCsv = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("following-details.csv"))
                .getPath();

        // Should work with negative batch size (though it may cause issues, the method should handle it)
        assertDoesNotThrow(() -> {
            userDetailImporterTool.importUserDetails(userDetailsCsv, followingDetailsCsv, "-10");
        });
    }
}

package com.joshtechnologygroup.minisocial.tool.util;

import com.joshtechnologygroup.minisocial.tool.bean.UserDetailRow;
import com.joshtechnologygroup.minisocial.tool.bean.UserFollowingDetailRow;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class CsvParser {

    public static List<UserDetailRow> parseUserDetailsCsv(String csvFilePath) {
        log.debug("Importing data from user details CSV: {}", csvFilePath);

        File userDetailsFile = new File(csvFilePath);
        if (isInvalidFile(userDetailsFile)) return null;

        List<UserDetailRow> rows;
        List<CsvException> capturedExceptions = new ArrayList<>();

        try (Reader reader = new FileReader(csvFilePath)) {
            rows = new CsvToBeanBuilder<UserDetailRow>(reader)
                .withType(UserDetailRow.class)
                .withExceptionHandler(e -> {
                    capturedExceptions.add(e);
                    return null;
                })
                .build()
                .parse();
        } catch (IOException e) {
            log.error(
                "Error reading user details CSV file: {}",
                e.getMessage()
            );
            return null;
        }
        if (!capturedExceptions.isEmpty()) {
            log.error(
                "Found {} parsing errors in CSV file:",
                capturedExceptions.size()
            );
            for (CsvException exception : capturedExceptions) {
                log.error(
                    "CSV parsing error at line {}: {}",
                    exception.getLineNumber(),
                    exception.getMessage()
                );
            }
            return null; // Or decide how to handle partial success
        }

        Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

        for (UserDetailRow row : rows) {
            Set<ConstraintViolation<UserDetailRow>> violations =
                validator.validate(row);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<
                    UserDetailRow
                > violation : violations) {
                    log.error(
                        "Validation error in row with email {}: {}",
                        row.getEmailId(),
                        violation.getMessage()
                    );
                }
                return null;
            }
        }

        log.info("Successfully validated {} user detail rows.", rows.size());

        return rows;
    }

    public static List<UserFollowingDetailRow> parseFollowingDetailsCsv(
        String csvFilePath
    ) throws IOException {
        log.debug("Importing data from following details CSV: {}", csvFilePath);

        File followingDetailsFile = new File(csvFilePath);
        if (isInvalidFile(followingDetailsFile)) return null;

        Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();
        List<UserFollowingDetailRow> followingDetails = new ArrayList<>();

        try (
            Reader reader = Files.newBufferedReader(
                followingDetailsFile.toPath()
            )
        ) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] row;
                try {
                    csvReader.readNext(); // Skip header
                    while ((row = csvReader.readNext()) != null) {
                        // Process each row
                        UserFollowingDetailRow rowBean =
                            new UserFollowingDetailRow();
                        rowBean.setUserEmail(row[0]);
                        List<String> followingList = new ArrayList<>(
                            Arrays.asList(row[1].split(" "))
                        );
                        rowBean.setFollowing(followingList);

                        Set<
                            ConstraintViolation<UserFollowingDetailRow>
                        > violations = validator.validate(rowBean);
                        if (!violations.isEmpty()) {
                            for (ConstraintViolation<
                                UserFollowingDetailRow
                            > violation : violations) {
                                log.error(
                                    "Validation error in row with user email {}: {}",
                                    rowBean.getUserEmail(),
                                    violation.getMessage()
                                );
                            }
                            return null;
                        }
                        followingDetails.add(rowBean);
                    }
                } catch (CsvValidationException e) {
                    log.error(
                        "Error reading following details CSV file: {}",
                        e.getMessage()
                    );
                    return null;
                }
            }
        }

        log.info(
            "Successfully validated {} following detail rows.",
            followingDetails.size()
        );

        return followingDetails;
    }

    private static boolean isInvalidFile(File file) {
        if (!file.exists()) {
            log.error("User details CSV file not found: {}", file);
            return true;
        }

        if (file.length() == 0) {
            log.error("User details CSV file is empty: {}", file);
            return true;
        }

        return false;
    }
}

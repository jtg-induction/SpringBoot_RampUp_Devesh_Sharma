package com.joshtechnologygroup.minisocial.tool.util;

import com.joshtechnologygroup.minisocial.tool.bean.UserDetailRow;
import com.joshtechnologygroup.minisocial.tool.bean.UserFollowingDetailRow;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class CsvParser {
    public static List<UserDetailRow> parseUserDetailsCsv(String csvFilePath) throws FileNotFoundException {
        log.debug("Importing data from user details CSV: {}", csvFilePath);

        File userDetailsFile = new File(csvFilePath);
        if(!validateFile(userDetailsFile)) return null;

        List<UserDetailRow> rows = new CsvToBeanBuilder<UserDetailRow>(new FileReader(csvFilePath))
                .withType(UserDetailRow.class)
                .build()
                .parse();

        Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();

        for (UserDetailRow row : rows) {
            Set<ConstraintViolation<UserDetailRow>> violations = validator.validate(row);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<UserDetailRow> violation : violations) {
                    log.error("Validation error in row with email {}: {}", row.getEmailId(), violation.getMessage());
                }
                return null;
            }
        }

        log.info("Successfully validated {} user detail rows.", rows.size());

        return rows;
    }

    public static List<UserFollowingDetailRow> parseFollowingrDetailsCsv(String csvFilePath) throws IOException {
        log.debug("Importing data from following details CSV: {}", csvFilePath);

        File followingDetailsFile = new File(csvFilePath);
        if (!followingDetailsFile.exists()) {
            log.error("Following details CSV file not found: {}", csvFilePath);
            return null;
        }

        if (followingDetailsFile.length() == 0) {
            log.error("Following details CSV file is empty: {}", csvFilePath);
            return null;
        }

        Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
        List<UserFollowingDetailRow> followingDetails = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(followingDetailsFile.toPath())) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] row = null;
                try {
                    csvReader.readNext(); // Skip header
                    while ((row = csvReader.readNext()) != null) {
                        // Process each row
                        UserFollowingDetailRow rowBean = new UserFollowingDetailRow();
                        rowBean.setUserEmail(row[0]);
                        List<String> followingList = new ArrayList<>(Arrays.asList(row[1].split(" ")));
                        rowBean.setFollowing(followingList);

                        Set<ConstraintViolation<UserFollowingDetailRow>> violations = validator.validate(rowBean);
                        if (!violations.isEmpty()) {
                            for (ConstraintViolation<UserFollowingDetailRow> violation : violations) {
                                log.error("Validation error in row with user email {}: {}", rowBean.getUserEmail(), violation.getMessage());
                            }
                            return null;
                        }
                        followingDetails.add(rowBean);
                    }
                } catch (CsvValidationException e) {
                    log.error("Error reading following details CSV file: {}", e.getMessage());
                    return null;
                }
            }
        }

        log.info("Successfully validated {} following detail rows.", followingDetails.size());

        return followingDetails;
    }

    private static boolean validateFile(File file) {
        if (!file.exists()) {
            log.error("User details CSV file not found: {}", file);
            return false;
        }

        if (file.length() == 0) {
            log.error("User details CSV file is empty: {}", file);
            return false;
        }

        return true;
    }
}

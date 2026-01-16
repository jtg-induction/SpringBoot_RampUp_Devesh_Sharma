package com.joshtechnologygroup.minisocial.tool;

import com.joshtechnologygroup.minisocial.tool.bean.UserDetailRow;
import com.joshtechnologygroup.minisocial.tool.bean.UserFollowingDetailRow;
import com.joshtechnologygroup.minisocial.tool.service.UserDetailImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.EnableCommand;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.joshtechnologygroup.minisocial.tool.util.CsvParser.parseFollowingrDetailsCsv;
import static com.joshtechnologygroup.minisocial.tool.util.CsvParser.parseUserDetailsCsv;

@EnableCommand(UserDetailImporterTool.class)
@Slf4j
@Component
public class UserDetailImporterTool {
    private final UserDetailImportService userDetailImportService;

    public UserDetailImporterTool(UserDetailImportService userDetailImportService) {
        this.userDetailImportService = userDetailImportService;
    }

    @Command(name = "import-user-details", description = "Imports user details from an external CSV", group = "Data Import", help = "Use this command to import user details from a specified CSV file into the system.")
    public void importUserDetails(
            @Option(
                    longName = "user-details",
                    shortName = 'u',
                    required = true,
                    description = "Input CSV file"
            ) String userDetailsCsv,
            @Option(
                    longName = "following-details",
                    shortName = 'f',
                    required = true,
                    description = "Following Detail CSV filename"
            ) String followingDetailsCsv,
            @Option(
                    longName = "batch-size",
                    shortName = 'b',
                    defaultValue = "50",
                    description = "Batch size for database inserts"
            ) String batchSizeStr) throws FileNotFoundException {
        try {

            int batchSize = 50;
            try {
                batchSize = Integer.parseInt(batchSizeStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid batch size provided ({}). Using default batch size of 50.", batchSizeStr);
            }
            List<UserDetailRow> userDetailRows = parseUserDetailsCsv(userDetailsCsv);
            if (userDetailRows == null) {
                log.error("Failed to import user details");
                return;
            }

            List<UserFollowingDetailRow> followingDetailRows = null;
            try {
                followingDetailRows = parseFollowingrDetailsCsv(followingDetailsCsv);
            } catch (IOException e) {
                log.error("IOException occurred while importing following details: {}", e.getMessage());
                return;
            }
            if (followingDetailRows == null) {
                log.error("Failed to import following details");
                return;
            }

            log.info("Parsed {} user details and {} following details successfully.", userDetailRows.size(), followingDetailRows.size());

            // Import to database
            // Clear existing data
            userDetailImportService.clearAllData();

            // Import User Details
            int batches = userDetailRows.size() / batchSize + (userDetailRows.size() % batchSize == 0 ? 0 : 1);
            for (int i = 0; i < batches; i++) {
                int batchStart = i * batchSize;
                int batchEnd = Math.min(batchStart + batchSize, userDetailRows.size());
                userDetailImportService.importUserDetails(userDetailRows.subList(batchStart, batchEnd));
            }
            log.info("User details import completed.");

            // Import Following Details
            batches = followingDetailRows.size() / batchSize + (followingDetailRows.size() % batchSize == 0 ? 0 : 1);
            for (int i = 0; i < batches; i++) {
                int batchStart = i * batchSize;
                int batchEnd = Math.min(batchStart + batchSize, userDetailRows.size());
                userDetailImportService.importFollowingDetails(followingDetailRows);
            }
            log.info("Following details import completed.");
            log.info("User detail import process finished successfully.");
            log.info("{} Users imported successfully.", userDetailRows.size());
        } catch (Exception e) {
            log.error("An unexpected error occurred during import: {}", e.getMessage());
        }
    }
}

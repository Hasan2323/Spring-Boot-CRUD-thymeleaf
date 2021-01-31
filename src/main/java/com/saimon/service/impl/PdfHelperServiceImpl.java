package com.saimon.service.impl;

import com.saimon.service.PdfHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Author Muhammad Saimon
 * @since 25/01/2021 15:22
 */

@Service
@Slf4j
public class PdfHelperServiceImpl implements PdfHelperService {

    public static final DateTimeFormatter FORMATTED_DATE_yyyy = DateTimeFormatter.ofPattern("yyyy");
    public static final DateTimeFormatter FORMATTED_DATE_MM = DateTimeFormatter.ofPattern("MM");
    public static final DateTimeFormatter FORMATTED_DATE_dd = DateTimeFormatter.ofPattern("dd");

    @Override
    public String getFileLocation() {
        String path = "/opt/vanguard/PRA/report-output/" + getDateWiseSubDir();
        String validatedPath = validatePath(path);
        if (createDirIfNotExist(validatedPath)) {
            return path;
        } else {
            log.error("Failed to get ConfigFileLocationEntity by KeyName");
            throw new RuntimeException("Local directory configuration not found");
        }
    }

    public static String getDateWiseSubDir() {
        LocalDate now = LocalDate.now();
        String YEAR = FORMATTED_DATE_yyyy.format(now);
        String MONTH = FORMATTED_DATE_MM.format(now);
        String DAY = FORMATTED_DATE_dd.format(now);
        return YEAR + "-" + MONTH + "-" + DAY + "/";
    }

    private String validatePath(String path) {
        path = fixDirPathIfNotValid(path);
        return path;
    }

    private String fixDirPathIfNotValid(String directory) {
        directory = directory.trim();

        if (!directory.startsWith("/")) {
            directory = "/" + directory;
        }

        if (!directory.endsWith("/")) {
            directory = directory + "/";
        }

        log.info("Validated Path : " + directory);

        return directory;
    }

    public boolean createDirIfNotExist(String path) {
        if (!isDirExist(path)) {
            return createDir(path);
        }
        return true;
    }

    private boolean isDirExist(String path) {
        File directory = new File(path);
        boolean isDirectoryExist = directory.exists();
        if (!isDirectoryExist) {
            log.info("Directory {} NOT Exists", path);
        }
        return isDirectoryExist;
    }

    private boolean createDir(String path) {
        File directory = new File(path);
        boolean mkdirs = directory.mkdirs();
        if (!mkdirs) {
            log.error("Created Path failed for : " + path);
            throw new RuntimeException("Can't create " + path + ", Permission denied");
        }
        log.info("Created Path : " + path);
        return mkdirs;
    }

}

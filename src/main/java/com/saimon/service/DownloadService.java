package com.saimon.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @Author Muhammad Saimon
 * @since 28/01/2021 12:54
 */

public interface DownloadService {
    ResponseEntity<Resource> downloadFile(String directory, String fileName) throws IOException;

    ResponseEntity<Resource> downloadFile(@NotNull String downloadUrl) throws IOException;

}

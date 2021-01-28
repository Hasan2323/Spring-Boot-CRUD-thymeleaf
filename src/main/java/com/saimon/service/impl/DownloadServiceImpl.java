package com.saimon.service.impl;

import com.saimon.service.DownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since Jan 28/01/2021 12:55
 */

@Slf4j
@Service
public class DownloadServiceImpl implements DownloadService {

    @Override
    public ResponseEntity<Resource> downloadFile(String directory, String fileName) throws IOException {
        log.info("downloading file with directory {} fileName {}", directory, fileName);
        File file = new File(directory + fileName);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(@NotNull String downloadUrl) throws IOException {
        log.info("downloading file with downloadUrl {}", downloadUrl);
        byte[] decodedBytes = Base64.getUrlDecoder().decode(downloadUrl);
        String decodedUrl = new String(decodedBytes);

        Map<String, String> map = getQueryMap(decodedUrl);

        File file = new File(map.get("outputDirectory") + map.get("fileName"));
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + map.get("fileName"));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

}

package com.example.OEP.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin
public class UploadController {

    private final String UPLOAD_DIR = "uploads/questions/";

    @PostMapping("/upload")
    public Map<String, String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        String filePath = UPLOAD_DIR + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        return Map.of("path", filePath);
    }
}

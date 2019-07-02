/**
 @author Vivan Singhal
 */

package com.health.imaging.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ImagingService {
    public static final String PATH = "photos";

    public String transformImage(byte[] imageBytes) {
        String imageTransformed = "image transformed";
        return imageTransformed;
    }

    public String saveImage(MultipartFile fileName) throws IOException {
        String imageDir = System.getProperty("user.dir") + File.separator + PATH + File.separator;
         if (!Files.exists(Paths.get(imageDir))) {
            Files.createDirectory(Paths.get(imageDir));
        }
        Path path = Paths.get(imageDir + File.separator + fileName.getOriginalFilename());
        System.out.println(path);
        Files.write(path, fileName.getBytes());
        return path.toString();
    }
}

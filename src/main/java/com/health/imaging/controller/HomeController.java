
/**
 @author Vivan Singhal
 */

package com.health.imaging.controller;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.CropHintsAnnotation;
import com.google.protobuf.ByteString;
import com.health.imaging.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Controller
public class HomeController {
    @Autowired
    private ImagingService service;
    @Autowired
    private DetectCropHints cropHints;
    @Autowired
    private DetectImageLabels imageLabels;
    @Autowired
    private DetectImageText imageText;
    @Autowired
    private ImageAnnotator imageAnnotator;

    @GetMapping(value = "/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/saveImage")
    public String uploadImage(@RequestParam("fileName") MultipartFile imageFile, Model model) throws IOException {
        String returnValue = "showImage";
        String path = service.saveImage(imageFile);
        model.addAttribute("filePath", path);
        return returnValue;
    }
    @PostMapping("/cropHints")
    public String getCropHints(@RequestParam("fileName") MultipartFile imageFile, Model model) throws Exception {
        String returnValue = "showCropHints";
        ByteString bytes = ByteString.copyFrom(imageFile.getBytes());
        CropHintsAnnotation annotation = cropHints.cropHints(bytes);
        model.addAttribute("cropHints", annotation);
        return returnValue;
    }
    @PostMapping("/imageLabels")
    public String getImageLabels(@RequestParam("fileName") MultipartFile imageFile, Model model) throws Exception {
        String returnValue = "showLabels";
        ByteString bytes = ByteString.copyFrom(imageFile.getBytes());
        List<AnnotateImageResponse> annotation = imageLabels.detectLabels(bytes);
        model.addAttribute("imageLabels", annotation);
        return returnValue;
    }
    @PostMapping("/imageText")
    public String getImageText(@RequestParam("fileName") MultipartFile imageFile, Model model) throws Exception {
        String returnValue = "showText";
        ByteString bytes = ByteString.copyFrom(imageFile.getBytes());
        List<AnnotateImageResponse> annotation = imageText.detectText(bytes);
        model.addAttribute("imageText", annotation);
        return returnValue;
    }
    @PostMapping("/imageAnnotations")
    public String getImageAnnotations(@RequestParam("fileName") MultipartFile imageFile, Model model) throws Exception {
        String returnValue = "showImageAnnotations";
        ByteString bytes = ByteString.copyFrom(imageFile.getBytes());
        List<AnnotateImageResponse> annotation = imageAnnotator.imageAnnotation(bytes);
        model.addAttribute("imageAnnotations", annotation);
        return returnValue;
    }
}




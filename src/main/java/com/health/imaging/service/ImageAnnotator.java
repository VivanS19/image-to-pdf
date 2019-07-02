package com.health.imaging.service;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageAnnotator {
        // Instantiates a client
    public List<AnnotateImageResponse> imageAnnotation(ByteString imgBytes) {
        ImageAnnotatorClient vision = null;
        try {
            vision = ImageAnnotatorClient.create();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

//        // The path to the image file to annotate
//        String fileName = file;
//
//        // Reads the image file into memory
//        Path path = Paths.get(fileName);
//        byte[] data = new byte[0];
//        try {
//            data = Files.readAllBytes(path);
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        ByteString imgBytes = ByteString.copyFrom(data);

        // Builds the image annotation request
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);
        List<AnnotateImageResponse> responses;
        // Performs label detection on the image file
        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
        responses = response.getResponsesList();

        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.printf("Error: %s\n", res.getError().getMessage());
                return responses;
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                annotation.getAllFields().forEach((k, v) ->
                        System.out.printf("%s : %s\n", k, v.toString()));
            }
        }

        return responses;
    }

}

/**
 @author Vivan Singhal
 */

package com.health.imaging.service;

import com.google.cloud.vision.v1.AnnotateImageRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectImageText {
    public List<AnnotateImageResponse> detectText(ByteString imgBytes) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

//        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);
        List<AnnotateImageResponse> responses;
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return responses;
                }

                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    System.out.printf("Text: %s\n", annotation.getDescription());
                    System.out.printf("Position : %s\n", annotation.getBoundingPoly());
                }
            }
        }
        return responses;
    }
}

/**
 @author Vivan Singhal
 */

package com.health.imaging.service;

import com.google.cloud.vision.v1.AnnotateImageRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectCropHints {
    public CropHintsAnnotation cropHints(ByteString imgBytes) throws Exception,
            IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
//        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.CROP_HINTS).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);
        CropHintsAnnotation annotation = null;
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.println("Error: %s\n" + "," + res.getError().getMessage());
                    return null;
                }

                annotation = res.getCropHintsAnnotation();
                for (CropHint hint : annotation.getCropHintsList()) {
                    System.out.println(hint.getBoundingPoly());
                }
            }
        }
        return annotation;
    }
}

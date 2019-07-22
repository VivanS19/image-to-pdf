/**
 @author Vivan Singhal
 */

package com.health.imaging.service;

import com.google.cloud.vision.v1.AnnotateImageRequest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.util.List;

@Service
public class DetectCropHints {
    private final String PHOTO_DIR = "photos";
    private final String CROPPED_DIR = "cropped";
    private final String BOUNDARY_DIR = "boundary";
    private BoundingPoly cropHints(ByteString imgBytes) throws Exception,
            IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
//        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
//        The aspect ratio for most of the pages is 8/11.
        CropHintsParams cropHintsParams = CropHintsParams.newBuilder()
                .addAspectRatios(0.7273f)
                .build();
        ImageContext context = ImageContext.newBuilder()
                .setCropHintsParams(cropHintsParams)
                .build();

        Image img = Image.newBuilder()
                .setContent(imgBytes)
                .build();
        Feature feat = Feature.newBuilder()
                .setType(Type.CROP_HINTS)
                .build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                        .addFeatures(feat)
                        .setImage(img)
                        .setImageContext(context)
                        .build();
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
            }
        }
        System.out.println(annotation.getCropHintsList().size());
        return annotation.getCropHints(0).getBoundingPoly();
    }

    private void createDirs() throws IOException {
        File file = new File(PHOTO_DIR + File.separator + CROPPED_DIR);
        if (!file.exists()){
            file.mkdir();
        }
        File file2 = new File(PHOTO_DIR + File.separator + BOUNDARY_DIR);
        if(!file2.exists()){
            file2.mkdir();
        }
    }

    public String cropImage(ByteString imgBytes, String inputFileName) throws Exception {
        createDirs();
        BufferedImage origImage = ImageIO.read(new ByteArrayInputStream(imgBytes.toByteArray()));
        BoundingPoly cropH = cropHints(imgBytes);
        System.out.println(cropH);
        BufferedImage croppedImage = origImage.getSubimage(cropH.getVertices(0).getX(), cropH.getVertices(0).getY(),cropH.getVertices(2).getX() - cropH.getVertices(0).getX() ,cropH.getVertices(2).getY()-cropH.getVertices(0).getY());
        System.out.println(inputFileName);
        File croppedFile = new File("photos/cropped/" + "cropped_" + inputFileName);
        ImageIO.write(croppedImage, inputFileName.split("\\.")[1] , croppedFile);
        System.out.println("Image Cropped Successfully!");
        return croppedFile.getCanonicalPath();

    }

    public String drawBoundary(ByteString imgBytes, String inputFileName) throws Exception {
        createDirs();
        BufferedImage origImage = ImageIO.read(new ByteArrayInputStream(imgBytes.toByteArray()));
        Graphics2D graphics = (Graphics2D) origImage.getGraphics();
        BoundingPoly cropH = cropHints(imgBytes);
        System.out.println(cropH);
        graphics.setStroke(new BasicStroke(3));
        graphics.setColor(Color.RED);
        graphics.drawRect(cropH.getVertices(0).getX(), cropH.getVertices(0).getY(),cropH.getVertices(2).getX() - cropH.getVertices(0).getX() ,cropH.getVertices(2).getY()-cropH.getVertices(0).getY());
        File drawnImageFile = new File("photos/boundary/" + "boundary_" + inputFileName);
        System.out.println(inputFileName);
        ImageIO.write(origImage, inputFileName.split("\\.")[1] , drawnImageFile);
        System.out.println("Image Cropped Successfully!");
        return drawnImageFile.getCanonicalPath();
    }

    public static void main(String[] args) throws Exception {
        DetectCropHints c = new DetectCropHints();
        byte[] imageBytes = Files.readAllBytes(new File("src/photos/abl.jpg").toPath());
        c.drawBoundary(ByteString.copyFrom(imageBytes), "abl.jpg");
        c.cropImage(ByteString.copyFrom(imageBytes), "abl.jpg");
    }
}

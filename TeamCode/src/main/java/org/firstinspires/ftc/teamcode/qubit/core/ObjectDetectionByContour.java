package org.firstinspires.ftc.teamcode.qubit.core;

import android.annotation.SuppressLint;

import org.firstinspires.ftc.teamcode.qubit.core.enumerations.GeometricShapeEnum;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ObjectDetectionByContour extends ObjectDetectionBase {
  private final Mat alternativeColorMat = new Mat();
  private final Mat processedMat = new Mat();

  public ObjectDetectionByContour() {
  }

  /**
   * PERFORMANCE
   * Initializes various Mats and sub mats so that we don't need to
   * reinitialize them on every frame..
   *
   * @param firstFrame The very first frame that is processed.
   */
  public void init(Mat firstFrame) {
  }

  /**
   * Process the current frame, looking for the game element.
   *
   * @param frame         The input frame to process.
   * @param gameElement   The game element to look for.
   * @param annotateFrame When true,draws annotations on the frame. Helps with debugging.
   */
  @SuppressLint("DefaultLocale")
  public void processFrame(Mat frame, GameElement gameElement, boolean annotateFrame) {
    Imgproc.cvtColor(frame, alternativeColorMat, gameElement.colorConversionCode);
    Core.inRange(alternativeColorMat, gameElement.lowerColorThreshold,
        gameElement.upperColorThreshold, processedMat);

    // Remove Noise
    Imgproc.morphologyEx(processedMat, processedMat, Imgproc.MORPH_OPEN, new Mat());
    Imgproc.morphologyEx(processedMat, processedMat, Imgproc.MORPH_CLOSE, new Mat());

    // Find Contours within the color thresholds
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(processedMat, contours, new Mat(), Imgproc.RETR_LIST,
        Imgproc.CHAIN_APPROX_SIMPLE);

    if (FtcUtils.DEBUG || annotateFrame) {
      for (int i = 0; i < contours.size(); i++) {
        double contourArea = Imgproc.contourArea(contours.get(i));
        if (contourArea >= gameElement.minSize.area()
            && contourArea <= gameElement.maxSize.area()) {
          Imgproc.drawContours(frame, contours, i, gameElement.elementColor,
              gameElement.borderSize);
        }
      }
    }

    gameElement.invalidate();

    // Loop Through Contours
    for (MatOfPoint contour : contours) {
      boolean geometricShapeMatched = true;
      if (gameElement.geometricShapeEnum != GeometricShapeEnum.UNKNOWN) {
        MatOfPoint2f curve = new MatOfPoint2f();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        contour.convertTo(curve, CvType.CV_32F);
        Imgproc.approxPolyDP(curve, approxCurve, 0.01 * Imgproc.arcLength(curve, true), true);
        geometricShapeMatched = gameElement.geometricShapeEnum.match(approxCurve.total());
        curve.release();
        approxCurve.release();
      }

      double contourArea = Imgproc.contourArea(contour);
      if (geometricShapeMatched && contourArea >= gameElement.minSize.area()
          && contourArea > gameElement.area
          && contourArea <= gameElement.maxSize.area()) {
        // Found contour within the area thresholds
        Point[] contourArray = contour.toArray();
        MatOfPoint2f areaPoints = new MatOfPoint2f(contourArray);
        RotatedRect tempRect = Imgproc.minAreaRect(areaPoints);
        areaPoints.release();
        double aspectRatio = FtcUtils.getAspectRatio(tempRect);
        if (aspectRatio >= gameElement.minAspectRatio && aspectRatio <= gameElement.maxAspectRatio) {
          // Found contour within the aspect ratio thresholds
          gameElement.validate(tempRect, aspectRatio, contourArea);
        }
      }

      contour.release();
    }

    if (gameElement.elementFound()) {
      if (FtcUtils.DEBUG || annotateFrame) {
        Imgproc.rectangle(frame, gameElement.boundingRect, gameElement.elementColor, gameElement.borderSize);

        // Display Data
        Imgproc.putText(frame, String.format("%s: A%.0f AR%.2f",
                gameElement.tag, gameElement.area, gameElement.aspectRatio),
            new Point(gameElement.boundingRect.x, gameElement.boundingRect.y - 20),
            Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, gameElement.elementColor, gameElement.borderSize);
      }
    }
  }
  @SuppressLint("DefaultLocale")
  public void processFrame(Mat frame, SampleElement sampleElement, boolean annotateFrame) {
    Imgproc.cvtColor(frame, alternativeColorMat, sampleElement.colorConversionCode);
    Core.inRange(alternativeColorMat, sampleElement.lowerColorThreshold,
        sampleElement.upperColorThreshold, processedMat);

    // Remove Noise
    Imgproc.morphologyEx(processedMat, processedMat, Imgproc.MORPH_OPEN, new Mat());
    Imgproc.morphologyEx(processedMat, processedMat, Imgproc.MORPH_CLOSE, new Mat());

    // Find Contours within the color thresholds
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(processedMat, contours, new Mat(), Imgproc.RETR_LIST,
        Imgproc.CHAIN_APPROX_SIMPLE);

    if (FtcUtils.DEBUG || annotateFrame) {
      for (int i = 0; i < contours.size(); i++) {
        double contourArea = Imgproc.contourArea(contours.get(i));
        if (contourArea >= sampleElement.minSize.area()
            && contourArea <= sampleElement.maxSize.area()) {
          Imgproc.drawContours(frame, contours, i, sampleElement.elementColor,
              sampleElement.borderSize);
        }
      }
    }

    sampleElement.invalidate();

    // Loop Through Contours
    for (MatOfPoint contour : contours) {
      boolean geometricShapeMatched = true;
      if (sampleElement.geometricShapeEnum != GeometricShapeEnum.UNKNOWN) {
        MatOfPoint2f curve = new MatOfPoint2f();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        contour.convertTo(curve, CvType.CV_32F);
        Imgproc.approxPolyDP(curve, approxCurve, 0.01 * Imgproc.arcLength(curve, true), true);
        geometricShapeMatched = sampleElement.geometricShapeEnum.match(approxCurve.total());
        curve.release();
        approxCurve.release();
      }

      double contourArea = Imgproc.contourArea(contour);
      if (geometricShapeMatched && contourArea >= sampleElement.minSize.area()
          && contourArea > sampleElement.area
          && contourArea <= sampleElement.maxSize.area()) {
        // Found contour within the area thresholds
        Point[] contourArray = contour.toArray();
        MatOfPoint2f areaPoints = new MatOfPoint2f(contourArray);
        RotatedRect tempRect = Imgproc.minAreaRect(areaPoints);
        areaPoints.release();
        double aspectRatio = FtcUtils.getAspectRatio(tempRect);
        if (aspectRatio >= sampleElement.minAspectRatio && aspectRatio <= sampleElement.maxAspectRatio) {
          // Found contour within the aspect ratio thresholds
          sampleElement.validate(tempRect, aspectRatio, contourArea);
        }
      }

      contour.release();
    }

    if (sampleElement.elementFound()) {
      if (FtcUtils.DEBUG || annotateFrame) {
        Imgproc.rectangle(frame, sampleElement.boundingRect, sampleElement.elementColor, sampleElement.borderSize);

        // Display Data
        Imgproc.putText(frame, String.format("%s: A %.0f AR %.2f",
                sampleElement.tag, sampleElement.area, sampleElement.aspectRatio),
            new Point(sampleElement.boundingRect.x, sampleElement.boundingRect.y - 20),
            Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, sampleElement.elementColor, sampleElement.borderSize);
      }
    }
  }
}

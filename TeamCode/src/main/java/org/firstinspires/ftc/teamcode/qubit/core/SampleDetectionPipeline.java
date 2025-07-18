package org.firstinspires.ftc.teamcode.qubit.core;

import org.firstinspires.ftc.teamcode.qubit.core.enumerations.ObjectColorEnum;
import org.firstinspires.ftc.teamcode.qubit.core.enumerations.ObjectDetectionAlgorithmEnum;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

public class SampleDetectionPipeline extends OpenCvPipeline {
  private static final String TAG = "MsdPipeline";
  private OpenCvWebcam openCvWebcam = null;
  private boolean annotateFrame = true;

  // Volatile because accessed by OpMode without syncObject
  public volatile boolean error = false;
  public volatile Exception lastException = null;
  private final ObjectDetectionByContour odbCon;

  public final SampleElement[] sampleElements = new SampleElement[]{
      SampleElement.getSample(ObjectColorEnum.BLUE),
      SampleElement.getSample(ObjectColorEnum.RED1),
      SampleElement.getSample(ObjectColorEnum.RED2),
      SampleElement.getSample(ObjectColorEnum.YELLOW)
  };

  public SampleDetectionPipeline(OpenCvWebcam openCvWebcam) {
    this.openCvWebcam = openCvWebcam;
    odbCon = new ObjectDetectionByContour();
  }

  public void disableAnnotations() {
    annotateFrame = false;
    if (openCvWebcam != null) {
      openCvWebcam.pauseViewport();
    }
  }

  public void enableAnnotations() {
    annotateFrame = true;
    if (openCvWebcam != null) {
      openCvWebcam.resumeViewport();
    }
  }

  @Override
  public void init(Mat firstFrame) {
    odbCon.init(firstFrame);
  }

  @Override
  public Mat processFrame(Mat frame) {
    error = false;
    lastException = null;
    try {
      for (SampleElement sampleElement : sampleElements) {
       if (sampleElement.tgeDetectionAlgorithm == ObjectDetectionAlgorithmEnum.CONTOUR) {
          odbCon.processFrame(frame, sampleElement, annotateFrame);
          sampleElement.takeAttendance();
        }
      }
    } catch (Exception exception) {
      lastException = exception;
      error = true;
    }

    return frame;
  }

  @Override
  public void onViewportTapped() {
    /*
     * The viewport (if one was specified in the constructor) can also be dynamically "paused"
     * and "resumed". The primary use case of this is to reduce CPU, memory, and power load
     * when you need your vision pipeline running, but do not require a live preview on the
     * robot controller screen. For instance, this could be useful if you wish to see the live
     * camera preview as you are initializing your robot, but you no longer require the live
     * preview after you have finished your initialization process; pausing the viewport does
     * not stop running your pipeline.
     *
     */
  }

  public void draw(Mat input, RotatedRect rotatedRect, Scalar color, int thickness) {
    Point[] points = new Point[4];
    try {
      rotatedRect.points(points);
      for (int i = 0; i < 4; i++) {
        Imgproc.line(input, points[i], points[(i + 1) % 4], color, thickness);
      }
    } catch (Exception ignored) {
    }
  }
}

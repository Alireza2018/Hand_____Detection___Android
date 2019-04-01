package templatemaching.app.alireza.com.templatemaching;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements CvCameraViewListener2 {

    private static final String  TAG = "OCVSample::Activity";

    private CustomSurfaceView mOpenCvCameraView;
    //private CircleOverlayView mOverlay;
    private TutorialView mTutorialView;
    private SeekBar mSeekBar;

    float overlayCenterX , overlayCenterY = 0;
    int metaState = 0;
    long downTime = SystemClock.uptimeMillis();
    long eventTime = SystemClock.uptimeMillis() + 100;
    float touchX , touchY = 0;



    // Used to load the 'native-lib' library on application startup.
//    y

    private BaseLoaderCallback mLoader = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                case LoaderCallbackInterface.INIT_FAILED:
                    Log.i(TAG, "OpenCV load failed");
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_surface_view);

        mOpenCvCameraView = findViewById(R.id.custom_surface_view);
        mTutorialView = findViewById(R.id.tutorialOverlay);
        mSeekBar = findViewById(R.id.camera_zoom_control);


        //mOverlay = findViewById(R.id.circleOverlay);


//        mOverlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                //view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                overlayCenterX =  mOverlay.getPivotX() / 2;
//                overlayCenterY = mOverlay.getPivotY()  / 2;
//
//                mOverlay.createWindowFrame( mOverlay.getPivotX() / 2, mOverlay.getPivotY()  / 2);
//            }
//        });

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setZoomControl(mSeekBar);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoader);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoader.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    //Camera view methods and function implementation
    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat mCamera = inputFrame.rgba();

        Mat hsv = new Mat();

        try{
            Imgproc.cvtColor(mCamera, hsv, Imgproc.COLOR_RGBA2RGB);
            Imgproc.cvtColor(hsv, hsv, Imgproc.COLOR_RGB2HSV_FULL);
            Mat result = new Mat(hsv.rows(), hsv.cols(), CvType.CV_8UC1);
            Core.inRange(hsv, new Scalar(0, 0.28*255, 0, 0), new Scalar(25, 0.68*255, 255, 0), result);


            // Perform and decrease noise
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2));
            Imgproc.erode(result, result, kernel);
            Imgproc.dilate(result, result, kernel);
            Imgproc.GaussianBlur(result, result, new Size(3,3), 0);

            //Find contours and draw a bounding box around the area
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(result, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            Log.e(TAG, "ContourSize: " + contours.size());


            double maxVal = 0;
            int maxValIdx = 0;
            for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
                double contourArea = Imgproc.contourArea(contours.get(contourIdx));
                if (maxVal < contourArea) {
                    maxVal = contourArea;
                    maxValIdx = contourIdx;
                }
            }

            //Imgproc.drawContours(mCamera, contours, maxValIdx, new Scalar(0, 255, 0), 2);

            //find center of the contour
            MatOfPoint mop = new MatOfPoint();
            mop.fromList(contours.get(maxValIdx).toList());
            Moments moments = Imgproc.moments(mop);

            final Point centroid = new Point();

            centroid.x = moments.get_m10() / moments.get_m00();
            centroid.y = moments.get_m01() / moments.get_m00();

            touchX = 500;
            touchY = 300;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MotionEvent motionEvent = MotionEvent.obtain(
                            downTime,
                            eventTime,
                            MotionEvent.ACTION_UP,
                            (float) centroid.x,
                            (float) centroid.y,
                            metaState
                    );
                    mTutorialView.dispatchTouchEvent(motionEvent);
                }
            });

            Imgproc.circle(mCamera, centroid, 9, new Scalar(0, 255, 0), 2);

            //center of the screen
            //Point screenCenter = new Point(overlayCenterX, overlayCenterY);
            //Imgproc.circle(mCamera, screenCenter, 8, new Scalar(0, 255, 0), 1);
        }
        catch(Exception e){}


        return mCamera;
    }


}


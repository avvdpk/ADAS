package de.uni_frankfurt.cs.ccc.sse.adam;

import android.app.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.util.List;

import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;


import static java.lang.Boolean.valueOf;
import static org.opencv.android.LoaderCallbackInterface.INIT_FAILED;
import static org.opencv.android.LoaderCallbackInterface.SUCCESS;
import static org.opencv.android.OpenCVLoader.OPENCV_VERSION_3_1_0;
import static org.opencv.android.OpenCVLoader.initAsync;
import static org.opencv.android.OpenCVLoader.initDebug;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StreetViewActivity extends Activity{
    private static final String TAG = "StreetViewActivity";

    private SpeedHandler speedHandler;
    private RotationHandler rotationHandler;

    private BaseLoaderCallback loaderCallback = new OpenCVLoaderCallback(this);
    private CameraHandler cameraHandler;
    private CameraBridgeViewBase mDashcamView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashcam);
        mDashcamView = (CameraBridgeViewBase) findViewById(R.id.dashcam_java_surface_view);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.speedHandler = new SpeedHandler((TextView) findViewById(R.id.txtCurrentSpeed), locationManager);
        this.rotationHandler = new RotationHandler(
                (ImageView) this.findViewById(R.id.arrow_right),
                (ImageView) this.findViewById(R.id.arrow_left),
                (TextView) this.findViewById(R.id.txtCurrentOrientation),
                (SensorManager) getSystemService(Context.SENSOR_SERVICE));
        cameraHandler = new CameraHandler();
        mDashcamView.setCvCameraViewListener(cameraHandler);
        mDashcamView.setMaxFrameSize(640,480);
        if (BuildConfig.DEBUG)
            mDashcamView.enableFpsMeter();
    }


    @Override
    protected void onDestroy() {
        mDashcamView.disableView();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        if (valueOf(System.getProperty("UNIT_TEST", Boolean.FALSE.toString()))) {
            loaderCallback.onManagerConnected(initDebug(false) ? SUCCESS : INIT_FAILED);
        } else {
            initAsync(OPENCV_VERSION_3_1_0, getApplicationContext(), loaderCallback);
        }
    }

    void enableOpenCV() {
        mDashcamView.enableView();
    }
}

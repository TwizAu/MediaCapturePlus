package cordova.plugin.mediacaptureplus;

import android.app.Activity;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageAssessmentActivity extends Activity implements SurfaceHolder.Callback {

    RecyclerView questionsRec;
    QuestionRecyclerViewAdapter adapter;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;
    Button start, stop, capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String package_name = getApplication().getPackageName();
        Resources resources = getApplication().getResources();
        setContentView(resources.getIdentifier("image_assessment_layout", "layout", package_name));

        getActionBar().hide();

        start = findViewById(resources.getIdentifier("btn_start", "id", package_name));
        stop = findViewById(resources.getIdentifier("btn_stop", "id", package_name));
        capture = findViewById(resources.getIdentifier("btn_capture", "id", package_name));
        surfaceView = findViewById(resources.getIdentifier("surface", "id", package_name));
        questionsRec = findViewById(resources.getIdentifier("rec_questions", "id", package_name));

        ArrayList<String> questionsTemp = new ArrayList<>();
        questionsTemp.add("This is a question 1?");
        questionsTemp.add("This is a question 2?");
        questionsTemp.add("This is a question 3?");

        ArrayList<Integer> answersTemp = new ArrayList<>();
        answersTemp.add(null);
        answersTemp.add(null);
        answersTemp.add(null);

        questionsRec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionRecyclerViewAdapter(this, questionsTemp, answersTemp);

        questionsRec.setAdapter(adapter);

        start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                startCamera();
            }
        });

        stop.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                stopCamera();
            }
        });
        capture.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                captureImage();
            }
        });

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                System.out.println("onPictureTaken - raw");
            }
        };

        shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() {
                System.out.println("onShutter'd");
            }
        };

        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream;
                try {
                    outStream = new FileOutputStream(
                            String.format(Environment.getExternalStorageDirectory().getPath() + "/%d.jpg",
                                    System.currentTimeMillis()));
                    outStream.write(data);
                    outStream.close();
                    System.out.println("onPictureTaken - wrote bytes: " + data.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("onPictureTaken - jpeg");
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCamera();
    }

    private void captureImage() {
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    private void startCamera() {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            System.out.println("init_camera: " + e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
        param.setPreviewSize(previewSize.width, previewSize.height);

        List<int[]> previewFpsRanges = param.getSupportedPreviewFpsRange();
        int[] previewFpsRange = previewFpsRanges.get(0);
        param.setPreviewFpsRange(previewFpsRange[1], previewFpsRange[0]);

        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            System.out.println("init_camera: " + e);
        }
    }

    private void stopCamera() {
        camera.stopPreview();
        camera.release();
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        startCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

}
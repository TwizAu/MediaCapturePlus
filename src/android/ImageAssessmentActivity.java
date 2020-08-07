package cordova.plugin.mediacaptureplus;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    ImageView captureDisplay;
    int minLength;
    int currentCapture = 0;
    int maxCapture = 3;
    ImageButton capture, nextButton, previousButton, doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String package_name = getApplication().getPackageName();
        Resources resources = getApplication().getResources();
        setContentView(resources.getIdentifier("image_assessment_layout", "layout", package_name));

        getActionBar().hide();

        capture = findViewById(resources.getIdentifier("btn_capture", "id", package_name));
        surfaceView = findViewById(resources.getIdentifier("surface", "id", package_name));
        captureDisplay = findViewById(resources.getIdentifier("capture_display", "id", package_name));
        questionsRec = findViewById(resources.getIdentifier("rec_questions", "id", package_name));
        nextButton = findViewById(resources.getIdentifier("btn_capture", "id", package_name));
        previousButton = findViewById(resources.getIdentifier("btn_capture", "id", package_name));
        doneButton = findViewById(resources.getIdentifier("btn_capture", "id", package_name));

        captureDisplay.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.GONE);
        previousButton.setVisibility(View.GONE);
        doneButton.setVisibility(View.GONE);

        ArrayList<String> questionsTemp = new ArrayList<>();
        questionsTemp.add("This is a question 1?");
        questionsTemp.add("This is a question 2?");
        questionsTemp.add("This is a question 3?");
        questionsTemp.add("This is a question 4?");

        ArrayList<Integer> answersTemp = new ArrayList<>();
        answersTemp.add(null);
        answersTemp.add(null);
        answersTemp.add(null);
        answersTemp.add(null);

        questionsRec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionRecyclerViewAdapter(this, questionsTemp, answersTemp);

        questionsRec.setAdapter(adapter);

        capture.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                capture.setEnabled(false);
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });

        nextButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                currentCapture++;
            }
        });

        previousButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                currentCapture--;
            }
        });

        doneButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                
            }
        });

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() { }
        };

        rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) { }
        };

        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

                FileOutputStream outStream;
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 1;
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, opt);

                Bitmap img = Bitmap.createScaledBitmap(bm, captureDisplay.getWidth(), captureDisplay.getHeight(), true);
                captureDisplay.setImageBitmap(img);
                captureDisplay.setVisibility(View.VISIBLE);
                //capture.setVisibility(View.INVISIBLE);

                /* if (maxCapture == 1) {
                    nextButton.setVisibility(View.GONE);
                    previousButton.setVisibility(View.GONE);
                    doneButton.setVisibility(View.VISIBLE);
                } else {
                    if (currentCapture == 0) {
                        nextButton.setVisibility(View.VISIBLE);
                        previousButton.setVisibility(View.GONE);
                        doneButton.setVisibility(View.GONE);
                    } else if (currentCapture == maxCapture - 1) {
                        nextButton.setVisibility(View.GONE);
                        previousButton.setVisibility(View.VISIBLE);
                        doneButton.setVisibility(View.VISIBLE);
                    } else {
                        nextButton.setVisibility(View.VISIBLE);
                        previousButton.setVisibility(View.VISIBLE);
                        doneButton.setVisibility(View.GONE);
                    }
                } */

                bm = Bitmap.createScaledBitmap(bm, minLength, minLength, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] dataCropped = stream.toByteArray();
                bm.recycle();

                try {
                    outStream = new FileOutputStream(
                            String.format(Environment.getExternalStorageDirectory().getPath() + "/%d.jpg",
                                    System.currentTimeMillis()));
                    outStream.write(dataCropped);
                    outStream.close();
                    System.out.println("onPictureTaken - wrote bytes: " + dataCropped.length);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCamera();
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
        List<Camera.Size> pictureSizes = param.getSupportedPictureSizes();
        Camera.Size previewSize = previewSizes.get(0);
        Camera.Size pictureSize = pictureSizes.get(0);
        param.setPreviewSize(previewSize.width, previewSize.height);
        param.setPictureSize(pictureSize.width, pictureSize.height);

        if (pictureSize.width < pictureSize.height) {
            minLength = pictureSize.width;
        } else {
            minLength = pictureSize.height;
        }

        List<int[]> previewFpsRanges = param.getSupportedPreviewFpsRange();
        Collections.reverse(previewFpsRanges);
        int[] previewFpsRange = previewFpsRanges.get(0);
        param.setPreviewFpsRange(previewFpsRange[0], previewFpsRange[1]);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < previewSizes.size(); i++) {
            String s = previewSizes.get(i).width + "x" + previewSizes.get(i).height;
            sb.append(s);
            sb.append("\t");
        }
        for (int i = 0; i < pictureSizes.size(); i++) {
            String s = pictureSizes.get(i).width + "x" + pictureSizes.get(i).height;
            sb.append(s);
            sb.append("\t");
        }
        for (int i = 0; i < previewFpsRanges.size(); i++) {
            String s = previewFpsRanges.get(i)[0] + "-" + previewFpsRanges.get(i)[1];
            sb.append(s);
            sb.append("\t");
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra("previewOptions", sb.toString());
        setResult(Activity.RESULT_OK, resultIntent);

        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            System.out.println("init_camera: " + e);
        }
    }

    private void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        startCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

}
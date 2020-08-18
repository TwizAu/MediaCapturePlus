package cordova.plugin.mediacaptureplus;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    QuestionRecyclerViewAdapter questionAdapter;

    RecyclerView lightboxRec;
    LightboxRecyclerViewAdapter lightboxAdapter;

    ConstraintLayout layout;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;
    ImageView captureDisplay;
    int currentCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    int minLength;
    int currentCapture = 0;
    int maxCapture = 3;
    ImageButton capture, redoButton, nextButton, previousButton, doneButton, swapButton, flashButton, closeButton;
    Group settingsGroup;
    ArrayList<Bitmap> captureList;
    int flashToggleStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String package_name = getApplication().getPackageName();
        final Resources resources = getApplication().getResources();
        setContentView(resources.getIdentifier("image_assessment_layout", "layout", package_name));

        getActionBar().hide();

        captureList = new ArrayList<>(Collections.<Bitmap>nCopies(maxCapture, null));

        capture = findViewById(resources.getIdentifier("btn_capture", "id", package_name));
        redoButton = findViewById(resources.getIdentifier("btn_redo", "id", package_name));
        surfaceView = findViewById(resources.getIdentifier("surface", "id", package_name));
        captureDisplay = findViewById(resources.getIdentifier("capture_display", "id", package_name));
        questionsRec = findViewById(resources.getIdentifier("rec_questions", "id", package_name));
        nextButton = findViewById(resources.getIdentifier("btn_next", "id", package_name));
        previousButton = findViewById(resources.getIdentifier("btn_previous", "id", package_name));
        doneButton = findViewById(resources.getIdentifier("btn_done", "id", package_name));
        settingsGroup = findViewById(resources.getIdentifier("settings_group", "id", package_name));
        swapButton = findViewById(resources.getIdentifier("btn_swap", "id", package_name));
        flashButton = findViewById(resources.getIdentifier("btn_flash", "id", package_name));
        closeButton = findViewById(resources.getIdentifier("btn_close", "id", package_name));
        layout = findViewById(resources.getIdentifier("layout", "id", package_name));
        
        if (layout.getTag().equals("tablet-port") || layout.getTag().equals("tablet-land")) {
            layout = findViewById(resources.getIdentifier("layout", "id", package_name));
            lightboxRec = findViewById(resources.getIdentifier("rec_lightbox", "id", package_name));
            lightboxRec.setLayoutManager(new LinearLayoutManager(this));
            ArrayList<Bitmap> lightboxImages = new ArrayList<>(Collections.nCopies(maxCapture, BitmapFactory.decodeResource(resources, resources.getIdentifier("lightbox", "drawable", package_name))));
            lightboxAdapter = new LightboxRecyclerViewAdapter(this, lightboxImages);
            lightboxRec.setAdapter(lightboxAdapter);
        }

        visibilityHelper();

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
        questionAdapter = new QuestionRecyclerViewAdapter(this, questionsTemp, answersTemp);
        questionsRec.setAdapter(questionAdapter);

        capture.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                capture.setEnabled(false);
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });

        redoButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                previousButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                redoButton.setVisibility(View.GONE);

                boolean isFull = true;
                for (int i = 0; i < captureList.size(); i++) {
                    if (captureList.get(i) == null) {
                        isFull = false;
                    }
                }

                if (isFull) {
                    startCamera(currentCameraID);
                } else {
                    captureList.set(currentCapture, null);
                }

                visibilityHelper();

            }
        });

        swapButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                swapButton.setEnabled(false);
                stopCamera();

                if(currentCameraID == Camera.CameraInfo.CAMERA_FACING_BACK){
                    currentCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    currentCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
                }

                startCamera(currentCameraID);

            }
        });

        flashButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                Camera.Parameters param;
                param = camera.getParameters();

                if (flashToggleStatus == 0) {
                    flashButton.setBackground(resources.getDrawable(resources.getIdentifier("baseline_flash_on_white_36dp", "drawable", package_name)));
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    flashToggleStatus = 1;
                } else if (flashToggleStatus == 1) {
                    flashButton.setBackground(resources.getDrawable(resources.getIdentifier("baseline_flash_auto_white_36dp", "drawable", package_name)));
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    flashToggleStatus = 2;
                } else {
                    flashButton.setBackground(resources.getDrawable(resources.getIdentifier("baseline_flash_off_white_36dp", "drawable", package_name)));
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    flashToggleStatus = 0;
                }

                camera.setParameters(param);

            }
        });

        closeButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        nextButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                currentCapture++;
                visibilityHelper();
            }
        });

        previousButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                currentCapture--;
                visibilityHelper();
            }
        });

        doneButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                finish();
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
                settingsGroup.setVisibility(View.GONE);
                captureList.set(currentCapture, img);
                capture.setVisibility(View.INVISIBLE);

                if (maxCapture == 1) {
                    nextButton.setVisibility(View.GONE);
                    previousButton.setVisibility(View.GONE);
                    doneButton.setVisibility(View.VISIBLE);
                } else if (currentCapture == 0) {
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

                bm = Bitmap.createScaledBitmap(bm, minLength, minLength, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] dataCropped = stream.toByteArray();
                bm.recycle();

                try {
                    outStream = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getPath() + "/%d.jpg", System.currentTimeMillis()));
                    outStream.write(dataCropped);
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 3);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(currentCameraID);
            } else {
                Toast.makeText(this, "Feature Requires Camera Permissions", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        if (requestCode == 4) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SUCCESS!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Feature Requires Write Storage Permissions", Toast.LENGTH_LONG).show();
                finish();
            }
        }
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

    private void startCamera(int cid) {
        try {
            camera = Camera.open(cid);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }

        setCameraRotation(currentCameraID, camera);

        capture.setEnabled(true);
        swapButton.setEnabled(true);

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

        /*StringBuilder sb = new StringBuilder();
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
        setResult(Activity.RESULT_OK, resultIntent);*/

        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
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
        startCamera(currentCameraID);
    }

    public void surfaceCreated(SurfaceHolder holder) { }

    public void surfaceDestroyed(SurfaceHolder holder) { }

    public void setCameraRotation(int cid, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cid, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void visibilityHelper() {
        if (captureList.get(currentCapture) != null) {
            captureDisplay.setImageBitmap(captureList.get(currentCapture));
            captureDisplay.setVisibility(View.VISIBLE);
            capture.setVisibility(View.INVISIBLE);
            settingsGroup.setVisibility(View.GONE);
            redoButton.setVisibility(View.VISIBLE);
        } else {
            capture.setVisibility(View.VISIBLE);
            redoButton.setVisibility(View.GONE);
            captureDisplay.setVisibility(View.GONE);
        }

        int inc = currentCapture + 1;
        int dec = currentCapture - 1;

        if (inc < this.maxCapture) {
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.GONE);
        }

        if (dec >= 0) {
            previousButton.setVisibility(View.VISIBLE);
        } else {
            previousButton.setVisibility(View.GONE);
        }

    }

}
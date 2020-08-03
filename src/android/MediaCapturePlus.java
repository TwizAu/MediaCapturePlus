package cordova.plugin.mediacaptureplus;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;

public class MediaCapturePlus extends CordovaPlugin {

    public CallbackContext temp;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            this.coolMethod(callbackContext);
            return true;
        }
        if (action.equals("testOpenCamera")) {
            this.testOpenCamera(callbackContext);
            return true;
        }
        if (action.equals("openImageAssessment")) {
            this.openImageAssessment(callbackContext);
            return true;
        }
        return false;
    }

    private void openImageAssessment(CallbackContext callbackContext) {
        this.temp = callbackContext;
        Intent intent = new Intent((CordovaPlugin) this, ImageAssessmentActivity.class);
        this.cordova.startActivityForResult((CordovaPlugin) this, intent, 2);
    }

    private void coolMethod(CallbackContext callbackContext) {
        VideoCaptureOptions vo = new VideoCaptureOptions(1920, 16, 9, 240, 60, 8000);
        callbackContext.success("Resolution: " + vo.getResolutionX() + "x"
                + (int) ((float) vo.getResolutionX() * vo.getAspectRatio()) + ", Aspect Ratio: 1:" + vo.getAspectRatio()
                + ", Recording Duraction (seconds): " + vo.getRecordingTimeLimit() + ", Framerate: " + vo.getFrameRate()
                + ", Bitrate (Mbit/s): " + vo.getBitrate() / 1000);
    }

    private void testOpenCamera(CallbackContext callbackContext) {
        this.temp = callbackContext;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager mPm = this.cordova.getActivity().getPackageManager();
        if (intent.resolveActivity(mPm) != null) {
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, 1);
        } else {
            this.temp.success("Failed!");
        }
    }

    public void onActivityResult(int requestId, int resultCode, Intent data) {
        if (requestId == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                this.temp.success("Success - Cancelled!");
            } else if (resultCode == Activity.RESULT_OK) {
                this.temp.success("Success - Ok");
            }
        }
        if (requestId == 2) {
            this.temp.success("Success = Activity");
        }
    }
}
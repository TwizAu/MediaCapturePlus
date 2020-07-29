package cordova.plugin.mediacaptureplus;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MediaCapturePlus extends CordovaPlugin {

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
        return false;
    }

    private void coolMethod(CallbackContext callbackContext) {
        VideoCaptureOptions vo = new VideoCaptureOptions(1920, 16, 9, 240, 60, 8000);
        callbackContext.success("Resolution: " + vo.getResolutionX() + "x" + (int)((float)vo.getResolutionX()*vo.getAspectRatio()) + ", Aspect Ratio: 1:" + vo.getAspectRatio() + ", Recording Duraction (seconds): " + vo.getRecordingTimeLimit() + ", Framerate: " + vo.getFrameRate() + ", Bitrate (Mbit/s): " + vo.getBitrate()/1000);
    }

    private void testOpenCamera(CallbackContext callbackContext) {
        callbackContext.success("Success!");
    }
}

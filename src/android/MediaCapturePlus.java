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
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        VideoCaptureOptions vo = new VideoCaptureOptions(1920, 16, 9, 240, 60, 8000);
        callbackContext.success("Resolution: " + vo.getResolutionX() + "x" + (float)vo.getResolutionX()*vo.getAspectRatio() + ", Aspect Ratio: 1:" + vo.getAspectRatio() + ", Recording Duraction (seconds): " + vo.getRecordingTimeLimit() + ", Framerate: " + vo.getFrameRate() + ", Bitrate (Mbit): " + vo.getBitrate()/1000);
    }
}

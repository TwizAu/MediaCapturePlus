package cordova.plugin.mediacaptureplus;

import android.app.Activity;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.Resolution;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.view.CameraRenderer;
import io.fotoapparat.view.Preview;
import org.apache.cordova.CallbackContext;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;

public class CaptureService extends Activity {
    private Fotoapparat fotoapparat;
    private CallbackContext callbackContext;

    public void createCaptureService() {
        fotoapparat = createFotoapparat();
    }

    CameraRenderer cr = new CameraRenderer() {
        @Override public void setScaleType(@NotNull ScaleType scaleType) { }
        @Override public void setPreviewResolution(@NotNull Resolution resolution) { }
        @NotNull @Override public Preview getPreview() { return null; }
    };
    private Fotoapparat createFotoapparat() {
        return Fotoapparat
                .with(this)
                .into(cr)
                .previewScaleType(ScaleType.CenterCrop)
                .lensPosition(back())
                .frameProcessor(new SampleFrameProcessor(callbackContext))
                .cameraErrorCallback(new CameraErrorListener() {
                    @Override
                    public void onError(@NotNull CameraException e) {
                        callbackContext.error("failed");
                    }
                })
                .build();
    }

    public void startCaptureService(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        fotoapparat.start();
    }

    public void stopCaptureService() {
        fotoapparat.stop();
    }
}

class SampleFrameProcessor implements FrameProcessor {
    
    private CallbackContext callbackContext;

    SampleFrameProcessor(CallbackContext callbackContext){
        this.callbackContext = callbackContext;
    }
    
    @Override
    public void process(@NotNull Frame frame) {
        System.out.println(frame + "Frame Res: " + frame.component1().width + "x" + frame.component1().height);
        this.callbackContext.success(frame + "Frame Res: " + frame.component1().width + "x" + frame.component1().height);
    }
}
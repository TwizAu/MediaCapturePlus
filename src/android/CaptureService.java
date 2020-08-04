package cordova.plugin.mediacaptureplus;

import android.app.Activity;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.FlashSelectorsKt.autoFlash;
import static io.fotoapparat.selector.FlashSelectorsKt.autoRedEye;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;
import static io.fotoapparat.selector.FocusModeSelectorsKt.autoFocus;
import static io.fotoapparat.selector.FocusModeSelectorsKt.continuousFocusPicture;
import static io.fotoapparat.selector.FocusModeSelectorsKt.fixed;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.PreviewFpsRangeSelectorsKt.highestFps;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;
import static io.fotoapparat.selector.SelectorsKt.firstAvailable;
import static io.fotoapparat.selector.SensorSensitivitySelectorsKt.highestSensorSensitivity;

import org.apache.cordova.CallbackContext;

public class CaptureService extends Activity {
    private Fotoapparat fotoapparat;
    private CallbackContext callbackContext;

    private CameraConfiguration cameraConfiguration = CameraConfiguration
            .builder()
            .photoResolution(standardRatio(
                    highestResolution()
            ))
            .focusMode(firstAvailable(
                    continuousFocusPicture(),
                    autoFocus(),
                    fixed()
            ))
            .flash(firstAvailable(
                    autoRedEye(),
                    autoFlash(),
                    torch(),
                    off()
            ))
            .previewFpsRange(highestFps())
            .sensorSensitivity(highestSensorSensitivity())
            .frameProcessor(new SampleFrameProcessor(callbackContext))
            .build();

    public void createCaptureService() {
        fotoapparat = createFotoapparat();
    }

    private Fotoapparat createFotoapparat() {
        return Fotoapparat
                .with(this)
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
package cordova.plugin.mediacaptureplus;

import android.app.Activity;
import android.os.Bundle;

public class ImageAssessmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String package_name = getApplication().getPackageName();
        Resources resources = getApplication().getResources();
        setContentView(resources.getIdentifier("image_assessment_layout", "layout", package_name));
    }
}
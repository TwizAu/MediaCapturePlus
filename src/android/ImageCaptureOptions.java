package cordova.plugin.mediacaptureplus;

public class ImageCaptureOptions {

    private int resolutionX;
    private float aspectRatio;

	ImageCaptureOptions() {
        this.resolutionX = 400;
        this.aspectRatio = 0.75;
    }

	ImageCaptureOptions(int resolutionX, int aspectRatio) {
        this.resolutionX = resolutionX;
        this.aspectRatio = aspectRatio;
    }

    ImageCaptureOptions(int resolutionX, int aspectX, int aspectY) {
        this.resolutionX = resolutionX;
        this.aspectRatio = aspectY/aspectX;
    }

	public int getResolutionX() {
		return this.resolutionX;
	}

	public void setResolutionX(int resolutionX) {
		this.resolutionX = resolutionX;
	}

	public float getAspectRatio() {
		return this.aspectRatio;
	}

	public void setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

}

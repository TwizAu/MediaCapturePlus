package cordova.plugin.mediacaptureplus;

public class VideoCaptureOptions {

    private int resolutionX;
    private float aspectRatio;
    private int recordingTimeLimit;
    private int frameRate;
    private int bitrate;

	VideoCaptureOptions() {
        this.resolutionX = 400;
        this.aspectRatio = 0.75f;
        this.recordingTimeLimit = 180;
        this.frameRate = 25;
        this.bitrate = 2000;
    }

    VideoCaptureOptions(int resolutionX, int aspectRatio, int recordingTimeLimit, int frameRate, int bitrate) {
        this.resolutionX = resolutionX;
        this.aspectRatio = aspectRatio;
        this.recordingTimeLimit = recordingTimeLimit;
        this.frameRate = frameRate;
        this.bitrate = bitrate;
    }

	VideoCaptureOptions(int resolutionX, int aspectX, int aspectY, int recordingTimeLimit, int frameRate, int bitrate) {
        this.resolutionX = resolutionX;
        this.aspectRatio = aspectY/aspectX;
        this.recordingTimeLimit = recordingTimeLimit;
        this.frameRate = frameRate;
        this.bitrate = bitrate;
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

	public int getRecordingTimeLimit() {
		return this.recordingTimeLimit;
	}

	public void setRecordingTimeLimit(int recordingTimeLimit) {
		this.recordingTimeLimit = recordingTimeLimit;
	}

	public int getFrameRate() {
		return this.frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public int getBitrate() {
		return this.bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

}

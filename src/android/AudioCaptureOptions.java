package cordova.plugin.mediacaptureplus;

public class AudioCaptureOptions {

    private int recordingTimeLimit;
    private int bitrate;

    AudioCaptureOptions() {
        this.recordingTimeLimit = 180;
        this.bitrate = 320;
    }

    AudioCaptureOptions(int recordingTimeLimit, int bitrate) {
        this.recordingTimeLimit = recordingTimeLimit;
        this.bitrate = bitrate;
    }

    public int getRecordingTimeLimit() {
		return this.recordingTimeLimit;
	}

	public void setRecordingTimeLimit(int recordingTimeLimit) {
		this.recordingTimeLimit = recordingTimeLimit;
	}

	public int getBitrate() {
		return this.bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

}

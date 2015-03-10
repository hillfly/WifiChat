package hillfly.wifichat.util;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;

public class AudioRecorderUtils {
    private static int SAMPLE_RATE_IN_HZ = 8000; // 采样率���

    private MediaRecorder mMediaRecorder;
    private String mVoicePath;

    public AudioRecorderUtils() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
    }

    public void setVoicePath(String path, String filename) {
        this.mVoicePath = path + File.separator + filename + ".amr";
    }

    public String getVoicePath() {
        return this.mVoicePath;
    }

    public void start() throws IOException {
        File directory = new File(mVoicePath).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Path to file could not be created");
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
        mMediaRecorder.setOutputFile(mVoicePath);
        mMediaRecorder.prepare();
        mMediaRecorder.start();
    }

    public void stop() throws IOException {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mMediaRecorder != null) {
            return (mMediaRecorder.getMaxAmplitude());
        }
        else
            return 0;
    }
}
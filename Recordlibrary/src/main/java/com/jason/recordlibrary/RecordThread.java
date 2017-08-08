package com.jason.recordlibrary;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.wiseuc.audiorecorder.SimpleLame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Admin on 2017-08-07.
 */

public class RecordThread extends Thread {
    private static final String TAG = Mp3Recorder.class.getSimpleName();
    private static final int SAMPLE_RATE_INHZ = 8000;
    private boolean running;
    private AudioRecord audioRecord;
    private int bufSize;
    private byte[] mBytes;
    private byte[] mp3Buffer;
    private FileOutputStream os;
    private Handler handler;

    public RecordThread(File file, Handler handler) {
        this.handler = handler;
        bufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufSize);
        mBytes = new byte[bufSize];

        SimpleLame.init(SAMPLE_RATE_INHZ, 1, SAMPLE_RATE_INHZ, 128);
        mp3Buffer = new byte[(int) (7200 + (bufSize * 2 * 1.25))];

        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            running = true;
            byte[] bytes_pkg;
            audioRecord.startRecording();
            while (running) {
                int len = audioRecord.read(mBytes, 0, bufSize);
                if (len > 0) {
                    updataVolume(mBytes);
                    bytes_pkg = mBytes.clone();
                    short[] innerBuf = new short[len / 2];
                    ByteBuffer.wrap(bytes_pkg).order(ByteOrder.LITTLE_ENDIAN)
                            .asShortBuffer().get(innerBuf);
                    int encodedSize = SimpleLame.encode(innerBuf, innerBuf, len / 2,
                            mp3Buffer);
                    try {
                        os.write(mp3Buffer, 0, encodedSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updataVolume(byte[] buffer) {
        long v = 0;
        // 将 buffer 内容取出，进行平方和运算
        for (int i = 0; i < buffer.length; i++) {
            v += buffer[i] * buffer[i];
        }
        // 平方和除以数据总长度，得到音量大小。
        double mean = v / (double) buffer.length;
        double volume = 10 * Math.log10(mean);

        Message message = new Message();
        message.obj = volume;
        handler.sendMessage(message);
    }

    public void quit() {
        running = false;
    }

    private void release() {
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        mBytes = null;
        try {
            int flushResult = SimpleLame.flush(mp3Buffer);
            if (flushResult > 0) {
                os.write(mp3Buffer, 0, flushResult);
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "stop");
    }
}

package com.jason.recordlibrary;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.jason.recordlibrary.utils.FileUtil;
import com.jason.recordlibrary.view.RecordViewDialog;
import java.io.File;

/**
 * Created by Admin on 2017-08-07.
 */

public class Mp3Recorder {
    private static Mp3Recorder instance;
    private String mp3Name = "temp.mp3";
    private RecordThread recordThread;
    private File file;
    private RecordViewDialog recordViewDialog;
    private RecordListener recordListener;

    private Mp3Recorder() {
    }

    public static synchronized Mp3Recorder getInstance() {
        if (instance == null) {
            instance = new Mp3Recorder();
        }
        return instance;
    }

    public Mp3Recorder setListener(RecordListener listener) {
        recordListener = listener;
        return instance;
    }

    public void start(Context context) {
        if (recordThread != null) {
            stop();
        }
        file = new File(FileUtil.getCacheRootFile(context), mp3Name);
        recordThread = new RecordThread(file, handler);
        recordThread.start();

        recordViewDialog = new RecordViewDialog(context, R.style.Dialog, onClickListener);
        recordViewDialog.show();
    }

    public void stop() {
        if (recordThread != null) {
            recordThread.quit();
            recordThread = null;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recordViewDialog != null) recordViewDialog.dismiss();
            }
        }, 100);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stop();
            int i = v.getId();
            if (i == R.id.ok) {
                recordListener.onComplete(file.getPath());
            } else if (i == R.id.delete) {
                new File(FileUtil.getCacheRootFile(v.getContext()), mp3Name).delete();
                recordListener.onCancel();
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (recordThread == null) return;
            if (recordViewDialog != null) {
                try {
                    recordViewDialog.setVolume(Integer.parseInt(new java.text.DecimalFormat("0").format(msg.obj)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
}

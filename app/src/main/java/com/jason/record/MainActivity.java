package com.jason.record;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.jason.recordlibrary.Mp3Recorder;
import com.jason.recordlibrary.RecordListener;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements RecordListener {
    private MediaPlayer mPlayer;
    private TextView path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        path = (TextView) findViewById(R.id.path);
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder();
            }
        });
        findViewById(R.id.paly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palyMedia(path.getText().toString());
            }
        });
    }

    private void recorder() {
        Mp3Recorder.getInstance().setListener(this).start(this);
    }

    @Override
    public void onComplete(String path) {
        this.path.setText(path);
    }

    @Override
    public void onCancel() {
        this.path.setText("");
    }

    public void palyMedia(String fileName) {
        try {
            if (mPlayer == null)
                mPlayer = new MediaPlayer();
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

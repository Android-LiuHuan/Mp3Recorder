package com.jason.recordlibrary;

/**
 * Created by Admin on 2017-08-07.
 */

public interface RecordListener {
    public void onComplete(String path);
    public void onCancel();
}

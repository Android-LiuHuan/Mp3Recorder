# Mp3Recorder

![image](https://github.com/Android-LiuHuan/Mp3Recorder/blob/master/picture/20170808164009.png)



一行代码开启录制

	Mp3Recorder.getInstance().setListener(this).start(this);


监听录音

	@Override
	public void onComplete(String path) {
	    //path 录音文件路径
	    //...
	}
  
	@Override
	public void onCancel() {
	    //...
	}



# Library projects
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.Android-LiuHuan:Mp3Recorder:1.0'
	}

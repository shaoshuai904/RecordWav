# RecordWav

集 【录制】、【播放】、【解析】于一体的wav文件的工具类。

功能包括：

－ 录制wav文件。（录音、暂停、完成）
－ 播放wav文件。（自定义播放、系统播放）
－ 解析已有wav文件的信息。

![show_recorder](https://github.com/shaoshuai904/RecordWav/blob/master/screens/show_recorder.png)


### 快速使用

**Step 1.** Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency

```groovy
dependencies {
	compile 'com.github.shaoshuai904:RecordWav:1.0'
}
```


###  MsRecorder 

	构造参数:[ 文件保存路径 + 参数配置 + 各类监听回调 ]
	方法：startRecording  pauseRecording  resumeRecording  stopRecording

```
    	Recorder recorder;
        recorder = MsRecorder.wav(
                new File(voicePath),
                new AudioRecordConfig.Default(),
                new PullTransport.Default(
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                Log.d("max  ", "amplitude: " + audioChunk.maxAmplitude());
                            }
                        }
                )
        );

	recorder.startRecording();
	recorder.pauseRecording();
	recorder.resumeRecording();
	recorder.stopRecording();

```


开发平台：android studio

运行平台：android 手机




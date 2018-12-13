# RecordWav

集 `录制`、`播放`、`解析` 于一体的wav文件的工具类。

功能包括：

- 录制
	- 录制`wav`/`pcm`文件。（开始、暂停、继续、完成）
	- 两种模式：`普通模式` (全录制)，`跳过静默区域模式` (只录有声部分)
- 播放`wav`文件。（自定义播放、系统播放）
- 解析本地`wav`文件的信息


![show_recorder](https://github.com/shaoshuai904/RecordWav/blob/master/screens/show_02.png)


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
        implementation 'com.github.shaoshuai904:RecordWav:1.0.2'
    }
```


###  MsRecorder 

	构造参数:[ 文件保存路径 + 参数配置 + 各类监听回调(音频数据块拉取监听/沉默监听) ]
	方法：startRecording  pauseRecording  resumeRecording  stopRecording

```java 
    	Recorder recorder;
        recorder = MsRecorder.wav(
                new File(voicePath),
                new AudioRecordConfig.Default(),
                new PullTransport.Default()
                        .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                Log.e("max  ", "amplitude: " + audioChunk.maxAmplitude());
                            }
                        })

        );

        recorder.startRecording(); 
        recorder.pauseRecording();
        recorder.resumeRecording();
        recorder.stopRecording();

```





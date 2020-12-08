# RecordWav


[![demo](https://img.shields.io/badge/download-demo-blue.svg)](https://github.com/shaoshuai904/RecordWav/blob/master/screens/app_v1.0.3_12.apk)
[![API](https://img.shields.io/badge/API-14%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=14)


集 `录制`、`播放`、`解析` 于一体的wav文件的工具类。

功能包括：

- 录制
	- 录制`wav`/`pcm`文件。（开始、暂停、继续、完成）
	- 两种模式：`普通模式` (全录制)，`跳过静默区域模式` (只录有声部分)
- 播放`wav`文件。（自定义播放、系统播放）
- 解析本地`wav`文件的信息


![show_recorder](/screens/show_02.png)


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
[![](https://jitpack.io/v/shaoshuai904/RecordWav.svg)](https://jitpack.io/#shaoshuai904/RecordWav)

```groovy 
    dependencies {
        implementation 'com.github.shaoshuai904:RecordWav:1.0.3'
    }
```


###  示例代码 

	构造参数:[ 文件保存路径 + 参数配置 + 各类监听回调(音频数据块拉取监听/沉默监听) ]
	方法：startRecording  pauseRecording  resumeRecording  stopRecording


获取普通录音机（java）

```java 
    	Recorder recorder;
        recorder = MsRecorder.wav(
                new File(voicePath),
                new AudioRecordConfig(),
                new PullTransport.Default()
                        .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                Log.e("数据监听", "amplitude: " + audioChunk.maxAmplitude());
                            }
                        })

        );

        recorder.startRecording(); // 开始
        recorder.pauseRecording(); // 暂停
        recorder.resumeRecording(); // 重新开始
        recorder.stopRecording(); // 结束

```

获取降噪录音机，跳过沉默区，只录"有声音"的部分（kotlin）

```java 
        MsRecorder.wav(
                File(getVoicePath()),
                AudioRecordConfig(),
                // AudioRecordConfig(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 44100),
                PullTransport.Noise()
                        // 数据监听
                        .setOnAudioChunkPulledListener { audioChunk ->
                            Log.e("数据监听", "最大值 : ${audioChunk.maxAmplitude()} ")
                        }
                        // 沉默监听
                        .setOnSilenceListener { silenceTime, discardTime ->
                            Log.e("降噪模式", "沉默时间：$silenceTime ,丢弃时间：$discardTime")
                        })
```





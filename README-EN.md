# Android Wav Recorder

[中文](/README.md) | In English

[![API](https://img.shields.io/badge/API-21%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Version](https://jitpack.io/v/shaoshuai904/RecordWav.svg)](https://jitpack.io/#shaoshuai904/RecordWav)

[download demo.apk](/screens/app_v1.2.2.apk?raw=true)

A tool for `recording`, `playing` and `parsing` WAV files.

Features include：

- Recording
    - Record `wav`/`pcm` files. (start, pause, continue, finish)
    - Two modes: `normal mode` (full recording) and `skip silent area mode` (recording only the audio part)
- Play the `wav` file. (custom playback, system playback)
- Parsing local `wav` file information

![show_recorder](/screens/show_02.png)

### Quick use

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
        implementation 'com.github.shaoshuai904:RecordWav:1.2.2'
    }
```

### Sample code

    Constructor: [file saving path + parameter configuration + various listening callbacks (audio data block pull listening / silent listening)]
	Method：startRecording  pauseRecording  resumeRecording  stopRecording

Get ordinary recorder（java）

```java 
    Recorder recorder;
    recorder = MsRecorder.wav(
        new File("savePath"),
        // new AudioRecordConfig(), // use default config
        new AudioRecordConfig(
            MediaRecorder.AudioSource.MIC, // 音频源
            44100, // 采样率，44100、22050、16000、11025 Hz
            AudioFormat.CHANNEL_IN_MONO, // 单声道、双声道/立体声
            AudioFormat.ENCODING_PCM_16BIT // 8/16 bit
        ),
        new PullTransport.Default()
            .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                @Override
                public void onAudioChunkPulled(AudioChunk audioChunk) {
                    Log.d("数据监听", "maxValue: " + audioChunk.maxAmplitude());
                }
            })
    );

    recorder.startRecording(); // start
    recorder.pauseRecording(); // pause
    recorder.resumeRecording(); // resume
    recorder.stopRecording(); // stop
```

Obtain the noise reduction recorder, skip the silence area, and record only the "sound" part（kotlin）

```java 
    MsRecorder.wav(
        File("savePath"),
        AudioRecordConfig(),
        PullTransport.Noise().setOnAudioChunkPulledListener { audioChunk ->
            Log.d("maple_log", "maxValue : ${audioChunk.maxAmplitude()} ")
        }.setOnSilenceListener { silenceTime, discardTime ->
            Log.e("NoiseMode", "Silence time：$silenceTime ,Discard time：$discardTime")
        }
    )
```

###  License

```
Copyright 2018 Shuai Shao

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


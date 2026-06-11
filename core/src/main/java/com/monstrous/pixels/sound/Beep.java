package com.monstrous.pixels.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

// Note:  Gdx.audio.newAudioDevice() is not supported by gdx-teavm which would allow to synthesize PCM samples
// into a float array and play them. We can only play sounds from sound files.
//
public class Beep implements Disposable {

    AudioDevice device;
    float[] floatPCM = new float[44100];
    Sound beep;
    Music music;

    public Beep() {

        //device = Gdx.audio.newAudioDevice(44100, true);

        beep = Gdx.audio.newSound(Gdx.files.internal("sound/beep-02.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music/Elijah_K - Game.mp3"));
    }


    public void beep(){
//        for(int i = 0; i < 44100; i++){
//            floatPCM[i] = 100f * (float)Math.sin((float)i/256f);
//        }
//        device.writeSamples(floatPCM, 0, floatPCM.length);

        beep.play();
    }

    public void startMusic(){
        music.setLooping(true);
        music.play();
    }

    public void stopMusic(){
        music.stop();
    }

    public boolean isMusicPlaying(){
        return music.isPlaying();
    }

    @Override
    public void dispose() {
        device.dispose();
    }


}

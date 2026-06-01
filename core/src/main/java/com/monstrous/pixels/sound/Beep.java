package com.monstrous.pixels.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.utils.Disposable;

public class Beep implements Disposable {

    AudioDevice device;
    float[] floatPCM = new float[44100];

    public Beep() {
        device = Gdx.audio.newAudioDevice(44100, true);
    }


    public void beep(){
        for(int i = 0; i < 44100; i++){
            floatPCM[i] = 100f * (float)Math.sin((float)i/256f);
        }
        device.writeSamples(floatPCM, 0, floatPCM.length);
    }

    @Override
    public void dispose() {
        device.dispose();
    }


}

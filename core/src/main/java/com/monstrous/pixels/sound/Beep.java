package com.monstrous.pixels.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

// Note:  Gdx.audio.newAudioDevice() is not supported by gdx-teavm which would allow to synthesize PCM samples
// into a float array and play them. We can only play sounds from sound files.
//
public class Beep implements Disposable {

    Sound beep;
    Music music;

    public Beep() {
        beep = Gdx.audio.newSound(Gdx.files.internal("sound/beep-02.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music/Elijah_K - Game.mp3"));
    }


    public void beep(){
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
        music.dispose();
    }


}

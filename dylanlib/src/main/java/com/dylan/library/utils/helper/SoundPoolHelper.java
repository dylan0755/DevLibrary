package com.dylan.library.utils.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.dylan.library.R;
import com.dylan.library.media.Sound;

/**
 * Author: Dylan
 * Date: 2022/07/18
 * Desc:
 */
public class SoundPoolHelper {
    private  SoundPool mSoundPool;


    public SoundPoolHelper(){
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,100);
    }


    public void play(Context context,int resId){
       int soundID= mSoundPool.load(context, resId,1);
        mSoundPool.play(soundID,1.0f,1.0f,1,0,1.0f);
    }

    public void release(){
        if (mSoundPool!=null){
            mSoundPool.release();
        }
    }



}

package com.Fpoly.music143.Fragment.Music.Notification;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Fpoly.music143.Activity.MainActivity;
import com.Fpoly.music143.Fragment.Music.PlayMusicFragment;
import com.Fpoly.music143.Model.Song;
import com.Fpoly.music143.R;

import java.io.IOException;


public class MusicService extends Service   {
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREVIOUS";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_GETDATA = "ACTION_GETDATA";
    ActionPlaying actionPlaying;
    public static Boolean isPlaying  = false ;
    public static Song song   ;
    private IBinder mBinder = new MyBinder() ;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind","Method");
        return mBinder;
    }

    public class MyBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("myActionName");
        if (intent !=null && intent.getExtras()!=null) {
            song = intent.getParcelableExtra("Songs");
        }
        if (actionName != null){
            switch (actionName){
/*                case ACTION_GETDATA:
                    if (intent !=null && intent.getExtras()!=null) {
                        song = intent.getParcelableExtra("Songs");
                        Log.d("song.getLink", song.getLink());
                        if (actionPlaying != null){
                            Bundle bundle = intent.getExtras() ;
                            bundle.putParcelable("Songs",song);
                            bundle.putInt("fragment",4);
                            getApplicationContext().startService(intent) ;
                            actionPlaying.getDataFromIntent();
                        }
                    }
                    break;*/
                case ACTION_PLAY:
                    if (actionPlaying != null){
                        actionPlaying.playClicked();
                    }
                    break;
                case ACTION_NEXT:
                    if (actionPlaying != null){
                        actionPlaying.nextClicked();
                    }
                    break;
                case ACTION_PREV:
                    if (actionPlaying != null){
                        actionPlaying.prevClicked();
                    }
                    break;

            }
        }
        return START_STICKY;
    }
    public void setCallBack(ActionPlaying actionPlaying){
            this.actionPlaying = actionPlaying;
    }
}

package com.Fpoly.music143.Fragment.Music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.Fpoly.music143.Model.Song;
import com.Fpoly.music143.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class BackgroundSoundService extends Service {
    private static final String TAG = "BackgroundSoundService";
    private final IBinder mBinder = (IBinder) new BinderExample();
    MediaPlayer mediaPlayer = new MediaPlayer();
    public static Song song  ;
    public static Boolean isPlaying  = false ;
    public  Song baihat = new Song("https://photo-zmp3.zadn.vn/banner/b/b/1/5/bb15a09e6bcd58040be6aea7684034a3.jpg","https://firebasestorage.googleapis.com/v0/b/humanresourcemanager-57f05.appspot.com/o/Vi%E1%BB%87t%20Nam%2FDong%20Nhi%2FGi%E1%BA%ADn%20L%C3%B2ng.mp3?alt=media&token=ad691f0d-d67c-48db-9efd-275c78c61e1c","sang","T01","S0016","nhachay",16)  ;





    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true); // Set looping
        mediaPlayer.setVolume(100,100);
        Log.i(TAG, "onCreate() , service started...");
        super.onCreate();
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent !=null && intent.getExtras()!=null) {
            song = intent.getParcelableExtra("Songs");
            Log.d(TAG, song.getLink());

        }
        Log.i(TAG, "onStartCommand()");
        if (intent.getAction().equals("PLAY")) {
            playmusic(song);
        } if (intent.getAction().equals("PAUSE")) {
            onPause();
        }  if (intent.getAction().equals("STOP")) {
            onStop() ;
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "onBind", Toast.LENGTH_SHORT).show();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "onUnbind", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }


    @Override
    public void onRebind(Intent intent) {
        Toast.makeText(this, "onRebind", Toast.LENGTH_SHORT).show();
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
//            player.release();
        Log.i(TAG, "onCreate() , service stopped...");
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }


    public class BinderExample extends Binder {
        public BackgroundSoundService getService(){
            return BackgroundSoundService.this;
        }
    }
    // ----------- - --   function - - --------------------------

    public void playmusic(Song song) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer = new MediaPlayer() ;
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(song.getLink());
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            Log.i(TAG, "onPause()");
        }
    }

    public void onStop() {
        mediaPlayer.stop();
        mediaPlayer.release();
        Log.i(TAG, "onStop() , service stopped...");
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    //Hàm format thời gian
    public int seekbar(){

//        txttotaltimesong.setText(s));
//        sktime.setMax(mediaPlayer.getDuration());
        return (mediaPlayer.getDuration()) ;
    }

    public String TimeSong() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return (simpleDateFormat.format(mediaPlayer.getDuration())) ;
    }


   /* @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        MyReceiver myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MediaService.MY_ACTION);
        this.registerReceiver(myReceiver, intentFilter);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String datapa = arg1.getStringExtra("DATAPASSED");
            String datap2 = arg1.getStringExtra("ALBUM_DATA");
            Log.d("TITLE OF SONG", datapa);
            Log.d("ALBUM", datap2);
            textView.setText(datapa);
            textView1.setText(datap2);
        }
    }*/




}

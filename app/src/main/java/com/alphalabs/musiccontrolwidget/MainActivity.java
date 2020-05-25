package com.alphalabs.musiccontrolwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MusicIntentReceiver myReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Started app", Toast.LENGTH_SHORT).show();
        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (!manager.isBluetoothA2dpOn()) {
            //audio is currently being routed to bluetooth -> bluetooth is connected
            Intent intent2 = new Intent(KUSTOM_ACTION);
            intent2.putExtra(KUSTOM_ACTION_EXT_NAME, "foo");
            intent2.putExtra(KUSTOM_ACTION_VAR_NAME, "myvar");
            intent2.putExtra(KUSTOM_ACTION_VAR_VALUE, "");
            sendBroadcast(intent2);
        }
        if (manager.isMusicActive()) {
            // Something is being played.
            IntentFilter iF = new IntentFilter();

            // Read action when music player changed current song
            // I just try it with stock music player form android

            // stock music player
            iF.addAction("com.android.music.metachanged");
            registerReceiver(mReceiver, iF);
            myReceiver = new MusicIntentReceiver();
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(myReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(myReceiver, filter);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
//            Toast.makeText(MainActivity.this, artist + ":" + album + ":" + track, Toast.LENGTH_SHORT).show();
            Log.d("Music", artist + ":" + album + ":" + track);
        }
    };


    final String KUSTOM_ACTION = "org.kustom.action.SEND_VAR";
    public static final String KUSTOM_ACTION_EXT_NAME = "org.kustom.action.EXT_NAME";
    public static final String KUSTOM_ACTION_VAR_NAME = "org.kustom.action.VAR_NAME";
    public static final String KUSTOM_ACTION_VAR_VALUE = "org.kustom.action.VAR_VALUE";
    public static final String KUSTOM_ACTION_VAR_NAME_ARRAY = "org.kustom.action.VAR_NAME_ARRAY";
    public static final String KUSTOM_ACTION_VAR_VALUE_ARRAY = "org.kustom.action.VAR_VALUE_ARRAY";

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())) {
                Log.d("HeadSet","Changed");
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Toast.makeText(MainActivity.this, device.getName(), Toast.LENGTH_SHORT).show();

                Intent intent2 = new Intent(KUSTOM_ACTION);
                intent2.putExtra(KUSTOM_ACTION_EXT_NAME, "foo");
                intent2.putExtra(KUSTOM_ACTION_VAR_NAME, "myvar");
                intent2.putExtra(KUSTOM_ACTION_VAR_VALUE, device.getName());
                sendBroadcast(intent2);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        long eventtime = SystemClock.uptimeMillis();
                        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                        mAudioManager.dispatchMediaKeyEvent(upEvent);
                        mAudioManager.dispatchMediaKeyEvent(downEvent);
                    }
                }, 3500);

            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
                Log.d("HeadSet","Disconnected");
                Intent intent2 = new Intent(KUSTOM_ACTION);
                intent2.putExtra(KUSTOM_ACTION_EXT_NAME, "foo");
                intent2.putExtra(KUSTOM_ACTION_VAR_NAME, "myvar");
                intent2.putExtra(KUSTOM_ACTION_VAR_VALUE, "");
                sendBroadcast(intent2);
            }
        }
    }

    public void change(View v){
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if(true) {
            long eventtime = SystemClock.uptimeMillis();
            if(((TextView)v).getText().equals("Pause")) {
            Log.d("MS",((TextView)v).getText()+"");
                KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                mAudioManager.dispatchMediaKeyEvent(upEvent);
                mAudioManager.dispatchMediaKeyEvent(downEvent);
            }
        if(((TextView)v).getText().equals("Play")) {
            Log.d("MS",((TextView)v).getText()+"");
            Intent intent = new
                    Intent("com.spotify.mobile.android.ui.widget.PLAY");
            intent.putExtra("paused",true);
            intent.setPackage("com.spotify.music");
            sendBroadcast(intent);
        }

            /*NEXT*/
        if(((TextView)v).getText().equals("Next")) {
            Log.d("MS",((TextView)v).getText()+"");
            KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,   KeyEvent.KEYCODE_MEDIA_NEXT, 0);
            mAudioManager.dispatchMediaKeyEvent(downEvent);
        }

            /*PREVIOUS*/
        if(((TextView)v).getText().equals("Prev")) {
            Log.d("MS",((TextView)v).getText()+"");
            KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
            mAudioManager.dispatchMediaKeyEvent(downEvent);
        }
        }
    }
}

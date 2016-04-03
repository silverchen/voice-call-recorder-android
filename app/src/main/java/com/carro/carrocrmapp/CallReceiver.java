package com.carro.carrocrmapp;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by LENOVO Y500 on 23/3/2016.
 */
public class CallReceiver extends BroadcastReceiver {
    private static long lastResume = 0;
    private static String lastState = "";
    private static final int MIN_RESUME_DIFF_MS = 1000000;
    private static boolean wasRinging = false;
    //String inCall, outCall;
    private static MediaRecorder recorder;
    private static boolean recordstarted = false;
    private static String filePath = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("IncomingBroadcastReceiver: onReceive: ", "flag1");

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        //prevent multiple calls of onReceive
        long now = System.currentTimeMillis();
        if (now - lastResume > MIN_RESUME_DIFF_MS || !lastState.equals(state) ) {
            lastResume = now;
            lastState = state;

            //Log.d("IncomingBroadcastReceiver: onReceive: ", state);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Log.d("Ringing", "Phone is ringing");

                //inCall = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                wasRinging = true;

                Intent i = new Intent(context, CallReceiverActivity.class);
                i.putExtras(intent);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                CallReceiverActivity._intent = intent;

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

//                abortBroadcast();
                context.startActivity(i);
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.d("Answered", "Phone is answered");

                if (wasRinging == true) {
                    File audiofile = null;
                    Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();

                    //String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                    File outDir = new File(Environment.getExternalStorageDirectory(), "/carrocrmapp");
                    if (!outDir.exists()) {
                        outDir.mkdirs();
                    }
                    String file_name = "Record";

                    try {
                        audiofile = File.createTempFile(file_name, ".mp3", outDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                    filePath = audiofile.getAbsolutePath();

                    recorder = new MediaRecorder();

                    recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                    //recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                    try {
                        recorder.prepare();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recorder.start();
                    recordstarted = true;
                }
            }else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.d("Ended", "Phone is ended");

                saveData(context, CallReceiverActivity._intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER), filePath);
                wasRinging = false;

                if (recordstarted) {
                    recorder.stop();
                    recordstarted = false;
                    filePath = "";

/*                    if (!filePath.equals("")){
                        saveFilePath(context, filePath);
                    }*/
                }

                Intent i = new Intent(context, CallReceiverActivity.class);
                i.putExtras(intent);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                CallReceiverActivity._intent = null;

                context.startActivity(i);
            }
        }
    }

/*    private void saveFilePath(Context context, String path){
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm myRealm = Realm.getInstance(realmConfig);

        CallHistory call = myRealm.where(CallHistory.class)
                .findAll().last();

        myRealm.beginTransaction();

        call.setFilePath(path);

        myRealm.commitTransaction();
    }*/

    private void saveData(Context context, String phoneNumber, String path){
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm myRealm = Realm.getInstance(realmConfig);

        long seconds = new Date().getTime();

        myRealm.beginTransaction();

        CallHistory call = myRealm.createObject(CallHistory.class);
        call.setPhoneNumber(phoneNumber);
        call.setDateTime(seconds);

        if (!path.equals("")){
            call.setFilePath(path);
        }

        myRealm.commitTransaction();
    }
}

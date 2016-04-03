package com.carro.carrocrmapp;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import nl.changer.audiowife.AudioWife;

public class CallReceiverActivity extends AppCompatActivity {
    public static Intent _intent;
    //public static Intent _oldIntent;
    Button btnAnswer, btnReject;
    TextView tvPhoneNo, tvCallCount, tvLastCall;
    LinearLayout llMediaPlayer;
    ListView lvRecordedCalls;
    Realm myRealm;
    ArrayList<String> arrayFilePaths = new ArrayList<String>();
    ArrayList<CallHistory> arrayCalls = new ArrayList<CallHistory>();
    //List<File> files;
    private static long lastResume = 0;
    private static final int MIN_RESUME_DIFF_MS = 5000;
//    private static final int REQUEST_CODE = 0;
    Boolean hasPlayerSetup = false;
    ArrayAdapter adapter;
    //AudioWife player;
//    private DevicePolicyManager mDPM;
//    private ComponentName mAdminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_callreceiver);

        btnAnswer = (Button) findViewById(R.id.btnAnswer);
        btnReject = (Button) findViewById(R.id.btnReject);
        tvPhoneNo = (TextView) findViewById(R.id.tvPhoneNo);
        tvCallCount = (TextView) findViewById(R.id.tvCallCount);
        tvLastCall = (TextView) findViewById(R.id.tvLastCall);
        llMediaPlayer = (LinearLayout) findViewById(R.id.llMediaPlayer);
        lvRecordedCalls = (ListView) findViewById(R.id.lvRecordedCalls);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        myRealm = Realm.getInstance(realmConfig);

        //files = getListFiles(new File(Environment.getExternalStorageDirectory(), "/carrocrmapp"));
        retrieveFiles();

        adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, arrayFilePaths);
        lvRecordedCalls.setAdapter(adapter);

        processCallInfo();

//        initiateDevicePolicyManager();

        if (arrayCalls.size() > 0){
            AudioWife.getInstance().init(CallReceiverActivity.this, Uri.parse(arrayCalls.get(0).getFilePath())).useDefaultUi(llMediaPlayer, getLayoutInflater());
            hasPlayerSetup = true;
        }

        lvRecordedCalls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                AudioWife.getInstance().pause();
                AudioWife.getInstance().release();

                if (hasPlayerSetup) {
                    AudioWife.getInstance().init(CallReceiverActivity.this, Uri.parse(arrayCalls.get(position).getFilePath()));
                }

            }
        });

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                Intent answer = new Intent(Intent.ACTION_MEDIA_BUTTON);
                answer.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                sendOrderedBroadcast(answer, null);

                // froyo and beyond trigger on buttonUp instead of buttonDown
                Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
                buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");*/

/*                answerPhoneHeadsethook(context, intent);*/

/*                Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
                headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
                headSetUnPluggedintent.putExtra("state", 0);
                headSetUnPluggedintent.putExtra("name", "Headset");
                try {
                    sendOrderedBroadcast(headSetUnPluggedintent, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
                buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                getApplicationContext().sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();


        //prevent multiple calls of OnResume on android
//        long now = System.currentTimeMillis();
//        if (now - lastResume > MIN_RESUME_DIFF_MS) {
//            lastResume = now;
            retrieveFiles();
            processCallInfo();
//        }
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            Intent intent = new Intent(CallReceiverActivity.this, TService.class);
            startService(intent);
        }
    }*/

/*    private void initiateDevicePolicyManager() {
        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(this.DEVICE_POLICY_SERVICE);
            mAdminName = new ComponentName(this, DeviceAdmin.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                // mDPM.lockNow();
                // Intent intent = new Intent(MainActivity.this,
                // TrackDeviceService.class);
                // startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

/*    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();

        arrayFilePaths.clear();

        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().endsWith(".mp3")){
                    inFiles.add(file);
                    arrayFilePaths.add(file.getAbsolutePath());
                }
            }
        }
        return inFiles;
    }*/

    private void processCallInfo(){
        if (_intent != null){
            //if (_oldIntent != _intent){
                //_oldIntent = _intent;
                String phoneNum = _intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                tvPhoneNo.setText(PhoneNumberUtils.formatNumber(phoneNum));

                retrieveData(phoneNum);
            //}
        }
    }

    private void retrieveFiles(){
        RealmResults<CallHistory> result = myRealm.where(CallHistory.class)
                .isNotNull(CallHistory.kCOL_FILE_PATH)
                .findAllSorted(CallHistory.kCOL_DATE_TIME, Sort.DESCENDING);

        arrayFilePaths.clear();
        arrayCalls.clear();

        if (result.size() > 0) {
            for(CallHistory c:result) {
                arrayFilePaths.add(PhoneNumberUtils.formatNumber(c.phoneNumber) + "   " + getDate(c.dateTime, "d/MM/yyyy HH:mm:ss"));
                arrayCalls.add(c);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();

            if (arrayCalls.size() > 0 && !hasPlayerSetup){
                AudioWife.getInstance().init(CallReceiverActivity.this, Uri.parse(arrayCalls.get(0).getFilePath())).useDefaultUi(llMediaPlayer, getLayoutInflater());
                hasPlayerSetup = true;
            }
        }
    }

    private void retrieveData(String phoneNumber){
        RealmResults<CallHistory> result = myRealm.where(CallHistory.class)
                .equalTo(CallHistory.kCOL_PHONE_NUM, phoneNumber)
                .findAllSorted(CallHistory.kCOL_DATE_TIME, Sort.DESCENDING);

        int numOfCall = result.size();

        tvCallCount.setText(String.valueOf(numOfCall));

        if (numOfCall > 0) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(result.first().dateTime * 1000L);
//            String dateString = String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", calendar);


//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(result.first().dateTime * 1000L);
//            SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy h:mm a");
//            System.out.println();


//            SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy HH:mm");
//            String dateString = formatter.format(new Date(result.first().dateTime * 1000L));
            tvLastCall.setText(getDate(result.first().dateTime, "d/MM/yyyy HH:mm:ss"));
        }else{
            tvLastCall.setText("No record");
        }
    }

    private String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

/*    public void answerPhoneHeadsethook(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d("@", "Incoming call from: " + number);
            Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
            buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
            try {
                context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
                Log.d("@", "ACTION_MEDIA_BUTTON broadcasted...");
            }
            catch (Exception e) {
                Log.d("@", "Catch block of ACTION_MEDIA_BUTTON broadcast !");
            }

            Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
            headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged  1 = Headset with microphone 2 = Headset without microphone
            headSetUnPluggedintent.putExtra("name", "Headset");
            // TODO: Should we require a permission?
            try {
                context.sendOrderedBroadcast(headSetUnPluggedintent, null);
                Log.d("@", "ACTION_HEADSET_PLUG broadcasted ...");
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                Log.d("@", "Catch block of ACTION_HEADSET_PLUG broadcast");
                Log.d("@", "Call Answered From Catch Block !!");
            }
            Log.d("@", "Answered incoming call from: " + number);
        }
        Log.d("@", "Call Answered using headsethook");
    }*/
}

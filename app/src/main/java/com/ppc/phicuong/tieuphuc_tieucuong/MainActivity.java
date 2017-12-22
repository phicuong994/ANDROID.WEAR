package com.ppc.phicuong.tieuphuc_tieucuong;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

public class MainActivity extends WearableActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    ListView llist;
    String wifis[];
    TextView TextDEN1, TextPWM1, TextDEN2, TextPWM2, TextDEN3, TextPWM3, TextDEN4, TextPWM4;
    ImageView imageDEN1, imageDEN2, imageDEN3, imageDEN4;
    Switch swOnOff1, swOnOff2, swOnOff3, swOnOff4;
    SeekBar seekBar1, seekBar2, seekBar3, seekBar4;
    Button btnThoat;
    Firebase myFirebase;
    EditText pass;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Log.e(TAG, "savedInstanceState is null");
        } else {
            Log.e(TAG, "savedInstanceState is not null");
        }
        setContentView(R.layout.activity_main);

        mainWifiObj = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mainWifiObj.setWifiEnabled(true);
        wifiReciever = new WifiScanReceiver();
        mainWifiObj.startScan();
        llist = (ListView) findViewById(R.id.list);


        try {
            llist.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0,
                                                View arg1,
                                                int arg2,
                                                long arg3) {
                            //đối số arg2 là vị trí phần tử trong Data Source (arr)
                            //  txt.setText("position :"+arg2+" ; value ="+arr[arg2]);
                            //Toast.makeText(MainActivity.this, "position :" + arg2 + " ; value =" + arr[arg2], Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this,String.valueOf( arg2), Toast.LENGTH_SHORT).show();
                            String selectedFromList = (llist.getItemAtPosition(arg2).toString());
                            //Toast.makeText(MainActivity.this, selectedFromList, Toast.LENGTH_SHORT).show();
                            //parent.getAdapter().getItem(position);
                            connectToWifi(selectedFromList);
                        }
                    });
        }catch (Exception e4)
        {
            Toast.makeText(MainActivity.this, e4.toString(), Toast.LENGTH_SHORT).show();
        }


        // Enables Always-on
        //setAmbientEnabled();
    }
    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

                    //Toast.makeText(MainActivity.this, "b5", Toast.LENGTH_SHORT).show();
                    // Do something with granted permission

                } else {
                    //Toast.makeText(MainActivity.this, "already has permissions", Toast.LENGTH_SHORT).show();
                    List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
                    boolean checkEnableWifi=false;
                    wifis = new String[wifiScanList.size()];
                    for(int i = 0; i < wifiScanList.size(); i++){
                        wifis[i] = ((wifiScanList.get(i)).toString());
                    }
                    String filtered[] = new String[wifiScanList.size()];
                    int counter = 0;
                    for (String eachWifi : wifis) {
                        String[] temp = eachWifi.split(",");

                        filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength

                        counter++;

                    }
                    List<WifiConfiguration> wificonfiged=mainWifiObj.getConfiguredNetworks();
                    for(int i = 0; i < wifiScanList.size(); i++){
                        for(int j =0; j<wificonfiged.size(); j++){
                            String scan ="\""+wifiScanList.get(i).SSID+"\"";
                            String configed = wificonfiged.get(j).SSID;

                            if(scan.equals(configed)){
                                Toast.makeText(MainActivity.this, " Wifi đã được lưu:"+ wificonfiged.get(j).SSID, Toast.LENGTH_SHORT).show();
                                int ID=wificonfiged.get(j).networkId;
                                while(checkEnableWifi==false){
                                    checkEnableWifi=mainWifiObj.enableNetwork(ID, true);}
                                mainWifiObj.reconnect();
                                if(checkEnableWifi!=false){

                                    Toast.makeText(MainActivity.this, "Đã kết nối với Wifi:"+ wificonfiged.get(j).SSID, Toast.LENGTH_SHORT).show();

                                } Controlled();
                            }
                        }
                    }
                    if(checkEnableWifi!=true) {
                        llist.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item,R.id.label, filtered));
                    }
                }
            } catch (Exception e3) {
                Toast.makeText(MainActivity.this, e3.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void finallyConnect(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
        mainWifiObj.setWifiEnabled(true);
        boolean checkEnableWifi=false;
        // remember id
        int netId = mainWifiObj.addNetwork(wifiConfig);
        mainWifiObj.disconnect();
        while(checkEnableWifi==false) {
            checkEnableWifi = mainWifiObj.enableNetwork(netId, true);
        }
        mainWifiObj.reconnect();
        if(checkEnableWifi!=false){
            Toast.makeText(MainActivity.this, "Đã kết nối với Wifi:"+ wifiConfig.SSID, Toast.LENGTH_SHORT).show();

        }
    }

    private void connectToWifi(final String wifiSSID) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect);
        dialog.setTitle("Connect to Network");
        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);

        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        pass = (EditText) dialog.findViewById(R.id.textPassword);
        textSSID.setText(wifiSSID);

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();
                finallyConnect(checkPassword, wifiSSID);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void Controlled() {

        final Dialog dialogled = new Dialog(this);
        dialogled.setContentView(R.layout.control_led);
        dialogled.setTitle("Control led ");
        swOnOff1 = (Switch) dialogled.findViewById(R.id.swOnOff1);
        swOnOff2 = (Switch) dialogled.findViewById(R.id.swOnOff2);
        swOnOff3 = (Switch) dialogled.findViewById(R.id.swOnOff3);
        swOnOff4 = (Switch) dialogled.findViewById(R.id.swOnOff4);
        seekBar1 = (SeekBar) dialogled.findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) dialogled.findViewById(R.id.seekBar2);
        seekBar3 = (SeekBar) dialogled.findViewById(R.id.seekBar3);
        seekBar4 = (SeekBar) dialogled.findViewById(R.id.seekBar4);
        TextDEN1 = (TextView) dialogled.findViewById(R.id.textDEN1);
        TextDEN2 = (TextView) dialogled.findViewById(R.id.textDEN2);
        TextDEN3 = (TextView) dialogled.findViewById(R.id.textDEN3);
        TextDEN4 = (TextView) dialogled.findViewById(R.id.textDEN4);
        TextPWM1 = (TextView) dialogled.findViewById(R.id.textPWM1);
        TextPWM2 = (TextView) dialogled.findViewById(R.id.textPWM2);
        TextPWM3 = (TextView) dialogled.findViewById(R.id.textPWM3);
        TextPWM4 = (TextView) dialogled.findViewById(R.id.textPWM4);
        imageDEN1 = (ImageView) dialogled.findViewById(R.id.imageDEN1);
        imageDEN2 = (ImageView) dialogled.findViewById(R.id.imageDEN2);
        imageDEN3 = (ImageView) dialogled.findViewById(R.id.imageDEN3);
        imageDEN4 = (ImageView) dialogled.findViewById(R.id.imageDEN4);
        btnThoat  = (Button)     dialogled.findViewById(R.id.buttonThoat);
        Firebase.setAndroidContext(this);
        myFirebase = new Firebase("https://savevalueonfirebase.firebaseio.com");
//*********************************************************************************************************
        //*******************TEXTVIEW UPDATE TRẠNG THÁI ĐÈN********************************************************
        // ******************************************************************************************************
        myFirebase.child("DEN 1").child("STATE").child("TRANG THAI").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("SANG")) {
                    imageDEN1.setImageResource(R.drawable.hinh1);
                    //        Toast.makeText(MainActivity.this, "Đã Cập Nhật", Toast.LENGTH_SHORT).show();

                } else if (dataSnapshot.getValue().equals("TAT")) {
                    imageDEN1.setImageResource(R.drawable.hinh2);
                    //        Toast.makeText(MainActivity.this, "Đã Cập Nhật", Toast.LENGTH_SHORT).show();
                }
                TextDEN1.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        myFirebase.child("DEN 2").child("STATE").child("TRANG THAI").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("SANG")) {
                    imageDEN2.setImageResource(R.drawable.hinh1);
                    //       Toast.makeText(MainActivity.this, "Đã Cập Nhật", Toast.LENGTH_SHORT).show();

                } else if (dataSnapshot.getValue().equals("TAT")) {
                    imageDEN2.setImageResource(R.drawable.hinh2);
                    //       Toast.makeText(MainActivity.this, "Đã Cập Nhật", Toast.LENGTH_SHORT).show();
                }
                TextDEN2.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        myFirebase.child("DEN 3").child("STATE").child("TRANG THAI").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("SANG")) {
                    imageDEN3.setImageResource(R.drawable.hinh1);
                    Toast.makeText(MainActivity.this, "Đã Cập Nhật", Toast.LENGTH_SHORT).show();

                } else if (dataSnapshot.getValue().equals("TAT")) {
                    imageDEN3.setImageResource(R.drawable.hinh2);
                    Toast.makeText(MainActivity.this, "Đã Cập Nhật", Toast.LENGTH_SHORT).show();
                }
                TextDEN3.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        myFirebase.child("DEN 4").child("STATE").child("TRANG THAI").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("SANG")) {
                    imageDEN4.setImageResource(R.drawable.hinh1);
                    Toast.makeText(MainActivity.this, "Đã Cập Nhật", Toast.LENGTH_SHORT).show();

                } else if (dataSnapshot.getValue().equals("TAT")) {
                    imageDEN4.setImageResource(R.drawable.hinh2);
                    Toast.makeText(MainActivity.this, "Đã Cập Nhật", Toast.LENGTH_SHORT).show();
                }
                TextDEN4.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        //*********************************************************************************************************
        //*******************TEXTVIEW UPDATE ĐỘ SÁNG ĐÈN*********************************************************
        // ******************************************************************************************************
        myFirebase.child("DEN 1").child("STATE").child("DO SANG").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextPWM1.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        myFirebase.child("DEN 2").child("STATE").child("DO SANG").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextPWM2.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        myFirebase.child("DEN 3").child("STATE").child("DO SANG").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextPWM3.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        myFirebase.child("DEN 4").child("STATE").child("DO SANG").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextPWM4.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        //*****************************************************************************************************
        //*******************SWITCH BẬT TẮT ĐÈN****************************************************************
        // **************************************************************************************************
        swOnOff1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myFirebase.child("DEN 1").child("STATE").child("TRANG THAI").setValue("SANG");
                } else {
                    myFirebase.child("DEN 1").child("STATE").child("TRANG THAI").setValue("TAT");
                }
            }
        });
        swOnOff2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myFirebase.child("DEN 2").child("STATE").child("TRANG THAI").setValue("SANG");
                } else {
                    myFirebase.child("DEN 2").child("STATE").child("TRANG THAI").setValue("TAT");
                }
            }
        });
        swOnOff3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myFirebase.child("DEN 3").child("STATE").child("TRANG THAI").setValue("SANG");
                } else {
                    myFirebase.child("DEN 3").child("STATE").child("TRANG THAI").setValue("TAT");
                }
            }
        });
        swOnOff4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myFirebase.child("DEN 4").child("STATE").child("TRANG THAI").setValue("SANG");
                    imageDEN4.setImageResource(R.drawable.hinh1);
                } else {
                    myFirebase.child("DEN 4").child("STATE").child("TRANG THAI").setValue("TAT");
                }
            }
        });
        //*********************************************************************************************************
        //*******************THANH ĐIỀU CHỈNH ĐỘ SÁNG ĐÈN DÙNG SEEKBAR*********************************************
        // ******************************************************************************************************
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value1;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_value1 = progress;
                myFirebase.child("DEN 1").child("STATE").child("DO SANG").setValue(String.valueOf((seekBar1.getProgress())));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Độ Sáng Thiết Lập Đèn 1: " + progress_value1, Toast.LENGTH_SHORT).show();
            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value2;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_value2 = progress;
                myFirebase.child("DEN 2").child("STATE").child("DO SANG").setValue(String.valueOf((seekBar2.getProgress())));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Độ Sáng Thiết Lập Đèn 2: " + progress_value2, Toast.LENGTH_SHORT).show();
            }
        });
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value3;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_value3 = progress;
                myFirebase.child("DEN 3").child("STATE").child("DO SANG").setValue(String.valueOf((seekBar3.getProgress())));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Độ Sáng Thiết Lập Đèn 3: " + progress_value3, Toast.LENGTH_SHORT).show();
            }
        });
        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value4;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_value4 = progress;
                myFirebase.child("DEN 4").child("STATE").child("DO SANG").setValue(String.valueOf((seekBar4.getProgress())));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Độ Sáng Thiết Lập Đèn 4: " + progress_value4, Toast.LENGTH_SHORT).show();
            }
        });

        dialogled.show();
    }
    public void clickExit(View v)
    {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "b3", Toast.LENGTH_SHORT).show();
            // Do something with granted permission
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            wifis = new String[wifiScanList.size()];
            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).toString());
            }
            String filtered[] = new String[wifiScanList.size()];
            int counter = 0;
            for (String eachWifi : wifis) {
                String[] temp = eachWifi.split(",");

                filtered[counter] = temp[0].substring(5).trim();//+"\n" + temp[2].substring(12).trim()+"\n" +temp[3].substring(6).trim();//0->SSID, 2->Key Management 3-> Strength

                counter++;

            }
            try {
                llist.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item,R.id.label, filtered));
            }catch (Exception e6)
            {
                Toast.makeText(MainActivity.this, e6.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}

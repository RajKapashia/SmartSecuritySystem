package com.example.smartsecuritysystem;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BluetoothDeatails extends AppCompatActivity {
TextView mac;
TextView rssi;
TextView name;
TextView paired;
TextView version;
ImageView imageView;
String mac_str="";
String rssi_str;
String paired_str;
String name_str;
String image;
Device device;
int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_deatails2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       mac=(TextView)findViewById(R.id.mac_detail);
        rssi=(TextView)findViewById(R.id.rssi_detail);
        name=(TextView)findViewById(R.id.name_detail);
        paired=(TextView)findViewById(R.id.paired_detail);
        version=(TextView)findViewById(R.id.version_detail);
        imageView=(ImageView)findViewById(R.id.img);
        paired_str = getIntent().getExtras().get("PAIRED").toString();
        name_str = getIntent().getExtras().get("NAME").toString();
       mac_str = getIntent().getExtras().get("MAC").toString();
        rssi_str = getIntent().getExtras().get("RSSI").toString();
        image=getIntent().getExtras().get("Image").toString();
       mac.setText(mac_str);
       version.setText(BuildConfig.VERSION_NAME);
        imageView.setImageResource(Integer.parseInt(image));
        name.setText(name_str);
        paired.setText(paired_str);
        rssi.setText(rssi_str);
    }

}

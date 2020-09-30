package com.example.smartsecuritysystem.ui.home;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartsecuritysystem.Device;
import com.example.smartsecuritysystem.DeviceAdapter;
import com.example.smartsecuritysystem.R;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private HomeViewModel homeViewModel;
    public static Context contextOfApplication;

    private int ACCESS_COARSE_LOCATION_CODE = 1;

    private int REQUEST_ENABLE_BLUETOOTH = 2;

    private int SCAN_MODE_ERROR = 3;

    private boolean bluetoothReceiverRegistered;

    private boolean scanModeReceiverRegistered;

    private SwipeRefreshLayout swipeRefreshLayout;

    TextView textDeviceName;

    TextView textDeviceMac;

    TextView textDevicePaired;

    TextView textDeviceSignal;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice mBluetoothDevice;

    private RecyclerView recyclerView;

    private DeviceAdapter deviceAdapter;

    private BluetoothLeScanner mBluetoothLeScanner = null;
    View v;
    String data = " ";
    HomeFragment context = this;
    Activity activity = this.getParent();

    private Activity getParent() {
        return this.activity;
    }

    private List<Device> devices = new ArrayList<>();
    private Handler mHandler = new Handler();
    private final Handler handler = new Handler();
    Runnable scanTask = new Runnable() {
        @RequiresApi(api = LOLLIPOP)
        @Override
        public void run() {
            mHandler.postDelayed(this, 3000);
            scanBluetooth();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        v  = inflater.inflate(R.layout.fragment_devices,container,false);


        contextOfApplication = getApplicationContext();
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        initView();
        //Request Permission
       /* if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Get permission", LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_CODE);
            }
        }*/

        initData();
        handler.post(scanTask);
        deviceAdapter.notifyDataSetChanged();


return v;
    }



    private Context getApplicationContext() {
        return contextOfApplication;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(scanTask);
    }

  /*  @Override
    public void onRestart() {
        super.onRestart();
        handler.post(scanTask);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothReceiverRegistered) {
            //unregisterReceiver(mScanCallback);
        }
        if (scanModeReceiverRegistered) {
            getActivity().unregisterReceiver(scanModeReceiver);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }



    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        textDeviceName = (TextView) v.findViewById(R.id.text_name);
        textDeviceMac = (TextView) v.findViewById(R.id.text_address);
        textDeviceSignal = (TextView) v.findViewById(R.id.text_signal);
        textDevicePaired = (TextView) v.findViewById(R.id.text_paired);
        deviceAdapter = new DeviceAdapter(devices);
        recyclerView.setAdapter(deviceAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initData() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @RequiresApi(api = LOLLIPOP)
    private void scanBluetooth() {
        bluetoothReceiverRegistered = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mScanCallback, filter);
        //
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
                .build();

        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, 10000);

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();

    }
    //@Override
    public void onRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothAdapter != null) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        //mBluetoothAdapter.enable();
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                    }
                    handler.post(scanTask);}
                deviceAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScanCallback mScanCallback = new ScanCallback() {
        // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            byte[] bytes = result.getScanRecord().getBytes();


            int length=bytes.length;
            byte[] bytes1=new byte[length-11];
            for(int i=0;i<length-11;i++)
            {
                bytes1[i]=bytes[11+i];
            }
            BluetoothClass bclass = result.getDevice().getBluetoothClass();
            short s = (short) result.getRssi();

            // for (byte b : bytes) {
            //   data = data+String.format("%02X", b);
            //}

            int j=bytes[10]& 0xff;

            // data=String.valueOf(bytes[10]& 0xff);

            data= new String(bytes1);
            if(j==0)
            {
                data = "http://www." + data;
                data = data.substring(0, data.length() - 1);
                data=data+".com";
            }

            if(j==1) {
                data = "https://www."+data;
                data = data.substring(0, data.length() - 1);
                data=data+".com";
            }
            if(j==2)
            {
                data = "http://" + data;
                data = data.substring(0, data.length() - 1);
                data=data+".com";
            }
            if(j==3) {
                data = "https://" + data;
                data = data.substring(0, data.length() - 1);
                data=data+".com";
            }



            int ch=bytes[8]& 0xff;
            // android:autoLink="web"
            if(ch==16)
            {
                //data = "no data";
               // Intent serviceIntent = new Intent(HomeFragment.this, ExampleService.class);
                //serviceIntent.putExtra("inputExtra", data);
                //if (Build.VERSION.SDK_INT >= 26)
                  //  context.startForegroundService(serviceIntent);
                // ContextCompat.startForegroundService(serviceIntent);
            }

            if(ch==16) {   //for adv packets
                Device mDevice = new Device("BLE Advertiser", result.getDevice().getBondState() == BluetoothDevice.BOND_BONDED, result.getDevice().getAddress(), s, getDeviceClassIcon(bclass), data);
                //Device mDevice = new Device(result.getDevice().getName(), result.getDevice().getBondState() == BluetoothDevice.BOND_BONDED, result.getDevice().getAddress(), s, getDeviceClassIcon(bclass), data);
                devices.remove(

                        scannedDevice(mDevice));
                devices.add(mDevice);
                deviceAdapter.notifyDataSetChanged();
                deviceAdapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        changeItem(position, "Clicked");
                    }
                });
            }

        }



        public void changeItem(int position, String text) {
            devices.get(position).changeText1(text);
            deviceAdapter.notifyItemChanged(position);
        }

        public int getDeviceClassIcon(BluetoothClass device) {
            Log.d("TAG", "Device.getDeviceClass() = " + device);
            // Toast.makeText(getApplicationContext(),"Device.getDeviceClass() = " + device,Toast.LENGTH_SHORT).show();
            int deviceClass = device.getDeviceClass();
            final int deviceClassMasked = deviceClass & 0x1F00;

            if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES) {
                return R.drawable.headphone;
            } else if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE) {
                return R.drawable.microphone;
            } else if (deviceClassMasked == BluetoothClass.Device.Major.COMPUTER) {
                return R.drawable.computer;
            } else if (deviceClassMasked == BluetoothClass.Device.Major.PHONE) {
                return R.drawable.cell_phone;
            } else if (deviceClassMasked == BluetoothClass.Device.Major.NETWORKING) {
                return R.drawable.networking;
            } else if (deviceClassMasked == BluetoothClass.Device.Major.WEARABLE) {
                return R.drawable.wear;
            } else if (deviceClassMasked == BluetoothClass.Device.Major.TOY) {
                return R.drawable.toy;
            } else {
                return R.drawable.blutooth;
            }
        }

        private Device scannedDevice(Device d) {
            for (Device device : devices) {
                if (d.getAddress().equals(device.getAddress())) {
                    return device;
                }
            }
            return null;
        }



    };
    private BroadcastReceiver scanModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, SCAN_MODE_ERROR);
            if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE
                    || scanMode == BluetoothAdapter.SCAN_MODE_NONE) {
                Toast.makeText(context, "The device is not visible to the outside", LENGTH_SHORT).show();
            }
        }
    };

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

}

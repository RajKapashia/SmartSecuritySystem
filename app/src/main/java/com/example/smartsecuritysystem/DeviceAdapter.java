package com.example.smartsecuritysystem;

import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsecuritysystem.ui.home.HomeFragment;

import java.util.List;

import static com.example.smartsecuritysystem.ui.home.HomeFragment.contextOfApplication;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> {

    public static List<Device> mDeviceList;
Context context=getContextOfApplication();

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    boolean usePercentage;
    //change

    private OnItemClickListener mListener;



    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //change



   public class DeviceHolder extends RecyclerView.ViewHolder {

        TextView textDeviceName;
        TextView textDeviceAddress;
        TextView textDeviceSignal;
        TextView textDevicePaired;
        ImageView imageView;
        TextView data;

        public DeviceHolder(View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            textDeviceName = (TextView) itemView.findViewById(R.id.text_name);
            textDeviceAddress = (TextView) itemView.findViewById(R.id.text_address);
            textDeviceSignal = (TextView) itemView.findViewById(R.id.text_signal);
            textDevicePaired = (TextView) itemView.findViewById(R.id.text_paired);
            data = (TextView) itemView.findViewById(R.id.text_data);
            imageView=(ImageView)itemView.findViewById(R.id.img);
            //
         //   data.setMovementMethod(LinkMovementMethod.getInstance());


            //Open link
         /*   data.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), LinkOpener.class);
                    intent.putExtra("data", data.getText().toString());
                //   Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                 // browserIntent.setData(Uri.parse("https://www.google.com"));
                  //browserIntent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
                    v.getContext().startActivity(intent);
                }
            });*/
            //change

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            Device device = mDeviceList.get(position);


                            Intent intent = new Intent(v.getContext(), BluetoothDeatails.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("MAC", device.getAddress());
                            intent.putExtra("RSSI", device.getSignal());
                            if(device.isPaired()==false)
                            intent.putExtra("PAIRED", "Not Paired");
                            else
                                intent.putExtra("PAIRED", "Paired");
                           // intent.putExtra("Type", device.getType());
                            intent.putExtra("NAME", device.getName());
                            intent.putExtra("Image", device.getType());

                           context.startActivity(intent);
                           int k= AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;
                            Toast.makeText(getApplicationContext(),"You have pressed " +( Math.pow(10, ((double) k - device.getSignal() )/ (10 * 2)))/10000, Toast.LENGTH_SHORT).show();


                        }
                    }
                }

                private Context getApplicationContext() {

                        return contextOfApplication;
                }
            });
            //change

        }


   }


    public DeviceAdapter(List<Device> deviceList) {
        mDeviceList = deviceList;
    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        DeviceHolder deviceHolder = new DeviceHolder(view,mListener);
        return deviceHolder;
    }


    @Override
    public void onBindViewHolder(DeviceHolder holder, int position) {


        Device device = mDeviceList.get(position);


        holder.textDeviceName.setText(device.getName());
        holder.textDeviceAddress.setText("MAC: " + device.getAddress());
       holder.imageView.setImageResource(device.getType());
       // Context applicationContext = Main2Activity.getContextOfApplication();
        Context applicationContext = HomeFragment.getContextOfApplication();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                //PreferenceManager.getDefaultSharedPreferences(applicationContext);
        usePercentage = sharedPreferences.getBoolean("key_settings_use_percentage", false);
        if (usePercentage) {
            holder.textDeviceSignal.setText(device.getSignal() + 100 + "%");
        } else {
            holder.textDeviceSignal.setText(device.getSignal() + "dBm");
        }

        holder.textDevicePaired.setText(device.isPaired() + "");

holder.data.setText(device.data());

/* device1=mDeviceList.get(4);
        holder.textDeviceName.setText("Hello");
        holder.textDeviceAddress.setText("MAC: " );
        holder.textDeviceSignal.setText("%");
        holder.textDevicePaired.setText("hello");*/

    }

    private Context getActivity() {
        return this.getActivity();
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }


}

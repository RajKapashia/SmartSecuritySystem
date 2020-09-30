package com.example.smartsecuritysystem;

public class Device {
    private String name;

    private String address;

    private boolean paired;

    private short signal;
    private String mText1;
private String data;
    private Integer type;
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean isPaired() {
        return paired;
    }

    public short getSignal() {
        return signal;
    }

    public Integer getType() {
        return type;
   } // For Image


    public void changeText1(String text) {
        mText1 = text;
    }

    public String data()
    {
        return data;
    }




    public Device(String name, boolean paired, String address, short signal, Integer type, String data) {
        this.name = name;
        this.paired = paired;
        this.address = address;
        this.signal = signal;
       this.type =type;
       this.data=data;
    }
}

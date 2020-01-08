package com.HK.android.tcp;

interface ITCPClientBackService{
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    boolean sendData(in byte[] data);
    void foregroundService(String notifyTitle, String notifyInfo);
    boolean isConnected();

}
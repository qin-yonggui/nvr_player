package com.test.nvrsearch;

import android.os.Message;
import android.util.Log;

import com.company.NetSDK.CB_fDisConnect;
import com.company.NetSDK.CB_fSearchDevicesCB;
import com.company.NetSDK.DEVICE_NET_INFO_EX;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_PARAM;
import com.hikvision.sadp.DeviceFindCallBack_V40;
import com.hikvision.sadp.SADP_DEVICE_INFO_V40;
import com.hikvision.sadp.Sadp;

import java.util.HashSet;
import java.util.Set;

public class NvrManager {
    private final static String TAG = "NvrManager";
    private Sadp mSadp;

    public void startDetect() {
        detectHiKNvrDevices();
        initSDK();
        detectDHNvrDevices();
    }

    public long lDevSearchHandle = 0;
    public void stop(){
        if(lDevSearchHandle != 0) {
            INetSDK.StopSearchDevices(lDevSearchHandle);
            lDevSearchHandle = 0;
        }
    }

    //haiKang
    private void detectHiKNvrDevices() {
        Log.d(TAG, " detectHiKNvrDevices ");
        DeviceFindCallBack_V40 g_fPlaybackCallBack_V40 = getFindDeviceCallback_V40();
        mSadp = new Sadp();
        mSadp.SADP_Start_V40(g_fPlaybackCallBack_V40);
        mSadp.SADP_Clearup();
        mSadp.SADP_SendInquiry(); //手动触发
    }

    //回调函数处理
    private DeviceFindCallBack_V40 getFindDeviceCallback_V40() {
        DeviceFindCallBack_V40 deviceFindCbf_V40 = new DeviceFindCallBack_V40() {
            public void fDeviceFindCallBack_V40(SADP_DEVICE_INFO_V40 lpDeviceInfo_V40) {
                SADP_DEVICE_INFO_V40 struDevInfo_V40 = lpDeviceInfo_V40;
                String strIpv4 = SdkTool.getInstance().byteArrayToString(lpDeviceInfo_V40.struSadpDeviceInfo.szIPv4Address);
                String strPort = String.valueOf(lpDeviceInfo_V40.struSadpDeviceInfo.dwPort);
                String strSerialNO = SdkTool.getInstance().byteArrayToString(lpDeviceInfo_V40.struSadpDeviceInfo.szSerialNO);
                String strMac = SdkTool.getInstance().byteArrayToString(lpDeviceInfo_V40.struSadpDeviceInfo.szMAC);
                String deviceType = SdkTool.getInstance().byteArrayToString(lpDeviceInfo_V40.struSadpDeviceInfo.szDevDesc);
                DetectNvrModel detectNvrModel = new DetectNvrModel(strIpv4, strPort, "2", strSerialNO, deviceType);
                //nvrDetectListener.onChange(detectNvrModel);
                Log.e(TAG, "Callback ipv4: " + strIpv4 + " strSerialNO :" + strSerialNO + " iResult:" + lpDeviceInfo_V40.struSadpDeviceInfo.iResult + "dwSDKOverTLSPort:" + lpDeviceInfo_V40.dwSDKOverTLSPort);
            }
        };
        return deviceFindCbf_V40;
    }

    //dahua
    private boolean mbInit = false;
    /// INetSDK 接口超时时间
    public static final int TIMEOUT_5S = 5000;      // 5 second
    public static final int TIMEOUT_10S = 10000;    // 10 second
    public static final int TIMEOUT_30S = 30000;    // 30 second
    private DeviceDisConnect mDisconnect;
    public boolean initSDK() {
        mDisconnect = new DeviceDisConnect();
        INetSDK.LoadLibrarys();
        if (mbInit) {
            Log.d(TAG, "Already init.");
            return true;
        }
        mbInit = true;

        /// 初始化接口在所有的SDK函数之前调用 并设置断线回调 :当app和设备端网络断开时，会触发回调
        /// 该接口仅需调用一次
        boolean zRet = INetSDK.Init(mDisconnect);
        if (!zRet) {
            Log.e(TAG, "init NetSDK error!");
            return false;
        }

        /// Set global parameters of NetSDK.
        NET_PARAM stNetParam = new NET_PARAM();
        stNetParam.nConnectTime = TIMEOUT_10S;
        stNetParam.nWaittime = TIMEOUT_10S; // Time out of common Interface.
        stNetParam.nSearchRecordTime = TIMEOUT_30S; // Time out of Playback interface.
        INetSDK.SetNetworkParam(stNetParam);
        return true;
    }

    // 断线回调
    public class DeviceDisConnect implements CB_fDisConnect {
        @Override
        public void invoke(long loginHandle, String deviceIp, int devicePort) {
            Log.e(TAG, "qyg Device " + deviceIp + " is disConnected !");
        }
    }

    public void detectDHNvrDevices() {
        inforSet.clear();
        Log.e(TAG, "DAHUA START");
        lDevSearchHandle = INetSDK.StartSearchDevices(callback);
    }

    private Set<String> inforSet = new HashSet<String>();

    private CB_fSearchDevicesCB callback = new  CB_fSearchDevicesCB(){

        @Override
        public void invoke(DEVICE_NET_INFO_EX device_net_info_ex) {
            String temp = "ip" + " : "+ new String(device_net_info_ex.szIP).trim() + "\n" +
                    "sn" + " : " + new String(device_net_info_ex.szSerialNo).trim();

            String strIpv4 = new String(device_net_info_ex.szIP).trim();
            String strSerialNO = new String(device_net_info_ex.szSerialNo).trim();
            Log.e(TAG, "strIpv4: " + strIpv4 + "  sn  " + strSerialNO);
            ///Search Device，Filter repeated
            ///设备搜索功能，过滤重复的
            if(!inforSet.contains(temp)
                    && !new String(device_net_info_ex.szIP).trim().contains("/")){
                inforSet.add(temp);

            }
        }
    };
}

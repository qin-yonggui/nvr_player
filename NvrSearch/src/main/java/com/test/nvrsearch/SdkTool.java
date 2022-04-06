package com.test.nvrsearch;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;

public class SdkTool {
    private static SdkTool instance;
    private final static String TAG = "sdkTool";
    public static Map<String, String> hikResolutionMap = new HashMap<>();

    private SdkTool() {}

    public static SdkTool getInstance() {
        if (instance == null) {
            instance = new SdkTool();
        }
        return instance;
    }

    // 将时间戳转成calendar
    Calendar getDate(String data) {
        long times = Long.parseLong(data);
        Date date = new Date(times);
        Calendar result = Calendar.getInstance();
        result.setTime(date);
        return result;
    }

    public String byteArrayToString(byte[] bytes) {
        String result = "";
        if (bytes ==null || bytes.length == 0) {
            return result;
        }
        try {
            int length = getByteLength(bytes);
            byte[] temp = Arrays.copyOf(bytes, length);
            result = new String(temp, "utf-8");
        } catch ( UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    public int getByteLength(byte[] data) {
        int result = 0;
        if (data ==null || data.length == 0) {
            return result;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '\0') {
                break;
            }
            result++;
        }
        return result;
    }

    public void parseJson(Context context) {
        try {
            // 获取json文件数据源,流的方式呈现
            InputStream inputStream = context.getAssets().open( "HikResolution.json");
            // 读取JSON文件流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp = "";
            String jsonSource = "";
            //一行一行的读取
            while ((temp = bufferedReader.readLine()) != null)
            {
                jsonSource += temp;
            }
            //关闭
            bufferedReader.close();
            inputStream.close();
            // JSON解析对象
            JSONObject jsonObject = new JSONObject(jsonSource);
            // 获取JOSN文件当中的数据对象hikResolutionMap【可知JSON文件数据只有两种，一是对象{}，二是数组[]】
            JSONArray array = jsonObject.getJSONArray("resolution");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject1 = array.getJSONObject(i);
                String index = jsonObject1.getString("index");
                String resolution = jsonObject1.getString("desc");
                hikResolutionMap.put(index, resolution);
            }
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception:" + e.toString());
        }
    }

    public String getResolutionForDahua(byte byImageSize) {
        String result;
        String resolutionRange[] = {"704*576(PAL) 704*480(NTSC)", "352*576(PAL) 352*480(NTSC)", "704*288(PAL) 704*240(NTSC)", "352*288(PAL) 352*240(NTSC)",
                "176*144(PAL) 176*120(NTSC)", "640*480", "320*240", "480*480", "160*128", "800*592", "1024*768", "1280*800",
                "1280*1024", "1600*1024", "1600*1200", "1920*1200", "240*192", "1280*720", "1920*1080", "1280*960",
                "1872*1408", "3744*1408", "2048*1536", "2432*2050", "1216*1024", "1408*1024", "3296*2472", "2560*1920(5M)",
                "960*576(PAL) 960*480(NTSC)", "960*720","640*360","320*180","160*90","none"};
        int index = (int)byImageSize;
        if (index >= 0 && index < resolutionRange.length) {
            result = resolutionRange[index];
        } else {
            result = "none";
        }
        return result;
    }

    public String getVideoEncodeTypeForDahua(byte byEncodeMode) {
        String result;
        String[] videoEncodeType = {"VIDEO_FORMAT_MPEG4", "VIDEO_FORMAT_MS_MPEG4","VIDEO_FORMAT_MPEG2","VIDEO_FORMAT_MPEG1",
                "VIDEO_FORMAT_H263","VIDEO_FORMAT_MJPG", "VIDEO_FORMAT_FCC_MPEG4", "VIDEO_FORMAT_H264", "VIDEO_FORMAT_H265",
                "VIDEO_FORMAT_SVAC"};
        int index = (int)byEncodeMode;
        if (index >= 0 && index < videoEncodeType.length) {
            result = videoEncodeType[index];
        } else {
            result = "none";
        }
        return result;
    }

    public String getAudioEncTypeForDahua(byte audioEncType) {
        String result;
        String[] audioEncodeTypeRange = {"G711A", "PCM", "G711U", "AMR", "AAC"};
        int index = (int)audioEncType;
        if (index >= 0 && index < audioEncodeTypeRange.length) {
            result = audioEncodeTypeRange[index];
        } else {
            result = "none";
        }
        return result;
    }

    public String getImageQualityForDahua(byte byPicQuality, byte byImageQltyType) {
        String result;
        if ((int)byImageQltyType == 0) {
            String[] qualityRange = {"差", "较差","一般","较好","次好","最好"};
            int index = (int)byPicQuality;
            if (index > 0 && index < 6) { //图像质量有六个等级
                result = qualityRange[index - 1];
            } else {
                result = String.valueOf(byPicQuality);
            }
        } else {
            result = String.valueOf(byPicQuality);
        }
        return result;
    }

    public String getImageQualityForHik(byte byPicQuality) {
        String result;
        String[] qualityRange = {"最好","次好","较好","一般","较差","差", "自动"};
        int index = (int)byPicQuality;
        if (index >= 0 && index < 6) { //图像质量有六个等级
            result = qualityRange[index];
        } else {
            result = qualityRange[qualityRange.length - 1];
        }
        return result;
    }

    public String getVideoBitRateForHik(int dwVideoBitrate) {
        String result;
        String[] bitRateRange = {"0", "16K", "32K", "48K", "64K", "80K", "96K", "128K", "160K", "192K", "224K", "256K",
                "320K", "384K", "448K", "512K", "640K", "768K", "896K", "1024K", "1280K", "1536K", "1792K", "2048K",
                "3072K", "4096K", "8192K", "16384K"};
        if (dwVideoBitrate >=0 && dwVideoBitrate < bitRateRange.length) {
            result = bitRateRange[dwVideoBitrate];
        } else if (dwVideoBitrate == 0xfffffffe) {
            result = "自动";
        } else {
            result = String.valueOf(dwVideoBitrate);
        }
        return result;
    }

    public String getFrameRateForHik(int dwVideoFrameRate) {
        String result;
        String[] frameRateRange = {"全帧率", "1/16", "1/8", "1/4", "1/2", "1", "2", "4", "6", "8", "10", "12",
                "16", "20", "15", "18", "22", "25", "30", "35", "40", "45", "50", "55", "60", "3", "5",
                "7", "9", "100", "120", "24", "48", "8.3"};
        if (dwVideoFrameRate >=0 && dwVideoFrameRate < frameRateRange.length) {
            result = frameRateRange[dwVideoFrameRate];
        } else if (dwVideoFrameRate == 0xfffffffe) {
            result = "自动";
        } else {
            result = String.valueOf(dwVideoFrameRate);
        }
        return result;
    }

    public String getAudioEncTypeForHik(byte audioEncType) {
        String result;
        String[] audioEncodeTypeRange = {"G722", "G711U", "G711A", "0", "0", "MP2L2", "G726", "AAC", "PCM"};
        int index = (int)audioEncType;
        if (index >= 0 && index < audioEncodeTypeRange.length) {
            result = audioEncodeTypeRange[index];
        } else if (index == 0xfe){
            result = "自动";
        } else {
            result = "none";
        }
        return result;
    }
    public String getVideoEncTypeForHik(byte videoEncType) {
        String result;
        String[] videoEncodeTypeRange = {"私有264", "标准h264", "标准mpeg4", "0", "0", "0", "0", "M-JPEG", "MPEG2", "SVAC", "标准h265"};
        int index = (int)videoEncType;
        if (index >= 0 && index < videoEncodeTypeRange.length) {
            result = videoEncodeTypeRange[index];
        } else if (index == 0xfe){
            result = "自动";
        } else {
            result = "none";
        }
        return result;
    }

    public String parseXml(byte[] data, String target) {
        XmlPullParser parser = Xml.newPullParser();
        String result = "";
        try {
            parser.setInput(new ByteArrayInputStream(data), "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (target.equals(parser.getName())) {
                            result = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getAudioEncTypeIndexDH(String audioEncType) {
        int result = -1;
        String[] audioEncodeTypeRange = {"G711A", "PCM", "G711U", "AMR", "AAC"};
        for (int i = 0; i < audioEncodeTypeRange.length; i++) {
            if (TextUtils.equals(audioEncType, audioEncodeTypeRange[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    public int getResolutionIndexDH(String resolution) {
        int result = -1;
        String resolutionRange[] = {"704*576(PAL) 704*480(NTSC)", "352*576(PAL) 352*480(NTSC)", "704*288(PAL) 704*240(NTSC)", "352*288(PAL) 352*240(NTSC)",
                "176*144(PAL) 176*120(NTSC)", "640*480", "320*240", "480*480", "160*128", "800*592", "1024*768", "1280*800",
                "1280*1024", "1600*1024", "1600*1200", "1920*1200", "240*192", "1280*720", "1920*1080", "1280*960",
                "1872*1408", "3744*1408", "2048*1536", "2432*2050", "1216*1024", "1408*1024", "3296*2472", "2560*1920(5M)",
                "960*576(PAL) 960*480(NTSC)", "960*720","640*360","320*180","160*90","none"};

        for (int i = 0; i < resolutionRange.length; i++) {
            if (resolutionRange[i].startsWith(resolution)) {
                result = i;
                break;
            }
        }
        return result;
    }

    public int getVideoEncTypeIndexDH(String videoEncType) {
        int result = -1;
        //CFG_VIDEO_COMPRESSION
        if (TextUtils.equals("H.264",videoEncType)) {
            result = 7;
        } else if (TextUtils.equals("H.265",videoEncType)) {
            result = 8;
        }
        return result;
    }

    public int getAudioEncTypeIndexHik(String audioEncType) {
        int result = -1;
        String[] audioEncodeTypeRange = {"G722", "G711U", "G711A", "0", "0", "MP2L2", "G726", "AAC", "PCM"};
        for (int i = 0; i < audioEncodeTypeRange.length; i++) {
            if (TextUtils.equals(audioEncType, audioEncodeTypeRange[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    public int getFrameRateIndexHik(String frameRate) {
        int result = -1;
        String[] frameRateRange = {"全帧率", "1/16", "1/8", "1/4", "1/2", "1", "2", "4", "6", "8", "10", "12",
                "16", "20", "15", "18", "22", "25", "30", "35", "40", "45", "50", "55", "60", "3", "5",
                "7", "9", "100", "120", "24", "48", "8.3"};
        for (int i = 0; i < frameRateRange.length; i++) {
            if (TextUtils.equals(frameRate, frameRateRange[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    public int getVideoEncTypeIndexHik(String videoEncType) {
        int result = -1;
        String[] videoEncodeTypeRange = {"私有264", "标准h264", "标准mpeg4", "0", "0", "0", "0", "M-JPEG", "MPEG2", "SVAC", "标准h265"};
        if (TextUtils.equals("H.264",videoEncType)) {
            result = 1;
        } else if (TextUtils.equals("H.265",videoEncType)) {
            result = 10;
        }
        return result;
    }

    public int getResolutionIndexHik(String resolution) {
        int result = -1;
        Map<Integer, String> resolutionMap = new HashMap<>();
        resolutionMap.put(19, "1280*720");
        resolutionMap.put(20, "1280*960");
        resolutionMap.put(37, "1920*1080");
        resolutionMap.put(1, "352*288");
        resolutionMap.put(3, "704*576");
        resolutionMap.put(16, "640*480");

        for (int index : resolutionMap.keySet()) {
            if (TextUtils.equals(resolution, resolutionMap.get(index))) {
                result = index;
                break;
            }
        }
        return result;
    }

}

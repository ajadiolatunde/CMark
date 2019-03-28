package com.phyrelinx.cp.cmark;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;




import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.WIFI_SERVICE;


public class CARUtil {


    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
    public  static String getPassTOkenpin(Long l){
        Timestamp timestamp =new Timestamp( l);
        String[] tNumber = timestamp.toLocaleString().split(" ")[3].split(":");
        int pass =Integer.parseInt( tNumber[1]+tNumber[0]);
        return String.valueOf(pass+48);
    }
    public static String getToday(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Timestamp  "+timestamp.toString());
        String[] tt = timestamp.toString().split(" ");
        return tt[0];

    }




    public static String getEventKey(String dat){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Timestamp  "+timestamp.toString());
        String[] tt = timestamp.toString().split(" ");
        //dat  to be like  = "15:30:00.000";
        String dandtime = tt[0]+"T"+dat;
        String res = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(dandtime);
            Timestamp ts = new Timestamp(parsedDate.getTime());
            res= String.valueOf(ts.getTime());
            System.out.println("Timestamp Tunde...................... "+res);
        } catch(Exception e) { //this generic but you can control nother types of exception
            // look the origin of excption
            e.printStackTrace();
        }
        return res;
    }

    //https://ourcodeworld.com/articles/read/32/how-to-download-a-file-to-a-server-using-jsch-sftp-in-android





    //https://ourcodeworld.com/articles/read/31/how-to-show-the-progress-of-an-upload-and-download-with-jsch-sftp-android



    public static String getIpaddress(Context context){
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public static int getArrivalTimeGroup(String time_start,String time_arrival) {

        long start = Long.parseLong(time_start);
        Timestamp ts_start = new Timestamp(start);
        long arrival = Long.parseLong(time_arrival);
        Timestamp ts_arrival = new Timestamp(arrival);

        long mills = ts_arrival.getTime() - ts_start.getTime();
        int hours = (int) mills / (1000 * 60 * 60);
        int mins = (int) (mills / (1000 * 60)) % 60;
        int total_min = (hours * 60) + mins;
        final String diff = "Started " + hours + ":" + mins + " ago";
        System.out.println("Tunde carutil "+total_min  +"  start "+ts_start.toString() +"  arrive "+ts_arrival.toLocaleString());
        return total_min;

    }
}

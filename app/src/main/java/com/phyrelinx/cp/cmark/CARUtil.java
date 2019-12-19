package com.phyrelinx.cp.cmark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

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
    public  static String getPassTOkenpin(Long l,boolean foruploads,Context context){
        Timestamp timestamp =new Timestamp( l);
        Log.d("Timestamp",timestamp.toLocaleString());
        String[] tNumber = timestamp.toLocaleString().split(" ")[3].split(":");
        int uploads =Integer.parseInt( tNumber[0]+tNumber[1]+tNumber[2]);

        int pass =Integer.parseInt( tNumber[1]+tNumber[0]);
        return (foruploads)?Whoyouutil.getDeviceId(context)+"_"+String.valueOf(+uploads):String.valueOf(pass);
    }

    public static boolean download(String data, String remote, Context context,AsynTaskCallback asynTaskCallback){
        FTPClient con = null;
        Boolean send = false;

        try
        {
            con = new FTPClient();
            con.connect(Constants.FTP_URL);
            if (con.login(Constants.FTP_USER, Constants.FTP_PASS))
            {
//                Toast.makeText(context,"Connected ",Toast.LENGTH_SHORT).show();
                asynTaskCallback.processFinish("Connected .....");
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                OutputStream out = new FileOutputStream(new File(data));
                asynTaskCallback.processFinish("Retrieve files .....");
                boolean result = con.retrieveFile(remote, out);
                out.close();
                if (result) {
                    Log.v("download result", "succeeded");
                    send = true;

                }
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            Log.v("download result","failed");
            asynTaskCallback.processFinish("Failed .");

            e.printStackTrace();
        }
        return send;

    }

    public static  boolean downloadsftp(String srcfullpath, String myfile, final Context context, final ProgressDialog progressBar){
        Singleton1 singleton1 = Singleton1.getInstance(context);
        Boolean status = false;

        try {
            JSch ssh = new JSch();
            Session session = ssh.getSession(Constants.FTP_USER, Constants.FTP_URL, 22);

            // Remember that this is just for testing and we need a quick access, you can add an identity and known_hosts file to prevent
            // Man In the Middle attacks
            java.util.Properties config = new java.util.Properties();
            //config.put("kex", "diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(Constants.FTP_PASS);

            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftp = (ChannelSftp) channel;

            //sftp.cd(directory);
            // If you need to display the progress of the upload, read how to do it in the end of the article

            // use the get method , if you are using android remember to remove "file://" and use only the relative path
            sftp.get(srcfullpath,context.getFilesDir().toString()+"/"+myfile,new progressMonitor(context, new AsynTaskCallback() {
                @Override
                public void processFinish(final String str) {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Integer.parseInt(str)%20==0) {
                                int progress = Integer.parseInt(str);
                                progressBar.setProgress(progress);
                                if (progress == 100)progressBar.dismiss();

                                Toast.makeText(context,str+" %",Toast.LENGTH_SHORT).show();
                            }
                            System.out.println("Tunde The file download !"+str);

                        }
                    });
                }
            }));

            Boolean success = true;
            status =success;

            if(success){
                System.out.println("Tunde The file has been succesfully downloaded");
            }else {
                System.out.println("Tunde The file download failed  !");

            }

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            System.out.println(e.getMessage().toString());
            System.out.println("Tunde The file     ......  download failed  !");



            e.printStackTrace();
        } catch (SftpException e) {
            System.out.println(e.getMessage().toString());
            e.printStackTrace();
        }

        return status;
    }


    //https://ourcodeworld.com/articles/read/31/how-to-show-the-progress-of-an-upload-and-download-with-jsch-sftp-android

    public static class progressMonitor implements SftpProgressMonitor {
        private long max                = 0;
        private long count              = 0;
        private long percent            = 0;
        Singleton1 singleton1 ;
        AsynTaskCallback callback;
        Context context;
        //private CallbackContext callbacks = null;

        // If you need send something to the constructor, change this method
        public progressMonitor(final Context context,AsynTaskCallback asynTaskCallback) {
            this.context = context;
            singleton1 = Singleton1.getInstance(context);
            callback = asynTaskCallback;

        }

        public void init(int op, String src, String dest, long max) {
            this.max = max;
            System.out.println("starting");
            System.out.println(src); // Origin destination
            System.out.println(dest); // Destination path
            System.out.println(max); // Total filesize
        }

        public boolean count(long bytes){
            this.count += bytes;
            long percentNow = this.count*100/max;
            if(percentNow>this.percent){
                this.percent = percentNow;
                //singleton1.setProStatus(String.valueOf(percent));

                System.out.println("progress "+this.percent); // Progress 0,0
                System.out.println(max); //Total ilesize
                System.out.println(this.count); // Progress in bytes from the total
                callback.processFinish(String.valueOf(percent));
            }

            return(true);
        }

        public void end(){
            System.out.println("finished");// The process is over
            System.out.println(this.percent); // Progress
            System.out.println(max); // Total filesize
            System.out.println(this.count); // Process in bytes from the total
        }

    }




    public static String getToday(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Timestamp  "+timestamp.toString());
        String[] tt = timestamp.toString().split(" ");
        return tt[0];

    }

    public static boolean uploadsftp(String localfilename, String destinationfullpath, final Context context){
        Singleton1 singleton1 = Singleton1.getInstance(context);
        Boolean success = false;
        Log.d("Tunde ---local--",localfilename);
        Log.d("Tunde---des--",destinationfullpath);


        try {
            JSch ssh = new JSch();
            Session session = ssh.getSession(Constants.FTP_USER,Constants.FTP_URL, 22);
            // Remember that this is just for testing and we need a quick access, you can add an identity and known_hosts file to prevent
            // Man In the Middle attacks
            java.util.Properties config = new java.util.Properties();
            //config.put("kex", "diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(Constants.FTP_PASS);

            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftp = (ChannelSftp) channel;

            //sftp.cd(directory);

            //sftp.put("mylocalfilepath.txt","myremotefilepath.txt",new progressMonitor());
            sftp.put(context.getFilesDir().toString()+"/"+localfilename,destinationfullpath,new progressMonitor(context, new AsynTaskCallback() {
                @Override
                public void processFinish(final String str) {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Integer.parseInt(str)%20==0) {
                                Toast.makeText(context,str+" %",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }));
            success=true;

            if(success){
                System.out.println("Tunde The file has been succesfully uploaded");
            }else {
                System.out.println("Tunde The file download failed  !");

            }

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            System.out.println(e.getMessage().toString());
            System.out.println("Tunde The file     ......  download failed  !");


            e.printStackTrace();
        } catch (SftpException e) {
            System.out.println(e.getMessage().toString());
            e.printStackTrace();
        }
        return success;
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

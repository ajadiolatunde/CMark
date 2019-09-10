package com.phyrelinx.cp.cmark;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by olatunde on 5/22/18.
 */

public class Whoyouutil {
    public static String getLocalDateTimeFromStamp(String ts){
        //System.out.println("Tunde time "+ts);
        long timestamp = Long.parseLong(ts); //Example -> in ms
        Date d = new Date(timestamp );
        return d.toLocaleString();
    }

    public static boolean connected(){
        boolean isConnected = false;
        try {
            // URL url = new URL("http://79.137.20.205:8888/nsvr/auth/partner/telephone/");

            URL url = new URL("https://www.physson.com/d");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int respCode = conn.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK || respCode == HttpURLConnection.HTTP_NOT_FOUND) {
                isConnected = true;
                //System.out.println("Tunde is connected........."+String.valueOf(respCode));
            }else {

            }
        }catch (IOException io){
            //System.out.println("Tunde isnot connected.........");
            io.printStackTrace();
        }
        return isConnected;

    }
//    public static String getDeviceimei(Context context){
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        return telephonyManager.getDeviceId();
//    }

    public static String getDeviceId(Context context){

         String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
         return android_id;
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure how much  you have to scale down
        int inSampleSIze = 1;
        if(srcHeight>destHeight || srcWidth > destWidth) {
            if (srcHeight > srcWidth) {
                inSampleSIze = Math.round(srcHeight / destHeight);
            } else {
                inSampleSIze = Math.round(srcWidth / destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSIze;


        return BitmapFactory.decodeFile(path,options);
    }
    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        //activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path,300,300);
    }
    public  static  void resizeImage(File file){
        String imageFormat = "jpg";
        Integer targetWidth = 400;
        Integer targetHeight = 600;
        //BufferedImage image = ImageIO.read(file);
        //BufferedImage scaledImage = Scalr.resize(image, 250);
    }
    public static  void resize_compress(Context mContext, String file){
        File externalFileDir  =  mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File filename = new File(externalFileDir,file );
        File jpgfile = new File(filename + ".jpg");
        OutputStream outStream = null;
        if (jpgfile.exists()) {
            jpgfile.delete();
            jpgfile = new File(filename + ".jpg");
            Log.e("Tunde file exist", "" + jpgfile + ",Bitmap= " + jpgfile);
        }
        try {
            Bitmap bitmapp = BitmapFactory.decodeFile(filename.toString());

            Bitmap bitmap = addTextToBitmap(bitmapp,mContext);
            outStream = new FileOutputStream(jpgfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream);
            outStream.flush();
            outStream.close();
            // System.out.println("Tund  .photo size "+String.valueOf(jpgfile.length()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Tunde file err", "" + jpgfile + ",Bitmap= " + jpgfile);
        }
    }
    public static Bitmap addTextToBitmap(Bitmap b, Context context){
        Bitmap al = Bitmap.createBitmap(b.getWidth(),b.getHeight(),b.getConfig());
        Canvas c = new Canvas(al);
        Paint p = new Paint();

        p.setTextSize(200);
        p.setColor(context.getResources().getColor(android.R.color.white));
        c.drawBitmap(b,0,0,p);
        c.drawText("PHYSON",b.getWidth()/4,b.getHeight()/2,p);
        //b= b.copy(Bitmap.Config.RGB_565,true);
        return  al;
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static String skipIdevice(Long tm,Context context){
        Timestamp ts = new Timestamp(tm);
        System.out.println("Tunde "+ts.toLocaleString());
        //Device id + time (hms)
        String all = getDeviceId(context).toUpperCase()+ts.toLocaleString().split(" ")[3].replaceAll(":","");

        System.out.println("Tunde "+all);
        return all;
    }
}

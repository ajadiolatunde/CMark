package com.phyrelinx.cp.cmark;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 * Created by olatunde on 6/4/2017.
 */

public class PictureUtils {
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
        File externalFileDir  = new File(mContext.getFilesDir(), Constants.MAPPHOTODIRR);

        File filename = new File(externalFileDir,file );
        if (filename.exists()){
            System.out.println("TUnde file exist");

        }else{
            System.out.println("TUnde file does not exist");
        }
        File jpgfile = new File(filename.toString());
        OutputStream outStream = null;
        if (jpgfile.exists()) {
            //jpgfile.delete();
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

    public static  void regresize_compress(Context mContext, String file){
        File externalFileDir  = new File(mContext.getFilesDir(), Constants.MAPPHOTODIRR);

        File filename = new File(externalFileDir.toString(),file);
        if (filename.exists()){
            System.out.println("TUnde file exist");

        }else{
            System.out.println("TUnde file does not exist");
        }
        File jpgfile = new File(filename + ".jpg");
        OutputStream outStream = null;
        if (jpgfile.exists()) {
            //jpgfile.delete();
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
        c.drawText("lfc",b.getWidth()/4,b.getHeight()/2,p);
        //b= b.copy(Bitmap.Config.RGB_565,true);
        return  al;
    }

    public static void getImage(){

//
//        URL url = new URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464");
//        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//        imageView.setImageBitmap(bmp);


    }


}

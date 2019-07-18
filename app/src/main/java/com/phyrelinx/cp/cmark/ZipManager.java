package com.phyrelinx.cp.cmark;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by olatunde on 6/2/18.
 */

public class ZipManager {
    private static final int BUFFER = 80000;

    public  void zip(String path,String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                if (_files[i].endsWith(".jpg")|| _files[i].endsWith(".att") || _files[i].endsWith(".fin")) {
                    FileInputStream fi = new FileInputStream(path + "/" + _files[i]);
                    origin = new BufferedInputStream(fi, BUFFER);

                    ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;

                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //https://github.com/ghost1372/Mzip-Android
//    public void zippassword(){
//        ZipArchive zipArchive = new ZipArchive();
//        zipArchive.zip(targetPath,destinationPath,password);
//    }



    public static void unzip1(String zipFile, String location)  {
        System.out.println(" cuat start");

        try {
            File f = new File(location);
//            if (!f.isDirectory()) {
//                f.mkdirs();
//                System.out.println(" cuat mkdir");
//            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            try {
                ZipEntry ze = null;
                int count = 0;
                while ((ze = zin.getNextEntry()) != null) {
                    System.out.println(" cuat inside while");
                    count++;
                    String path = location + File.separator + ze.getName();
                    System.out.println(count+" Tunde "+path);

                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                            System.out.println(" cuat mkdir ze");

                        }
                    } else {
                        FileOutputStream fout = new FileOutputStream(path, false);
                        System.out.println(" cuat else ....");


                        try {
                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                fout.write(c);
                                //System.out.println("Tunde fout");

                            }
                            zin.closeEntry();
                        } finally {
                            System.out.println(" cuat finally");

                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
                System.out.println(" cuat close");

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" cuat erro");

            Log.e("cuat ", "Unzip exception", e);
        }
    }



    public static void unzip(String _zipFile, String _targetLocation) {

        //create target location folder if not exist
        //dirChecker(_targetLocatioan);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fin));
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    //dirChecker(ze.getName());
                    File fmd = new File(_targetLocation);
                    fmd.mkdirs();
                } else {
                    //FileOutputStream fout = new FileOutputStream(ze.getName());

                    FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //https://stackoverflow.com/questions/3382996/how-to-unzip-files-programmatically-in-android

    public static boolean unpackZip(String zipfile, String targetpath)
    {
        InputStream is;
        ZipInputStream zis;

        try
        {

            String filename;
            is = new FileInputStream(zipfile);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            int ct = 0;
            try {


                while ((ze = zis.getNextEntry()) != null) {
                    String path = targetpath + File.separator + ze.getName();
                    ct++;
                    System.out.println(ct+" Tunde cuat  ...."+path);
                    FileOutputStream fout = new FileOutputStream(path,false);


                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }

                    fout.close();
                    zis.closeEntry();
                    System.out.println(" Tunde after while ....");

                }

                zis.close();
            }catch (IOException io){
                io.printStackTrace();
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }


        return true;
    }
}
package com.phyrelinx.cp.cmark;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Restrive extends AppCompatActivity {
    Singleton1 singleton1;
    String uploadkey;
    TextView key_txt,status_txt,upload_txt,download_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton1 = Singleton1.getInstance(getApplicationContext());

        setContentView(R.layout.retrive);
        key_txt = (TextView)findViewById(R.id.ret_key);
        status_txt = (TextView)findViewById(R.id.ret_status);
        upload_txt = (TextView)findViewById(R.id.ret_upload);
        download_txt =(TextView)findViewById(R.id.ret_download);

        String tNumber = CARUtil.getPassTOkenpin(System.currentTimeMillis());
        uploadkey = CARUtil.getToday()+"_"+tNumber;
        key_txt.setText(tNumber);
        upload_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_txt.setText("Loading files ......");
                try {
                    Thread.sleep(1000);

                }catch (InterruptedException io){

                }
                makezipfile(uploadkey);
                status_txt.setText("Uploading files ......");

                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean statuse = CARUtil.uploadsftp(uploadkey+".zip", Constants.SSHPATHUPLOAD, Restrive.this);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                status_txt.setText("Done ......");

                                if (statuse) Toast.makeText(getBaseContext(),"Success",Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                });
                tr.start();

            }
        });
        //boolean status =CARUtil.downloadsftp(Constants.SSHPATHRESTORE+"/"+"PASS"+".zip","PASS.zip",getApplicationContext());



    }

    private void makezipfile(String key){
        File filedir = new File(getApplicationContext().getFilesDir().toString(), Constants.MAPPHOTODIRR);
        if (!filedir.exists()) filedir.mkdir();

        writeToFile(singleton1.getPrefKey(Constants.ATTENDANCE),getBaseContext(),"att",".att");

        final File zipfile = new File(getApplicationContext().getFilesDir(), key+".zip");
        if (zipfile.exists()) {
            zipfile.delete();

            //all=all+"zipfile exist deleting \n";
        }
        ZipManager newzip = new ZipManager();
        String[] pics = filedir.list();

        newzip.zip(filedir.toString(), pics, zipfile.toString());
//                    for (String k : pics) {
//                        System.out.println("Tunde  Files " + k);
//                    }

    }

    private void writeToFile(String data, Context context, String attendance, String externsion) {
//        System.out.println("Tunde writing..... "+org+externsion);
//        System.out.println("Tunde writing  data.........."+data);
        //Directory to store fule
        File filedir = new File(getApplicationContext().getFilesDir().toString(), Constants.MAPPHOTODIRR);
        if (!filedir.exists()) filedir.mkdir();
        final File file = new File(filedir.toString(), attendance+externsion);

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}

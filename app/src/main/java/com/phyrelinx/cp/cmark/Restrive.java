package com.phyrelinx.cp.cmark;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText download_edit;
    Button cache_btn;
    boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton1 = Singleton1.getInstance(getApplicationContext());

        setContentView(R.layout.retrive);
        key_txt = (TextView)findViewById(R.id.ret_key);
        status_txt = (TextView)findViewById(R.id.ret_status);
        upload_txt = (TextView)findViewById(R.id.ret_upload);
        cache_btn = (Button)findViewById(R.id.clear_btn);
        cache_btn.setEnabled(false);
        download_txt =(TextView)findViewById(R.id.ret_download);
        download_edit = (EditText)findViewById(R.id.ret_edit);
        download_txt.setEnabled(false);
        download_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textas =download_edit.getText().toString().toLowerCase();
                if (textas.length()>0){
                    download_txt.setEnabled(true);
                }else {
                    download_txt.setEnabled(false);
                }
            }
        });
        cache_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File filere = new File(getApplicationContext().getFilesDir().toString(),Constants.MAPPHOTODIRR);
                for (File f :filere.listFiles()){
                    f.delete();
                }
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,Constants.CLOSE);
                singleton1.addStringSharedPreff(Constants.CHECKINTABLE, Constants.CLOSE);



            }
        });

        download_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_txt.setText("Connecting  ......");
                try {
                    Thread.sleep(1000);

                }catch (InterruptedException io){

                }
                download_edit.setEnabled(false);
                status_txt.setText("Downloading files ......");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String file = CARUtil.getToday()+"_"+download_edit.getText().toString()+".zip";
                        final boolean status =CARUtil.downloadsftp(Constants.SSHPATHRESTORE+"/"+file,file,getBaseContext());
                        final Handler handler = new Handler(Looper.getMainLooper());

                        if (status) {


                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    File zipfile = new File(getApplicationContext().getFilesDir().toString(), Constants.MAPPHOTODIRR);
                                    if (!zipfile.exists()) zipfile.mkdir();
                                    //(zipfile,destination)
                                    ZipManager.unpackZip(getApplicationContext().getFilesDir().toString() + "/" + file, zipfile.toString());
                                    File attfile = new File(zipfile,  "att.att");


                                    if (attfile.exists()) {
                                        new Jasonparse(getBaseContext()).resToreFIle(attfile,Constants.ATTENDANCE);
                                        System.out.println("Tunde restore file exist att");
                                        success = true;
                                        status_txt.setText("Done !");

                                    } else {
                                        status_txt.setText("Failed !");


                                    }
                                    if (success) {
                                        Toast.makeText(getBaseContext(), "File restored ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "File not found ", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                        }else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(getBaseContext(), "Download failed, try again ", Toast.LENGTH_SHORT).show();
                                    status_txt.setText("Failed ");
                                    download_edit.setEnabled(true);

                                }
                            });
                        }


                    }
                });
                t.start();


            }
        });

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
                status_txt.setText("Uploading files ..");

                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean statuse = CARUtil.uploadsftp(uploadkey+".zip", Constants.SSHPATHUPLOAD, Restrive.this);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                status_txt.setText("Done ......");

                                if (statuse) {Toast.makeText(getBaseContext(),"Success",Toast.LENGTH_SHORT).show();
                                cache_btn.setEnabled(true);

                                }

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
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Restrive.this, MainActivity.class);
        startActivity(intent);
        finish();

        // Otherwise defer to system default behavior.
        // super.onBackPressed();
    }

}

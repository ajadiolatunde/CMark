package com.phyrelinx.cp.cmark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
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
    private Handler mainThreadHandler;
    Thread tr;
    Handler mHandler;
    ProgressDialog mProgressBar;

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
        // This handler is used to handle child thread message from main thread message queue.
        mainThreadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        status_txt.setText("Loading ... .");
                        break;
                    case 2:
                        status_txt.setText("Uploading files.. .");
                        tr.start();
                        break;
                    case 3:
                        Toast.makeText(getBaseContext(),"Success",Toast.LENGTH_SHORT).show();
                            cache_btn.setEnabled(true);
                        status_txt.setText("Done...");
                        break;



                    case 4:
                        Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_SHORT).show();
                            cache_btn.setEnabled(false);
                        status_txt.setText("Failed");
                        upload_txt.setEnabled(true);
                        break;
                    case 5:
                        status_txt.setText("Processing files....");
                        break;

                }

            }
        };

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
                AlertDialog.Builder builder = new AlertDialog.Builder(Restrive.this);
                final EditText input = new EditText(Restrive.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                final String number = String.valueOf((int) (Math.random() * 2000 + 1000));
                builder.setTitle("Pin")
                        .setMessage("Please enter "+number)
                        .setView(input)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (input.getText().toString().equals(number)) {

                                    File filere = new File(getApplicationContext().getFilesDir().toString(),Constants.MAPPHOTODIRR);
                                    for (File f :filere.listFiles()){
                                        f.delete();
                                    }
                                    singleton1.addStringSharedPreff(Constants.ATTENDANCE,Constants.CLOSE);
                                    singleton1.addStringSharedPreff(Constants.CHECKINTABLE, Constants.CLOSE);
                                    status_txt.setText("Cleared ");
                                    cache_btn.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(getBaseContext(), "Failed ,wrong token", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        download_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler=new Handler();
                mProgressBar= new ProgressDialog(Restrive.this);
                download_edit.setEnabled(false);
                //status_txt.setText("Downloading files ......");
                mProgressBar.setMax(100);
                mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressBar.show();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String file = CARUtil.getToday()+"_"+download_edit.getText().toString()+".zip";
                        final boolean status =CARUtil.downloadsftp(Constants.SSHPATHRESTORE+"/"+file,file,getBaseContext(),mProgressBar);
                        final Handler handler = new Handler(Looper.getMainLooper());

                        if (status) {


                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    File zipfile = new File(getApplicationContext().getFilesDir().toString(), Constants.MAPPHOTODIRR);
                                    if (!zipfile.exists()) zipfile.mkdir();
                                    Message ms = new Message();
                                    ms.what = 5;
                                    mainThreadHandler.sendMessage(ms);


                                    //(zipfile,destination)
                                    ZipManager.unpackZip(getApplicationContext().getFilesDir().toString() + "/" + file, zipfile.toString());
                                    File attfile = new File(zipfile,  "att.att");


                                    if (attfile.exists()) {
                                        new Jasonparse(getBaseContext()).resToreFIle(attfile,Constants.ATTENDANCE);
                                        success = true;
                                        Message mss = new Message();
                                        mss.what = 3;
                                        mainThreadHandler.sendMessage(mss);

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
        /**fgg
         * tNUmber format is
         *
         *
         * */
        uploadkey = CARUtil.getToday()+"_"+tNumber;
        key_txt.setText(tNumber);
        upload_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_txt.setEnabled(false);
                makezipfile(uploadkey);

                tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean statuse = CARUtil.uploadsftp(uploadkey+".zip", Constants.SSHPATHUPLOAD, Restrive.this);
                        Message fin  = new Message();
                        if (statuse){fin.what = 3;}
                        else{
                            fin.what = 4;
                        }
                        mainThreadHandler.sendMessage(fin);
                    }
                });


            }
        });
        //boolean status =CARUtil.downloadsftp(Constants.SSHPATHRESTORE+"/"+"PASS"+".zip","PASS.zip",getApplicationContext());



    }

    private void makezipfile(final String key){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message childThreadMessage = new Message();
                childThreadMessage.what = 1;
                // Put the message in main thread message queue.
                mainThreadHandler.sendMessage(childThreadMessage);
                File filedir = new File(getApplicationContext().getFilesDir().toString(), Constants.MAPPHOTODIRR);
                if (!filedir.exists()) filedir.mkdir();
                writeToFile(singleton1.getPrefKey(Constants.ATTENDANCE),getBaseContext(),"att",".att");
                final File zipfile = new File(getApplicationContext().getFilesDir(), key+".zip");
                if (zipfile.exists()) zipfile.delete();
                ZipManager newzip = new ZipManager();
                String[] pics = filedir.list();
                newzip.zip(filedir.toString(), pics, zipfile.toString());
                Message childThreadMessage1 = new Message();
                childThreadMessage1.what = 2;
                mainThreadHandler.sendMessage(childThreadMessage1);
            }
        });
        thread.start();


    }

    private void writeToFile(String data, Context context, String attendance, String externsion) {
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

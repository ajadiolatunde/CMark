package com.phyrelinx.cp.cmark;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Singleton1 singleton1;
    Button capturebtn,markbtn,edituserbtn,check_in_btn,reportbtn,mark_toilet_btn,check_out_btn;
    Button downloadAttend_btn,transfer_btn;
    TextView download_sum,attend_sum,checkintable_sum,toilettable_sum,txt_id;
    TextView green_txt,red_txt,yellow_txt;
    ImageView imageView;
    Integer count =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        singleton1 = Singleton1.getInstance(getApplicationContext());
        singleton1.setmSharedPrefrence();
        green_txt = (TextView)findViewById(R.id.txt_green_health);
        red_txt = (TextView)findViewById(R.id.txt_reds_health);
        yellow_txt = (TextView)findViewById(R.id.txt_yellow_health);
        green_txt.setVisibility(View.GONE);
        yellow_txt.setVisibility(View.GONE);
        red_txt.setVisibility(View.GONE);
        txt_id = (TextView)findViewById( R.id.class_id_txt) ;

        txt_id.setText(singleton1.getStaffname());

        download_sum = (TextView)findViewById( R.id.downloaded_sum_txt) ;
        attend_sum = (TextView)findViewById( R.id.attendance_sum_txt) ;
        checkintable_sum = (TextView)findViewById( R.id.checkin_sum_txt) ;
        toilettable_sum = (TextView)findViewById( R.id.toilet_sum_txt) ;
        transfer_btn = (Button)findViewById(R.id.transfers_btn);
        transfer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Restrive.class);
                startActivity(intent);
                finish();
                //Toast.makeText(getBaseContext(),"Get teachers",Toast.LENGTH_SHORT).show();
            }
        });

        toilettable_sum.setText(new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.TOILETTABLE));

        checkintable_sum.setText(new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.CHECKINTABLE));
        checkintable_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                final String number = String.valueOf((int) (Math.random() * 2000 + 1000));
                builder.setTitle("Registration pin")
                        .setMessage(Constants.PASSDEFAULT)
                        .setView(input)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String tNumber = CARUtil.getPassTOkenpin(System.currentTimeMillis());
                                if (input.getText().toString().equals("2019") || input.getText().toString().equals(tNumber)) {

                                    singleton1.addStringSharedPreff(Constants.CHECKINTABLE, Constants.CLOSE);
                                    singleton1.addStringSharedPreff(Constants.TOILETTABLE, Constants.CLOSE);
                                    checkintable_sum.setText(" ");
                                    toilettable_sum.setText(" ");

                                    if (new Jasonparse(getBaseContext()).countAttendance().equals("0"))green_txt.setVisibility(View.VISIBLE);
                                    if (!new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.TOILETTABLE).equals("0"))red_txt.setVisibility(View.VISIBLE);
                                    if (!new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.CHECKINTABLE).equals("0"))yellow_txt.setVisibility(View.VISIBLE);




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

        attend_sum.setText(new Jasonparse(getBaseContext()).countAttendance());
        attend_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ViewAttendance.class);
                if (!new Jasonparse(getBaseContext()).countAttendance().equals("0"))startActivity(intent);

            }
        });

        download_sum.setText(new Jasonparse(getBaseContext()).countUsers());
        download_sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        capturebtn = (Button)findViewById(R.id.capture);
        downloadAttend_btn = (Button)findViewById(R.id.dattendancebtn);
        mark_toilet_btn = (Button)findViewById(R.id.mark_toilet_btn);

        check_out_btn = (Button)findViewById(R.id.mark_out_btn);

        imageView = (ImageView)findViewById(R.id.auth_user_img);
        edituserbtn = (Button)findViewById(R.id.editeusers_main_btn);
        check_in_btn = (Button)findViewById(R.id.manageusers_main_btn);
        check_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!singleton1.getDatalist()) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<DataModel> ft = new ArrayList<>();
                            new Jasonparse(getBaseContext()).loadDatatoList(Constants.DOREGISTER, ft);
                            singleton1.setDatalist(true);
                            singleton1.setDataModelArrayList((ArrayList<DataModel>)ft.clone());
                            ft.clear();
                        }
                    });
                    thread.start();


                }

                Intent intent = new Intent(MainActivity.this,Qrcheckout.class);
                if(!new Jasonparse(getBaseContext()).countAttendance().equals("0")) {
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getBaseContext(),"Nobody has checked-in",Toast.LENGTH_SHORT).show();
                }
            }
        });

        reportbtn = (Button)findViewById(R.id.report);

        downloadAttend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackAsyncdata async = new TrackAsyncdata( Constants.HTTP_URL_DREGISTER, "let me download data ", MainActivity.this, getBaseContext(), new AsynTaskCallback() {
                    @Override
                    public void processFinish(String str) {
                        String res  =str;
                        if (!res.contains("connect")) {
                            singleton1.addStringSharedPreff(Constants.DOREGISTER, str);
                            Toast.makeText(getBaseContext(), "Downloaded", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getBaseContext(), res, Toast.LENGTH_SHORT).show();

                        }
                        download_sum.setText(new Jasonparse(getBaseContext()).countUsers());
                        //Ensure refresh
                        singleton1.setDatalist(false);

                    }
                });
                async.execute();
            }
        });

        capturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
                finish();
            }
        });
        mark_toilet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.CHECKINTABLE).equals("0")){
                    Toast.makeText(getBaseContext(),"Can't visit toilet now",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this, QrToilet.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        edituserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditUser.class);
                startActivity(intent);
                finish();
            }
        });

        check_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!singleton1.getPrefKey(Constants.SESSION_TEACHER).equals(Constants.CLOSE)) {
                    if (new Jasonparse(getBaseContext()).getMode().equals(Constants.MODE_AF)) {
                        final Intent intent = new Intent(MainActivity.this, ManageUser.class);

                        if (singleton1.getDatalist()){
                            startActivity(intent);
                            finish();
                        }else{
                            final ProgressDialog progress = new ProgressDialog(MainActivity.this);
                            progress.setTitle("Loading");
                            progress.setMessage("Wait while loading...");
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();
                            Thread loadthred = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<DataModel> ft = new ArrayList<>();
                                    new Jasonparse(getBaseContext()).loadDatatoList(Constants.DOREGISTER,ft);
                                    singleton1.setDatalist(true);
                                    singleton1.setDataModelArrayList((ArrayList<DataModel>)ft.clone());
                                    ft.clear();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.dismiss();
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                            loadthred.start();



                        }


                    } else {
                        Intent intent = new Intent(MainActivity.this, Qrcodedetect.class);
                        intent.putExtra("id", Constants.SKIP);
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        reportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final  Intent intent = new Intent(MainActivity.this, Report.class);

                if (new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.CHECKINTABLE).equals("0")) {
                    startActivity(intent);
                    finish();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    final String number = String.valueOf((int) (Math.random() * 2000 + 1000));
                    builder.setTitle("Registration pin")
                            .setMessage(Constants.PASSDEFAULT)
                            .setView(input)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String tNumber = CARUtil.getPassTOkenpin(System.currentTimeMillis());
                                    if (input.getText().toString().equals("2019") || input.getText().toString().equals(tNumber)) {

                                        startActivity(intent);
                                        finish();

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
            }
        });

        Glide.with(getBaseContext())
                .load(Constants.HTTP_URL_STAFFPHOTO+singleton1.getStaff()+".jpg")
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .circleCrop()
                        .skipMemoryCache(true))
                .into(imageView);

        if (new Jasonparse(getBaseContext()).countAttendance().equals("0"))green_txt.setVisibility(View.VISIBLE);
        if (!new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.TOILETTABLE).equals("0"))red_txt.setVisibility(View.VISIBLE);
        if (!new Jasonparse(getBaseContext()).countCheckIn_toilet(Constants.CHECKINTABLE).equals("0"))yellow_txt.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!new Jasonparse(getBaseContext()).iftoday()){
                    File fi = new File(getFilesDir(),Constants.MAPPHOTODIRR);
                    System.out.println("Tunde len of photo len  "+fi.listFiles().length);
                    for (File df: fi.listFiles()){
                        df.delete();
                    }
                    singleton1.addStringSharedPreff(Constants.CHECKINTABLE, Constants.CLOSE);
                    singleton1.addStringSharedPreff(Constants.TOILETTABLE, Constants.CLOSE);

                }
            }
        });
        thread.start();

    }
    @Override
    public void onBackPressed() {
        Toast.makeText(getBaseContext(),"Tap again to close the app",Toast.LENGTH_SHORT).show();
        count++;
        if (count==2) {
            singleton1.addStringSharedPreff(Constants.SESSION_TEACHER,Constants.CLOSE);
            finish();
            System.exit(0);
        }

        // Otherwise defer to system default behavior.
        // super.onBackPressed();
    }



}

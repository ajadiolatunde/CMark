package com.phyrelinx.cp.cmark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Singleton1 singleton1;
    Button capturebtn,markbtn,edituserbtn,reportbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        singleton1 = Singleton1.getInstance(getApplicationContext());
        singleton1.setmSharedPrefrence();
        capturebtn = (Button)findViewById(R.id.capture);
        markbtn = (Button)findViewById(R.id.mark);
        edituserbtn = (Button)findViewById(R.id.manageusers);
        reportbtn = (Button)findViewById(R.id.report);

        capturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });
        markbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Markattendance.class);
                startActivity(intent);
            }
        });

        edituserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditUser.class);
                startActivity(intent);
            }
        });

        reportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Report.class);
                startActivity(intent);
            }
        });
    }
}

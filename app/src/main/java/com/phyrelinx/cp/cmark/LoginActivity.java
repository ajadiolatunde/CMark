package com.phyrelinx.cp.cmark;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.sql.Timestamp;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    Singleton1 singleton1;
    Switch mode_sw;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _emailText =(EditText)findViewById(R.id.input_email) ;
        _passwordText =(EditText)findViewById(R.id.input_password) ;
        _loginButton= (Button) findViewById(R.id.btn_login) ;
        _signupLink =(TextView) findViewById(R.id.link_signup);
        singleton1 = Singleton1.getInstance(getApplicationContext());
        singleton1.setmSharedPrefrence();
        mode_sw =(Switch)findViewById(R.id.sw_mode);
        mode_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    singleton1.addStringSharedPreff(Constants.MODE, Constants.MODE_AF);
                    mode_sw.setText("Deactivate Attendance");

                    Toast.makeText(getApplication(),"AF",Toast.LENGTH_SHORT).show();
                }else {
                    singleton1.addStringSharedPreff(Constants.MODE, Constants.CLOSE);
                    mode_sw.setText("Activate Attendance");


                    Toast.makeText(getApplication(),"SF",Toast.LENGTH_SHORT).show();

                }
            }
        });
        mode_sw.setChecked(false);
        singleton1.addStringSharedPreff(Constants.MODE,Constants.CLOSE);



        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    String username = _emailText.getText().toString();
                    boolean st= new Jasonparse(getBaseContext()).passMatch(username,_passwordText.getText().toString().toLowerCase());
                    if (st){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);

                        startActivity(intent);

                        finish();

                    }else {
                        Toast.makeText(getBaseContext(),"Login failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                TrackAsyncdata asyncdata = new TrackAsyncdata(Constants.HTTP_URL_DPASS, "key", LoginActivity.this, LoginActivity.this, new AsynTaskCallback() {
                    @Override
                    public void processFinish(String str) {
                        String res = str;
                        if (!(res.startsWith("Cant")|| res.contains("html") || res.equals(Constants.CLOSE))){
                            singleton1.addStringSharedPreff(Constants.PASTABLE,res);
                            String ct = new Jasonparse(getBaseContext()).countPass();
                            Toast.makeText(getBaseContext(),ct+" _ Refreshed",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getBaseContext(),res,Toast.LENGTH_SHORT).show();

                        }

                    }
                });
                asyncdata.execute();
            }
        });

//        File file = new File(getFilesDir(),Constants.PHOTODIRR);
//        if (!file.exists())file.mkdir();
//        for (File f:file.listFiles()){
//            System.out.println("Tunde "+f.toString());
//            //f.delete();
//        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    Integer count =0;

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        Toast.makeText(getBaseContext(),"Tap again to close the app",Toast.LENGTH_SHORT).show();
        count++;
        if (count==2) {
            finish();
            System.exit(0);
        }
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("Name is required");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10  characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}
package com.phyrelinx.cp.cmark;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class Markattendance extends AppCompatActivity {
    EditText nfcedit;
    TextView nfcuser,nfccut;
    ImageView nfcimg;
    Singleton1 singleton1;
    String cut,previous;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.markattendance);
        previous=" ";
        nfcedit = (EditText)findViewById(R.id.edit_id);
        nfcuser = (TextView)findViewById(R.id.mark_id);
        nfccut = (TextView)findViewById(R.id.cut_id);

        nfcimg = (ImageView)findViewById(R.id.img_id);

        nfcedit.requestFocus();
        singleton1 =Singleton1.getInstance(getBaseContext());
        singleton1.setNameidList();
        nfcedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ch=nfcedit.getText().toString();
                System.out.println("Tunde "+ch+" "+s.toString());

                if (ch.length()>8 && ch.endsWith("\n") ){

                    String dt = nfcedit.getText().toString();
                    Toast.makeText(getBaseContext(),dt,Toast.LENGTH_SHORT);
                    ch = ch.substring(0, ch.length()-1);
                    ch = ch.replaceAll("^0+(?=\\d+$)", "");
                    if (!previous.equals(ch)) {
                        nfcuser.setText(ch);
                        toneDisplay(getBaseContext(), ch);
                        if (new Jasonparse(getBaseContext()).canTakeAttendance(ch)) {
                            System.out.println("Tunde "+singleton1.getUserDetails());
                            new Jasonparse(getBaseContext()).markAttendnance(ch);
                            File filedir = new File(getFilesDir(), Constants.PHOTODIRR);
                            File f = new File(filedir.toString(), ch + ".jpg");
                            nfcuser.setText(singleton1.getNameidList(Integer.parseInt(ch)));

                            Glide.with(Markattendance.this)
                                    .load(f.toString())
                                    .apply(new RequestOptions()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true))
                                    .into(nfcimg);
                        }else{
                            Toast.makeText(getBaseContext(),"Not found",Toast.LENGTH_SHORT).show();
                        }
                        cut = (singleton1.getCount().equals(null))?"0":singleton1.getCount();
                        nfccut.setText(cut);

                        previous = ch;
                    }
                    nfcedit.setText("");


                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("Tunde "+"  ,,,,,,  "+" "+s.toString() +" "+String.valueOf(s.toString().length()));
                if (s.toString().endsWith("\n")){

                    System.out.println("Tunde "+"  yes it cont ");


                }

            }
        });

    }

    private void toneDisplay(Context context, String cod){
        Toast.makeText(context, cod, Toast.LENGTH_SHORT).show();
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,150);
    }
}

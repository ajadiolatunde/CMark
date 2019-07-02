package com.phyrelinx.cp.cmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class EditUser extends AppCompatActivity {
    EditText search;
    TextView cttxt;
    ListView listView;
    ArrayAdapter adapter;
    ArrayList<DataModel> ft;
    ArrayList<String> sft;
    Singleton1 singleton1 = Singleton1.getInstance(getApplication());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edituser);

        cttxt =(TextView)findViewById(R.id.countedittxt);
        search = (EditText)findViewById(R.id.search_edit);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textas =search.getText().toString().toLowerCase();
                filter(textas);

            }
        });
        listView = (ListView)findViewById(R.id.listuser);

        ft = new ArrayList<>();
        sft = new ArrayList<>();
        //load data here   id name group
        new Jasonparse(getBaseContext()).loadDatatoList(Constants.REGISTER,ft);
        System.out.println("Tunde ft "+ft.size());
        cttxt.setText(String.valueOf(ft.size()));
        System.out.println("Tunde ft "+ft.size());
        String all = singleton1.getPrefKey(Constants.REGISTER);
        System.out.println("Tunde reg "+all);


        //adapter = new ArrayAdapter(EditUser.this, android.R.layout.simple_list_item_1, ft);
        adapter = new EdituserCustomAdapter(Constants.REGISTER,ft,sft,EditUser.this,EditUser.this);
        listView.setAdapter(adapter);

    }

    public void filter(String text){
        String ctext = text.toLowerCase(Locale.getDefault());
        ArrayList<DataModel> lt =new ArrayList<>();
        new Jasonparse(getBaseContext()).loadDatatoList(Constants.REGISTER,lt);


        if (ctext.length()==0){
            System.out.println("Tunde..................... ");
            // adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, lt);
            adapter.notifyDataSetChanged();

        }else{
            ft.clear();
            for (DataModel model: lt){

                if (model.toString().toLowerCase().contains(ctext)){
                    ft.add(model);
                }
            }
            System.out.println("Tunde all size  "+ft.size());
            cttxt.setText(String.valueOf(ft.size()));
            //adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, ft);
            adapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onBackPressed() {
       Intent intent=new Intent(EditUser.this, MainActivity.class);
       startActivity(intent);
       finish();

        // Otherwise defer to system default behavior.
       // super.onBackPressed();
    }

}

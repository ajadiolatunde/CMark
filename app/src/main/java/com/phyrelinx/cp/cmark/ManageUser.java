package com.phyrelinx.cp.cmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class ManageUser extends AppCompatActivity {
    EditText search;
    TextView cttxt,skip_txt;
    ListView listView;
    Tag tag;

    ArrayAdapter adapter;
    ArrayList<DataModel> ft,lt;
    ArrayList<String> sft;
    Singleton1 singleton1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manageuser);
        singleton1 = Singleton1.getInstance(getBaseContext());
        cttxt =(TextView)findViewById(R.id.countedittxt);
        search = (EditText)findViewById(R.id.search_edit);
        skip_txt = (TextView)findViewById(R.id.skip_manageuser_txt);
        skip_txt.setVisibility(View.GONE);
        skip_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageUser.this,Qrcodedetect.class);
                intent.putExtra("id",Constants.SKIP);
                startActivity(intent);
                finish();
            }
        });
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


        sft = new ArrayList<>();
        if (singleton1.getDatalist()){
            ft = (ArrayList<DataModel>)singleton1.getDataModelArrayList().clone();
        }else{
            ft = new ArrayList<>();
            new Jasonparse(getBaseContext()).loadDatatoList(Constants.DOREGISTER,ft);
            singleton1.setDatalist(true);
            singleton1.setDataModelArrayList((ArrayList<DataModel>)ft.clone());

        }
        //load data here   id name group
        cttxt.setText(String.valueOf(ft.size()));
        lt  = (ArrayList<DataModel>)ft.clone();

        //adapter = new ArrayAdapter(EditUser.this, android.R.layout.simple_list_item_1, ft);
        adapter = new ManageUserCustomAdapter(Constants.DOREGISTER,ft,sft,ManageUser.this,ManageUser.this);
        listView.setAdapter(adapter);
        if (!new Jasonparse(getBaseContext()).getMode().equals(Constants.MODE_AF)){
            tag = singleton1.getTagtochange();
            search.setText(tag.getPhone().replace(" ",""));

        }


    }

    public void filter(String text){
        String ctext = text.toLowerCase(Locale.getDefault());

        if (ctext.length()==0){
            // adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, lt);
            adapter.notifyDataSetChanged();

        }else{
            ft.clear();
            for (DataModel model: lt){

                if (model.toString().toLowerCase().contains(ctext)){
                    ft.add(model);
                }
            }
            cttxt.setText(String.valueOf(ft.size()));
            //adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, ft);
            adapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onBackPressed() {
       Intent intent=new Intent(ManageUser.this, MainActivity.class);
       startActivity(intent);
       finish();

        // Otherwise defer to system default behavior.
       // super.onBackPressed();
    }

}

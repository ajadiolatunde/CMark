package com.phyrelinx.cp.cmark;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;

public class ViewAttendance extends AppCompatActivity {
    Singleton1 singleton1;
    ListView listView;
    EditText searchedit;
    ArrayList<Tag> gj,lt;
    ViewAttendanceCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewattendance);
        singleton1 = Singleton1.getInstance(getApplicationContext());
        searchedit =(EditText)findViewById(R.id.searchattendance_edit);
        listView = (ListView)findViewById(R.id.listviewattendanceuser);

         gj = new ArrayList<>();

        searchedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textas =searchedit.getText().toString().toLowerCase();
                filter(textas);

            }
        });

        new Jasonparse(getBaseContext()).loadTagsAttendance(gj);
        lt  = (ArrayList<Tag>) gj.clone();

        adapter = new ViewAttendanceCustomAdapter(gj,ViewAttendance.this);
        listView.setAdapter(adapter);
    }

    public void filter(String text){
        String ctext = text.toLowerCase(Locale.getDefault());
//        ArrayList<DataModel> lt =new ArrayList<>();
//        new Jasonparse(getBaseContext()).loadDatatoList(Constants.DOREGISTER,lt);


        if (ctext.length()==0){
            adapter.notifyDataSetChanged();

        }else{
            gj.clear();
            for (Tag tag: lt){

                if (tag.parent.toLowerCase().contains(ctext.toLowerCase()) || tag.tag_id.toUpperCase().contains(ctext.toLowerCase())){
                    gj.add(tag);
                }
            }
            adapter.notifyDataSetChanged();

        }

    }


}

package com.phyrelinx.cp.cmark;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class Report extends AppCompatActivity {
    ListView listView;
    ToolsCustomAdapter toolsCustomAdapter;
    Singleton1 singleton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        listView = (ListView)findViewById(R.id.report_listview);
        final ArrayList<Event> arrayList=new ArrayList<>();
        toolsCustomAdapter = new ToolsCustomAdapter(arrayList,Report.this,Report.this);
        listView.setAdapter(toolsCustomAdapter);
        new Jasonparse(getBaseContext()).getAllevent(arrayList,toolsCustomAdapter);
        singleton1 = Singleton1.getInstance(getBaseContext());
        singleton1.setNameidList();

    }
}

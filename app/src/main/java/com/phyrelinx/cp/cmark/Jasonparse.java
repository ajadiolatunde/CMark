package com.phyrelinx.cp.cmark;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.util.Hex;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Jasonparse {
    Context context;
    Singleton1 singleton1;

    public Jasonparse(Context context){
        this.context = context;
        singleton1 = Singleton1.getInstance(context);
    }


    //called before mark attendance
    public boolean canRegister(String id){
        String data = singleton1.getPrefKey(Constants.REGISTER);
        boolean status = false;
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            status = false;
        }else{
            try{
                JSONObject body = new JSONObject(data);
                if (body.has(id)){
                    status = true;
                    singleton1.setUserDetails(body.get(id).toString());
                    System.out.println("Tunde userdetails "+body.get(id));
                }
            }catch (JSONException io){

            }
        }
        return status;
    }
    //check if child tag not used

    //Check in table for student ,updated along markattendance
    public boolean canUsechildTag(String child_tag){
        String data = singleton1.getPrefKey(Constants.CHECKINTABLE);
        boolean status = false;

        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            status = true;
        }else{
            try{
                JSONObject oldbody = new JSONObject(data);
                if (oldbody.has(child_tag)){
                    status =false;
                }else {
                    status = true;
                }

            }catch (JSONException io){

            }
        }
        return status;
    }

    //checkin  table
    public void checkIn(String child_tag,String parent){
        String data = singleton1.getPrefKey(Constants.CHECKINTABLE);
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            try {
                body.put(child_tag, parent);
                singleton1.addStringSharedPreff(Constants.CHECKINTABLE,body.toString());
            }catch (JSONException io){
                io.printStackTrace();
            }

        }else{
            try{
                JSONObject oldbody = new JSONObject(data);
                oldbody.put(child_tag,parent);
                singleton1.addStringSharedPreff(Constants.CHECKINTABLE,oldbody.toString());
            }catch (JSONException io){

            }
        }
    }


    //Add to toilet table
    public void ToiletIn(String child_tag){
        String data = singleton1.getPrefKey(Constants.TOILETTABLE);
        String time = String.valueOf(System.currentTimeMillis());
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            try {
                body.put(child_tag, time);
                singleton1.addStringSharedPreff(Constants.TOILETTABLE,body.toString());
            }catch (JSONException io){
                io.printStackTrace();
            }

        }else{
            try{
                JSONObject oldbody = new JSONObject(data);
                oldbody.put(child_tag,time);
                singleton1.addStringSharedPreff(Constants.TOILETTABLE,oldbody.toString());
            }catch (JSONException io){

            }
        }
    }

    public void  TOiletOut(String child_tag){
        String data = singleton1.getPrefKey(Constants.TOILETTABLE);

        try{
            JSONObject oldbody = new JSONObject(data);
            oldbody.remove(child_tag);
            singleton1.addStringSharedPreff(Constants.TOILETTABLE,oldbody.toString());
        }catch (JSONException io){
        }
    }



    //before child chectout
    public boolean tagExistinToilet(String child_tag){
        boolean status = true;
        String data = singleton1.getPrefKey(Constants.TOILETTABLE);
        System.out.println("Tunde toilet "+data);
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            status =false;
        }else {
            try {
                JSONObject body = new JSONObject(data);
                if (body.has(child_tag)){
                    status=true;
                }else {
                    status = false;
                }
            } catch (JSONException io) {
                io.printStackTrace();

            }
        }


        return status;
    }
    //checkout and update mark attendance
    // parents card first

    public void  checkOut(String child_tag){
        String data = singleton1.getPrefKey(Constants.CHECKINTABLE);

            try{
                JSONObject oldbody = new JSONObject(data);
                oldbody.remove(child_tag);
                singleton1.addStringSharedPreff(Constants.CHECKINTABLE,oldbody.toString());
            }catch (JSONException io){
            }
    }

    //can parent check out, return child id

    public String  canParentCheckOut(String parent){
        String res = "0";
        String data = singleton1.getPrefKey(Constants.CHECKINTABLE);
        if (data.equals("{}") || data.equals(Constants.CLOSE)){

        }else{
            try{
                JSONObject oldbody = new JSONObject(data);
                for (int x=0;x<oldbody.names().length();x++){
                    String cid = (String)oldbody.names().get(x);
                    String pid = oldbody.getString(cid);
                    if (pid.equals(parent)){
                        res = cid;
                        break;
                    }
                }
            }catch (JSONException io){

            }
        }
        return res;
    }
    //check if realid already marked attendance
    public boolean canTakeAttendance(String realid){
        boolean status = true;

        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String today = CARUtil.getToday();
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            status=true;
        }else{
            try {
                JSONObject oldbody = new JSONObject(data);
                if (oldbody.has(today)){
                    String js = oldbody.get(today).toString();
                    JSONObject today_body = new JSONObject(js);
                    if (today_body.has(realid)){
                        status = false;
                    }


                }else{
                    status=true;
                }

            }catch (JSONException io){
                io.printStackTrace();
            }

        }
        return status;

    }

    //Getuser realid to update attendance table
    public String  getRealId(String tagid){
        String id = "0";
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String today = CARUtil.getToday();
        if (!canUsechildTag(tagid)){
            try {
                JSONObject oldbody = new JSONObject(data);
                String js = oldbody.get(today).toString();
                JSONObject today_body = new JSONObject(js);
                for (int x=0;x<today_body.names().length();x++){
                    String relId =(String)today_body.names().get(x);
                    Tag tg = new Gson().fromJson(today_body.getString(relId),Tag.class);
                    if (tg.getTag_id().equals(tagid)){
                        id =tg.id;
                        singleton1.setTagtochange(tg);
                        break;
                    }
                }

            }catch (JSONException io){
            io.printStackTrace();
        }

        }else {
            Toast.makeText(context,"Tag not registered",Toast.LENGTH_SHORT).show();
        }

        return id;

    }

    public void updateMarkAttendance(Tag t,String realid){


    }

//    public void handleJSONObject(JSONObject jsonObject) {
//       while( jsonObject.keys().hasNext()){
//            Object value = jsonObject.get(key);
//            logger.info("Key: {0}", key);
//            handleValue(value);
//        });
//    }

    //Status table for database
    public void markAttendnance(String realid,String tag){
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String today = CARUtil.getToday();
        System.out.println("Tunde today "+today);
        String time = String.valueOf(System.currentTimeMillis());
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            JSONObject body_fortoday= new JSONObject();
            try{
                body_fortoday.put(realid,new JSONObject(tag));
                body.put(today,body_fortoday);
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,body.toString());
                Toast.makeText(context,"Done bye!  1",Toast.LENGTH_SHORT).show();
            }catch (JSONException io){
                io.printStackTrace();
            }
            //check if timestamp exist
        }else{
            try {
                JSONObject oldbody = new JSONObject(data);
                if (oldbody.has(today)){
                    String js = oldbody.get(today).toString();
                    JSONObject today_body = new JSONObject(js);
                    today_body.put(realid,new JSONObject(tag));
                    oldbody.put(today,today_body);
                    System.out.println("Tunde len "+today_body.names().toString());
                    singleton1.setCount(String.valueOf(today_body.names().length()));
                }else{
                    JSONObject today_body = new JSONObject();
                    today_body.put(realid,new JSONObject(tag));
                    oldbody.put(today,today_body);
                    System.out.println("Tunde len "+today_body.names().toString());
                }
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,oldbody.toString());
                Toast.makeText(context,"Done bye!  2",Toast.LENGTH_SHORT).show();
            }catch (JSONException io){
                io.printStackTrace();
            }
        }
    }

    //update table attendance

    public void updateAttendnance(String realid,Tag tag,boolean out){
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String today = CARUtil.getToday();

        if (out){
            tag.setTag_id(tag.getTag_id()+"X");
        }
        System.out.println("Tunde today "+today);

            try {
                JSONObject oldbody = new JSONObject(data);

                String js = oldbody.get(today).toString();
                JSONObject today_body = new JSONObject(js);
                today_body.put(realid,new JSONObject(new Gson().toJson(tag,Tag.class)));
                oldbody.put(today,today_body);
                singleton1.setCount(String.valueOf(today_body.names().length()));
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,oldbody.toString());
                Toast.makeText(context,"Out bye! ",Toast.LENGTH_SHORT).show();
                System.out.println("Tunde len "+today_body.toString());

            }catch (JSONException io){
                io.printStackTrace();
            }

    }


    //get count
    public String countAttendance(){
        String count = "0";
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String today = CARUtil.getToday();
        if (!(data.equals("{}") || data.equals(Constants.CLOSE))) {
            try {
                JSONObject body = new JSONObject(data);
                if (body.has(today)){
                    JSONObject body_today = (JSONObject) body.get(today);
                    count = String .valueOf(body_today.names().length());
                }

            }catch (JSONException io){
                io.printStackTrace();

            }
        }

        return count;

    }

    //get count
    public String countCheckIn_toilet(String table){
        String count = "0";
        String data = singleton1.getPrefKey(table);

        if (!(data.equals("{}") || data.equals(Constants.CLOSE))) {
            try {
                JSONObject body = new JSONObject(data);
                count = String .valueOf(body.names().length());

            }catch (JSONException io){
                io.printStackTrace();

            }
        }

        return count;

    }
    //load on start to update markattendacne ,on delete ,on mark


   //Rest checkin entry
    public void ResetAll(String table){
        singleton1.addStringSharedPreff(table,Constants.CLOSE);
    }

    public void loadTagsAttendance(ArrayList<Tag> arrayList){
        arrayList.clear();

        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String today = CARUtil.getToday();
        if (!countAttendance().equals("0")){
            try {
                JSONObject body = new JSONObject(data);
                if (body.has(today)){
                    JSONObject body_today = (JSONObject) body.get(today);
                    for (int x=0;x<body_today.names().length();x++){
                        String cid =(String) body_today.names().get(x);
                        Tag tag =  new Gson().fromJson(body_today.getString(cid),Tag.class);
                        arrayList.add(tag);
                    }
                }

            }catch (JSONException io){
                io.printStackTrace();

            }
        }


    }


    public ArrayList<String> getEventsStudentsDetails(JSONObject obj){
        ArrayList<String> ar = new ArrayList<>();
        JSONObject bd = obj;

        for (int x=0;x<bd.names().length();x++){
            try {
                String id = (String)bd.names().get(x);
                String time = bd.getString(id);
                ar.add(id+" "+time);

            }catch (JSONException io){
                io.printStackTrace();
            }

        }
        return ar;
    }


    public void getAllevent(ArrayList<Event> arrayList, AllEventsCustomAdapter adapter){
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        arrayList.clear();
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            System.out.println("Nothing ..");
        }else {
            try {
                JSONObject body = new JSONObject(data);
                int len = body.names().length();
                for(int i = 0; i<body.names().length(); i++){
                    String day = body.names().getString(i);

                    JSONObject studentsdata = new JSONObject(body.get(body.names().getString(i)).toString());
                    //value is the name key is time
                    String  lenofStudent = String.valueOf(studentsdata.names().length());

                    //Timestamp timestamp = new Timestamp(Long.parseLong(key));
                    //Event event = new Event(timestamp.toLocaleString(),key,value,lofStu);
                    Event event = new Event(day,studentsdata,lenofStudent);
                    arrayList.add(event);//add the hashmap into arrayList

                    //System.out.println( "Tunde  Time = " + timestamp.toLocaleString() + " value = " +timestamp.getTime()+"  "+timestamp.getDate() );
                }

                Collections.sort(arrayList, new Comparator<Event>(){
                    public int compare(Event one, Event two) {
                        return two.getDay().compareTo(one.getDay());
                    }
                });

                adapter.notifyDataSetChanged();
                System.out.println("array  size " +arrayList.size() );

            }catch (JSONException io){
                io.printStackTrace();
            }
        }

    }
//    onlogin success full store id as key for names
    public ArrayList<NameId> buildNamefromid(){
        String data = singleton1.getPrefKey(Constants.REGISTER);
        System.out.println("Tunde test empty "+data);
        ArrayList<NameId> nameIdslist = new ArrayList<>();
        if (!(data.equals("{}") || data.equals(Constants.CLOSE))){
            try{
                JSONObject body = new JSONObject(data);
                if (body.toString().equals("{}")){

                }else{
                    for (int i=0;i<body.names().length();i++) {
                        String id = body.names().getString(i);
                        JSONObject conbondy = body.getJSONObject(id);
                        String fn = conbondy.getString("firstname");
                        String ln = conbondy.getString("lastname");
                        NameId nameId = new NameId(id, fn + " " + ln);
                    nameIdslist.add(nameId);
                    }
                }
            }catch (JSONException io){
                io.printStackTrace();
            }

        }


        return nameIdslist;
    }


    //User details
    public boolean register(String id,String fname,String mname,String lname,String dob,String gender,String phone,String teacher,String upstatus){
        String data = singleton1.getPrefKey(Constants.REGISTER);
        boolean status = false;

        // First stage
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            JSONObject key_body = new JSONObject();

            try{

                key_body.put("firstname",fname);
                key_body.put("middlename",mname);
                key_body.put("lastname",lname);
                key_body.put("gender",gender);
                key_body.put("phone",phone);
                key_body.put("dob",dob);
                key_body.put("teacherid",teacher);
                //0 for not sent 1 for sent
                key_body.put("upstatus",upstatus);
                body.put(id,key_body);
                singleton1.addStringSharedPreff(Constants.REGISTER,body.toString());
                Toast.makeText(context,"Done bye!  1",Toast.LENGTH_SHORT).show();
                status = true;

            }catch (JSONException io){
                io.printStackTrace();

            }
            //check if timestamp exist
        }else{
            try {
                JSONObject oldbody = new JSONObject(data);
                JSONObject key_body = new JSONObject();
                boolean suc = true;
                if (suc){
                    key_body.put("firstname",fname);
                    key_body.put("middlename",mname);
                    key_body.put("lastname",lname);
                    key_body.put("gender",gender);
                    key_body.put("phone",phone);
                    key_body.put("teacherid",teacher);
                    key_body.put("dob",dob);
                    //0 for not sent 1 for sent
                    key_body.put("upstatus",upstatus);
                    oldbody.put(id,key_body);
                    singleton1.addStringSharedPreff(Constants.REGISTER,oldbody.toString());

                    Toast.makeText(context,"Done bye! 2",Toast.LENGTH_SHORT).show();
                    status = true;
                    System.out.println("Tunde done by 2 ");

                }else{
                    Toast.makeText(context,"Duplicate  please change id 2",Toast.LENGTH_SHORT).show();

                    System.out.println("Tunde it has 3");

                }

            }catch (JSONException io){
                io.printStackTrace();
            }


        }

        return status;


    }


    public void removeSEnt(String j, Context ct){

        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        String archives = singleton1.getPrefKey(Constants.UPATTENDANCE);

        try {
            JSONObject bd = new JSONObject(data);
            String names =bd.get(j).toString();
            System.out.println("TUNDE name to reomve  "+j+","+names);
            JSONObject tosend = new JSONObject(names);
            String android_id = Settings.Secure.getString(ct.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            tosend.put("optime",String.valueOf(System.currentTimeMillis()));
            tosend.put("id",android_id);
            //test if that key is available
            if (bd.has(j)){
                if (archives.equals("{}")|| archives.equals(Constants.CLOSE)){
                    JSONObject arbd = new JSONObject();
                    arbd.put(j,tosend.toString());
                    singleton1.addStringSharedPreff(Constants.UPATTENDANCE,arbd.toString());
                    System.out.println("TUNDE len 1"+arbd.names().length());


                }else {
                    JSONObject arbd = new JSONObject(archives);
                    arbd.put(j,tosend.toString());
                    singleton1.addStringSharedPreff(Constants.UPATTENDANCE,arbd.toString());
                    System.out.println("TUNDE len 2 "+arbd.names().length());
                    System.out.println("TUNDE len 2 "+arbd.names().toString());

                }
                bd.remove(j);
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,bd.toString());

            }else{
                System.out.println("TUNDE cant find to remove ");
            }




        }catch (JSONException ik){
            ik.printStackTrace();
        }

    }

    public void deleteAccount(String id){
        String regdata = singleton1.getPrefKey(Constants.REGISTER);
        try{
            JSONObject body = new JSONObject(regdata);
            body.remove(id);
            singleton1.addStringSharedPreff(Constants.REGISTER,body.toString());

        }catch (JSONException io){
            io.printStackTrace();

        }


    }

    public void loadDatatoList(String table,ArrayList<DataModel> arrayList){
        arrayList.clear();
        String regdata = singleton1.getPrefKey(table);
        if (regdata.equals("{}") || regdata.equals(Constants.CLOSE)){
            // hashMap = null;
        }else {
            try {
                JSONObject body = new JSONObject(regdata);
                for(int i = 0 ;i<body.names().length();i++){
                    String id = (String)body.names().get(i);

                    JSONObject ct = body.getJSONObject(id);
                    String md = (ct.has("middlename"))?ct.getString("middlename"):ct.getString("middle");

                    String name = ct.getString("firstname")+" "+md+" "+ct.getString("lastname");
                    String gender =ct.getString("gender");
                    String phone = "00000000000";

                    if (ct.has("phone")) phone = ct.getString("phone");

                    String teacher = "0000";
                    if (ct.has("teacherid")) teacher=ct.getString("teacherid");

                    DataModel detailsModel = new DataModel(ct.getString("firstname"),md,ct.getString("lastname"),id,gender,(ct.has("dob"))?ct.getString("dob"):"000000",phone,teacher);
                    arrayList.add(detailsModel);
                   // System.out.println("Tunde list "+id+" "+name);
                }
            }catch (JSONException io){
                io.printStackTrace();
            }

        }

    }





    public String getGroupLocation(String grp){
        String data = singleton1.getPrefKey(Constants.GROUP);
        String loc = "N";
        try {
            JSONObject bd = new JSONObject(data);
            loc = bd.getString(grp);


        }catch (JSONException io){
            io.printStackTrace();
        }

        return loc;

    }




    public String getgroupvalue(String grp){
        String grpdata = singleton1.getPrefKey(Constants.GROUP);
        String res = " ";

        try {
            JSONObject gbody = new JSONObject(grpdata);
            res = gbody.getString(grp);

        }catch (JSONException io){
            io.printStackTrace();
        }
        return res;
    }



    public void getListof(ArrayList<String> arrayList,String table){
        arrayList.clear();
        String regdata = singleton1.getPrefKey(table);
        if (regdata.equals("{}") || regdata.equals(Constants.CLOSE)){
            // hashMap = null;
        }else {
            try {
                JSONObject body = new JSONObject(regdata);
                for(int i = 0 ;i<body.names().length();i++){
                    String name = (String)body.names().get(i);
                    String value = body.getString(name);

                    arrayList.add(name);
                    System.out.println("Tunde list "+name+" "+value);
                }
            }catch (JSONException io){
                io.printStackTrace();
            }

        }

    }





    //obtain current session js
    public  String currentSessiontimeStamp(){
        String data  = singleton1.getPrefKey(Constants.SESSION);


        String cs = String.valueOf(System.currentTimeMillis());;

        try {
            JSONObject jsonObject = new JSONObject(data);
            cs = jsonObject.get(Constants.OPENTIME).toString();
        }catch (JSONException io){
            io.printStackTrace();
        }

        return cs;
    }

    public  String currentSessionkey(){
        String data  = singleton1.getPrefKey(Constants.SESSION);


        String cs = String.valueOf(System.currentTimeMillis());;

        try {
            JSONObject jsonObject = new JSONObject(data);
            cs = jsonObject.get(Constants.SESSION_KEY).toString();
        }catch (JSONException io){
            io.printStackTrace();
        }

        return cs;
    }

    //obtain current session js
    public  String currentSessionEvent(){
        String data  = singleton1.getPrefKey(Constants.SESSION);
        String cs = Constants.CLOSE;
        try {
            JSONObject jsonObject = new JSONObject(data);
            cs = jsonObject.get(Constants.EVENTNAME).toString();
        }catch (JSONException io){
            io.printStackTrace();
        }
        return cs;
    }

    //To select from list to push
    public String getthisEventbyTime(String j,Context ct){
        String res ="N";
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        try {
            JSONObject bd = new JSONObject(data);
            String names =bd.get(j).toString();
            JSONObject tosend = new JSONObject(names);
            String android_id = Settings.Secure.getString(ct.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            tosend.put("optime",j);
            tosend.put("id",android_id);
            res=tosend.toString();


        }catch (JSONException ik){
            ik.printStackTrace();
        }
        return res;
    }


    public  void markAttendnance(String matric,String staff,String type,String time,Context context){
        String data = singleton1.getPrefKey(Constants.ATTENDANCE);
        // First stage
        if (data.equals("{}") || data.equals(Constants.CLOSE)){
            JSONObject body = new JSONObject();
            JSONObject ct = new JSONObject();
            JSONObject cstu = new JSONObject();
            JSONObject ctype = new JSONObject();

            try{
                cstu.put(matric,time);
                ct.put("students",cstu);
                ctype.put(matric,type);
                ct.put("type",ctype);
                ct.put("staff",staff);
                ct.put(Constants.SESSION_KEY,currentSessionkey());

                ct.put(Constants.EVENTNAME,currentSessionEvent());
                body.put(currentSessiontimeStamp(),ct);
                singleton1.addStringSharedPreff(Constants.ATTENDANCE,body.toString());
                singleton1.addStringSharedPreff(Constants.COUNT,"1");
                Toast.makeText(context,"Done bye!  1",Toast.LENGTH_SHORT).show();
            }catch (JSONException io){

            }
            //check if timestamp exist
        }else{
            try {
                JSONObject oldbody = new JSONObject(data);
                if (oldbody.has(currentSessiontimeStamp())){
                    System.out.println("Tunde it has 2");
                    System.out.println("Tunde it has 2 "+oldbody.toString());
                    String currentSes = currentSessiontimeStamp();
                    JSONObject cujs = new JSONObject(oldbody.get(currentSes).toString());
                    System.out.println("Tunde it has key 2 "+cujs);



                    JSONObject cstu = new JSONObject(cujs.get("students").toString());
                    System.out.println("Tunde it has 2 stu "+cstu.toString());

                    cstu.put(matric,time);
                    System.out.println("Tunde it has 2 put student len "+cstu.length());
                    singleton1.addStringSharedPreff(Constants.COUNT,String.valueOf(cstu.length()));



                    cujs.put("students",cstu);
                    // System.out.println("Tunde it has 2 put cujs  "+cujs.toString());

                    JSONObject ctyp = new JSONObject(cujs.get("type").toString());
                    ctyp.put(matric,type);
                    cujs.put("type",ctyp);

                    oldbody.put(currentSessiontimeStamp(),cujs);
                    //System.out.println("Tunde it has 2 put body  "+oldbody.toString());


                    singleton1.addStringSharedPreff(Constants.ATTENDANCE,oldbody.toString());
                    Toast.makeText(context,"Done bye! 2",Toast.LENGTH_SHORT).show();


                }else{
                    JSONObject cstu = new JSONObject();
                    System.out.println("Tunde it has 3");

                    cstu.put(matric,time);

                    JSONObject ctypee = new JSONObject();
                    ctypee.put(matric,type);

                    JSONObject ct = new JSONObject();
                    ct.put("students",cstu);
                    ct.put("staff",staff);
                    ct.put("type",ctypee);
                    ct.put(Constants.SESSION_KEY,currentSessionkey());
                    ct.put(Constants.EVENTNAME,currentSessionEvent());
                    oldbody.put(currentSessiontimeStamp(),ct);
                    singleton1.addStringSharedPreff(Constants.ATTENDANCE,oldbody.toString());
                    singleton1.addStringSharedPreff(Constants.COUNT,"1");

                    Toast.makeText(context,"Done bye! 3",Toast.LENGTH_SHORT).show();


                }

            }catch (JSONException io){
                io.printStackTrace();
            }


        }



    }


    //response from upload

    public String getResponseTypes(String rjs){
        String res = "Error";
        try {
            JSONObject js = new JSONObject(rjs);
            res = js.getString(Constants.MESSAGE);
        }catch (JSONException je){
            je.printStackTrace();
        }
        return res;
    }



    //Count total in dowloaded register
    public String countUsers(){
        String data = singleton1.getPrefKey(Constants.DOREGISTER);

        String res = "0";
        System.out.println("Tunde   ============ "+data);
        if (!data.equals("{}")){
            try {
                JSONObject body = new JSONObject(data);
                int total = body.names().length();
                res = String.valueOf(total);
            }catch (JSONException ie){
                ie.printStackTrace();
            }
        }
        return res;
    }

    //get pass match
    public boolean passMatch(String id,String pass){
        boolean status = false;
        String hex = Whoyouutil.md5(pass);
        String dt = singleton1.getPrefKey(Constants.PASTABLE);
        if (!(dt.equals("{}")|| dt.equals(Constants.CLOSE))){
            try {
                JSONObject jsbody  = new JSONObject(dt);
                for (int i=0;i<jsbody.names().length();i++){
                    String key = (String )jsbody.names().get(i);
                    String value = jsbody.getString(key);

                    //"1001_Patricia_kanayo_four": "96e79218965eb72c92a549dd5a330112",
                    String[] details = key.split("_");
                    if (details[0].equals(id) && value.equals(hex)){
                        status = true;
                        singleton1.setStaff(details[0]);
                        singleton1.setUnit(details[3]);
                        singleton1.setStaffname(details[1]+" "+details[2]);
                        break;

                    }

                }

            }catch (JSONException io){
                io.printStackTrace();
            }
        }

        return  status;
    }

    public String countPass(){
        String ct = "0";
        String dt = singleton1.getPrefKey(Constants.PASTABLE);

        if (!(dt.equals("{}")|| dt.equals(Constants.CLOSE))){
            try {
                JSONObject jsbody  = new JSONObject(dt);
                ct = String.valueOf(jsbody.names().length());

            }catch (JSONException io){
                io.printStackTrace();
            }
        }
        return ct;
    }




}

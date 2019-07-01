package com.phyrelinx.cp.cmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.methods.RequestBuilder;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by olatunde on 7/14/2017.
 */

public class TrackAsync extends AsyncTask<Void, Void, String> {
    private ProgressDialog progressDialog;
    private Activity activity;
    private Context context;
    private String url,id;
    private String  data,endata;
    private AsynTaskCallback listener;


    public TrackAsync(String id, String url, String data, Activity activity, AsynTaskCallback asynTaskCallback) {
        this.activity = activity;
        this.data =data;
        //route to call
        this.id = id+".jpg";

        try {
            this.endata = URLEncoder.encode(data, "UTF-8");
        }catch (IOException io){
            this.endata="Error ";
        }
        this.url = url;
        this.listener=asynTaskCallback;

        progressDialog = new ProgressDialog(this.activity);

    }
    @Override
    protected void onPreExecute() {
        progressDialog.show();
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Void... params) {
        String ans =processRequest();
        System.out.println("Tunde ....ans .."+ans);
        return ans;
    }
    @Override
    protected void onPostExecute(String result) {
        String ans = result;
        progressDialog.dismiss();
        this.listener.processFinish(ans);
    }



    private String processRequest(){
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String urll = url;
        System.out.println("Tunde url............... "+urll);

        try {
            File filepath = new File(activity.getFilesDir(), Constants.PHOTODIRR);

            File picFile = new File(filepath.toString(),id);

            System.out.println("Tunde pic............... "+picFile.toString());
            File file = new File(activity.getFilesDir(),Constants.PHOTODIRR);
            for (File f:file.listFiles()){
                System.out.println("Tunde "+f.toString());
                //f.delete();
            }

            HttpUriRequest auth = RequestBuilder.post()
                    .setUri(new URI(urll))
                    .setEntity(MultipartEntityBuilder.create()
                            .addPart("photo", new FileBody(picFile))
                            .addTextBody("data",data)
                            .build())
                    .addHeader("Authorization", "Phyrelinx".toString())
                    .build();
            response = httpclient.execute(auth);
            HttpEntity entity = response.getEntity();
            System.out.println("--------------------------------------");
            System.out.println(response.getStatusLine());
            //System.out.println(EntityUtils.toString(response.getEntity()));
            String res= EntityUtils.toString(response.getEntity());
            EntityUtils.consumeQuietly(entity);
            return res;
        } catch (Exception ex) {
            ex.printStackTrace();
            //Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            return "Cant connect ..";
        }
    }
}

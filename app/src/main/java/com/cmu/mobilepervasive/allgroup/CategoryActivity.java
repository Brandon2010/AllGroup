package com.cmu.mobilepervasive.allgroup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmu.allgroup.utils.JsonTools.getEvents;


public class CategoryActivity extends ActionBarActivity {
    private ListView listView;
   // private ArrayList<HashMap<String, String>> eventList = new ArrayList<HashMap<String, String>>();
    private List<Map<String, Object>> events;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        new connect().execute();

        Intent intent = getIntent();
        String title = intent.getStringExtra("categoryName");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Events");
        listView = (ListView) findViewById(R.id.listView);

//        HashMap<String, String> tmp = new HashMap<>();
//
//        tmp = new HashMap<String, String>();
//        tmp.put("event", "INI 25th Anniversary");
//        eventList.add(tmp);//modify this
//        tmp = new HashMap<String, String>();
//        tmp.put("event", "Spring Festival Party");
//        eventList.add(tmp);
//        tmp = new HashMap<String, String>();
//        tmp.put("event", "Movie night");
//        eventList.add(tmp);

        //ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>
        //        (this, android.R.layout.simple_list_item_1, eventList);
        //listView.setAdapter(myArrayAdapter);

        SimpleAdapter sa = new SimpleAdapter(this, events, android.R.layout.simple_list_item_2,
                new String[]{"event"}, new int[]{android.R.id.text2});
        listView.setAdapter(sa);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CategoryActivity.this, EventActivity.class);
                HashMap<String, String> tmp = (HashMap<String, String>) listView.getItemAtPosition(pos);
                intent.putExtra("eventName", tmp.get("event"));

                startActivity(intent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class connect extends AsyncTask {
        // 通过AsyncTask类提交数据 异步显示


        @Override
        protected Object doInBackground(Object... params_obj) {
            String responseStr = "";
            String uriAPI = "http://128.237.218.208:8080/AllGroupServerSide/servlet/EventServlet?eventOperation=getEventCate&id=2";
            HttpGet httpRequest = new HttpGet(uriAPI);
            /*发送请求并等待响应*/
            HttpResponse httpResponse = null;

            try {
                httpResponse = new DefaultHttpClient().execute(httpRequest);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(httpResponse.getStatusLine().getStatusCode() == 200)
            {
                try {
                    responseStr = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                    Log.v("DEBUG", responseStr);

                    events =  getEvents("events", responseStr);
                    for(int i = 0; i < events.size(); i++){
                        Log.v("DEBUG",events.get(i).toString());
                    }

                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else{
                System.out.println("!!!!!!!!!!!!!!!!!!Error Response: "+httpResponse.getStatusLine().toString());
            }
            return responseStr;
        }
    }

}
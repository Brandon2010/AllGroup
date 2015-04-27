package com.cmu.mobilepervasive.allgroup;

import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cmu.allgroup.utils.JsonTools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.cmu.allgroup.utils.JsonTools.getEvents;


public class CategoryActivity extends ActionBarActivity {
    private ListView listView;
   // private ArrayList<HashMap<String, String>> eventList = new ArrayList<HashMap<String, String>>();
    private List<Map<String, Object>> events;
    private View loadingView;
    private SimpleAdapter sa;
    private boolean isEnd = false;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message paramMessage) {
            if (paramMessage.what == 1) {
                loadingView.setVisibility(View.GONE);
            } else if (paramMessage.what == 2) {
                listView.removeFooterView(loadingView);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Intent intent = getIntent();
        String title = intent.getStringExtra("categoryName");

        String uriAPI = getResources().getText(R.string.host) + "EventServlet?eventOperation=getEventCate&id=" + intent.getLongExtra("categoryId", 0);
        Log.v("DEBUG", uriAPI);
        new connect().execute(uriAPI);
//
//        Intent intent = getIntent();
//        String title = intent.getStringExtra("categoryName");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Events");
        listView = (ListView) findViewById(R.id.listView);
        loadingView = LayoutInflater.from(this).inflate(R.layout.listfooter,
                null);

        listView.addFooterView(loadingView);


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
        List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();

        sa = new SimpleAdapter(this, eventList, android.R.layout.simple_list_item_2,
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
        Log.v("DEBUG", "end in create");
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


    public void serverDataArrived(List list, boolean isEnd) {
        this.isEnd = isEnd;
        Iterator iter = list.iterator();
//        while (iter.hasNext()) {
//            mData.add((Map<String, Object>) iter.next());
//        }
        android.os.Message localMessage = new Message();
        if (!isEnd) {
            localMessage.what = 1;
        } else {
            localMessage.what = 2;
        }

        this.handler.sendMessage(localMessage);
    }


    public class connect extends AsyncTask <String, Integer, List<Map<String, Object>>>{
        // 通过AsyncTask类提交数据 异步显示
        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result == null) {
                Toast.makeText(CategoryActivity.this, "Network Problem", Toast.LENGTH_LONG).show();
                return;
            }
            events = result;
            List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < events.size(); i++) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("title", events.get(i).get("name").toString());
                list.add(item);
            }
            sa = new SimpleAdapter(CategoryActivity.this, list, android.R.layout.simple_list_item_2,
                    new String[]{"title"}, new int[]{android.R.id.text2});
            listView.setAdapter(sa);
//            sa.notifyDataSetChanged();
            serverDataArrived(result, true);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected List<Map<String, Object>> doInBackground(String... arg0) {
            ArrayList<Map<String, Object>> tmpEvents = null;

            try {
                Log.v("DEBUG", arg0[0]);
                URL url = new URL(arg0[0]);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                int code = connection.getResponseCode();
                if (code == 200) {
                    String jsonString = ChangeInputStream(connection
                            .getInputStream());
                    tmpEvents = (ArrayList<Map<String, Object>>) JsonTools
                            .getEvents("events", jsonString);
                    for(int i = 0; i < tmpEvents.size(); i++){
                        Log.v("DEBUG", tmpEvents.get(i).toString());
                    }
                }
//                System.out.println(tmpEvents.size() + "hits");
                Log.v("DEBUG", tmpEvents.size() + "hits");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return tmpEvents;
        }
        /**
         * Get json string
         *
         * @param inputStream
         * @return
         */
        public String ChangeInputStream(InputStream inputStream) {
            String jsonString = "";
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len = 0;
            byte[] data = new byte[1024];

            try {
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                jsonString = new String(outputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonString;
        }
    }

}
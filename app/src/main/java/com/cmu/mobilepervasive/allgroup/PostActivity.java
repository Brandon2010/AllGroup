package com.cmu.mobilepervasive.allgroup;

import android.content.Intent;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cmu.allgroup.utils.JsonTools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class PostActivity extends ActionBarActivity {

    private ListView listView;
    private EditText editText;
    private Button postButton;
    private View loadingView;
    private SimpleAdapter sa;
    private boolean isEnd = false;

    //private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> posts = null;

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
        setContentView(R.layout.activity_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final int eventId = (int)intent.getLongExtra("eventId", 0);
        String uriAPI = getResources().getText(R.string.host) + "PostServlet?postOperation=getPostsEvent&id=" + eventId;
        Log.v("DEBUG", uriAPI);
        new getPost().execute(uriAPI);

        listView = (ListView) findViewById(R.id.post_list_1);
        loadingView = LayoutInflater.from(this).inflate(R.layout.listfooter,
                null);
        listView.addFooterView(loadingView);

//        map.put("title", "Xi Wang");
//        map.put("info", "The Information Networking Institute (INI) is marking " +
//                "25 years of world-class graduate education in networking, " +
//                "security and mobility. The celebration will culminate on " +
//                "Saturday, April 18.");
//        map.put("icon", R.drawable.xi);
//        list.add(map);

//        map = new HashMap<String, Object>();
//        map.put("title", "Shan Gao");
//        map.put("info", "The celebration is coming. This is the post area.");
//        map.put("icon", R.drawable.shan);
//        list.add(map);

        List<Map<String, Object>> postList = new ArrayList<Map<String, Object>>();
        sa = new SimpleAdapter(this, postList, R.layout.post_item,
                new String[] {"title", "info", "icon"},
                new int[] {R.id.post_title, R.id.post_info, R.id.post_icon});
        listView.setAdapter(sa);

        editText = (EditText) findViewById(R.id.edit_post);
        postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                new sendPost().execute(userId, eventId, content, dateFormat.format(cal.getTime()));
//                Map<String, Object> map = new HashMap<String, Object>();
//                //TODO: CHANGE TITLE, Hardcoded icon
//                map.put("title", "Zhengyang Zuo");
//                map.put("info", editText.getText().toString());
//                map.put("icon", R.drawable.zhengyang);
//                posts.add(map);

                //sa.notifyDataSetChanged();

                editText.setText("");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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
        android.os.Message localMessage = new android.os.Message();
        if (!isEnd) {
            localMessage.what = 1;
        } else {
            localMessage.what = 2;
        }

        this.handler.sendMessage(localMessage);
    }
    public class getPost extends AsyncTask <String, Integer, List<Map<String, Object>>>{
        // 通过AsyncTask类提交数据 异步显示
        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result == null) {
                Toast.makeText(PostActivity.this, "Network Problem", Toast.LENGTH_LONG).show();
                return;
            }
            posts = result;
            List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < posts.size(); i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("title", posts.get(i).get("userName").toString());
                item.put("info", posts.get(i).get("content").toString());
                item.put("icon", R.drawable.shan);
                list.add(item);
            }
            sa = new SimpleAdapter(PostActivity.this, posts, R.layout.post_item,
                    new String[] {"title", "info", "icon"},
                    new int[] {R.id.post_title, R.id.post_info, R.id.post_icon});
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
                    posts = (ArrayList<Map<String, Object>>) JsonTools
                            .getEvents("posts", jsonString);
                    for(int i = 0; i < posts.size(); i++){
                        Log.v("DEBUG", posts.get(i).toString());
                    }
                }
//                System.out.println(tmpEvents.size() + "hits");
                Log.v("DEBUG", posts.size() + "hits");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return posts;
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

    public class sendPost extends AsyncTask <String, Integer, List<Map<String, Object>>>{
        // 通过AsyncTask类提交数据 异步显示
        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result == null) {
                Toast.makeText(PostActivity.this, "Network Problem", Toast.LENGTH_LONG).show();
                return;
            }
            posts = result;
            List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < posts.size(); i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("title", posts.get(i).get("userName").toString());
                item.put("info", posts.get(i).get("content").toString());
                item.put("icon", R.drawable.shan);
                list.add(item);
            }
            sa = new SimpleAdapter(PostActivity.this, posts, R.layout.post_item,
                    new String[] {"title", "info", "icon"},
                    new int[] {R.id.post_title, R.id.post_info, R.id.post_icon});
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
            ArrayList<Map<String, Object>> categories = null;

            System.out.println("In AsncTask!!");
            try {
                URL url = new URL(getResources().getText(R.string.host)
                        + "PostServlet");
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                StringBuffer params = new StringBuffer();
                params.append("postOperation=add&userId=").append(userId).
                        append("&eventId=").append(arg0[0]).
                        append("&content=").append(arg0[1]).
                        append("&time=").append(arg0[2]);
                byte[] bypes = params.toString().getBytes();
                connection.getOutputStream().write(bypes);
                int code = connection.getResponseCode();
                if (code == 200) {
                    String jsonString = ChangeInputStream(connection
                            .getInputStream());
                    categories = (ArrayList<Map<String, Object>>) JsonTools
                            .getCategories("categories", jsonString);
                }
                System.out.println(categories.size() + "hits");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return categories;
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

package com.cmu.mobilepervasive.allgroup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;


public class EventActivity extends ActionBarActivity {
    //private ArrayList<ImageView> imageView = new ArrayList<ImageView>();
    private final static Logger LOGGER = Logger.getLogger(EventActivity.class.getName());
    private ListView listView1;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<Map<String, Object>> picList = new ArrayList<Map<String, Object>>();
    private TextView description;
    private Button chatButton;
    private Button postButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        String title = intent.getStringExtra("eventName");
        String eventDescription = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        String time = intent.getStringExtra("time");
        time = time.substring(0, time.length() - 2);

        String image_url = getResources().getText(R.string.host)
                + "EventServlet?eventOperation=image&path=" + intent.getStringExtra("image_url");
        new GetImageAsyncTask().execute(image_url);

        final Long eventId = intent.getLongExtra("eventId", 0);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView1 = (ListView) findViewById(R.id.event_list1);
        list.add(title);
        list.add(time);
        list.add(location);

        //list.add("INI 25th Anniversary");//modify this
        //list.add("May 14th, 09:00");
        //list.add("Cohon University Center");
        // ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>
        //         (this, android.R.layout.simple_list_item_1, list);
        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setPadding(15, 0, 0, 0);

                return view;
            }

            ;

        };
        listView1.setAdapter(myArrayAdapter);

        /*// TODO: imageView is hardcoded
        imageView.add((ImageView) findViewById(R.id.event_icon1));
        imageView.add((ImageView) findViewById(R.id.event_icon2));
        imageView.add((ImageView) findViewById(R.id.event_icon3));

        imageView.get(0).setImageDrawable(R.drawable.add_food);
        */

        imageView = (ImageView) findViewById(R.id.event_image);
//        imageView.setImageResource(R.drawable.cover);

        description = (TextView) findViewById(R.id.event_description);
        description.setText(eventDescription);

        chatButton = (Button) findViewById(R.id.event_chat_button);
        postButton = (Button) findViewById(R.id.event_post_button);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, ChatActivity.class);

                startActivity(intent);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, PostActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
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


    /**
     * The specific thread to get image
     *
     * @author Brandon
     * @version 1.0 2014-05-23
     */
    public class GetImageAsyncTask extends AsyncTask<String, Integer, byte[]> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(byte[] result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result == null) {
                Toast.makeText(EventActivity.this, "Network Error", Toast.LENGTH_LONG);
                return;
            }
            byte[] data = result;
            int length = data.length;
            Bitmap bitMap = BitmapFactory.decodeByteArray(data, 0, length);
            imageView.setImageBitmap(bitMap);
//            LayoutParams params = imageView.getLayoutParams();
//            params.height=450;
//            params.
////            params.width =100;
//            imageView.setLayoutParams(params);
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

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected byte[] doInBackground(String... arg0) {
            byte[] result = null;
            InputStream in = null;

            System.out.println("In AsncTask!!");
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                int code = connection.getResponseCode();
                if (code == 200) {
                    System.out.println("in result init");
                    in = connection.getInputStream();
                    result = readStream(in);
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return result;
        }

        /**
         * Read Image Stream
         *
         * @param in
         * @return
         * @throws Exception
         */
        public byte[] readStream(InputStream in) throws Exception {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            in.close();
            return outputStream.toByteArray();
        }

    }


}
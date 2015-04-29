package com.cmu.mobilepervasive.allgroup;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cmu.allgroup.utils.JsonTools;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EventActivity extends ActionBarActivity {
    private ListView listView1;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<Map<String, Object>> picList = new ArrayList<Map<String, Object>>();
    private TextView description;

    private Button chatButton;
    private Button postButton;
    //private ArrayList<ImageView> imageView = new ArrayList<ImageView>();
    private final static Logger LOGGER = Logger.getLogger(EventActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        String title = intent.getStringExtra("eventName");
        String eventDescription = intent.getStringExtra("description");
        String location = intent.getStringExtra("location");
        String time = intent.getStringExtra("time");
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
        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, list);
        listView1.setAdapter(myArrayAdapter);

        /*// TODO: imageView is hardcoded
        imageView.add((ImageView) findViewById(R.id.event_icon1));
        imageView.add((ImageView) findViewById(R.id.event_icon2));
        imageView.add((ImageView) findViewById(R.id.event_icon3));

        imageView.get(0).setImageDrawable(R.drawable.add_food);
        */

        ImageView imageView = (ImageView) findViewById(R.id.event_image);
        imageView.setImageResource(R.drawable.cover);

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

}
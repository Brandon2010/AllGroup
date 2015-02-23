package com.cmu.mobilepervasive.allgroup;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


public class EventActivity extends ActionBarActivity {
    private ListView listView1;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<Map<String, Object>> picList = new ArrayList<Map<String, Object>>();
    private TextView description;

    //private ArrayList<ImageView> imageView = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView1 = (ListView) findViewById(R.id.event_list1);
        list.add("INI 25th Anniversary");//modify this
        list.add("May 14th, 09:00");
        list.add("Cohon University Center");
        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, list);
        listView1.setAdapter(myArrayAdapter);

        /*// TODO: imageView is hardcoded
        imageView.add((ImageView) findViewById(R.id.event_icon1));
        imageView.add((ImageView) findViewById(R.id.event_icon2));
        imageView.add((ImageView) findViewById(R.id.event_icon3));

        imageView.get(0).setImageDrawable(R.drawable.add_food);
        */

        description = (TextView)findViewById(R.id.event_description);
        description.setText("The Information Networking Institute (INI) is marking " +
                "25 years of world-class graduate education in networking, " +
                "security and mobility. The celebration will culminate on " +
                "Saturday, April 18.");

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

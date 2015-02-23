package com.cmu.mobilepervasive.allgroup;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChatActivity extends ActionBarActivity {

    private ListView listView;
    private EditText editText;
    private Button chatButton;
    private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.chat_list_1);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "Zack");
        map.put("info", "Hello Everybody.");
        map.put("icon", R.drawable.add_food);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "Bob");
        map.put("info", "Hi Zack. Anyone else here?");
        map.put("icon", R.drawable.add_friends);
        list.add(map);

        // TODO: Now just use post_item here
        final SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.post_item,
                new String[] {"title", "info", "icon"},
                new int[] {R.id.post_title, R.id.post_info, R.id.post_icon});
        listView.setAdapter(adapter);

        editText = (EditText) findViewById(R.id.edit_chat);

        chatButton = (Button) findViewById(R.id.chat_button);
        chatButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", "Me");
                map.put("info", editText.getText().toString());
                map.put("icon", R.drawable.icon);
                list.add(map);

                adapter.notifyDataSetChanged();

                editText.setText("");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

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
    //private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    //private ArrayList<Map<String, Object>> listTmp = new ArrayList<Map<String, Object>>();
    //private ListView listViewTmp;


    private MessagesListAdapter adapter;

    private ArrayList<Message> listMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.chat_list_1);

        /*Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "Zack");
        map.put("info", "Hello Everybody.");
        map.put("icon", R.drawable.add_food);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "Bob");
        map.put("info", "Hi Zack. Anyone else here?");
        map.put("icon", R.drawable.add_friends);
        list.add(map);

        final SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.chat_item_other,
                new String[] {"title", "info", "icon"},
                new int[] {R.id.chat_title, R.id.chat_info, R.id.chat_icon});
        listView.setAdapter(adapter);*/


        /*final SimpleAdapter adapterTmp = new SimpleAdapter(this, list, R.layout.chat_item_other,
                new String[] {"title", "info", "icon"},
                new int[] {R.id.chat_title, R.id.chat_info, R.id.chat_icon});
        listViewTmp.setAdapter(adapter);*/


        listMessages = new ArrayList<Message>();

        listMessages.add(new Message("Xi Wang", "Hello Everybody.", R.drawable.xi, false));
        listMessages.add(new Message("Shan Gao", "Hi Xi. Anyone else here?", R.drawable.shan, false));

        adapter = new MessagesListAdapter(this, listMessages);
        listView.setAdapter(adapter);


        editText = (EditText) findViewById(R.id.edit_chat);

        chatButton = (Button) findViewById(R.id.chat_button);
        chatButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listMessages.add(new Message("Zhengyang Zuo", editText.getText().toString(), R.drawable.zhengyang, true));

                /*Map<String, Objdddect> map = new HashdMap<String, Object>();
                map.put("title", "Me");
                map.put("info", editText.getText().toString());
                map.put("icon", R.drawable.icon);
                list.add(map);*/

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

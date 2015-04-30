package com.cmu.mobilepervasive.allgroup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
//import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cmu.allgroup.utils.ClientThread;

import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;



public class ChatActivity extends ActionBarActivity {

    private ListView listView;
    private EditText editText;
    private Button chatButton;
    private String username;
    private long user_id;
    private Socket socket;
    private OutputStream os;
    private Thread thread;
    //private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    //private ArrayList<Map<String, Object>> listTmp = new ArrayList<Map<String, Object>>();


    private MessagesListAdapter adapter;

    private ArrayList<Message> listMessages;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x234) {
                String content = msg.obj.toString();
                String[] contents = content.split("_");
                String name = contents[0];
                String mes = contents[1];
                if (name.equals(username)) {
                    if (name.equals("Zhengyang Zuo")) {
                        listMessages.add(new Message(name, mes, R.drawable.zhengyang, true));
                    } else if (name.equals("Xi Wang")) {
                        listMessages.add(new Message(name, mes, R.drawable.xi, true));
                    } else {
                        listMessages.add(new Message(name, mes, R.drawable.shan, true));
                    }
                } else {
                    if (name.equals("Zhengyang Zuo")) {
                        listMessages.add(new Message(name, mes, R.drawable.zhengyang, false));
                    } else if (name.equals("Xi Wang")) {
                        listMessages.add(new Message(name, mes, R.drawable.xi, false));
                    } else {
                        listMessages.add(new Message(name, mes, R.drawable.shan, false));
                    }
                }

                adapter.notifyDataSetChanged();
            }
        }
    };


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

//        listMessages.add(new com.cmu.mobilepervasive.allgroup.Message("Xi Wang", "Hello Everybody.", R.drawable.xi, false));
//        listMessages.add(new com.cmu.mobilepervasive.allgroup.Message("Shan Gao", "Hi Xi. Anyone else here?", R.drawable.shan, false));


        adapter = new MessagesListAdapter(this, listMessages);
        listView.setAdapter(adapter);


        editText = (EditText) findViewById(R.id.edit_chat);

        chatButton = (Button) findViewById(R.id.chat_button);
        chatButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                listMessages.add(new Message("Zhengyang Zuo", editText.getText().toString(), R.drawable.zhengyang, true));
                String mes = editText.getText().toString();
//                if (username.equals("Zhengyang Zuo")) {
//                    listMessages.add(new Message(username, mes, R.drawable.zhengyang, true));
//                } else if (username.equals("Xi Wang")) {
//                    listMessages.add(new Message(username, mes, R.drawable.zhengyang, true));
//                } else {
//                    listMessages.add(new Message(username, mes, R.drawable.shan, true));
//                }
                try {
                    os.write((username + "_" + mes + "\r\n").getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();

                editText.setText("");
            }
        });

        SharedPreferences settings = this.getSharedPreferences("usersetting", 0);
        username = settings.getString("name", "Xi Wang");
        user_id = settings.getLong("user_id", 7);


        thread = new Thread() {
            public void run() {

                try {
                    socket = new Socket(getResources().getText(R.string.chat_host).toString(), 20000);
                    new Thread(new ClientThread(socket, handler)).start();
                    os = socket.getOutputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
        thread.start();


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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            os.close();
            thread.interrupt();
        } catch (Exception e) {

        }
    }
}

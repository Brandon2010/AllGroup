package com.cmu.mobilepervasive.allgroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.cmu.allgroup.utils.HolderView;
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


public class AddFriendActivity extends ActionBarActivity {
    Button doneButton = null;
    ListView lv = null;
    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    MyAdapter adapter;
    ArrayList<String> listStr = null;
    String[] name = new String[]{"Shan Gao", "Xi Wang"};
    Object[] icons = new Object[]{R.drawable.shan, R.drawable.xi};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        getSupportActionBar().setTitle("Add Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //listView = (ListView) findViewById(R.id.listView);
        lv = (ListView) this.findViewById(R.id.lv);
        showCheckBoxListView();
        doneButton = (Button) findViewById(R.id.add_friend_done_button);
        doneButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendActivity.this.finish();
            }
        });


//
//        SimpleAdapter sa = new SimpleAdapter(this, list, R.layout.add_friend,
//                new String[] {"title", "icon"},
//                new int[] {R.id.friend_name, R.id.friend_icon});
//        listView.setAdapter(sa);

    }

    public void showCheckBoxListView() {

        for (int i = 0; i < name.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("item_tv", name[i]);
            map.put("item_icon", icons[i]);
            map.put("item_cb", false);
            list.add(map);

            adapter = new MyAdapter(this, list, R.layout.add_friend,
                    new String[]{"item_icon", "item_tv", "item_cb"}, new int[]{R.id.item_icon,
                    R.id.item_tv, R.id.item_cb});
            lv.setAdapter(adapter);
            listStr = new ArrayList<String>();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int position, long arg3) {
                    HolderView holder = (HolderView) view.getTag();
                    holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
                    MyAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态
                    if (holder.cb.isChecked() == true) {
                        listStr.add(name[position]);
                    } else {
                        listStr.remove(name[position]);
                    }
                }

            });
        }
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

    public static class MyAdapter extends BaseAdapter {
        public static HashMap<Integer, Boolean> isSelected;
        private Context context = null;
        private LayoutInflater inflater = null;
        private List<HashMap<String, Object>> list = null;
        private String keyString[] = null;
        private String itemString = null; // 记录每个item中textview的值
        private int idValue[] = null;// id值

        public MyAdapter(Context context, List<HashMap<String, Object>> list,
                         int resource, String[] from, int[] to) {
            this.context = context;
            this.list = list;
            keyString = new String[from.length];
            idValue = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, idValue, 0, to.length);
            inflater = LayoutInflater.from(context);
            init();
        }

        // 初始化 设置所有checkbox都为未选择
        public void init() {
            isSelected = new HashMap<Integer, Boolean>();
            for (int i = 0; i < list.size(); i++) {
                isSelected.put(i, false);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup arg2) {
            HolderView holder = new HolderView();

            if (view == null) {
                view = inflater.inflate(R.layout.add_friend, null);
            }
            holder.iv = (ImageView) view.findViewById(R.id.item_icon);
            holder.tv = (TextView) view.findViewById(R.id.item_tv);
            holder.cb = (CheckBox) view.findViewById(R.id.item_cb);
            view.setTag(holder);

        HashMap<String, Object> map = list.get(position);
        if (map != null) {

            itemString = (String) map.get(keyString[1]);
            holder.iv.setImageResource((int) map.get(keyString[0]));
            holder.tv.setText(itemString);
        }
        holder.cb.setChecked(isSelected.get(position));
        return view;
    }

}
}
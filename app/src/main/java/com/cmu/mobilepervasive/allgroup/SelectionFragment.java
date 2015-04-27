package com.cmu.mobilepervasive.allgroup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cmu.allgroup.utils.JsonTools.getEvents;

//import android.net.Uri;

public class SelectionFragment extends Fragment {

    private static final String TAG = "debug";
    private Context context;
    private String[] categories = {"Party", "Meeting", "Anniversary", "Sports", "Unorganized"};

    private ListView listView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectionFragment.
     */
    public static SelectionFragment newInstance(String param1, String param2) {
        SelectionFragment fragment = new SelectionFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    //
    public SelectionFragment() {
        // Required empty public constructor
    }

    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //super.onCreateView(inflater, container, savedInstanceState);
        View category_view = inflater.inflate(R.layout.fragment_selection, container, false);
        listView = (ListView) category_view.findViewById((R.id.category_list));
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < categories.length; i++) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("title", categories[i]);
            list.add(item);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String out = "Click" + id;
                Log.v(TAG, out);
                Intent intent = new Intent(getActivity(), CategoryActivity.class);

                HashMap<String, String> map = (HashMap<String, String>)listView.getItemAtPosition(position);

                intent.putExtra("categoryName", map.get("title"));

                startActivity(intent);
            }
        });
        SimpleAdapter sa = new SimpleAdapter(this.context, list, android.R.layout.simple_list_item_2,
                new String[]{"title"}, new int[]{android.R.id.text2});
        listView.setAdapter(sa);
        return category_view;
    }

    //    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = (MainActivity) activity;
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    //
    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public void updateListView(String category) {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("title", category);
        list.add(item);
        for (int i = 0; i < categories.length; i++) {
            HashMap<String, String> existed = new HashMap<String, String>();
            existed.put("title", categories[i]);
            list.add(existed);
        }

        SimpleAdapter sa = new SimpleAdapter(this.context, list, android.R.layout.simple_list_item_2,
                new String[]{"title"}, new int[]{android.R.id.text2});
        listView.setAdapter(sa);
        sa.notifyDataSetChanged();
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

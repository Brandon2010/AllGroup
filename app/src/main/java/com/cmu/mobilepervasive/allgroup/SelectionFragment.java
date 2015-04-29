package com.cmu.mobilepervasive.allgroup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import android.net.Uri;

public class SelectionFragment extends Fragment {

    private static final String TAG = "debug";
    private Context context;
    //private String[] categories = {"Party", "Meeting", "Anniversary", "Sports", "Unorganized"};
    private List<Map<String, Object>> filterData;
    private ListView listView;
    private SimpleAdapter sa;
    private View loadingView;
    private boolean isEnd = false;
    //private long MainActivity.userId = -1;

    //private boolean isInit = true;

    Handler handler = new Handler() {
        public void handleMessage(Message paramMessage) {
            if (paramMessage.what == 1) {
                loadingView.setVisibility(View.GONE);
            } else if (paramMessage.what == 2) {
                listView.removeFooterView(loadingView);
            }
        }
    };

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

        Log.d(TAG, "Into onCreateView");

        if (MainActivity.userId <= 0) {
            try {
                MainActivity.semUserCate.acquire();
                //semUserCate.acquire();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        //MainActivity.userId = MainActivity.MainActivity.userId;

        // Inflate the layout for this fragment
        //super.onCreateView(inflater, container, savedInstanceState);
        View category_view = inflater.inflate(R.layout.fragment_selection, container, false);

//        Session session = Session.getActiveSession();
//
//        if (session != null && session.isOpened()) {
//
//        }

        //Log.d(TAG, "Before getting categories from server");

        Thread t = new Thread() {
            @Override
            public void run() {

                Log.d(TAG, "MainActivity.userId: " + MainActivity.userId);
                if (MainActivity.userId <= 0) {
                    try {
                        Log.d(TAG, "Before in run acquire");
                        MainActivity.semUserCate.acquire();
                        MainActivity.semUserCate.release();
                        Log.d(TAG, "After in run acquire");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //userId = MainActivity.userId;
                Log.d(TAG, "userId: " + MainActivity.userId);

                String url = getResources().getText(R.string.host) + "CategoryServlet?cateOperation=getId&userId=" + MainActivity.userId;
                new GetCateAsyncTask().execute(url);
            }
        };
        t.start();

//        String url = getResources().getText(R.string.host) + "CategoryServlet?cateOperation=getId&MainActivity.userId=1";
//        new GetCateAsyncTask().execute(url);
        listView = (ListView) category_view.findViewById((R.id.category_list));

        loadingView = LayoutInflater.from(this.getActivity()).inflate(R.layout.listfooter,
                null);

        listView.addFooterView(loadingView);
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

//        for (int i = 0; i < categories.length; i++) {
//            HashMap<String, String> item = new HashMap<String, String>();
//            item.put("title", categories[i]);
//            list.add(item);
//        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String out = "Click" + filterData.get((int)id).get("name");
                Log.v(TAG, out);
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                //HashMap<String, String> map = (HashMap<String, String>)listView.getItemAtPosition(position);
                intent.putExtra("categoryId", (Long)filterData.get((int)id).get("cateId"));
                intent.putExtra("categoryName", (String)filterData.get((int)id).get("name"));

                startActivity(intent);
            }
        });
        sa = new SimpleAdapter(this.context, list, android.R.layout.simple_list_item_2,
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
        new CreateCateAsyncTask().execute(category);

    }
    public void serverDataArrived(List list, boolean isEnd) {
        this.isEnd = isEnd;
        Iterator iter = list.iterator();
//        while (iter.hasNext()) {
//            mData.add((Map<String, Object>) iter.next());
//        }
        Message localMessage = new Message();
        if (!isEnd) {
            localMessage.what = 1;
        } else {
            localMessage.what = 2;
        }

        this.handler.sendMessage(localMessage);
    }

    public class GetCateAsyncTask extends AsyncTask <String, Integer, List<Map<String, Object>>> {
        /*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result == null) {
                Toast.makeText(SelectionFragment.this.getActivity(), "Network Problem", Toast.LENGTH_LONG).show();
                return;
            }
            filterData = result;
            List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < filterData.size(); i++) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("title", filterData.get(i).get("name").toString());
                list.add(item);
            }
            sa = new SimpleAdapter(SelectionFragment.this.context, list, android.R.layout.simple_list_item_2,
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
            ArrayList<Map<String, Object>> categories = null;

//            System.out.println("In AsncTask!!");
            try {
                Log.v(TAG, arg0[0]);
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
                    categories = (ArrayList<Map<String, Object>>) JsonTools
                            .getCategories("categories", jsonString);
                }
//                System.out.println(categories.size() + "hits");
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

    public List<Map<String, Object>> getFilterData(){
        return filterData;
    }



    public class CreateCateAsyncTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            super.onPostExecute(result);
            if(result == null) {
                Toast.makeText(SelectionFragment.this.getActivity(), "Network Problem", Toast.LENGTH_LONG).show();
                return;
            }
            filterData = result;

            if (MainActivity.state == MainActivity.IMPORT) {
                Log.d(TAG, "Before release semImport");
                MainActivity.semImport.release();
            }

            List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < filterData.size(); i++) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("title", filterData.get(i).get("name").toString());
                list.add(item);
            }
            sa = new SimpleAdapter(SelectionFragment.this.context, list, android.R.layout.simple_list_item_2,
                    new String[]{"title"}, new int[]{android.R.id.text2});
            listView.setAdapter(sa);
//            sa.notifyDataSetChanged();

            // TODO Check if MainActivity is importing
            if (MainActivity.state == MainActivity.NORMAL) {
                serverDataArrived(result, true);
            }

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
        protected List<Map<String, Object>> doInBackground(String... arg0) {
            ArrayList<Map<String, Object>> categories = null;

            System.out.println("In AsncTask!!");
            try {
                URL url = new URL(getResources().getText(R.string.host)
                        + "CategoryServlet");
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                StringBuffer params = new StringBuffer();
                params.append("cateOperation=create&userId=")
                        .append(MainActivity.userId).append("&name=")
                        .append(arg0[0]);

                Log.d(TAG, "userId: " + MainActivity.userId);

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

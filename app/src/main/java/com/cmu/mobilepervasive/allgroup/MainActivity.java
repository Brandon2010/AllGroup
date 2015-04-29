package com.cmu.mobilepervasive.allgroup;

//import android.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cmu.allgroup.utils.JsonTools;
import com.facebook.AppEventsLogger;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "debug";
    private static final String _EVENT = "New Event";
    private static final String _Category = "New Category";
    private static final String _PRIVACYPOLICY = "Privacy Policy";
    private static final String _IMPORT = "Import FB Events";

    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = SETTINGS + 1;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private MenuItem settings;
    private MenuItem privacyPolicy;
    private MenuItem newCategory;
    private MenuItem newEvent;

    private MenuItem importEvent;
  //  private ImageButton edit;
    private boolean isResumed = false;

    // TODO Use semaphore temporarily
    public static Semaphore semInner = new Semaphore(1, false);
    public static Semaphore semUserCate = new Semaphore(1, false);
    public static Semaphore semImport = new Semaphore(1, false);


    public static final int NORMAL = 0, IMPORT = 1;
    public static int state = NORMAL;


    public static long userId = -1;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session,
                                 SessionState state, Exception exception) {
                    onSessionStateChange(session, state, exception);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

        constructActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
        constructActionBar();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragments[SELECTION].isVisible()) {
            constructActionBar();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment

            Log.d(TAG, "onResumeFragments");

            showFragment(SELECTION, false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
            getSupportActionBar().setCustomView(actionbarLayout);
//            edit = (ImageButton) findViewById(R.id.right_imbt);
//            edit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.v(TAG, "click on edit dialogue");
//                    showEditDialog();
//                }
//            });
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            showFragment(SPLASH, false);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            //getSupportActionBar().setCustomView(null);
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[SELECTION].isVisible()) {
            if (menu.size() == 0) {
                newEvent = menu.add(_EVENT);
                newCategory = menu.add(_Category);
                settings = menu.add(R.string.settings);
                privacyPolicy = menu.add(_PRIVACYPOLICY);
                importEvent = menu.add(_IMPORT);

            }
            return true;
        } else {
            menu.clear();
            settings = null;
            privacyPolicy = null;
            newEvent = null;
            newCategory = null;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.equals(settings)) {
            showFragment(SETTINGS, true);
//            getSupportActionBar().setCustomView(null);
//            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            return true;
        }
        else if(item.equals(privacyPolicy)){
            Intent intent = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        }
        else if(item.equals(newEvent)){
            SelectionFragment sf = (SelectionFragment)fragments[SELECTION];
            List<Map<String, Object>> filterdata = sf.getFilterData();
            ArrayList<String> list = new ArrayList<>();

            Bundle bundle = new Bundle();
            //Map<String, Long> map = new HashMap<>();

            for(int i = 0; i < filterdata.size(); i++){
                list.add((String)filterdata.get(i).get("name"));
                //map.put((String)filterdata.get(i).get("name"), (Long)filterdata.get(i).get("cateId"));
                bundle.putLong((String)filterdata.get(i).get("name"), (Long)filterdata.get(i).get("cateId"));
            }

            Intent intent = new Intent(MainActivity.this, NewEventActivity.class);
            intent.putStringArrayListExtra("filterData", list);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
        else if(item.equals(newCategory)){
//            Log.v(TAG, "EDIT");
            AlertDialog.Builder inner = new AlertDialog.Builder(MainActivity.this);
            inner.setTitle(R.string.edit_category);

            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.create_category_layout, null);
            inner.setView(dialogView);

            final EditText editText = (EditText) dialogView.findViewById(R.id.category_name);

            inner.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            inner.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // create new category
                    String newName = editText.getText().toString();
                    SelectionFragment sf = (SelectionFragment)fragments[SELECTION];
                    sf.updateListView(newName);
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = inner.create();
            alertDialog.show();
        }
        else if (item.equals(importEvent)) {

            Log.d(TAG, "Click Import");

            Thread t = new Thread() {
                public void run() {
                    Session session = Session.getActiveSession();

                    if (session != null && session.isOpened()) {

                        Log.d(TAG, "Session opened");

                        state = IMPORT;

                        try {
                            Log.d(TAG, "Before acquire semImport 1: " + semImport.availablePermits());
                            semImport.acquire();
                            Log.d(TAG, "After acquire semImport 1: " + semImport.availablePermits());

                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }

                        // TODO Getting event IDs
                        final List<String> eventIDs = new ArrayList<>();
                        Response eventsResponse = new Request(
                                session,
                                "/me/events",
                                null,
                                HttpMethod.GET,
                                null
                        ).executeAndWait();
                        JSONObject jsonEvents = eventsResponse.getGraphObject().getInnerJSONObject();
                        Log.d(TAG, jsonEvents.toString());
                        eventIDs.addAll(JsonTools.getEventIDsFromFB("data", jsonEvents.toString()));

                        Log.d(TAG, "Between getting eventIDs and events");

                        // TODO Getting events
                        List<Request> requests = new ArrayList<>();
                        for (String id : eventIDs) {
                            //Long id = (Long) map.get("id");
                            requests.add(
                                    new Request(
                                            session,
                                            "/" + id,
                                            null,
                                            HttpMethod.GET,
                                            null
                                    ));
                        }
                        RequestBatch batch = new RequestBatch(requests);
                        List<Response> responses = batch.executeAndWait();

                        final List<Map<String, Object>> events = new ArrayList<>();

                        for (Response res : responses) {
                            Log.d(TAG, "Get FB events: " + res.getRawResponse());
                            events.add(JsonTools.getEventFromFB(res.getRawResponse()));
                        }

                        if (events == null) {
                            return;
                        }

                        // TODO Create new category "From Facebook"
                        final SelectionFragment sf = (SelectionFragment)fragments[SELECTION];
                        List<Map<String, Object>> categories = sf.getFilterData();
                        final String fbCateName = getString(R.string.facebook_category);
                        long cateId = -1;

                        for (Map<String, Object> cate : categories) {
                            if (cate.get("name").equals(fbCateName)) {
                                cateId = (Long) cate.get("cateId");
                            }
                        }

                        boolean hasImported = false;
                        if (cateId > 0) {
                            hasImported = true;
                        }
                        if (!hasImported) {   // Need to create new category

                            Thread tmp = new Thread() {
                                public void run() {
                                    sf.updateListView(fbCateName);
                                }
                            };
                            tmp.start();
                            //sf.updateListView(fbCateName);

                            try {
                                sleep(100);

                                Log.d(TAG, "Before acquire semImport 2: " + semImport.availablePermits());
                                semImport.acquire();
                                Log.d(TAG, "After acquire semImport 2: " + semImport.availablePermits());
                                semImport.release();
                                Log.d(TAG, "After release semImport 2: " + semImport.availablePermits());

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Log.d(TAG, "Before ")
                            //semImport.release();
                            Log.d(TAG, "Import: Get categories");

                            categories = sf.getFilterData();
                            for (Map<String, Object> cate : categories) {
                                if (cate.get("name").equals(fbCateName)) {
                                    cateId = (Long) cate.get("cateId");

                                }
                            }

                            // TODO Create events

                            for (Map<String, Object> event : events) {
                                event.put("cateId", cateId);
                                String time = (String) event.get("time");
                                time = time.replace("T", " ").substring(0, time.length() - 5);
                                Log.d(TAG, "Changed time: " + time);

                                //event.put("time", time);

                                new CreateEventAsyncTask()
                                        .execute((String) event.get("name"),
                                                time,
                                                (String) event.get("location"),
                                                (String) event.get("description"),
                                                String.valueOf(cateId));

                                try {
                                    Log.d(TAG, "Before acquire semImport in loop");
                                    semImport.acquire();
                                    Log.d(TAG, "After acquire semImport in loop");
                                    semImport.release();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            sf.serverDataArrived(categories, true);
                        }

                        state = NORMAL;
                        Log.d(TAG, "Finish importing.");
                    }
                }
            };

            t.start();

        }

        return false;
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Only make changes if the activity is visible
        if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            // Get the number of entries in the back stack
            int backStackSize = manager.getBackStackEntryCount();
            // Clear the back stack
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            if (state.isOpened()) {
                // If the session state is open:
                // Show the authenticated fragment

                Log.d(TAG, "onSessionStateChange");
                // TODO Connect to FB
                Log.d(TAG, "Try to request to FB");
                new Request(
                        session,
                        "/me",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            public void onCompleted(Response response) {
                                //JSONObject json = response.getGraphObject().getInnerJSONObject();
                                //Log.d("RESPONSE", json.toString());

                                String userInfo = response.getRawResponse();
                                Log.d("RESPONSE", userInfo);

                                Map<String, Object> user = JsonTools.getUserFromFB(userInfo);
                                Long facebookId = (Long) user.get("facebookId");
                                String name = (String) user.get("name");
                                //name.replace(" ", "%20");
                                //name = name.replaceAll("\\s+", "%20");

                                //Log.d(TAG, "After replace: " + name);

                                String url = getResources().getText(R.string.host)
                                        + "UserServlet?userOperation=searchID&facebookId=" + facebookId + "&name=" + name;
                                url = url.replaceAll("\\s+", "%20");

                                try {
                                    //Log.d(TAG, "Before semInner acquire");
                                    semInner.acquire();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                                new GetUserAsyncTask().execute(url);

                                try {
                                    Log.d(TAG, "Before semInner acquire");
                                    semInner.acquire();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Log.d(TAG, "Before semUserCate release");
                                semUserCate.release();

                                Log.d(TAG, "After semUserCate release");

                                showFragment(SELECTION, false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                getSupportActionBar().setHomeButtonEnabled(true);
                                getSupportActionBar().setDisplayShowHomeEnabled(false);
                                getSupportActionBar().setDisplayShowTitleEnabled(false);
                                getSupportActionBar().setDisplayShowCustomEnabled(true);
                                View actionbarLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.actionbar_layout, null);
                                getSupportActionBar().setCustomView(actionbarLayout);
                                getSupportActionBar().show();

                            }
                        }
                ).executeAsync();

//                showFragment(SELECTION, false);
//                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                getSupportActionBar().setHomeButtonEnabled(true);
//                getSupportActionBar().setDisplayShowHomeEnabled(false);
//                getSupportActionBar().setDisplayShowTitleEnabled(false);
//                getSupportActionBar().setDisplayShowCustomEnabled(true);
//                View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
//                getSupportActionBar().setCustomView(actionbarLayout);
////                edit = (ImageButton) findViewById(R.id.right_imbt);
////                edit.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        Log.v(TAG, "click on plus");
////                        showEditDialog();
////                    }
////                });
//                getSupportActionBar().show();


            } else if (state.isClosed()) {
                // If the session state is closed:
                // Show the login fragment
                showFragment(SPLASH, false);
                getSupportActionBar().setDisplayShowCustomEnabled(false);
                //getSupportActionBar().setCustomView(null);
                getSupportActionBar().setTitle(R.string.app_name);
                getSupportActionBar().hide();
            }
        }
    }

//    private void showEditDialog() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//        dialog.setTitle(R.string.edit_hint);
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.select_dialog);
//        arrayAdapter.add(DIALOG_ADD);
//        arrayAdapter.add(DIALOG_EDIT);
//        arrayAdapter.add(DIALOG_PRIVACYPOLICY);
//        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setAdapter(arrayAdapter,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String selected = arrayAdapter.getItem(which);
//                        if (selected.equals(DIALOG_ADD)) {
//                            Intent intent = new Intent(MainActivity.this, NewEventActivity.class);
//                            startActivity(intent);
//                        } else if (selected.equals(DIALOG_EDIT)) {
//                            Log.v(TAG, "EDIT");
//                            AlertDialog.Builder inner = new AlertDialog.Builder(MainActivity.this);
//                            inner.setTitle(R.string.edit_category);
//
//                            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
//                            View dialogView = inflater.inflate(R.layout.create_category_layout, null);
//                            inner.setView(dialogView);
//
//                            final EditText editText = (EditText) dialogView.findViewById(R.id.category_name);
//
//                            inner.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                            inner.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // create new category
//                                    String newName = editText.getText().toString();
//                                    SelectionFragment sf = (SelectionFragment)fragments[SELECTION];
//                                    sf.updateListView(newName);
//                                    dialog.dismiss();
//                                }
//                            });
//
//                            AlertDialog alertDialog = inner.create();
//                            alertDialog.show();
//                        }
//                        else if(selected.equals(DIALOG_PRIVACYPOLICY)){
//
//                        }
//                    }
//                });
//        dialog.show();
//    }

    private void constructActionBar() {
        if (fragments[SELECTION].isVisible()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
            getSupportActionBar().setCustomView(actionbarLayout);
//            edit = (ImageButton) findViewById(R.id.right_imbt);
//            edit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.v(TAG, "click on plus");
//                    showEditDialog();
//                }
//            });
        } else {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }



    public class GetUserAsyncTask extends AsyncTask<String, Integer, Map<String, Object>> {
        /*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
        @Override
        protected void onPostExecute(Map<String, Object> result) {
            // TODO Auto-generated method stub

            Log.d(TAG, "Enter postExecute");

            super.onPostExecute(result);
            if(result == null) {
                Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                return;
            }

//            Bundle bundle = new Bundle();
//            bundle.putLong("userId", (Long) result.get("userId"));
//
//            Log.d(TAG, "Before setArguments");
//
//            fragments[SELECTION].setArguments(new Bundle());

//            userId = (Long) result.get("userId");
//
//            Log.d(TAG, "Before semInner release");
//            semInner.release();
//            Log.d(TAG, "After semInner release");

            //serverDataArrived(result, true);
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

        protected Map<String, Object> doInBackground(String... arg0) {
            Map<String, Object> user = null;

            System.out.println("In AsncTask!!");
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
                    user = (Map<String, Object>) JsonTools
                            .getUser("user", jsonString);

                    Log.d(TAG, jsonString);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // TODO Temporarily release semaphore here, should exists better solution?
            userId = (Long) user.get("userId");

            Log.d(TAG, "Before semInner release");
            semInner.release();
            Log.d(TAG, "After semInner release");

            Log.d(TAG, "background end");

            return user;
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



    public class CreateEventAsyncTask extends
            AsyncTask<String, Integer, List<Map<String, Object>>> {

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
        protected void onPostExecute(List<Map<String, Object>> result) {
            if (MainActivity.state == MainActivity.IMPORT) {
                Log.d("DEBUG", "in CreateEventAsync before release semImport");
                MainActivity.semImport.release();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected List<Map<String, Object>> doInBackground(String... arg0) {
            ArrayList<Map<String, Object>> events = null;

            System.out.println("In AsncTask!!");
            try {
                URL url = new URL(getResources().getText(R.string.host)
                        + "EventServlet");
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                StringBuffer params = new StringBuffer();

                String description = arg0[3];
                description = Uri.encode(description);
                description = description.replaceAll("_", "")
                        .replaceAll("'", "%27")
                        .replaceAll("\\(", "%28")
                        .replaceAll("\\)", "%29")
                        .replaceAll("%E2%80%9C", "%22")
                        .replaceAll("%E2%80%9D", "%22")
                        .replaceAll("%E2%80%93", "-")
                        .replaceAll("%E2%80%A2", "")
                        .replaceAll("%E2%80%99", "%27");

                if (description.length() > 500) {
                    description = description.substring(0, 500);
                }

                Log.d(TAG, "description: " + description);

                params.append("eventOperation=add&cateId=")
                        .append(arg0[4]).append("&name=")
                        .append(arg0[0]).append("&time=")
                        .append(arg0[1]).append("&location=")
                        .append(arg0[2]).append("&description=")
                        .append(description);


                Log.v("DEBUG", params.toString());
                byte[] bypes = params.toString().getBytes();
                connection.getOutputStream().write(bypes);
                int code = connection.getResponseCode();
                if (code == 200) {
                    String jsonString = ChangeInputStream(connection
                            .getInputStream());
                    events = (ArrayList<Map<String, Object>>) JsonTools
                            .getEvents("events", jsonString);
                    for(int i = 0; i < events.size(); i++){
                        Log.v("DEBUG", events.get(i).toString());
                    }
                }
                System.out.println(events.size() + "hits");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return events;
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

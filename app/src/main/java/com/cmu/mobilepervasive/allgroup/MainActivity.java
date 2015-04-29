package com.cmu.mobilepervasive.allgroup;

//import android.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "debug";
    private static final String _EVENT = "New Event";
    private static final String _Category = "New Category";
    private static final String _PRIVACYPOLICY = "Privacy Policy";
    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = SETTINGS + 1;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private MenuItem settings;
    private MenuItem privacyPolicy;
    private MenuItem newCategory;
    private MenuItem newEvent;
  //  private ImageButton edit;
    private boolean isResumed = false;

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
        else if(item.equals(newCategory)){
            SelectionFragment sf = (SelectionFragment)fragments[SELECTION];
            List<Map<String, Object>> filterdata = sf.getFilterData();
            ArrayList<String> list = new ArrayList<>();
            for(int i = 0; i < filterdata.size(); i++){
                list.add((String)filterdata.get(i).get("name"));
            }

            Intent intent = new Intent(MainActivity.this, NewEventActivity.class);
            intent.putStringArrayListExtra("filterData", list);
            startActivity(intent);
        }
        else if(item.equals(newEvent)){
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
                showFragment(SELECTION, false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
                getSupportActionBar().setCustomView(actionbarLayout);
//                edit = (ImageButton) findViewById(R.id.right_imbt);
//                edit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.v(TAG, "click on plus");
//                        showEditDialog();
//                    }
//                });
                getSupportActionBar().show();


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

}

package com.cmu.mobilepervasive.allgroup;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cmu.allgroup.utils.JsonTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NewEventActivity extends ActionBarActivity {
    private Spinner year;
    private Spinner month;
    private Spinner date;
    private Spinner time;
    private Spinner category;
    private Button doneButton;
    private EditText editName;
    private EditText editLoation;
    private EditText editDetail;
    private Button upload;
    private ImageView uploadImage;

    private File tempFile;

    private static final int PHOTO_REQUEST_GALLERY = 1;
    private static final int PHOTO_REQUEST_CUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        year = (Spinner) findViewById(R.id.newevent_year);
        month = (Spinner) findViewById(R.id.newevent_month);
        date = (Spinner) findViewById(R.id.newevent_date);
        time = (Spinner) findViewById(R.id.newevent_time);
        category = (Spinner) findViewById(R.id.newevent_category);
        editDetail = (EditText) findViewById(R.id.edit_detail);
        editLoation = (EditText) findViewById(R.id.edit_location);
        editName = (EditText) findViewById(R.id.edit_name);

        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(
                this, R.array.year_array,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(
                this, R.array.month_array,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterDate = ArrayAdapter.createFromResource(
                this, R.array.date_array,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterTime = ArrayAdapter.createFromResource(
                this, R.array.time_array,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getIntent().getStringArrayListExtra("filterData"));

        final Bundle bundle = getIntent().getBundleExtra("bundle");

        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        year.setAdapter(adapterYear);
        month.setAdapter(adapterMonth);
        date.setAdapter(adapterDate);
        time.setAdapter(adapterTime);
        category.setAdapter(adapterCategory);

        upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

        uploadImage = (ImageView) findViewById(R.id.imageViewUpload);

        doneButton = (Button) findViewById(R.id.newevent_done_button);

        doneButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedTime = year.getSelectedItem().toString() + "-"
                        + month.getSelectedItem().toString() + "-" + date.getSelectedItem().toString()
                        + " " + time.getSelectedItem().toString() + ":00";
                String selectedCate = category.getSelectedItem().toString();
                String selectedLocation = editLoation.getText().toString();
                String selectedName = editName.getText().toString();
                String selectedDetail = editDetail.getText().toString();

                String cateId = String.valueOf(bundle.getLong(selectedCate));

                new CreateEventAsyncTask().execute(selectedName, selectedTime, selectedLocation, selectedDetail, /*selectedCate, */cateId);

                Intent intent = new Intent(NewEventActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
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
                //TODO: cateId is hardcoded as 1
                params.append("eventOperation=add&cateId=")
                        .append(arg0[4]).append("&name=")
                        .append(arg0[0]).append("&time=")
                        .append(arg0[1]).append("&location=")
                        .append(arg0[2]).append("&description=")
                        .append(arg0[3]);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("In Result");
        System.out.println("RequestCode: " + requestCode);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            System.out.println("In 1");
            if (data != null) {
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                try {
                    System.out.println(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    if (bitmap == null) {
                        System.out.println("hah");
                        return;
                    }
                    uploadImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException fe) {
                    fe.printStackTrace();
                }
            }

        }

        System.out.println("End result");
        super.onActivityResult(requestCode, resultCode, data);
    }


}

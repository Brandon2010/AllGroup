package com.cmu.mobilepervasive.allgroup;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class NewEventActivity extends ActionBarActivity {

    private Spinner month;
    private Spinner date;
    private Spinner time;
    private Spinner category;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        month = (Spinner) findViewById(R.id.newevent_month);
        date = (Spinner) findViewById(R.id.newevent_date);
        time = (Spinner) findViewById(R.id.newevent_time);
        category = (Spinner) findViewById(R.id.newevent_category);


        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(
                this, R.array.month_array,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterDate = ArrayAdapter.createFromResource(
                this, R.array.date_array,
                android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterTime = ArrayAdapter.createFromResource(
                this, R.array.time_array,
                android.R.layout.simple_spinner_item);

        // TODO: category_array is hardcoded
        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(
                this, R.array.category_array,
                android.R.layout.simple_spinner_item);

        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        month.setAdapter(adapterMonth);
        date.setAdapter(adapterDate);
        time.setAdapter(adapterTime);
        category.setAdapter(adapterCategory);

        doneButton = (Button) findViewById(R.id.newevent_done_button);

        doneButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}

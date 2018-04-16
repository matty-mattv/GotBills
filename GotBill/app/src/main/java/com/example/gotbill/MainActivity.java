package com.example.gotbill;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    public static final String EXTRA_PLAN = "com.example.gotbill.planCost";
    public static final String EXTRA_TAX = "com.example.gotbill.taxCost";
    public static final String EXTRA_DATE = "com.example.gotbill.date";

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getInformatiion(View view) {
        //Grab dates
        EditText month1 = (EditText) findViewById(R.id.month1);
        EditText month2 = (EditText) findViewById(R.id.month2);
        EditText day1 = (EditText) findViewById(R.id.day1);
        EditText day2 = (EditText) findViewById(R.id.day2);

        String firstMonth = month1.getText().toString();
        String secondMonth = month2.getText().toString();
        String firstDay= day1.getText().toString();
        String secondDay = day2.getText().toString();

        SimpleDateFormat formatter = new SimpleDateFormat("yy");
        Date dateYear = new Date();

        String dateYearString = formatter.format(dateYear).toString();

        date = firstMonth + "_" + firstDay + "_"  + dateYearString
                 + "-" + secondMonth + "_" + secondDay+ "_" + dateYearString;

        String planCost = ( (EditText) findViewById(R.id.planCost) ).getText().toString();
        String taxCost = ( (EditText) findViewById(R.id.taxCost) ).getText().toString();

        Intent intent = new Intent(this, AddInformation.class);
        intent.putExtra(EXTRA_PLAN, planCost);
        intent.putExtra(EXTRA_TAX, taxCost);
        intent.putExtra(EXTRA_DATE, date);
        startActivity(intent);
    }
}


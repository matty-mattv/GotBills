package com.example.gotbill;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddInformation extends AppCompatActivity {

    public static final String EXTRA_BILL = "com.example.gotbill.calculation";
    public static final String EXTRA_COMMENTS = "com.example.gotbill.comments";

    public static final int numUsers = 5;
    /* 0 = Matthew
           1 = Christine
           2 = Thu
           3 = Janice
           4 = Barani
           */
    private double[] personBill = new double[numUsers];
    private String[] userComments = new String[numUsers];
    private String date;
    private double planCost;
    private double taxCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_information);

        Bundle bundle = getIntent().getExtras();
        planCost = Double.parseDouble( bundle.getString(MainActivity.EXTRA_PLAN));
        taxCost = Double.parseDouble( bundle.getString(MainActivity.EXTRA_TAX));
        date = bundle.getString(MainActivity.EXTRA_DATE);
    }

    public void calcuulateBill(View view) {

        EditText planCostInput = (EditText) findViewById(R.id.planCost);
        EditText taxCostInput = (EditText)  findViewById(R.id.taxCost);

        //Divdie by 4 users becaise Thu and Janice are 1
        double costPerPerson =  (planCost / 4) + (taxCost / 4);

        EditText[] userEquipmentCost = new EditText[5];
        EditText[] userUsageCost = new EditText[5];

        userEquipmentCost[0] = (EditText) findViewById(R.id.matthewEquipment);
        userUsageCost[0] = (EditText) findViewById(R.id.matthewUsage);
        userEquipmentCost[1] = (EditText) findViewById(R.id.christineEquipment);
        userUsageCost[1] = (EditText) findViewById(R.id.christineUsage);
        userEquipmentCost[2] = (EditText) findViewById(R.id.thuEquipment);
        userUsageCost[2] = (EditText) findViewById(R.id.thuUsage);
        userEquipmentCost[3] = (EditText) findViewById(R.id.janiceEquipment);
        userUsageCost[3] = (EditText) findViewById(R.id.janiceUsage);
        userEquipmentCost[4] = (EditText) findViewById(R.id.baraniEquipment);
        userUsageCost[4] = (EditText) findViewById(R.id.baraniUsage);

        userComments[0] = ( (EditText) findViewById(R.id.matthewComments) ).getText().toString();
        userComments[1] = ( (EditText) findViewById(R.id.christineComments) ).getText().toString();
        userComments[2] = ( (EditText) findViewById(R.id.thuComments) ).getText().toString();
        userComments[3] = ( (EditText) findViewById(R.id.janiceComments) ).getText().toString();
        userComments[4] = ( (EditText) findViewById(R.id.baraniComments) ).getText().toString();

        for(int i = 0; i < numUsers; ++i) {
            double userEquipment = Double.parseDouble( userEquipmentCost[i].getText().toString() );
            double userUsage = Double.parseDouble( userUsageCost[i].getText().toString() );
            personBill[i] = costPerPerson + userEquipment + userUsage;
        }

        customCharge();

        Intent intent = new Intent(this, DisplayResultActivity.class);
        intent.putExtra(EXTRA_BILL, personBill);
        intent.putExtra(EXTRA_COMMENTS, userComments);
        intent.putExtra(MainActivity.EXTRA_DATE, date);
        startActivity(intent);
    }

    // Since Janice only pays 13 and mom pays under 30, we split up one user by subtracting 13
    // 2 = thu 3 = janice
    private void customCharge() {
        personBill[2] -= 13;
        personBill[3] = 13;
    }
}

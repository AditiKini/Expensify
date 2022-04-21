package com.example.expensetrackmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.anychart.graphics.vector.text.FontStyle;
import com.example.expensetrackmanager.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private DatabaseReference expensesRef,personalRef;
    private String uid="";
    private AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        getSupportActionBar().setTitle("Expense Graph");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        uid=mUser.getUid();

        expensesRef = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        personalRef = FirebaseDatabase.getInstance().getReference("Personal").child(uid);

        TextView expenseTotalSum=findViewById(R.id.expense_txt_result_pie);
        expenseTotalSum.setText("₹0.00");

        expensesRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                int totalvalue = 0;

                for(DataSnapshot mysnapshot:snapshot.getChildren())
                {
                    Data data=mysnapshot.getValue(Data.class);

                    totalvalue+=data.getAmount();

                    String stTotalvalue=String.valueOf(totalvalue);

                    expenseTotalSum.setText("₹"+stTotalvalue+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        anyChartView = findViewById(R.id.anyChartView);

        getTotalHousingExpense();
        getTotalTransportExpense();
        getTotalFoodExpense();
        getTotalUtilitiesExpense();
        getTotalInsuranceExpense();
        getTotalHealthCareExpense();
        getTotalSavingsExpense();
        getTotalPersonalExpense();
        getTotalEntertainmentExpense();
        getTotalMiscellaneousExpense();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        loadGraph();
                    }
                },
                500
        );

    }

    private void getTotalHousingExpense() {

        String itemNday = "Housing";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayHous").setValue(totalAmount);

                }
                else {
                    personalRef.child("dayHous").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getTotalTransportExpense() {

        String itemNday = "Transportation";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayTrans").setValue(totalAmount);

                }
                else {
                    personalRef.child("dayTrans").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getTotalFoodExpense(){

        String itemNday = "Food";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayFood").setValue(totalAmount);
                }else {
                    personalRef.child("dayFood").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalUtilitiesExpense() {

        String itemNday = "Utilities";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayUtill").setValue(totalAmount);

                }
                else {
                    personalRef.child("dayUtill").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getTotalInsuranceExpense() {

        String itemNday = "Insurance";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayInsuarnce").setValue(totalAmount);

                }
                else {
                    personalRef.child("dayInsuarnce").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getTotalHealthCareExpense() {

        String itemNday = "HealthCare";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayHealth").setValue(totalAmount);

                }
                else {
                    personalRef.child("dayHealth").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getTotalSavingsExpense() {

        String itemNday = "Savings and Debts";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("daySavings").setValue(totalAmount);

                }
                else {
                    personalRef.child("daySavings").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getTotalPersonalExpense() {

        String itemNday = "Personal Spending";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayPersonal").setValue(totalAmount);

                }
                else {
                    personalRef.child("dayPersonal").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getTotalEntertainmentExpense() {

        String itemNday = "Entertainment";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayEntertainment").setValue(totalAmount);

                }
                else {
                    personalRef.child("dayEntertainment").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getTotalMiscellaneousExpense() {

        String itemNday = "Miscellaneous";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);
        Query query = reference.orderByChild("type").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    personalRef.child("dayMiscellaneous").setValue(totalAmount);

                }
                else {
                    personalRef.child("dayMiscellaneous").setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void loadGraph(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    int housTotal;
                    if (snapshot.hasChild("dayHous")){
                        housTotal = Integer.parseInt(snapshot.child("dayHous").getValue().toString());
                    }else {
                        housTotal = 0;
                    }

                    int traTotal;
                    if (snapshot.hasChild("dayTrans")){
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    }else {
                        traTotal = 0;
                    }

                    int foodTotal;
                    if (snapshot.hasChild("dayFood")){
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    }else {
                        foodTotal = 0;
                    }

                    int utillTotal;
                    if (snapshot.hasChild("dayUtill")){
                        utillTotal = Integer.parseInt(snapshot.child("dayUtill").getValue().toString());
                    }else {
                        utillTotal = 0;
                    }

                    int insurTotal;
                    if (snapshot.hasChild("dayInsuarnce")){
                        insurTotal = Integer.parseInt(snapshot.child("dayInsuarnce").getValue().toString());
                    }else {
                        insurTotal = 0;
                    }

                    int healthTotal;
                    if (snapshot.hasChild("dayHealth")){
                        healthTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    }else {
                        healthTotal = 0;
                    }

                    int savingsTotal;
                    if (snapshot.hasChild("daySavings")){
                        savingsTotal = Integer.parseInt(snapshot.child("daySavings").getValue().toString());
                    }else {
                        savingsTotal = 0;
                    }

                    int personalTotal;
                    if (snapshot.hasChild("dayPersonal")){
                        personalTotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                    }else {
                        personalTotal = 0;
                    }

                    int entertainTotal;
                    if (snapshot.hasChild("dayEntertainment")){
                        entertainTotal = Integer.parseInt(snapshot.child("dayEntertainment").getValue().toString());
                    }else {
                        entertainTotal = 0;
                    }

                    int misTotal;
                    if (snapshot.hasChild("dayMiscellaneous")){
                        misTotal = Integer.parseInt(snapshot.child("dayMiscellaneous").getValue().toString());
                    }else {
                        misTotal = 0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Housing", housTotal));
                    data.add(new ValueDataEntry("Transportation", traTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("Utilities", utillTotal));
                    data.add(new ValueDataEntry("Insurance", insurTotal));
                    data.add(new ValueDataEntry("HealthCare", healthTotal));
                    data.add(new ValueDataEntry("Savings and Debts", savingsTotal));
                    data.add(new ValueDataEntry("Personal Spending", personalTotal));
                    data.add(new ValueDataEntry("Entertainment", entertainTotal));
                    data.add(new ValueDataEntry("Miscellaneous", misTotal));

                    pie.data(data);

                    pie.title("Analytics")
                            .title().align(Align.CENTER).fontWeight(500).fontSize(22).fontColor("Red");

                    pie.labels().position("outside");

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                            .align(Align.CENTER);

                    anyChartView.setChart(pie);
                }
                else {
                    Toast.makeText(AnalyticsActivity.this,"Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnalyticsActivity.this,"Child does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
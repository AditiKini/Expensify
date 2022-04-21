package com.example.expensetrackmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetrackmanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Handler;

public class DashBoardFragment extends Fragment {

    //Floating Button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating Button Textview..

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //Boolean

    private boolean isOpen=false;

    //Animation

    private Animation FadeOpen,FadeClose;

    //Dashboard income and expense result

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;
    private TextView totalBalanceResult;


    //Firebase..

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mBalanceDatabase;

    //Recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    // Balance
    int i,e,totalbal;
    String balance;

    //Refresh

    SwipeRefreshLayout refreshLayout;

    //DatePicker

    private int year, month, day;
    private Calendar calendar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dash_board, container, false);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        mBalanceDatabase= FirebaseDatabase.getInstance().getReference().child("BalanceData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);
        mBalanceDatabase.keepSynced(true);

        //Connect Floating Button to Layout

        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);

        //Connect Folating Text..

        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Total income & expense result test

        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);
        totalBalanceResult=myview.findViewById(R.id.balance_set_result);

        //Recycler

        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);

        //Animation Connect

        FadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        //Refresh

        refreshLayout=myview.findViewById(R.id.refresh);
        
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                reload();
                refreshLayout.setRefreshing(false);
            }

        });

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View v) {

                addData();

                if(isOpen)
                {
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }
                else
                {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }

            }

        });

        //Calculate total income

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum=0;

                totalIncomeResult.setText("+ ₹0.00");

                for(DataSnapshot mysnap:snapshot.getChildren())
                {
                    Data data=mysnap.getValue(Data.class);
                    totalsum+=data.getAmount();
                    i=totalsum;
                    String stResult=String.valueOf(totalsum);
                    totalIncomeResult.setText("+ ₹"+stResult+".00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum=0;

                totalExpenseResult.setText("- ₹0.00");

                for(DataSnapshot mysnapshot:snapshot.getChildren())
                {
                    Data data=mysnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();
                    e=totalsum;
                    String strTotalSum=String.valueOf(totalsum);
                    totalExpenseResult.setText("- ₹"+strTotalSum+".00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total balance

        mBalanceDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalbal=i-e;
                balance=String.valueOf(totalbal);
                totalBalanceResult.setText("₹"+balance+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutMangerIncome= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutMangerIncome.setStackFromEnd(true);
        layoutMangerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutMangerIncome);

        LinearLayoutManager layoutManagerExpense= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;

    }

    private void reload()
    {
        startActivity(new Intent(getActivity(),MainActivity.class));
    }

    //Floating Button Animation

    private void ftAnimation()
    {
        if(isOpen)
        {
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;
        }
        else
        {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadeOpen);
            fab_expense_txt.startAnimation(FadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }
    }

    private void addData()
    {
        //Fab Button Income..

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { incomeDataInsert(); }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert()
    {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);
        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText editAmount=myview.findViewById(R.id.amount_edt);
        EditText editNote=myview.findViewById(R.id.note_edt);
        EditText editDate=myview.findViewById(R.id.date_edt);

        Spinner editType=myview.findViewById(R.id.type_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btncancel=myview.findViewById(R.id.btnCancel);

        //DatePicker

        calendar = Calendar.getInstance();
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String[] mons = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
                        int m = month;
                        String mName = mons[m];

                        String date = dayOfMonth+" "+mName+" "+year;
                        editDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type=editType.getSelectedItem().toString().trim();
                String amount=editAmount.getText().toString().trim();
                String note=editNote.getText().toString().trim();

                if(TextUtils.isEmpty(amount))
                {
                    editAmount.setError("Required Filed");
                    return;
                }

                int ouramountint=Integer.parseInt(amount);

                if(TextUtils.isEmpty(note))
                {
                    editNote.setError("Required Filed");
                    return;
                }

                if(type.equals("Select Category"))
                {
                    Toast.makeText(getActivity(),"Select a valid category",Toast.LENGTH_LONG).show();
                }
                else {

                    String id = mIncomeDatabase.push().getKey();

                    String mDate = editDate.getText().toString();

                    Data data = new Data(ouramountint, type, note, id, mDate);

                    mIncomeDatabase.child(id).setValue(data);

                    Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(DashBoardFragment.this).attach(DashBoardFragment.this).commit();

                    ftAnimation();

                    dialog.dismiss();

                }
            }

        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void expenseDataInsert()
    {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);
        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText amount=myview.findViewById(R.id.amount_edt);
        EditText note=myview.findViewById(R.id.note_edt);
        EditText mdate=myview.findViewById(R.id.date_edt);

        Spinner type=myview.findViewById(R.id.type_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btncancel=myview.findViewById(R.id.btnCancel);

        //DatePicker
        Calendar calendar = Calendar.getInstance();
        mdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String[] mons = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
                        int m = month;
                        String mName = mons[m];

                        String date = dayOfMonth+" "+mName+" "+year;
                        mdate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmount=amount.getText().toString().trim();
                String tmtype=type.getSelectedItem().toString().trim();
                String tmnote=note.getText().toString().trim();

                if(TextUtils.isEmpty(tmAmount))
                {
                    amount.setError("Required Filed");
                    return;
                }

                int inamount=Integer.parseInt(tmAmount);

                if(TextUtils.isEmpty(tmnote))
                {
                    note.setError("Required Filed");
                    return;
                }

                if(tmtype.equals("Select Category"))
                {
                    Toast.makeText(getActivity(),"Select a valid category",Toast.LENGTH_SHORT).show();
                }
                else {

                    String id = mExpenseDatabase.push().getKey();

                    String mDate = mdate.getText().toString();

                    Data data = new Data(inamount, tmtype, tmnote, id, mDate);

                    mExpenseDatabase.child(id).setValue(data);

                    Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(DashBoardFragment.this).attach(DashBoardFragment.this).commit();

                    ftAnimation();

                    dialog.dismiss();
                }

            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data,IncomeViewHolder>(options)
        {
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent,false));
            }
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model)
            {
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());

                switch (model.getType()){
                    case "Housing":
                        holder.imageView.setImageResource(R.drawable.ic_housing);
                        break;

                    case "Transportation":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;

                    case "Food":
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;

                    case "Utilities":
                        holder.imageView.setImageResource(R.drawable.ic_utilities);
                        break;

                    case "Insurance":
                        holder.imageView.setImageResource(R.drawable.ic_insurance);
                        break;

                    case "HealthCare":
                        holder.imageView.setImageResource(R.drawable.ic_medical);
                        break;

                    case "Savings and Debts":
                        holder.imageView.setImageResource(R.drawable.ic_savings);
                        break;

                    case "Personal Spending":
                        holder.imageView.setImageResource(R.drawable.ic_personal_spending);
                        break;

                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.ic_entertainment);
                        break;

                    case "Miscellaneous":
                        holder.imageView.setImageResource(R.drawable.ic_others);
                        break;
                }

            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();


        FirebaseRecyclerOptions<Data> option=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data,ExpenseViewHolder>(option)
        {
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense,parent,false));
            }
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model)
            {
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseType(model.getType());
                holder.setExpenseDate(model.getDate());

                switch (model.getType()){
                    case "Housing":
                        holder.imageView.setImageResource(R.drawable.ic_housing);
                        break;

                    case "Transportation":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;

                    case "Food":
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;

                    case "Utilities":
                        holder.imageView.setImageResource(R.drawable.ic_utilities);
                        break;

                    case "Insurance":
                        holder.imageView.setImageResource(R.drawable.ic_insurance);
                        break;

                    case "HealthCare":
                        holder.imageView.setImageResource(R.drawable.ic_medical);
                        break;

                    case "Savings and Debts":
                        holder.imageView.setImageResource(R.drawable.ic_savings);
                        break;

                    case "Personal Spending":
                        holder.imageView.setImageResource(R.drawable.ic_personal_spending);
                        break;

                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.ic_entertainment);
                        break;

                    case "Miscellaneous":
                        holder.imageView.setImageResource(R.drawable.ic_others);
                        break;
                }

            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();

    }

    //For Income Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mIncomeView=itemView;
            imageView = itemView.findViewById(R.id.DashboardIncomeIconView);
        }

        public void setIncomeType(String type)
        {
            TextView mtype=mIncomeView.findViewById(R.id.type_income_ds);
            mtype.setText(type);
        }

        public void setIncomeAmount(int amount)
        {
            TextView mAmount=mIncomeView.findViewById(R.id.amount_income_ds);
            String strAmount=String.valueOf(amount);
            mAmount.setText("+ ₹"+strAmount);
        }

        public void setIncomeDate(String date)
        {
            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    //For Expense Data

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mExpenseView=itemView;
            imageView = itemView.findViewById(R.id.DashboardExpenseIconView);
        }

        public void setExpenseType(String type)
        {
            TextView mtype=mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);
        }

        public void setExpenseAmount(int amount)
        {
            TextView mAmount=mExpenseView.findViewById(R.id.amount_expense_ds);
            String strAmount=String.valueOf(amount);
            mAmount.setText("- ₹"+strAmount);
        }

        public void setExpenseDate(String date)
        {
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }

}
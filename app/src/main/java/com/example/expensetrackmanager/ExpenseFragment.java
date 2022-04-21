package com.example.expensetrackmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;


public class ExpenseFragment extends Fragment {

    //Firebase Database

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Recyclerview

    private RecyclerView recyclerView;

    //TextView

    private TextView expenseTotalSum;

    //Update edit text

    private EditText edtAmount;
    private EditText edtNote;
    private EditText edtDate;
    private Spinner edtType;

    //Button for Upsate & Delete

    private Button btnUpdate;
    private Button btnDelete;

    //Data Item Value

    private String type;
    private String note;
    private String date;
    private int amount,selected;

    private String post_key;

    //DatePicker

    private int year, month, day;
    private Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        expenseTotalSum=myview.findViewById(R.id.expense_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener()
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

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> firebaseRecyclelerAdapter = new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>(options) {
            @NonNull
            @Override
            public ExpenseFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseFragment.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data,parent,false));
            }
            @Override
            protected void onBindViewHolder(@NonNull ExpenseFragment.MyViewHolder holder, int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setAmount(model.getAmount());
                holder.setDate(model.getDate());

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

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(position).getKey();

                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmount();
                        date=model.getDate();

                        updateDataItem();
                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclelerAdapter);

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            imageView = itemView.findViewById(R.id.ExpenseIconView);
        }

        private void setType(String type)
        {
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note)
        {
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setDate(String date)
        {
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setAmount(Integer amount)
        {
            TextView mAmount=mView.findViewById(R.id.amount_txt_expense);
            String stamount=String.valueOf(amount);
            mAmount.setText("- ₹"+stamount);
        }

    }

    private void updateDataItem()
    {
        AlertDialog.Builder mydialog= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmount=myview.findViewById(R.id.amount_edt);
        edtType=myview.findViewById(R.id.type_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        edtDate=myview.findViewById(R.id.date_edt);

        //Set Data to Edit Text

        selected=edtType.getSelectedItemPosition();
        edtType.setSelection(selected);
        //edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtDate.setText(date);
        edtDate.setSelection(date.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate=myview.findViewById(R.id.btn_upd_update);
        btnDelete=myview.findViewById(R.id.btn_upd_Delete);

        AlertDialog dialog=mydialog.create();

        //DatePicker

        calendar = Calendar.getInstance();
        edtDate.setOnClickListener(new View.OnClickListener() {
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
                        edtDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type=edtType.getSelectedItem().toString();
                note=edtNote.getText().toString().trim();

                if(type.equals("Select Category"))
                {
                    Toast.makeText(getActivity(),"Select a valid category",Toast.LENGTH_SHORT).show();
                }
                else {
                    String mdamount = String.valueOf(amount);
                    mdamount = edtAmount.getText().toString().trim();
                    int myAmount = Integer.parseInt(mdamount);

                    String mDate = edtDate.getText().toString();

                    Data data = new Data(myAmount, type, note, post_key, mDate);

                    mExpenseDatabase.child(post_key).setValue(data);

                    dialog.dismiss();
                }

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseDatabase.child(post_key).removeValue();
                getFragmentManager().beginTransaction().detach(ExpenseFragment.this).attach(ExpenseFragment.this).commit();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}





package com.example.mad_project;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ReturnRenewDateDialogBox extends DialogFragment {
    public static final String TAG = "return_date_dialog_box";
    EditText date;
    String requestRef;
    Button returnButton, cancelButton;
    DatePickerDialog.OnDateSetListener dateSetListener;

    public ReturnRenewDateDialogBox(String requestRef) {
        this.requestRef = requestRef;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
            Objects.requireNonNull(Objects.requireNonNull(dialog).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.w(TAG, "In Return Date");
        Log.w(TAG, "" + R.layout.return_date_dialog_box);
        return inflater.inflate(R.layout.return_date_dialog_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date = view.findViewById(R.id.date_selector);
        cancelButton = view.findViewById(R.id.cancel_button);
        returnButton = view.findViewById(R.id.return_button);

        date.setShowSoftInputOnFocus(false);
        setDatePicker();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests").child(requestRef);
                ref.child("status").setValue("issue");
                ref.child("issuedDate").setValue(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                ref.child("returnDate").setValue(date.getText().toString());
                dismiss();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDatePicker() {
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(Objects.requireNonNull(getContext()), android.R.style.Theme_Material_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dateDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                dateDialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month++;
                String dateText = day + "/" + month + "/" + year;
                date.setText(dateText);
            }
        };
    }
}
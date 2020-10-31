package com.example.mad_project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class RequestAdapter extends ArrayAdapter<Requests> {
    private final Context context;
    private final BookInfoNav parentInstance;
    private final int resource;
    View view;

    public RequestAdapter(Context context, int resource, ArrayList<Requests> objects, BookInfoNav parentInstance) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.parentInstance = parentInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);
        view = convertView;

        LinearLayout card = convertView.findViewById(R.id.outer_box);
        boolean isLibrarian = LoginDetails.isLibrarian();

        if(isLibrarian) {
            if(resource == R.layout.requested_card)
                cardIsLibrarianRequests(position);
            else if(resource == R.layout.issued_card)
                cardIsLibrarianIssued(position);
            else if(resource == R.layout.returned_card)
                cardIsLibrarianReturn(position);
        } else {
            if(resource == R.layout.requested_card)
                cardIsStudentRequests(position);
            else if(resource == R.layout.issued_card)
                cardIsStudentIssued(position);
            else if(resource == R.layout.returned_card)
                cardIsStudentReturn(position);
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.frame, new BookDetails(resource, Objects.requireNonNull(getItem(position)).getBook())).addToBackStack(null).commit();
            }
        });

        return convertView;
    }

    @SuppressLint("SetTextI18n")
    private void cardIsLibrarianRequests(final int position) {
        TextView userInfo = view.findViewById(R.id.user_info);
        userInfo.setVisibility(View.VISIBLE);
        userInfo.setText(LoginDetails.getEmail());

        final Button unsendRequestButton = view.findViewById(R.id.unsend_button);
        Button declineButton = view.findViewById(R.id.decline_button);

        unsendRequestButton.setVisibility(View.VISIBLE);
        declineButton.setVisibility(View.VISIBLE);

        if(Objects.requireNonNull(getItem(position)).getStatus().equals("request"))
            unsendRequestButton.setText("ISSUE");
        else if(Objects.requireNonNull(getItem(position)).getStatus().equals("renew"))
            unsendRequestButton.setText("RENEW");
        declineButton.setText("DECLINE");

        unsendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentInstance.getReturnDateFragment(Objects.requireNonNull(getItem(position)).getKey());
                if(unsendRequestButton.getText().toString().equals("ISSUE")) {
                    FirebaseDatabase.getInstance().getReference("library").child(Objects.requireNonNull(getItem(position)).getBookRef())
                            .child("available").setValue(Objects.requireNonNull(getItem(position)).getBook().getAvailable() - 1);
                }
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlertDialogForDelete(position);
            }
        });

        setRequestCardDetails(position);
    }

    @SuppressLint("SetTextI18n")
    private void cardIsStudentRequests(final int position) {
        TextView userInfo = view.findViewById(R.id.user_info);
        userInfo.setVisibility(View.GONE);

        Button unsendRequestButton = view.findViewById(R.id.unsend_button);
        Button declineButton = view.findViewById(R.id.decline_button);
        unsendRequestButton.setVisibility(View.GONE);
        declineButton.setVisibility(View.VISIBLE);
        declineButton.setText("UNSEND REQUEST");

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("requests").child(Objects.requireNonNull(getItem(position)).getKey()).removeValue();
            }
        });

        setRequestCardDetails(position);
    }

    private void cardIsLibrarianIssued(int position) {
        TextView userInfo = view.findViewById(R.id.user_info);
        userInfo.setVisibility(View.VISIBLE);
        userInfo.setText(LoginDetails.getEmail());

        LinearLayout buttonPanel = view.findViewById(R.id.button_panel);
        buttonPanel.setVisibility(View.GONE);

        setIssuedReturnCardDetails(position);
    }

    private void cardIsStudentIssued(final int position) {
        TextView userInfo = view.findViewById(R.id.user_info);
        userInfo.setVisibility(View.GONE);

        LinearLayout buttonPanel = view.findViewById(R.id.button_panel);
        buttonPanel.setVisibility(View.VISIBLE);

        Button returnButton = this.view.findViewById(R.id.return_button);
        Button renewButton = this.view.findViewById(R.id.renew_button);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests").child(Objects.requireNonNull(getItem(position)).getKey());
                ref.child("status").setValue("return");
                ref.child("returnDate").setValue(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                FirebaseDatabase.getInstance().getReference("library").child(Objects.requireNonNull(getItem(position)).getBookRef())
                        .child("available").setValue(Objects.requireNonNull(getItem(position)).getBook().getAvailable() + 1);
            }
        });

        renewButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests").child(Objects.requireNonNull(getItem(position)).getKey());
                ref.child("status").setValue("renew");
                ref.child("requestDate").setValue(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            }
        });

        setIssuedReturnCardDetails(position);
    }

    private void cardIsLibrarianReturn(int position) {
        TextView userInfo = view.findViewById(R.id.user_info);
        userInfo.setVisibility(View.VISIBLE);

        setIssuedReturnCardDetails(position);
    }

    private void cardIsStudentReturn(int position) {
        TextView userInfo = view.findViewById(R.id.user_info);
        userInfo.setVisibility(View.GONE);

        setIssuedReturnCardDetails(position);
    }

    @SuppressLint("SetTextI18n")
    private void setRequestCardDetails(int position) {
        String requestedDateText = Objects.requireNonNull(getItem(position)).getRequestDate();
        String userInfoText = Objects.requireNonNull(getItem(position)).getEmail();

        TextView requestDate = this.view.findViewById(R.id.requested_date);
        TextView userInfo = this.view.findViewById(R.id.user_info);

        setBookDetails(position);
        requestDate.setText("Requested: " + requestedDateText);
        userInfo.setText(userInfoText);
    }

    @SuppressLint("SetTextI18n")
    private void setIssuedReturnCardDetails(int position) {
        String requestedDateText = Objects.requireNonNull(getItem(position)).getRequestDate();
        String issuedDateText = Objects.requireNonNull(getItem(position)).getIssuedDate();
        String returnDateText = Objects.requireNonNull(getItem(position)).getReturnDate();
        String userInfoText = Objects.requireNonNull(getItem(position)).getEmail();

        TextView requestDate = this.view.findViewById(R.id.requested_date);
        TextView issuedDate = this.view.findViewById(R.id.issued_date);
        TextView returnDate = this.view.findViewById(R.id.return_date);
        TextView userInfo = this.view.findViewById(R.id.user_info);

        setBookDetails(position);
        requestDate.setText("Requested: " + requestedDateText);
        issuedDate.setText("Issued: " + issuedDateText);
        returnDate.setText("Return: " + returnDateText);
        userInfo.setText(userInfoText);
    }

    @SuppressLint("SetTextI18n")
    private void setBookDetails(int position) {
        long isbnText = Objects.requireNonNull(getItem(position)).getBook().getIsbn();
        String bookNameText = Objects.requireNonNull(getItem(position)).getBook().getBookName();
        String authorText = Objects.requireNonNull(getItem(position)).getBook().getAuthor();
        String publisherText = Objects.requireNonNull(getItem(position)).getBook().getPublisher();
        String genreText = Objects.requireNonNull(getItem(position)).getBook().getGenre();
        String imageURL = Objects.requireNonNull(getItem(position)).getBook().getImageURL();

        TextView isbn = this.view.findViewById(R.id.isbn);
        TextView bookName = this.view.findViewById(R.id.book_name);
        TextView author = this.view.findViewById(R.id.author);
        TextView publisher = this.view.findViewById(R.id.publisher);
        TextView genre = this.view.findViewById(R.id.genre);
        ImageView bookPhoto = this.view.findViewById(R.id.book_photo);

        isbn.setText("" + isbnText);
        bookName.setText(bookNameText);
        author.setText(authorText);
        publisher.setText(publisherText);
        genre.setText(genreText);
        Glide.with(getContext()).load(imageURL).into(bookPhoto);
    }

    private void openAlertDialogForDelete(final int position) {
        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(getContext());
        deleteDialogBuilder.setMessage("Decline This Request");
        deleteDialogBuilder.setPositiveButton("REJECT",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        FirebaseDatabase.getInstance().getReference("requests").child(Objects.requireNonNull(getItem(position)).getKey()).removeValue();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog deleteDialog = deleteDialogBuilder.create();
        deleteDialog.show();
    }
}

package com.example.mad_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class BookAdapter extends ArrayAdapter<Book> {
    private final Context context;
    private final int resource;
    protected static int lastBookNum;
    View view;

    public BookAdapter(Context context, int resource, ArrayList<Book> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);
        view = convertView;

        TextView bookName = view.findViewById(R.id.book_name), author = view.findViewById(R.id.author),
                publisher = view.findViewById(R.id.publisher), genre = view.findViewById(R.id.genre);
        bookName.setSelected(true);
        author.setSelected(true);
        publisher.setSelected(true);
        genre.setSelected(true);

        LinearLayout card = convertView.findViewById(R.id.outer_box);
        boolean isLibrarian = LoginDetails.isLibrarian();

        if(isLibrarian) {
            if(resource == R.layout.rejected_card)
                cardIsLibrarianLibrary(position);
        } else {
            if(resource == R.layout.rejected_card)
                cardIsStudentLibrary(position);
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.frame, new BookDetails(resource, getItem(position))).addToBackStack(null).commit();
            }
        });

        return convertView;
    }

    private void cardIsLibrarianLibrary(int position) {
        Button sendRequestButton = view.findViewById(R.id.send_button);
        sendRequestButton.setVisibility(View.GONE);

        setCardDetails(position);
    }

    @SuppressLint("SetTextI18n")
    private void cardIsStudentLibrary(final int position) {
        Button sendRequestButton = view.findViewById(R.id.send_button);
        sendRequestButton.setVisibility(View.VISIBLE);
        sendRequestButton.setText("SEND REQUEST");

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                if(Objects.requireNonNull(getItem(position)).getAvailable() > 0) {
                    Requests newRequest = new Requests();
                    newRequest.setBookRef(Objects.requireNonNull(getItem(position)).getKey());
                    newRequest.setEmail(LoginDetails.getEmail());
                    newRequest.setIssuedDate(" ");
                    newRequest.setRequestDate(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                    newRequest.setReturnDate(" ");
                    newRequest.setStatus("request");

                    FirebaseDatabase.getInstance().getReference("requests").child("request" + lastBookNum).setValue(newRequest);
                } else {
                    Toast.makeText(getContext(), "Sorry! This book is out of availability", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setCardDetails(position);
    }

    @SuppressLint("SetTextI18n")
    private void setCardDetails(int position) {
        long isbnText = Objects.requireNonNull(getItem(position)).getIsbn();
        String bookNameText = Objects.requireNonNull(getItem(position)).getBookName();
        String authorText = Objects.requireNonNull(getItem(position)).getAuthor();
        String publisherText = Objects.requireNonNull(getItem(position)).getPublisher();
        String genreText = Objects.requireNonNull(getItem(position)).getGenre();
        String imageURL = Objects.requireNonNull(getItem(position)).getImageURL();
        int availableText = Objects.requireNonNull(getItem(position)).getAvailable();

        TextView isbn = this.view.findViewById(R.id.isbn);
        TextView bookName = this.view.findViewById(R.id.book_name);
        TextView author = this.view.findViewById(R.id.author);
        TextView publisher = this.view.findViewById(R.id.publisher);
        TextView genre = this.view.findViewById(R.id.genre);
        TextView available = this.view.findViewById(R.id.availale);
        ImageView bookPhoto = this.view.findViewById(R.id.book_photo);

        isbn.setText("" + isbnText);
        bookName.setText(bookNameText);
        author.setText(authorText);
        publisher.setText(publisherText);
        genre.setText(genreText);
        available.setText("Available: " + availableText);
        Glide.with(getContext()).load(imageURL).into(bookPhoto);

    }
}
package com.example.mad_project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class BookDetails extends Fragment {
    View view;
    private final int cardLayout;
    private final Book book;
    private Button rightButton, leftButton;

    public BookDetails(int cardLayout, Book book) {
        this.cardLayout = cardLayout;
        this.book = book;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_details, container, false);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        TextView desc = view.findViewById(R.id.description);
        TextView bookName = view.findViewById(R.id.book_name), author = view.findViewById(R.id.author),
                publisher = view.findViewById(R.id.publisher), genre = view.findViewById(R.id.genre);
        rightButton = view.findViewById(R.id.right_button);
        leftButton = view.findViewById(R.id.left_button);
        ImageView editButton = view.findViewById(R.id.edit_button);
        ImageView deleteButton = view.findViewById(R.id.delete_button);

        desc.setMovementMethod(new ScrollingMovementMethod());
        bookName.setSelected(true);
        author.setSelected(true);
        publisher.setSelected(true);
        genre.setSelected(true);

        if(!LoginDetails.isLibrarian()) {
//            leftButton.setVisibility(View.GONE);
//            rightButton.setVisibility(View.VISIBLE);
//            setButtonAsPerCardLayoutOnlyForStudent();
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else {
//            leftButton.setVisibility(View.GONE);
//            rightButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogBox.display(getFragmentManager(), book);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAlertDialogForDelete();
                }
            });
        }

        setBookDetails();
    }

//    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
//    private void setButtonAsPerCardLayoutOnlyForStudent() {
//        switch (this.cardLayout) {
//            case R.layout.rejected_card:
//                leftButton.setVisibility(View.GONE);
//                rightButton.setVisibility(View.VISIBLE);
//                rightButton.setText("SEND REQUEST");
//                break;
//            case R.layout.requested_card:
//                leftButton.setVisibility(View.GONE);
//                rightButton.setVisibility(View.VISIBLE);
//                rightButton.setText("UNSEND REQUEST");
//                break;
//            case R.layout.returned_card:
//                leftButton.setVisibility(View.GONE);
//                rightButton.setVisibility(View.GONE);
//                break;
//            case R.layout.issued_card:
//                leftButton.setVisibility(View.VISIBLE);
//                rightButton.setVisibility(View.VISIBLE);
//                leftButton.setText("RETURN");
//                rightButton.setText("RENEW");
//                break;
//            default:
//                break;
//        }
//    }
    
    private void openAlertDialogForDelete() {
        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(getContext());
        deleteDialogBuilder.setMessage("Confirm Delete");
        deleteDialogBuilder.setPositiveButton("DELETE",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    FirebaseDatabase.getInstance().getReference("library").child(book.getKey()).removeValue();
                    FirebaseStorage.getInstance().getReferenceFromUrl(book.getImageURL())
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("Image Deleted");
                                }
                            });
                    FirebaseDatabase.getInstance().getReference("requests").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                if(Objects.equals(ds.child("bookRef").getValue(), book.getKey())) {
                                    ds.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
        });

        AlertDialog deleteDialog = deleteDialogBuilder.create();
        deleteDialog.show();
    }
    
    @SuppressLint("SetTextI18n")
    private void setBookDetails() {
        long isbnText = book.getIsbn();
        String bookNameText = book.getBookName();
        String authorText = book.getAuthor();
        String publisherText = book.getPublisher();
        String genreText = book.getGenre();
        String descriptionText = book.getDescription();
        String imageURL = book.getImageURL();
        int availableText = book.getAvailable();

        TextView isbn = this.view.findViewById(R.id.isbn);
        TextView bookName = this.view.findViewById(R.id.book_name);
        TextView author = this.view.findViewById(R.id.author);
        TextView publisher = this.view.findViewById(R.id.publisher);
        TextView genre = this.view.findViewById(R.id.genre);
        TextView available = this.view.findViewById(R.id.availale);
        TextView description = this.view.findViewById(R.id.description);
        ImageView bookPhoto = this.view.findViewById(R.id.book_photo);

        isbn.setText("" + isbnText);
        bookName.setText(bookNameText);
        author.setText(authorText);
        publisher.setText(publisherText);
        genre.setText(genreText);
        available.setText("Available: " + availableText);
        description.setText(descriptionText);
        Glide.with(Objects.requireNonNull(getContext())).load(imageURL).into(bookPhoto);
    }
}

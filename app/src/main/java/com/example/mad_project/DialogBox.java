package com.example.mad_project;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class DialogBox extends DialogFragment {
    public static final String TAG = "full_screen_dialog_box";
    public static int lastBookNum;
    ImageView imageView;
    NachoTextView nachoTextView;
    View view;
    static Book book;
    Uri imageURI;

    public static void display(FragmentManager fragmentManager) {
        DialogBox fullScreenDialog = new DialogBox();
        fullScreenDialog.show(fragmentManager, TAG);
        book = null;
    }

    public static void display(FragmentManager fragmentManager, Book book) {
        DialogBox fullScreenDialog = new DialogBox();
        fullScreenDialog.show(fragmentManager, TAG);
        DialogBox.book = book;
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
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
            Objects.requireNonNull(Objects.requireNonNull(dialog).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.w(TAG, "" + R.layout.book_full_screen_dialog_box);
        return inflater.inflate(R.layout.book_full_screen_dialog_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        nachoTextView = view.findViewById(R.id.input_genre);
        nachoTextView.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

        Button cancel = view.findViewById(R.id.cancel_button), save = view.findViewById(R.id.save_button);
        imageView = view.findViewById(R.id.book_img);

        if(book != null)
            setBookDetails();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(book == null)
                    addNewBook(lastBookNum);
                else
                    updateExistingBook();
                dismiss();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(book == null)
                    selectImage();
            }
        });
    }

    private void selectImage() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/jpeg");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(imageIntent, "Select an Image"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == -1 && data != null) {
            imageURI = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), imageURI);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        EditText isbn = this.view.findViewById(R.id.input_isbn);
        EditText bookName = this.view.findViewById(R.id.input_name);
        EditText author = this.view.findViewById(R.id.input_author);
        EditText publisher = this.view.findViewById(R.id.input_publisher);
        EditText available = this.view.findViewById(R.id.input_available);
        EditText description = this.view.findViewById(R.id.input_description);
        ImageView bookPhoto = this.view.findViewById(R.id.book_img);

        isbn.setEnabled(false);
        isbn.setText("" + isbnText);
        bookName.setText(bookNameText);
        author.setText(authorText);
        publisher.setText(publisherText);
        nachoTextView.setText(Arrays.asList(genreText.replace("#", "").split(" ")));
        available.setText("" + availableText);
        description.setText(descriptionText);
        Glide.with(Objects.requireNonNull(getContext())).load(imageURL).into(bookPhoto);
    }

    private void addNewBook(final int number) {
        try{
            EditText isbn = this.view.findViewById(R.id.input_isbn);
            EditText bookName = this.view.findViewById(R.id.input_name);
            EditText author = this.view.findViewById(R.id.input_author);
            EditText publisher = this.view.findViewById(R.id.input_publisher);
            EditText available = this.view.findViewById(R.id.input_available);
            EditText description = this.view.findViewById(R.id.input_description);

            isbn.setEnabled(true);
            final String hash = "#";
            StringBuilder genre = new StringBuilder();
            for(Chip chip : nachoTextView.getAllChips()) {
                genre.append(hash.concat(chip.getText().toString() + " "));
            }

            final Book insertBook = new Book();
            insertBook.setBookName(bookName.getText().toString());
            insertBook.setAuthor(author.getText().toString());
            insertBook.setIsbn(Long.parseLong(isbn.getText().toString()));
            insertBook.setPublisher(publisher.getText().toString());
            insertBook.setGenre(genre.toString());
            insertBook.setAvailable(Integer.parseInt(available.getText().toString()));
            insertBook.setDescription(description.getText().toString());

            StorageReference uploadImage = FirebaseStorage.getInstance().getReference().child(isbn.getText().toString());
            uploadImage.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            insertBook.setImageURL(uri.toString());

                            DatabaseReference addBookRef = FirebaseDatabase.getInstance().getReference("library");
                            addBookRef.child("book" + number).setValue(insertBook);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            insertBook.setImageURL("no-value");

                            DatabaseReference addBookRef = FirebaseDatabase.getInstance().getReference("library");
                            addBookRef.child("book" + number).setValue(insertBook);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    insertBook.setImageURL("no-value");

                    DatabaseReference addBookRef = FirebaseDatabase.getInstance().getReference("library");
                    addBookRef.child("book" + lastBookNum).setValue(insertBook);
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Unexpected Error.\nCheck your inputs", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateExistingBook() {
        try {
            EditText isbn = this.view.findViewById(R.id.input_isbn);
            EditText bookName = this.view.findViewById(R.id.input_name);
            EditText author = this.view.findViewById(R.id.input_author);
            EditText publisher = this.view.findViewById(R.id.input_publisher);
            EditText available = this.view.findViewById(R.id.input_available);
            EditText description = this.view.findViewById(R.id.input_description);

            final String hash = "#";
            StringBuilder genre = new StringBuilder();
            for(Chip chip : nachoTextView.getAllChips()) {
                genre.append(hash.concat(chip.getText().toString() + " "));
            }

            final Book updateBook = new Book();
            updateBook.setBookName(bookName.getText().toString());
            updateBook.setAuthor(author.getText().toString());
            updateBook.setIsbn(Long.parseUnsignedLong(isbn.getText().toString()));
            updateBook.setPublisher(publisher.getText().toString());
            updateBook.setGenre(genre.toString());
            updateBook.setAvailable(Integer.parseUnsignedInt(available.getText().toString()));
            updateBook.setDescription(description.getText().toString());
            updateBook.setImageURL(DialogBox.book.getImageURL());

            DatabaseReference addBookRef = FirebaseDatabase.getInstance().getReference("library");
            addBookRef.child(book.getKey()).setValue(updateBook);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Unexpected Error.\nCheck your inputs", Toast.LENGTH_SHORT).show();
        }
    }
}
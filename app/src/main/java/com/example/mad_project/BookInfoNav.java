package com.example.mad_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class BookInfoNav extends Fragment {
    View view;
    SearchView searchView;
    int cardLayout;

    public BookInfoNav(int cardLayout) {
        this.cardLayout = cardLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_info_nav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        searchView = view.findViewById(R.id.search_bar);
        ImageView addNewBook = view.findViewById(R.id.add_new_book);
        if(LoginDetails.isLibrarian() && cardLayout == R.layout.rejected_card) {
            addNewBook.setVisibility(View.VISIBLE);
            addNewBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogBox.display(getFragmentManager());
                }
            });
        } else {
            addNewBook.setVisibility(View.GONE);
        }

        if(LoginDetails.isLibrarian()) {
            if(this.cardLayout == R.layout.rejected_card)
                setLayoutInLibraryForLibrarian();
            else if(this.cardLayout == R.layout.requested_card)
                setLayoutInRequestForLibrarian();
            else if(this.cardLayout == R.layout.issued_card)
                setLayoutInIssueForLibrarian();
            else if(this.cardLayout == R.layout.returned_card)
                setLayoutInReturnForLibrarian();
        } else {
            if(this.cardLayout == R.layout.rejected_card)
                setLayoutInLibraryForStudent();
            else if(this.cardLayout == R.layout.requested_card)
                setLayoutInRequestForStudent();
            else if(this.cardLayout == R.layout.issued_card)
                setLayoutInIssueForStudent();
            else if(this.cardLayout == R.layout.returned_card)
                setLayoutInReturnForStudent();
        }

        search();
    }

    private void setLayoutInLibraryForLibrarian() {
        final ArrayList<Book> bookList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("library");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();
                String key = "";
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    key = ds.getKey();
                    assert book != null;
                    book.setKey(key);
                    bookList.add(book);
                }
                Collections.reverse(bookList);
                assert key != null;
                DialogBox.lastBookNum = Integer.parseInt(key.replace("book", "")) + 1;
                BookAdapter personAdapter = new BookAdapter(getContext(), cardLayout, bookList);
                listView.setAdapter(personAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLayoutInRequestForLibrarian() {
        final ArrayList<Requests> requestList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(final DataSnapshot ds : snapshot.getChildren()) {
                    final Requests req = ds.getValue(Requests.class);
                    if(Objects.requireNonNull(req).getStatus().equals("request") || req.getStatus().equals("renew")) {
                        FirebaseDatabase.getInstance().getReference("library").child(req.getBookRef())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String key;
                                        req.setBook(snapshot.getValue(Book.class));
                                        if(req.getBook() != null) {
                                            req.getBook().setKey(req.getBookRef());
                                            key = ds.getKey();
                                            req.setKey(key);
                                            requestList.add(req);
                                            Collections.reverse(requestList);

                                            RequestAdapter personAdapter = new RequestAdapter(getContext(), cardLayout, requestList, BookInfoNav.this);
                                            listView.setAdapter(personAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLayoutInIssueForLibrarian() {
        final ArrayList<Requests> requestList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(final DataSnapshot ds : snapshot.getChildren()) {
                    final Requests req = ds.getValue(Requests.class);
                    if(Objects.requireNonNull(req).getStatus().equals("issue")) {
                        FirebaseDatabase.getInstance().getReference("library").child(req.getBookRef())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String key;
                                        req.setBook(snapshot.getValue(Book.class));
                                        if(req.getBook() != null) {
                                            req.getBook().setKey(req.getBookRef());
                                            key = ds.getKey();
                                            req.setKey(key);
                                            requestList.add(req);
                                            Collections.reverse(requestList);

                                            RequestAdapter personAdapter = new RequestAdapter(getContext(), cardLayout, requestList, BookInfoNav.this);
                                            listView.setAdapter(personAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLayoutInReturnForLibrarian() {
        final ArrayList<Requests> requestList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(final DataSnapshot ds : snapshot.getChildren()) {
                    final Requests req = ds.getValue(Requests.class);
                    if(Objects.requireNonNull(req).getStatus().equals("return")) {
                        FirebaseDatabase.getInstance().getReference("library").child(req.getBookRef())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String key ;
                                        req.setBook(snapshot.getValue(Book.class));
                                        if(req.getBook() != null) {
                                            req.getBook().setKey(req.getBookRef());
                                            key = ds.getKey();
                                            req.setKey(key);
                                            requestList.add(req);
                                            Collections.reverse(requestList);

                                            RequestAdapter personAdapter = new RequestAdapter(getContext(), cardLayout, requestList, BookInfoNav.this);
                                            listView.setAdapter(personAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLayoutInLibraryForStudent() {
        final ArrayList<Book> bookList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("library");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();
                String key;
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    key = ds.getKey();
                    assert book != null;
                    book.setKey(key);
                    bookList.add(book);
                }
                Collections.reverse(bookList);
                getNumberOfRequests();
                BookAdapter personAdapter = new BookAdapter(getContext(), cardLayout, bookList);
                listView.setAdapter(personAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLayoutInRequestForStudent() {
        final ArrayList<Requests> requestList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(final DataSnapshot ds : snapshot.getChildren()) {
                    final Requests req = ds.getValue(Requests.class);
                    if(Objects.requireNonNull(req).getEmail().equals(LoginDetails.getEmail()) && (req.getStatus().equals("request") || req.getStatus().equals("renew"))) {
                        FirebaseDatabase.getInstance().getReference("library").child(req.getBookRef())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String key;
                                        req.setBook(snapshot.getValue(Book.class));
                                        if(req.getBook() != null) {
                                            req.getBook().setKey(req.getBookRef());
                                            key = ds.getKey();
                                            req.setKey(key);
                                            requestList.add(req);
                                            Collections.reverse(requestList);

                                            RequestAdapter personAdapter = new RequestAdapter(getContext(), cardLayout, requestList, BookInfoNav.this);
                                            listView.setAdapter(personAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLayoutInIssueForStudent() {
        final ArrayList<Requests> requestList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(final DataSnapshot ds : snapshot.getChildren()) {
                    final Requests req = ds.getValue(Requests.class);
                    if(Objects.requireNonNull(req).getEmail().equals(LoginDetails.getEmail()) && req.getStatus().equals("issue")) {
                        FirebaseDatabase.getInstance().getReference("library").child(req.getBookRef())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String key;
                                        req.setBook(snapshot.getValue(Book.class));
                                        if(req.getBook() != null) {
                                            req.getBook().setKey(req.getBookRef());
                                            key = ds.getKey();
                                            req.setKey(key);
                                            requestList.add(req);
                                            Collections.reverse(requestList);

                                            RequestAdapter personAdapter = new RequestAdapter(getContext(), cardLayout, requestList, BookInfoNav.this);
                                            listView.setAdapter(personAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLayoutInReturnForStudent() {
        final ArrayList<Requests> requestList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(final DataSnapshot ds : snapshot.getChildren()) {
                    final Requests req = ds.getValue(Requests.class);
                    if(Objects.requireNonNull(req).getEmail().equals(LoginDetails.getEmail()) && req.getStatus().equals("return")) {
                        FirebaseDatabase.getInstance().getReference("library").child(req.getBookRef())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String key;
                                        req.setBook(snapshot.getValue(Book.class));
                                        if(req.getBook() != null) {
                                            req.getBook().setKey(req.getBookRef());
                                            key = ds.getKey();
                                            req.setKey(key);
                                            requestList.add(req);
                                            Collections.reverse(requestList);

                                            RequestAdapter personAdapter = new RequestAdapter(getContext(), cardLayout, requestList, BookInfoNav.this);
                                            listView.setAdapter(personAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void getReturnDateFragment(String requestRef) {
        ReturnRenewDateDialogBox returnDateFragment = new ReturnRenewDateDialogBox(requestRef);
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().add(returnDateFragment, "return_date_dialog_box").commit();
    }

    private void getNumberOfRequests() {
        FirebaseDatabase.getInstance().getReference("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String key = "";
                for(DataSnapshot ds : snapshot.getChildren()) {
                    key = ds.getRef().getKey();
                }
                BookAdapter.lastBookNum = Integer.parseInt(Objects.requireNonNull(key).replace("request", "")) + 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void search() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(cardLayout == R.layout.rejected_card)
                    searchBookByName(s);
                else if (cardLayout == R.layout.requested_card)
                    searchRequests("request", s);
                else if (cardLayout == R.layout.issued_card)
                    searchRequests("issue", s);
                else if (cardLayout == R.layout.returned_card)
                    searchRequests("return", s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(cardLayout == R.layout.rejected_card)
                    searchBookByName(s);
                else if (cardLayout == R.layout.requested_card)
                    searchRequests("request", s);
                else if (cardLayout == R.layout.issued_card)
                    searchRequests("issue", s);
                else if (cardLayout == R.layout.returned_card)
                    searchRequests("return", s);
                return false;
            }
        });
    }

    private void searchBookByName(String bookName) {
        final ArrayList<Book> bookList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        FirebaseDatabase.getInstance().getReference("library").orderByChild("bookName")
                .startAt(bookName).endAt(bookName + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();
                String key = "";
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    key = ds.getKey();
                    assert book != null;
                    book.setKey(key);
                    bookList.add(book);
                }
                Collections.reverse(bookList);
                assert key != null;
                BookAdapter bookAdapter = new BookAdapter(getContext(), cardLayout, bookList);
                listView.setAdapter(bookAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void searchRequests(final String status, final String email) {
        final ArrayList<Requests> requestList = new ArrayList<>();
        final ListView listView = this.view.findViewById(R.id.list_view);

        FirebaseDatabase.getInstance().getReference("requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestList.clear();
                        for(final DataSnapshot ds : snapshot.getChildren()) {
                            final Requests req = ds.getValue(Requests.class);
                            if(Objects.requireNonNull(req).getEmail().equals(email)) {
                                if(Objects.requireNonNull(req).getStatus().equals(status) || req.getStatus().equals("renew")) {
                                    FirebaseDatabase.getInstance().getReference("library").child(Objects.requireNonNull(req).getBookRef())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String key;
                                                    req.setBook(snapshot.getValue(Book.class));
                                                    if(req.getBook() != null) {
                                                        req.getBook().setKey(req.getBookRef());
                                                        key = ds.getKey();
                                                        req.setKey(key);
                                                        requestList.add(req);
                                                        Collections.reverse(requestList);

                                                        RequestAdapter requestAdapter = new RequestAdapter(getContext(), cardLayout, requestList, BookInfoNav.this);
                                                        listView.setAdapter(requestAdapter);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

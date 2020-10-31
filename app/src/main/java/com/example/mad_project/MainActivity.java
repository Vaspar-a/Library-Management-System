package com.example.mad_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    LinearLayout library, requests, issued, returned;
    TextView[] text;
    ImageView[] vector;
    int[] layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLoginInfo();
        library = findViewById(R.id.library);
        requests = findViewById(R.id.requests);
        issued = findViewById(R.id.issued);
        returned = findViewById(R.id.returned);

        text = new TextView[]{findViewById(R.id.library_text), findViewById(R.id.requests_text), findViewById(R.id.issued_text), findViewById(R.id.returned_text)};
        vector = new ImageView[]{findViewById(R.id.library_image), findViewById(R.id.requests_image), findViewById(R.id.issued_image), findViewById(R.id.returned_image)};
        layouts = new int[]{R.layout.rejected_card, R.layout.requested_card, R.layout.issued_card, R.layout.returned_card};

        setBookInfoNavFragment(0);

        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBookInfoNavFragment(0);
            }
        });

        issued.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBookInfoNavFragment(2);
            }
        });

        returned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBookInfoNavFragment(3);
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBookInfoNavFragment(1);
            }
        });
    }

    private void setBookInfoNavFragment(int position) {
        changeActiveStateOfBottomBar(position);
        
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame, new BookInfoNav(layouts[position])).addToBackStack(null).commit();
    }

    private void changeActiveStateOfBottomBar(int position) {
        for (int i = 0; i < 4; i++) {
            if(i == position) {
                text[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.brightWhite));
                DrawableCompat.setTint(vector[i].getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.brightWhite));
            } else {
                text[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.paleWhite));
                DrawableCompat.setTint(vector[i].getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.paleWhite));
            }
        }
    }

    private void setLoginInfo() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        assert acct != null;
        String photoUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "";
        new LoginDetails(acct.getDisplayName(), Objects.requireNonNull(acct.getEmail()), photoUrl);
    }
}
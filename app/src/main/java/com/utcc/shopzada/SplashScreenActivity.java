package com.utcc.shopzada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utcc.shopzada.Models.UserModel;
import com.utcc.shopzada.Prevalent.Prevalent;

import io.paperdb.Paper;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Paper.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        String username = Paper.book().read(Prevalent.UsernameKey);
        String password = Paper.book().read(Prevalent.PasswordKey);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    allowAccess(username, password);
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
                    Animatoo.INSTANCE.animateZoom(SplashScreenActivity.this);
                    finish();
                }
            }
        }, 5000);

    }

    private void allowAccess(final String username, final String password) {
        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("PAPER", username);
                Log.d("PAPER", password);
                if (snapshot.child(username).exists()) {
                    UserModel userData = snapshot.child(username).getValue(UserModel.class);

                    if (userData.getUsername().equals(username)) {
                        if (userData.getPassword().equals(password)) {
                            Toast.makeText(SplashScreenActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();

                            Prevalent.currentOnlineUser = userData;

                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            Animatoo.INSTANCE.animateSlideLeft(SplashScreenActivity.this);
                            finish();
                        } else {
                            Toast.makeText(SplashScreenActivity.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                            Paper.book().destroy();

                            startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
                            Animatoo.INSTANCE.animateZoom(SplashScreenActivity.this);
                            finish();
                        }
                    } else {
                        Toast.makeText(SplashScreenActivity.this, "Username is incorrect!", Toast.LENGTH_SHORT).show();
                        Paper.book().destroy();

                        startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
                        Animatoo.INSTANCE.animateZoom(SplashScreenActivity.this);
                        finish();
                    }
                } else {
                    Toast.makeText(SplashScreenActivity.this, "Username is not exists!", Toast.LENGTH_SHORT).show();
                    Paper.book().destroy();

                    startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
                    Animatoo.INSTANCE.animateZoom(SplashScreenActivity.this);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
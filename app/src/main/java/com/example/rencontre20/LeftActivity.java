package com.example.rencontre20;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LeftActivity extends AppCompatActivity
{
    FirebaseAuth auth;
    Button logoutButton;
    TextView email;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.left_page_layout);

        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout);
        email = findViewById(R.id.textEmail);
        user = auth.getCurrentUser();

        if(user == null)
        {
            Intent intent = new Intent(getApplicationContext(), L_LoginBottomSheetDialogFragment.class);
            startActivity(intent);
            finish();
        }
        else
        {
            email.setText(user.getEmail());
        }

        logoutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), L_LandingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    float x1, x2;
    public boolean onTouchEvent (MotionEvent touchEvent)
    {
        switch(touchEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if (x1 > x2)
                {
                    Intent i = new Intent(LeftActivity.this, SwipingActivity.class);
                    startActivity(i);
                    finish();
                }

        }
        return false;
    }
}

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
    Button button;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.left_page);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();

        if(user == null)
        {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else
        {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), Login.class);
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
                    Intent i = new Intent(LeftActivity.this, LandingActivity.class);
                    startActivity(i);
                    finish();
                }

        }
        return false;
    }
}

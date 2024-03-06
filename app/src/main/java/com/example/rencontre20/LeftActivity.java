package com.example.rencontre20;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class LeftActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.left_page);
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
                }

        }
        return false;
    }
}

package com.example.rencontre20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class L_LandingActivity extends AppCompatActivity
{
    FirebaseAuth mAuth;
    Button buttonLogin;
    Button buttonRegister;


    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            Intent intent = new Intent(getApplicationContext(), SwipingActivity.class);
            startActivity(intent);
            finish();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        mAuth = FirebaseAuth.getInstance();
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Show the bottom sheet login dialog fragment
                L_LoginBottomSheetDialogFragment LLoginBottomSheetDialogFragment = new L_LoginBottomSheetDialogFragment();
                LLoginBottomSheetDialogFragment.show(getSupportFragmentManager(), LLoginBottomSheetDialogFragment.getTag());
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                L_RegisterBottomSheetDialogFragment LRegisterBottomSheetDialogFragment = new L_RegisterBottomSheetDialogFragment();
                LRegisterBottomSheetDialogFragment.show(getSupportFragmentManager(), LRegisterBottomSheetDialogFragment.getTag());
            }
        });
    }
}

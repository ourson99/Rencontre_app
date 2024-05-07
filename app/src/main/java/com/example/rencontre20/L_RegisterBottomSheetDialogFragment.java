package com.example.rencontre20;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class L_RegisterBottomSheetDialogFragment extends BottomSheetDialogFragment
{

    EditText editTextEmail, editTextPassword;
    TextInputEditText e; // More clean ui ?
    Button buttonRegister;
    FirebaseAuth mAuth;
    //FirebaseStorage mStorage;
    String imageURL = null;
    String uri;

    public L_RegisterBottomSheetDialogFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_register_sheet_dialog, container, false);
    }

    // You can initialize your views here if you need to set up listeners or adapters
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = view.findViewById(R.id.emailEditText);
        editTextPassword = view.findViewById(R.id.passwordEditText);


        buttonRegister = view.findViewById(R.id.registerButton);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String email, password;

                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(getContext(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(getContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (password.length() < 6)
                {
                    Toast.makeText(getContext(), "Password needs to be 6 characters or more", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Account created", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                                else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    // If there is a user collision (the email is already used)
                                    Toast.makeText(getContext(), "This email is already in use by another account.", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Account not created", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void saveData()
    {
    }
}

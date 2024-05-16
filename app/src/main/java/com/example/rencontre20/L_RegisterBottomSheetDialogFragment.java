package com.example.rencontre20;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class L_RegisterBottomSheetDialogFragment extends BottomSheetDialogFragment {
    EditText editTextEmail, editTextPassword, editTextPhone, editTextName;
    Button buttonRegister;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_bottom_register_sheet_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextEmail = view.findViewById(R.id.emailEditText);
        editTextPassword = view.findViewById(R.id.passwordEditText);
        editTextPhone = view.findViewById(R.id.phoneEditText);
        editTextName = view.findViewById(R.id.nameEditText);
        buttonRegister = view.findViewById(R.id.registerButton);

        buttonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String name = editTextName.getText().toString().trim();


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone))
        {
            Toast.makeText(getContext(), "Remplir tout les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6)
        {
            Toast.makeText(getContext(), "Le mot de passe doit posséder 6 charactères au moins", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            saveUserInformation();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Enregistrement échoué: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserInformation()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("name", name);
        user.put("phone", phone);
        user.put("password", password);

        if (mAuth.getCurrentUser() == null)
        {
            Toast.makeText(getContext(), "Aucun utilisateur de connecté.", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("users").document(mAuth.getCurrentUser().getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful()) {
                            dismiss();
                        } else {
                            Exception exception = task.getException();
                            Toast.makeText(getContext(), "La sauvegarde de donnée a échouée: " + (exception != null ? exception.getMessage() : "Erreur inconnue"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

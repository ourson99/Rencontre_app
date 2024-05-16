package com.example.rencontre20;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class LeftActivity extends AppCompatActivity
{
    FirebaseAuth auth;
    FirebaseFirestore db;

    Button logoutButton, rightArrow;
    TextView email, name, description;
    TextView changeName, changePicture, changeDescription;
    ImageView profileImage;
    StorageReference storageReference;
    FirebaseStorage storage;

    Uri imageUri;

    FirebaseUser user;

    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.left_page_layout);

        // Initialize FirebaseAuth, Firestore, storange and current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profileImage");
        storageReference  = storageReference.child("profileImage");

        // Set up UI components
        logoutButton = findViewById(R.id.logout);
        email = findViewById(R.id.textEmail);
        name = findViewById(R.id.textName);
        description = findViewById(R.id.textDescr);
        profileImage = findViewById(R.id.profilePicture);

        // Fetch user data from Firestore
        fetchUserData();


        // Buttons
        changeDescription = findViewById(R.id.changeDescription);
        rightArrow = findViewById(R.id.right_arrow_left);
        changeName = findViewById(R.id.changeName);
        changePicture = findViewById(R.id.changePicture);
        changeDescription.setOnClickListener(v -> changeUserDescription());
        changeName.setOnClickListener(v -> changeUserName());
        changePicture.setOnClickListener(v -> changeUserProfilePicture());

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), L_LandingActivity.class);
            startActivity(intent);
            finish();
        });
        rightArrow.setOnClickListener(view -> {
            Intent i = new Intent(LeftActivity.this, SwipingActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void fetchUserData()
    {
        if (user == null) return;

        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists())
                            {
                                // Update UI with data from Firestore
                                email.setText(user.getEmail());
                                name.setText(document.getString("name"));
                                description.setText(document.getString("description"));
                                // Load profile image URL from Firestore and display it
                                String imageUrl = document.getString("profileImageUrl");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(LeftActivity.this)
                                            .load(imageUrl)
                                            .into(profileImage);
                                }

                            }
                        }
                    }
                });
    }

    private void changeUserDescription()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Description");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String newDescription = input.getText().toString();
                if (user == null) return; // Check for logged in user

                Map<String, Object> updates = new HashMap<>();
                updates.put("description", newDescription);

                db.collection("users").document(user.getUid())
                        .set(updates, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            description.setText(newDescription);  // Update the TextView directly
                        })
                        .addOnFailureListener(e -> Toast.makeText(LeftActivity.this, "Mise à jour échouée: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void changeUserName()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Name");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                if (user == null) return; // Check for logged in user

                Map<String, Object> updates = new HashMap<>();
                updates.put("name", newName);

                db.collection("users").document(user.getUid())
                        .set(updates, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(LeftActivity.this, "Description updated successfully!", Toast.LENGTH_SHORT).show();
                            name.setText(newName);  // Update the TextView directly
                        })
                        .addOnFailureListener(e -> Toast.makeText(LeftActivity.this, "Failed to update description: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void changeUserProfilePicture() {
        SelectImage();
    }

    public void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/jpeg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select JPEG"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri)
    {
        if (imageUri != null)
        {
            final StorageReference fileRef = storageReference.child("profileImage/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                updateProfileImageUrl(imageUrl);
            })).addOnFailureListener(e -> {
                Toast.makeText(this, "Échec de l'envoit de l'image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateProfileImageUrl(String imageUrl)
    {
        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageUrl", imageUrl);

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(LeftActivity.this)
                                .load(imageUrl)
                                .into(profileImage);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Mise à jour échouée: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}

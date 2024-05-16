package com.example.rencontre20;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private ImageView profileImageView;
    private TextView nameTextView, descriptionTextView;
    private Button yesButton, noButton, quitButton;
    private TextView invitation;

    private List<UserProfile> userProfiles;
    private int currentUserIndex = 0;

    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_bottom_match_sheet_dialog, container, false);
        profileImageView = view.findViewById(R.id.imageView3);
        nameTextView = view.findViewById(R.id.textView);
        descriptionTextView = view.findViewById(R.id.textView6);
        yesButton = view.findViewById(R.id.yesButton);
        noButton = view.findViewById(R.id.noButton);
        quitButton = view.findViewById(R.id.quitButton);
        invitation = view.findViewById(R.id.invitationText);

        yesButton.setOnClickListener(v -> {
            handleAccept();
        });

        noButton.setOnClickListener(v -> {
            loadNextProfile();
        });

        quitButton.setOnClickListener(v -> dismiss());

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initializeUserProfiles();

        return view;
    }

    private void initializeUserProfiles()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userProfiles = new ArrayList<>();

        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    String userId = document.getId();
                    if (!userId.equals(currentUserId))
                    {  // Skip the current user's ID
                        String name = document.getString("name");
                        String description = document.getString("description");
                        String imageUrl = document.getString("profileImageUrl");

                        UserProfile profile = new UserProfile(name, description, imageUrl, userId);
                        userProfiles.add(profile);
                    }
                }
                if (!userProfiles.isEmpty())
                {
                    loadUserProfile(currentUserIndex);
                }
                else
                {
                    Toast.makeText(getContext(), "Aucun profils trouvé.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(), "Échec au téléchargement des profils.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile(int index)
    {
        if (index < userProfiles.size())
        {
            UserProfile userProfile = userProfiles.get(index);
            nameTextView.setText(userProfile.getName());
            descriptionTextView.setText(userProfile.getDescription());
            Glide.with(getContext()).load(userProfile.getImageUrl()).into(profileImageView);
        }
        else
        {
            dismiss(); // No more profiles to show, close the dialog
            Toast.makeText(getContext(), "Plus de profils disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNextProfile()
    {
        currentUserIndex++;
        loadUserProfile(currentUserIndex);
    }

    private void handleAccept()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserProfile matchedProfile = userProfiles.get(currentUserIndex);
        String matchedUserId = matchedProfile.getUserID();

        String invitationText = invitation.getText().toString();

        DocumentReference currentUserRef = db.collection("users").document(currentUserId);
        DocumentReference matchedUserRef = db.collection("users").document(matchedUserId);

        // Create a data object that can be used for updates
        Map<String, Object> matchData = new HashMap<>();
        matchData.put("invitation", invitationText);
        matchData.put("matchedUserId", matchedUserId);

        // Update current user's document with the matched user's ID and invitation
        currentUserRef.update("matches", FieldValue.arrayUnion(matchData))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Tu as match avec " + matchedProfile.getName() + " et l'invitation est envoyée.", Toast.LENGTH_SHORT).show();
                    loadNextProfile();  // Load next profile or handle UI update
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Échec à l'ajout du match: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        // Optionally, update the matched user's document to include the current user's ID
        matchedUserRef.update("matchedBy", FieldValue.arrayUnion(currentUserId), "invitations", FieldValue.arrayUnion(invitationText))
                .addOnSuccessListener(aVoid -> {
                    // Optionally notify the current user that the match was added successfully
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Échec à la mise à jour pour l'autre utilisateur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
        invitation.setText("");

    }
}

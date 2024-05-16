package com.example.rencontre20;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RightActivity extends AppCompatActivity implements MatchesAdapter.OnItemClickListener
{
    Button leftArrow;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView mRecyclerView;
    private MatchesAdapter mAdapter;
    private List<UserProfile> mUserProfiles;
    private Map<String, String> invitationsMap = new HashMap<>();

    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView reviewsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.right_page);

        profileImageView = findViewById(R.id.profileImageRight);
        nameTextView = findViewById(R.id.nameTextRight);
        descriptionTextView = findViewById(R.id.descriptionTextRight);
        reviewsTextView = findViewById(R.id.reviewsRight);

        mRecyclerView = findViewById(R.id.matchesRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mUserProfiles = new ArrayList<>();

        // Pass the context, userProfiles list, invitations map, and the listener
        mAdapter = new MatchesAdapter(this, mUserProfiles, invitationsMap, this);
        mRecyclerView.setAdapter(mAdapter);

        leftArrow = findViewById(R.id.left_arrow_right);
        leftArrow.setOnClickListener(view -> {
            startActivity(new Intent(this, SwipingActivity.class));
            finish();
        });

        if (auth.getCurrentUser() != null)
        {
            fetchMatches();
        }
    }

    @Override
    public void onItemClick(UserProfile userProfile)
    {
        Glide.with(this).load(userProfile.getImageUrl()).into(profileImageView);
        nameTextView.setText(userProfile.getName());
        descriptionTextView.setText(userProfile.getDescription());
        String invitation = invitationsMap.get(userProfile.getUserID());
        if (invitation != null)
        {
            reviewsTextView.setText(invitation);
        }
        else
        {
            reviewsTextView.setText("");
        }
    }

    private void fetchMatches()
    {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("matches"))
            {
                List<Object> matches = (List<Object>) documentSnapshot.get("matches");
                List<String> userIds = new ArrayList<>();
                for (Object match : matches)
                {
                    if (match instanceof String)
                    {
                        userIds.add((String) match);
                    }
                    else if (match instanceof Map)
                    {
                        Map<String, Object> matchMap = (Map<String, Object>) match;
                        if (matchMap.containsKey("matchedUserId"))
                        {
                            String matchedUserId = (String) matchMap.get("matchedUserId");
                            userIds.add(matchedUserId);
                            if (matchMap.containsKey("invitation"))
                            {
                                invitationsMap.put(matchedUserId, (String) matchMap.get("invitation"));
                            }
                        }
                    }
                }
                loadUserProfiles(userIds);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Échec de la récupération des correspondances: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void loadUserProfiles(List<String> userIds)
    {
        if (userIds.isEmpty())
        {
            Toast.makeText(this, "Aucun ID utilisateur pour lequel charger des profils", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("users").whereIn(FieldPath.documentId(), userIds).get().addOnSuccessListener(queryDocumentSnapshots -> {
            mUserProfiles.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots)
            {
                if (document.exists()) {
                    String name = document.getString("name");
                    String description = document.getString("description");
                    String imageUrl = document.getString("profileImageUrl");
                    String userId = document.getId();
                    UserProfile userProfile = new UserProfile(name, description, imageUrl, userId);
                    mUserProfiles.add(userProfile);
                }
            }
            mAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Échec du chargement des profils utilisateur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}

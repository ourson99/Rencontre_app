package com.example.rencontre20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder>
{

    public interface OnItemClickListener
    {
        void onItemClick(UserProfile userProfile);
    }

    private List<UserProfile> mUserProfiles;
    private Context mContext;
    private OnItemClickListener mListener;
    private Map<String, String> mInvitations;

    public MatchesAdapter(Context context, List<UserProfile> userProfiles, Map<String, String> invitations, OnItemClickListener listener)
    {
        mUserProfiles = userProfiles;
        mContext = context;
        mListener = listener;
        mInvitations = invitations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.matches_recycle, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        UserProfile userProfile = mUserProfiles.get(position);
        holder.bind(userProfile);
    }

    @Override
    public int getItemCount() {
        return mUserProfiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView profileImage;
        public TextView profileName;

        public ViewHolder(View itemView, OnItemClickListener listener)
        {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImageView);
            profileName = itemView.findViewById(R.id.profileNameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    listener.onItemClick(mUserProfiles.get(position));
                }
            });
        }

        public void bind(UserProfile userProfile)
        {
            profileName.setText(userProfile.getName());
            Glide.with(mContext).load(userProfile.getImageUrl()).into(profileImage);
        }
    }
}

package com.optibe.android.beoptimist;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class AccountFragment extends Fragment {

    private Button edit_profile, log_out, update_email;
    private TextView username;
    private ImageView profile_image;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private List<BlogPost> list;
    private ImageButton share_btn;
    private String currentId;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_account, container, false);
        edit_profile = v.findViewById(R.id.edit_profile);
        log_out = v.findViewById(R.id.log_out);
        username = v.findViewById(R.id.username);
        update_email = v.findViewById(R.id.update_email);
        profile_image = v.findViewById(R.id.imageView2);
        share_btn = v.findViewById(R.id.share);
        mAuth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        MobileAds.initialize(getContext().getApplicationContext(), "ca-app-pub-2747396326626657/6938356672");

        AdView mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        firebaseFirestore = FirebaseFirestore.getInstance();


        if (mAuth.getCurrentUser() != null) {

            currentId = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("users").document(currentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult() != null && task.getResult().getString("image") != null) {
                        String username = task.getResult().getString("name");
                        String url = task.getResult().getString("image");
                        AccountFragment.this.username.setText(username);
                        if (isAdded()) {
                            Glide.with(AccountFragment.this).load(url).apply(RequestOptions.circleCropTransform()).into(profile_image);
                        }
                    } else if (task.getResult().getString("image") == null && task.getResult().getString("name") != null) {
                        String username = task.getResult().getString("name");
                        AccountFragment.this.username.setText(username);
                    }
                }
            });
            }
            edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Profile_edit.class);
                    startActivity(intent);

                }
            });

            update_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), UpdateEmail.class);
                    startActivity(intent);
                }
            });

            log_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });

            share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textToShare = "Hey Pal, I discovered a new app full of positivy It's so helpful for you too : https://play.google.com/store/apps/details?id=com.optibe.android.beoptimist";
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "A very useful app");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                    startActivity(Intent.createChooser(shareIntent, "share"));
                }
            });
            return v;
        }
    }
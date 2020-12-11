package com.optibe.android.beoptimist;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private Context context;
    private String currentUserId;

    public BlogRecyclerAdapter(List<BlogPost> blog_list) { this.blog_list = blog_list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_row, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String blogPostId = blog_list.get(position).blogPostId;
        final String user_id = blog_list.get(position).getUser_id();
        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        // Manage user's data
        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setUserData(userName, userImage);
                } else {
                    Toast.makeText(context, "There is a problem in the data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Manage post time
        long milliseconds = blog_list.get(position).getTimerstamp().getTime();
        String dateString = DateFormat.format("hh:mm A dd/MM", new Date(milliseconds)).toString();
        holder.setDate(dateString);

        //Get likes count
        firebaseFirestore.collection("posts/" + blogPostId + "/likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int count = queryDocumentSnapshots.size();
                        holder.updateLikesCount(count);
                    } else {
                        holder.updateLikesCount(0);
                    }
                }
            }
        });

        //Get likes
        firebaseFirestore.collection("posts/" + blogPostId + "/likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        holder.blogLikeBtn.setImageResource(R.mipmap.like_purple);
                    } else {
                        holder.blogLikeBtn.setImageResource(R.mipmap.like_grey);
                    }
                }
            }
        });

        // Manage the likes
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("posts/" + blogPostId + "/likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("posts/" + blogPostId + "/likes").document(currentUserId).set(likesMap);
                        }
//                        else {
//                            firebaseFirestore.collection("posts/" + blogPostId + "/likes").document(currentUserId).delete();
//                        }
                    }
                });
            }
        });

        holder.blogMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(context, holder.blogMenu, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE, 1, 1, "Report post");
                if (user_id.equals(currentUserId)) {
                    popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete post");;
                }
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 0:
                                firebaseFirestore.collection("posts").document(blogPostId).delete();
                                Toast.makeText(context, "Post deleted successfully :-)", Toast.LENGTH_SHORT).show();
                            case 1:
                                firebaseFirestore.collection("reports").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        Map<String, String> reportMap = new HashMap<>();
                                        reportMap.put("Report : ", "this post was reported");
                                        firebaseFirestore.collection("reports").document(blogPostId).set(reportMap);
                                    }
                                });
                                Toast.makeText(context,"Report successfully sent, thank you ;-)",Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private CircleImageView blogUserImage;
        private TextView descView;
        public ImageButton blogLikeBtn, blogMenu;
        public TextView blogLikeCount;
        private TextView blogDate;
        private TextView blogUserName;
        private TextView user_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogMenu = mView.findViewById(R.id.post_menu_btn);
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
        }

        public void setDescText(String descText) {
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }

        public void setDate(String date) {
            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

        public void setUserData(String name, String image) {
            user_name = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            user_name.setText(name);

            RequestOptions placeHolderOption = new RequestOptions();
            placeHolderOption.placeholder(R.drawable.profile_image);
            Glide.with(context).applyDefaultRequestOptions(placeHolderOption).load(image).into(blogUserImage);
        }

        public void updateLikesCount(int count) {
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            if (count > 1) {
                blogLikeCount.setText(count + " Likes");
            } else {
                blogLikeCount.setText(count + " Like");
            }
        }
    }
}
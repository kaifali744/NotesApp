package com.example.notesapp.model;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BindingAdapter {
//    @BindingAdapter("imageUrl")

    //Binding Adapter for displaying Image
    @androidx.databinding.BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .into(view);
    }

    //Binding Adapter for displaying time Added
    @androidx.databinding.BindingAdapter("timeAdded")
    public static void setTimeAdded(TextView view, Timestamp timeAdded) {
        if (timeAdded != null) {
            Date date = timeAdded.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            view.setText(sdf.format(date));
        } else {
            view.setText(""); // Handle null case
        }
    }
}

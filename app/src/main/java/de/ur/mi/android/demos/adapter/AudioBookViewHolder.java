package de.ur.mi.android.demos.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import de.ur.mi.android.demos.R;
import de.ur.mi.android.demos.data.audiobook.AudioBook;
import de.ur.mi.android.demos.utils.TimeFormatter;

public class AudioBookViewHolder extends RecyclerView.ViewHolder {

    public final AudioBookViewHolderClickListener listener;

    public AudioBookViewHolder(@NonNull View itemView, AudioBookViewHolderClickListener listener) {
        super(itemView);
        this.listener = listener;
    }

    public void bindViews(final AudioBook audioBook, final Context context) {
        TextView txtTitle = itemView.findViewById(R.id.txt_title),
                txtAuthor = itemView.findViewById(R.id.txt_author),
                txtDescription = itemView.findViewById(R.id.txt_description),
                txtDuration = itemView.findViewById(R.id.txt_duration);
        ImageView imageView = itemView.findViewById(R.id.img_thumbnail);
        txtTitle.setText(audioBook.getTitle());
        txtAuthor.setText(audioBook.getAuthor());
        txtDescription.setText(audioBook.getDescription().substring(0, 100) + "...");
        txtDuration.setText(TimeFormatter.formatSecondsToDurationString(audioBook.getDuration()));
        Glide.with(context)
                .load(audioBook.getWallpaperURLString())
                .centerCrop()
                .into(imageView);
        itemView.setOnClickListener(v -> listener.onViewHolderClicked(getLayoutPosition()));
    }

    public interface AudioBookViewHolderClickListener {
        void onViewHolderClicked(int position);
    }
}

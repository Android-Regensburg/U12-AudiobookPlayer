package de.ur.mi.android.demos.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.ur.mi.android.demos.R;
import de.ur.mi.android.demos.audio.AudioBook;

public class AudioBookAdapter extends RecyclerView.Adapter<AudioBookViewHolder> {

    private final ArrayList<AudioBook> dataList;
    private final Activity context;
    private final OnAudioBookAdapterItemClickedListener listener;

    public AudioBookAdapter(Activity context, OnAudioBookAdapterItemClickedListener listener) {
        this.context = context;
        dataList = new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(ArrayList<AudioBook> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AudioBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audiobook_item, parent, false);
        return new AudioBookViewHolder(view, position -> listener.onItemClicked(dataList.get(position)));
    }

    @Override
    public void onBindViewHolder(@NonNull AudioBookViewHolder holder, int position) {
        holder.bindViews(dataList.get(position), context);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnAudioBookAdapterItemClickedListener {
        void onItemClicked(AudioBook audioBook);
    }
}

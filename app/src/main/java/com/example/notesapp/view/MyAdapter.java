package com.example.notesapp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.databinding.ActivityJournalBinding;
import com.example.notesapp.databinding.JournalItemBinding;
import com.example.notesapp.model.Journal;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<Journal> journalArrayList;

    public void setJournals(ArrayList<Journal> journalArrayList) {
        this.journalArrayList = journalArrayList;
        notifyDataSetChanged(); // Notify RecyclerView that the data set has changed
    }

    public MyAdapter(Context context, ArrayList<Journal> journalArrayList) {
        this.context = context;
        this.journalArrayList = journalArrayList;
    }

    public MyAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        JournalItemBinding journalBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.journal_item,
                        parent,
                        false);
        return new MyViewHolder(journalBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Journal journal = journalArrayList.get(position);
        holder.journalBinding.setJournal(journal);
    }

    @Override
    public int getItemCount() {
        return journalArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private JournalItemBinding journalBinding;
        public MyViewHolder(JournalItemBinding journalBinding) {
            super(journalBinding.getRoot());
            this.journalBinding = journalBinding;
        }

    }
}

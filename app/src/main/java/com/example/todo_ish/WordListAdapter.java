package com.example.todo_ish;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;


public class WordListAdapter extends
        RecyclerView.Adapter<WordListAdapter.WordViewHolder> {
    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView wordItemView;
        public Button deleteButton;
        final WordListAdapter mAdapter;

        public WordViewHolder(View itemView, WordListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word);
            deleteButton = itemView.findViewById(R.id.delete);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
            deleteButton.setTag(mWordList.size());
        }

        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            // Use that to access the affected item in mWordList.
            Task element = mWordList.get(mPosition);
            mWordList.set(mPosition, new Task(element.getValue(), !element.isDone()));
            mAdapter.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        View mItemView = mInflater.inflate(R.layout.wordlist_item,
                parent, false);
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Task mCurrent = mWordList.get(position);
        if (mCurrent.isDone()) {
            holder.wordItemView.setTextColor(Color.parseColor("#777980"));
            holder.wordItemView.setText(R.string.done);
        } else {
            holder.wordItemView.setTextColor(Color.BLACK);
            holder.wordItemView.setText(mCurrent.getValue());
        }
        holder.deleteButton.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    public WordListAdapter(Context context,
                           LinkedList<Task> wordList) {
        mInflater = LayoutInflater.from(context);
        this.mWordList = wordList;
    }

    private final LinkedList<Task> mWordList;
    private LayoutInflater mInflater;

}
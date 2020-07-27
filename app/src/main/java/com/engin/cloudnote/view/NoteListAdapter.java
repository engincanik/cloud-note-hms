package com.engin.cloudnote.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.engin.cloudnote.R;
import com.engin.cloudnote.model.NoteOT;

import java.util.ArrayList;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {
    ArrayList<NoteOT> mNote;
    LayoutInflater inflater;
    Context context = null;

    public NoteListAdapter(Context context, ArrayList<NoteOT> notes) {
        this.mNote = notes;
        this.context = context;
    }


    @NonNull
    @Override
    public NoteListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.MyViewHolder holder, int position) {
        NoteOT selectedNote = mNote.get(position);
        holder.setData(selectedNote, position);
    }

    @Override
    public int getItemCount() {
        return mNote.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView noteCreator, noteDesc;
        Button detailBtn;

        public MyViewHolder(View view) {
            super(view);
            noteCreator = (TextView) view.findViewById(R.id.noteCreator);
            noteDesc = (TextView) view.findViewById(R.id.noteDesc);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        NoteOT clickedNoteItem = mNote.get(pos);
                        Toast.makeText(v.getContext(), "clicked clickedFilmItem id: " + clickedNoteItem.getId() + " name: " +
                                clickedNoteItem.getCreator(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("noteItemID", clickedNoteItem.getId());
                        intent.putExtra("noteItemCreator", clickedNoteItem.getCreator());
                        intent.putExtra("noteItemNote", clickedNoteItem.getNote());
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void setData(NoteOT selectedNote, int position) {
            this.noteCreator.setText(selectedNote.getCreator());
            this.noteDesc.setText(selectedNote.getNote());
        }

    }
}

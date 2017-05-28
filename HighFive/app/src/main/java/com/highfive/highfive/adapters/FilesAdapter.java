package com.highfive.highfive.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highfive.highfive.R;
import com.highfive.highfive.model.MyFile;

import java.util.List;

/**
 * Created by dan on 18.04.17.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {


    private OnItemClickListener listener;
    private List<MyFile> myFiles;

    public void setMyFiles(List<MyFile> myFiles) {
        this.myFiles = myFiles;
    }

    public interface OnItemClickListener {
        void onItemClick(int item);
    }

    public FilesAdapter(List<MyFile> myFiles, OnItemClickListener listener) {
        this.listener = listener;
        this.myFiles = myFiles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(myFiles.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return myFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private OnItemClickListener listener;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.file_item_name);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }
}

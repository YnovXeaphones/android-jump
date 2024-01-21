package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Data.Score;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<Score> scores;
    private OnItemClickListener onItemClickListener;

    public ScoreAdapter(List<Score> scores, OnItemClickListener listener) {
        this.scores = scores;
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onUsernameClicked(String username);
    }

    @Override
    public ScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScoreViewHolder holder, int position) {
        Score score = scores.get(position);

        holder.rankTextView.setText(String.valueOf(position + 2));
        holder.usernameTextView.setText(score.getUsername());
        holder.scoreTextView.setText("Score : " + score.getScore());
        holder.dateTextView.setText(convertLongToDate(score.getDate()));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    private String convertLongToDate(Long time) {
        if (time == null) return "Inconnu";
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(date);
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        TextView usernameTextView;
        TextView scoreTextView;
        TextView dateTextView;

        ScoreViewHolder(View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);

            usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onUsernameClicked(scores.get(position).getUsername());
                    }
                }
            });
        }
    }
}


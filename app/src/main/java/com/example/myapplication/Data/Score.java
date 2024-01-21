package com.example.myapplication.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Score {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "score")
    private int score;

    @ColumnInfo(name = "date")
    private Long date;

    public Score(String username, int score, Long date) {
        this.username = username;
        this.score = score;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public Long getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }
}

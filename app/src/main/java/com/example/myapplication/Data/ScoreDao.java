package com.example.myapplication.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDao {
    @Query("SELECT * FROM score ORDER BY score DESC")
    List<Score> getScores();

    @Query("SELECT * FROM score WHERE username = :username ORDER BY score DESC")
    List<Score> getScoresByUsername(String username);

    @Query("SELECT * FROM score ORDER BY score DESC LIMIT 1")
    Score getHighestScore();

    @Query("SELECT * FROM score WHERE score < (SELECT MAX(score) FROM score) ORDER BY score DESC")
    List<Score> getAllButHighestScore();

    @Insert
    void insertScore(Score score);

    @Delete
    void deleteScore(Score score);
}

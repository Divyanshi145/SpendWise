package com.example.expensetracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> getUserById(int id);

    @Update
    void updateUser(User user);

    @Query("UPDATE users SET dailyBudgetLimit = :budget WHERE id = :userId")
    void updateDailyBudget(int userId, double budget);

    @Delete
    void deleteUser(User user);
}

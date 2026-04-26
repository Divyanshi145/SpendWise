package com.example.expensetracker.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.concurrent.Future;

public class UserRepository {
    private UserDao mUserDao;

    public UserRepository(Application application) {
        ExpenseDatabase db = ExpenseDatabase.getDatabase(application);
        mUserDao = db.userDao();
    }

    public void insert(User user) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> mUserDao.insertUser(user));
    }

    public User getUserByEmail(String email) {
        try {
            Future<User> future = ExpenseDatabase.databaseWriteExecutor.submit(() -> mUserDao.getUserByEmail(email));
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }

    public LiveData<User> getUserById(int id) {
        return mUserDao.getUserById(id);
    }

    public void update(User user) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> mUserDao.updateUser(user));
    }

    public void updateDailyBudget(int userId, double budget) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> mUserDao.updateDailyBudget(userId, budget));
    }
}

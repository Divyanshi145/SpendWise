package com.example.expensetracker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.expensetracker.data.User;
import com.example.expensetracker.data.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public void insert(User user) { mRepository.insert(user); }
    public User getUserByEmail(String email) { return mRepository.getUserByEmail(email); }
    public LiveData<User> getUserById(int id) { return mRepository.getUserById(id); }
    public void update(User user) { mRepository.update(user); }
    public void updateDailyBudget(int userId, double budget) { mRepository.updateDailyBudget(userId, budget); }
}

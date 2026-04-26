package com.example.expensetracker.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Expense.class, User.class}, version = 2, exportSchema = false)
public abstract class ExpenseDatabase extends RoomDatabase {
    public abstract ExpenseDao expenseDao();
    public abstract UserDao userDao();

    private static volatile ExpenseDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create users table
            database.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fullName` TEXT, `email` TEXT, `passwordHash` TEXT, `currency` TEXT, `dailyBudgetLimit` REAL NOT NULL, `createdAt` TEXT, `profileInitials` TEXT, `avatarColor` TEXT)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_email` ON `users` (`email`) ");
            
            // Add userId to expense table
            database.execSQL("ALTER TABLE `expense` ADD COLUMN `userId` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_expense_userId` ON `expense` (`userId`) ");
        }
    };

    public static ExpenseDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ExpenseDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ExpenseDatabase.class, "spendwise_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

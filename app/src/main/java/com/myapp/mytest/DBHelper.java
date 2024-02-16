package com.myapp.mytest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Reportdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Reportdetails(id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT,category TEXT, account TEXT, money FLOAT, note TEXT, date TEXT, icon INTEGER)");
        DB.execSQL("create Table Overalldetails(id INTEGER PRIMARY KEY, totalExpense FLOAT DEFAULT 0.00, totalIncome FLOAT DEFAULT 0.00, totalBalance FLOAT DEFAULT 0.00)");
        DB.execSQL("create Table Initialdetails(id INTEGER PRIMARY KEY, creditInitial FLOAT DEFAULT 0.00, cashInitial FLOAT DEFAULT 0.00, savingsInitial FLOAT DEFAULT 0.00)");

        DB.execSQL("create Table ExpensesDetails(id INTEGER PRIMARY KEY, Food FLOAT DEFAULT 0.00, Beauty FLOAT DEFAULT 0.00, Clothing FLOAT DEFAULT 0.00, Electricity FLOAT DEFAULT 0.00, Transportation FLOAT DEFAULT 0.00)");
        DB.execSQL("create Table IncomeDetails(id INTEGER PRIMARY KEY, Salary FLOAT DEFAULT 0.00, Coupons FLOAT DEFAULT 0.00, Awards FLOAT DEFAULT 0.00)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int ii) {
        DB.execSQL("drop Table if exists Reportdetails");
        DB.execSQL("drop Table if exists Overalldetails");
        DB.execSQL("drop Table if exists Initialdetails");

        DB.execSQL("drop Table if exists ExpensesDetails");
        DB.execSQL("drop Table if exists IncomeDetails");
    }

    public void updateExpensesData(String columnName, float newValue){
        SQLiteDatabase DB = this.getWritableDatabase();

        if (isInitialEmpty(columnName,"ExpensesDetails")) {
            // Perform insert since the column is empty
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, newValue);
            DB.insert("ExpensesDetails", null, contentValues);
        } else {
            // Perform update since the column is not empty
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, newValue);
            DB.update("ExpensesDetails", contentValues, "id=?", new String[]{"1"});
        }
    }

    public void updateIncomeData(String columnName, float newValue){
        SQLiteDatabase DB = this.getWritableDatabase();

        if (isInitialEmpty(columnName,"IncomeDetails")) {
            // Perform insert since the column is empty
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, newValue);
            DB.insert("IncomeDetails", null, contentValues);
        } else {
            // Perform update since the column is not empty
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, newValue);
            DB.update("IncomeDetails", contentValues, "id=?", new String[]{"1"});
        }
    }

    public float getExpenseValue(String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + columnName + " FROM ExpensesDetails LIMIT 1", null);

        float value = 0.00f;

        if (cursor.moveToFirst())
            value = cursor.getFloat(0);

        cursor.close();
        return value;
    }

    public float getIncomeValue(String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + columnName + " FROM IncomeDetails LIMIT 1", null);

        float value = 0.00f;

        if (cursor.moveToFirst())
            value = cursor.getFloat(0);

        cursor.close();
        return value;
    }

    public void updateOverallData(String columnName, float newValue){
        SQLiteDatabase DB = this.getWritableDatabase();

        if (isInitialEmpty(columnName,"Overalldetails")) {
            // Perform insert since the column is empty
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, newValue);
            DB.insert("Overalldetails", null, contentValues);
        } else {
            // Perform update since the column is not empty
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, newValue);
            DB.update("Overalldetails", contentValues, "id=?", new String[]{"1"});
        }
    }

    public float getOverallValue(String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + columnName + " FROM Overalldetails LIMIT 1", null);

        float initialValue = 0.00f;

        if (cursor.moveToFirst())
            initialValue = cursor.getFloat(0);

        cursor.close();
        return initialValue;
    }

    public boolean updateInitialValue(String payment, float newValue){
        SQLiteDatabase DB = this.getWritableDatabase();

        String columnName = getCategoryColumnName(payment);

        if (isInitialEmpty(columnName, "Initialdetails")) {
            // Perform insert since the column is empty
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, newValue);
            long rowId = DB.insert("Initialdetails", null, contentValues);
            return rowId != -1;
        } else {
            // Perform update since the column is not empty
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, newValue);
            int rowsAffected = DB.update("Initialdetails", contentValues, "id=?", new String[]{"1"});
            return rowsAffected > 0;
        }
    }

    public float getInitialValue(String payment) {
        SQLiteDatabase db = this.getReadableDatabase();
        String columnName = getCategoryColumnName(payment);
        Cursor cursor = db.rawQuery("SELECT " + columnName + " FROM Initialdetails LIMIT 1", null);

        float initialValue = 0.00f;

        if (cursor.moveToFirst())
            initialValue = cursor.getFloat(0);

        cursor.close();
        return initialValue;
    }

    public boolean isInitialEmpty(String columnName, String tableName) {
        SQLiteDatabase DB = this.getReadableDatabase();
        try (Cursor cursor = DB.rawQuery("SELECT " + columnName + " FROM " + tableName + " WHERE id = 1", null)) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(columnName);// Check if the column is NULL or empty string
                return cursor.isNull(columnIndex) || cursor.getString(columnIndex).isEmpty();
            } else {
                return true; // Row with id = 1 not found, consider it as empty
            }
        }
    }

    private String getCategoryColumnName(String category) {
        switch (category) {
            case "Credit":
                return "creditInitial";
            case "Cash":
                return "cashInitial";
            case "Savings":
                return "savingsInitial";
            default:
                return null;
        }
    }

    public boolean insertReportData(String type, String category, String account, float money, String note, String date, int icon) {
        SQLiteDatabase DB = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("type",type);
            contentValues.put("category",category);
            contentValues.put("account",account);
            contentValues.put("money",money);
            contentValues.put("note",note);
            contentValues.put("date",date);
            contentValues.put("icon",icon);

            long result = DB.insert("Reportdetails",null,contentValues);
            return result != -1;
        } finally {
            closeDatabase();
        }
    }

    public boolean deleteReportData(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("Reportdetails", "id=?", new String[]{String.valueOf(itemId)});
        return rowsAffected > 0;
    }

    public List<ReportItem> getDataList() {
        SQLiteDatabase DB = this.getWritableDatabase();
        List<ReportItem> reportItems = new ArrayList<>();

        Cursor cursor = DB.rawQuery("Select * from Reportdetails", null);

        if (cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndex("id");
                int categoryColumnIndex = cursor.getColumnIndex("category");
                int typeColumnIndex = cursor.getColumnIndex("type");
                int accountColumnIndex = cursor.getColumnIndex("account");
                int moneyColumnIndex = cursor.getColumnIndex("money");
                int noteColumnIndex = cursor.getColumnIndex("note");
                int dateColumnIndex = cursor.getColumnIndex("date");
                int iconColumnIndex = cursor.getColumnIndex("icon");

                int id = idColumnIndex >= 0 ? cursor.getInt(idColumnIndex) : -1;
                String category = categoryColumnIndex >= 0 ? cursor.getString(categoryColumnIndex) : "";
                String type = typeColumnIndex >= 0 ? cursor.getString(typeColumnIndex) : "";
                String account = accountColumnIndex >= 0 ? cursor.getString(accountColumnIndex) : "";
                String money = moneyColumnIndex >= 0 ? cursor.getString(moneyColumnIndex) : "";
                String note = noteColumnIndex >= 0 ? cursor.getString(noteColumnIndex) : "";
                String date = dateColumnIndex >= 0 ? cursor.getString(dateColumnIndex) : "";
                int icon = iconColumnIndex >= 0 ? cursor.getInt(iconColumnIndex) : -1;

                ReportItem reportItem = new ReportItem(id, type, category, account, money, note, date, icon);
                reportItems.add(reportItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return reportItems;
    }

    public void closeDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}

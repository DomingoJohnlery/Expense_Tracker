package com.myapp.mytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import com.myapp.mytest.databinding.ActivityAddBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddActivity extends AppCompatActivity {
    ActivityAddBinding binding;
    DBHelper DB;
    String type = "Expense";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            if (checkedRadioButton != null){
                type = checkedRadioButton.getText().toString();
                binding.btnAccount.setText("Accounts");
                binding.btnCategory.setText("Categories");
            }

        });

        binding.btnCancel.setOnClickListener(view -> finish());
        binding.btnAccount.setOnClickListener(view -> showDialogAC(R.layout.bottom_sheet_accounts,ViewGroup.LayoutParams.WRAP_CONTENT));

        binding.btnCategory.setOnClickListener(view -> {
            int categoryLayout = R.layout.bottom_sheet_category;
            int categoryLayoutParams = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (type.equals("Income"))
                categoryLayout = R.layout.income_sheet_category;

            showDialogAC(categoryLayout,categoryLayoutParams);
        });

        DB = new DBHelper(this);

        binding.btnSave.setOnClickListener(view -> {
            String categoryText = binding.btnCategory.getText().toString();
            String accountText = binding.btnAccount.getText().toString();
            String moneyString = binding.singleLine.getText().toString();
            Log.d("Money String: ",moneyString);


            if (accountText.equals("Accounts") || categoryText.equals("Categories")) {
                Toast.makeText(this, "Select Account or Category", Toast.LENGTH_SHORT).show();
            } else if(moneyString.equals("")) {
                Toast.makeText(this, "Enter Value", Toast.LENGTH_SHORT).show();
            } else {
                float moneyValue = Float.parseFloat(binding.singleLine.getText().toString());
                String noteText = binding.multiLine.getText().toString();

                int iconId = R.drawable.baseline_list_alt_24;
                switch (categoryText) {
                    case "Food":
                        iconId = R.drawable.icon_food;
                        break;
                    case "Beauty":
                        iconId = R.drawable.icon_beauty;
                        break;
                    case "Clothing":
                        iconId = R.drawable.icon_clothing;
                        break;
                    case "Electricity":
                        iconId = R.drawable.icon_electricity;
                        break;
                    case "Transportation":
                        iconId = R.drawable.icon_transportation;
                        break;
                    case "Salary":
                        iconId = R.drawable.icon_wallet;
                        break;
                    case "Coupons":
                        iconId = R.drawable.icon_coupons;
                        break;
                    case "Awards":
                        iconId = R.drawable.icon_awards;
                        break;
                }

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault());
                String dateText = dateFormat.format(calendar.getTime());

                boolean checkInsertData = DB.insertReportData(type,categoryText,accountText,moneyValue,noteText,dateText,iconId);

                float account = DB.getInitialValue(accountText);
                if (type.equals("Expense")) {
                    float newAccVal = account - moneyValue;
                    DB.updateInitialValue(accountText,newAccVal);
                } else if (type.equals("Income")) {
                    float newAccVal = account + moneyValue;
                    DB.updateInitialValue(accountText,newAccVal);
                }

                if (type.equals("Expense")) {
                    float categoryExpense = DB.getExpenseValue(categoryText);
                    float newExpenseValue = categoryExpense + moneyValue;
                    DB.updateExpensesData(categoryText,newExpenseValue);
                } else if (type.equals("Income")) {
                    float categoryIncome = DB.getIncomeValue(categoryText);
                    float newIncomeValue = categoryIncome + moneyValue;
                    DB.updateIncomeData(categoryText,newIncomeValue);
                }

                //Updates on overall expenses and income
                if (Objects.equals(type, "Expense")) {
                    float totalExpense = DB.getOverallValue("totalExpense");
                    float newValue = totalExpense + moneyValue;
                    DB.updateOverallData("totalExpense",newValue);
                } else if (Objects.equals(type, "Income")) {
                    float totalIncome = DB.getOverallValue("totalIncome");
                    float newValue = totalIncome + moneyValue;
                    DB.updateOverallData("totalIncome",newValue);
                }

                if (checkInsertData) {
                    sendRefreshBroadcast();
                } else {
                    Log.d("SendRefreshBroadcast: ", "New Entry Not Inserted");
                }
                finish();
            }

        });
    }

    private void sendRefreshBroadcast() {
        Intent intent = new Intent(reportsFragment.ACTION_REFRESH_DATA);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @SuppressLint("SetTextI18n")
    private void showDialogAC(int layout, int height) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);

        //Accounts
        LinearLayout creditLayout = dialog.findViewById(R.id.layoutCredit);
        LinearLayout cashLayout = dialog.findViewById(R.id.layoutCash);
        LinearLayout savingsLayout = dialog.findViewById(R.id.layoutSavings);

        if (layout == R.layout.bottom_sheet_accounts) {
            creditLayout.setOnClickListener(credit -> {
                binding.btnAccount.setText("Credit");
                dialog.dismiss();
            });
            cashLayout.setOnClickListener(cash -> {
                binding.btnAccount.setText("Cash");
                dialog.dismiss();
            });
            savingsLayout.setOnClickListener(savings -> {
                binding.btnAccount.setText("Savings");
                dialog.dismiss();
            });

        } else if (layout == R.layout.bottom_sheet_category) {
            LinearLayout foodLayout = dialog.findViewById(R.id.layoutFood);
            LinearLayout beautyLayout = dialog.findViewById(R.id.layoutBeauty);
            LinearLayout clothingLayout = dialog.findViewById(R.id.layoutClothing);
            LinearLayout electricLayout = dialog.findViewById(R.id.layoutElectricity);
            LinearLayout transportLayout = dialog.findViewById(R.id.layoutTransport);

            foodLayout.setOnClickListener(food -> {
                binding.btnCategory.setText("Food");
                dialog.dismiss();
            });
            beautyLayout.setOnClickListener(beauty -> {
                binding.btnCategory.setText("Beauty");
                dialog.dismiss();
            });
            clothingLayout.setOnClickListener(cloth -> {
                binding.btnCategory.setText("Clothing");
                dialog.dismiss();
            });
            electricLayout.setOnClickListener(electric-> {
                binding.btnCategory.setText("Electricity");
                dialog.dismiss();
            });
            transportLayout.setOnClickListener(trans -> {
                binding.btnCategory.setText("Transportation");
                dialog.dismiss();
            });
        } else if (layout == R.layout.income_sheet_category) {
            LinearLayout salaryLayout = dialog.findViewById(R.id.layoutSalary);
            LinearLayout couponsLayout = dialog.findViewById(R.id.layoutCoupons);
            LinearLayout awardsLayout = dialog.findViewById(R.id.layoutAwards);

            salaryLayout.setOnClickListener(view -> {
                binding.btnCategory.setText("Salary");
                dialog.dismiss();
            });
            couponsLayout.setOnClickListener(view -> {
                binding.btnCategory.setText("Coupons");
                dialog.dismiss();
            });
            awardsLayout.setOnClickListener(view -> {
                binding.btnCategory.setText("Awards");
                dialog.dismiss();
            });
        }

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
package com.myapp.mytest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class analyticsFragment2 extends Fragment {

    PieChart pieChart;
    Button btnSwitchFrag;
    TextView tvSalaryData, tvCouponsData, tvAwardsData;
    DBHelper DB;


    public analyticsFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_analytics_income, container, false);

        DB = new DBHelper(getActivity());

        pieChart = rootView.findViewById(R.id.expensePiechart);
        tvSalaryData = rootView.findViewById(R.id.tvSalaryData);
        tvCouponsData = rootView.findViewById(R.id.tvCouponsData);
        tvAwardsData = rootView.findViewById(R.id.tvAwardsData);

        btnSwitchFrag = rootView.findViewById(R.id.btnSwitchFrag);
        btnSwitchFrag.setOnClickListener(this::showPopupMenu);

        setData();

        return rootView;
    }

    @SuppressLint("DefaultLocale")
    public void setData() {
        float salaryIncome = DB.getIncomeValue("Salary");
        float couponsIncome = DB.getIncomeValue("Coupons");
        float awardsIncome = DB.getIncomeValue("Awards");

        float salaryPercentage = getPercentage(salaryIncome);
        float couponsPercentage = getPercentage(couponsIncome);
        float awardsPercentage = getPercentage(awardsIncome);

        tvSalaryData.setText(String.format("%.2f%%",salaryPercentage));
        tvCouponsData.setText(String.format("%.2f%%",couponsPercentage));
        tvAwardsData.setText(String.format("%.2f%%",awardsPercentage));

        pieChart.addPieSlice(new PieModel("Salary",salaryPercentage, Color.parseColor("#FF0000")));
        pieChart.addPieSlice(new PieModel("Coupons",couponsPercentage, Color.parseColor("#00FF00")));
        pieChart.addPieSlice(new PieModel("Awards",awardsPercentage, Color.parseColor("#0000FF")));
        pieChart.addPieSlice(new PieModel("Default",0, Color.parseColor("#808080")));

        pieChart.startAnimation();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.dropdown_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_expense) {
                replaceFragment(new analyticsFragment());
                return true;
            } else return itemId == R.id.menu_income;
        });

        popupMenu.show();
    }

    public float getPercentage(float expenseType) {
        float totalExpense = DB.getOverallValue("totalIncome");

        return expenseType/totalExpense*100;
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}
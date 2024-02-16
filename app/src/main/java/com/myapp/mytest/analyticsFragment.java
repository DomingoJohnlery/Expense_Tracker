package com.myapp.mytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
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

public class analyticsFragment extends Fragment {
    FabVisibilityListener fabVisibilityListener;
    PieChart pieChart;
    Button btnSwitchFrag;
    TextView tvFoodData, tvBeautyData, tvClothData, tvElectricData, tvTransportData;
    DBHelper DB;

    public analyticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FabVisibilityListener) {
            fabVisibilityListener = (FabVisibilityListener) context;
        } else {
            throw new ClassCastException(context + " must implement FabVisibilityListener");
        }
        fabVisibilityListener.setFabVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_analytics, container, false);

        DB = new DBHelper(getActivity());
        pieChart = rootView.findViewById(R.id.expensePiechart);
        tvFoodData = rootView.findViewById(R.id.tvFoodData);
        tvBeautyData = rootView.findViewById(R.id.tvBeautyData);
        tvClothData = rootView.findViewById(R.id.tvClothingData);
        tvElectricData = rootView.findViewById(R.id.tvElectricData);
        tvTransportData = rootView.findViewById(R.id.tvTransportData);

        btnSwitchFrag = rootView.findViewById(R.id.btnSwitchFrag);
        btnSwitchFrag.setOnClickListener(this::showPopupMenu);

        setData();

        return rootView;
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.dropdown_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_expense) {
                return true;
            } else if (itemId == R.id.menu_income) {
                replaceFragment(new analyticsFragment2());
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
    @SuppressLint("DefaultLocale")
    public void setData() {
        float foodExpense = DB.getExpenseValue("Food");
        float beautyExpense = DB.getExpenseValue("Beauty");
        float clothingExpense = DB.getExpenseValue("Clothing");
        float ElectricityExpense = DB.getExpenseValue("Electricity");
        float TransportationExpense = DB.getExpenseValue("Transportation");

        float foodPercentage = getPercentage(foodExpense);
        float beautyPercentage = getPercentage(beautyExpense);
        float clothingPercentage = getPercentage(clothingExpense);
        float electricityPercentage = getPercentage(ElectricityExpense);
        float transportationPercentage = getPercentage(TransportationExpense);

        tvFoodData.setText(String.format("%.2f%%",foodPercentage));
        tvBeautyData.setText(String.format("%.2f%%",beautyPercentage));
        tvClothData.setText(String.format("%.2f%%",clothingPercentage));
        tvElectricData.setText(String.format("%.2f%%",electricityPercentage));
        tvTransportData.setText(String.format("%.2f%%",transportationPercentage));

        pieChart.addPieSlice(new PieModel("Food",foodPercentage, Color.parseColor("#00FF00")));
        pieChart.addPieSlice(new PieModel("Beauty",beautyPercentage, Color.parseColor("#FF0000")));
        pieChart.addPieSlice(new PieModel("Clothing",clothingPercentage, Color.parseColor("#FF8C00")));
        pieChart.addPieSlice(new PieModel("Electricity",electricityPercentage, Color.parseColor("#FFFF00")));
        pieChart.addPieSlice(new PieModel("Transportation",transportationPercentage, Color.parseColor("#0000FF")));
        pieChart.addPieSlice(new PieModel("Default",0, Color.parseColor("#808080")));

        pieChart.startAnimation();
    }
    public float getPercentage(float expenseType) {
        float totalExpense = DB.getOverallValue("totalExpense");

        return expenseType/totalExpense*100;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}
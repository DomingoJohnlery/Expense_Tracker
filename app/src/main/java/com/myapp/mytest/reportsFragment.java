package com.myapp.mytest;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class reportsFragment extends Fragment implements RecyclerViewInterface {
    public static final String ACTION_REFRESH_DATA = "com.myapp.mytest.ACTION_REFRESH_DATA";
    FabVisibilityListener fabVisibilityListener;
    RecyclerView recyclerView;
    ArrayList<ReportItem> reportItems;
    DBHelper DB;
    Dialog dialog;
    public ReportAdapter adapter;

    public reportsFragment() {
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
        fabVisibilityListener.setFabVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(ACTION_REFRESH_DATA);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(refreshReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);
        dialog = new Dialog(requireActivity());
        DB = new DBHelper(getActivity());
        reportItems = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        adapter = new ReportAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        displayData();
        return rootView;
    }

    private void displayData() {
        List<ReportItem> reportItems = DB.getDataList();
        adapter.submitList(reportItems);
    }

    private final BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_REFRESH_DATA)) {
                displayData();
            }
        }
    };

    @Override
    public void onItemClick(int position) {
        ReportItem clickedItem = adapter.getCurrentList().get(position);

        String type = clickedItem.getType();
        String money = clickedItem.getMoney();
        String accountType = clickedItem.getAccount();
        String categoryType = clickedItem.getCategory();
        String note = clickedItem.getNote();
        String date = clickedItem.getDate();

        dialog.setContentView(R.layout.dialog_window);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopAnimation;

        ImageButton btnCancel = dialog.findViewById(R.id.btnCancel);
        ImageButton btnDelete = dialog.findViewById(R.id.btnDelete);
        TextView tvType = dialog.findViewById(R.id.tvType);
        TextView tvMoney = dialog.findViewById(R.id.tvMoney);
        TextView tvAccount = dialog.findViewById(R.id.tvAccountType);
        TextView tvCategory = dialog.findViewById(R.id.tvCategoryType);
        TextView tvNote = dialog.findViewById(R.id.tvNote);
        TextView tvDate = dialog.findViewById(R.id.tvDate);

        tvType.setText(type);
        tvMoney.setText("₱".concat(money));
        tvAccount.setText(accountType);
        tvCategory.setText(categoryType);
        tvNote.setText(note);
        tvDate.setText(date);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnDelete.setOnClickListener(view -> {
            if (!adapter.getCurrentList().isEmpty()) {
                int itemIdToDelete = clickedItem.getId();
                boolean isDeleted = DB.deleteReportData(itemIdToDelete);
                if (isDeleted) adapter.removeItemById(itemIdToDelete);

                float moneyValue = Float.parseFloat(money);

                float account = DB.getInitialValue(accountType);
                if (type.equals("Expense")) {
                    float newAccVal = account + moneyValue;
                    DB.updateInitialValue(accountType,newAccVal);
                } else if (type.equals("Income")) {
                    float newAccVal = account - moneyValue;
                    DB.updateInitialValue(accountType,newAccVal);
                }

                if (type.equals("Expense")) {
                    float categoryExpense = DB.getExpenseValue(categoryType);
                    float newExpenseValue = categoryExpense - moneyValue;
                    DB.updateExpensesData(categoryType,newExpenseValue);
                } else if (type.equals("Income")) {
                    float categoryIncome = DB.getIncomeValue(categoryType);
                    float newIncomeValue = categoryIncome - moneyValue;
                    DB.updateIncomeData(categoryType,newIncomeValue);
                }

                //Updates on overall expenses and income
                if (Objects.equals(type, "Expense")) {
                    float totalExpense = DB.getOverallValue("totalExpense");
                    float newValue = totalExpense - moneyValue;
                    DB.updateOverallData("totalExpense",newValue);
                } else if (Objects.equals(type, "Income")) {
                    float totalIncome = DB.getOverallValue("totalIncome");
                    float newValue = totalIncome - moneyValue;
                    DB.updateOverallData("totalIncome",newValue);
                }

                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
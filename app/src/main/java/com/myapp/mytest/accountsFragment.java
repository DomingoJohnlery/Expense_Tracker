package com.myapp.mytest;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.myapp.mytest.databinding.FragmentAccountsBinding;
public class accountsFragment extends Fragment {
    FabVisibilityListener fabVisibilityListener;
    FragmentAccountsBinding binding;
    DBHelper DB;
    Dialog dialog;

    public accountsFragment() {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountsBinding.inflate(inflater,container,false);
        dialog = new Dialog(requireActivity());
        DB = new DBHelper(getActivity());

        binding.creditPopup.setOnClickListener(view -> openDialog("Credit"));
        binding.cashPopup.setOnClickListener(view -> openDialog("Cash"));
        binding.savingsPopup.setOnClickListener(view -> openDialog("Savings"));

        displayData();

        return binding.getRoot();
    }

    public void openDialog(String payment) {
        dialog.setContentView(R.layout.dialog_edit_account);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopAnimation2;

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        TextView tvPayment = dialog.findViewById(R.id.tvPaymentName);
        EditText etInitial = dialog.findViewById(R.id.initialForm);

        tvPayment.setText(payment);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnSave.setOnClickListener(view -> {
            String initial = etInitial.getText().toString();
            if (!initial.equals("")) {
                float amount = Float.parseFloat(initial);
                boolean updated = DB.updateInitialValue(payment,amount);
                if (updated) {
                    displayData();
                } else {
                    Toast.makeText(getActivity(), "Data Initial Not Updated", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(getActivity(), "Empty Initial Account", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    private void displayData() {
        float credit = DB.getInitialValue("Credit");
        float cash = DB.getInitialValue("Cash");
        float savings = DB.getInitialValue("Savings");

        binding.amountCredit.setText(String.format("₱%.2f",credit));
        binding.amountCash.setText(String.format("₱%.2f",cash));
        binding.amountSavings.setText(String.format("₱%.2f",savings));

        calculateTotalBalance(credit,cash,savings);

        float totalExpense = DB.getOverallValue("totalExpense");
        float totalIncome = DB.getOverallValue("totalIncome");
        float totalBalance = DB.getOverallValue("totalBalance");

        binding.totalExpense.setText(String.format("-₱%.2f",totalExpense));
        binding.totalIncome.setText(String.format("₱%.2f",totalIncome));
        binding.totalBalance.setText(String.format("₱%.2f",totalBalance));
    }

    private void calculateTotalBalance(float credit, float cash, float savings) {
        float totalBalance = credit + cash + savings;
        DB.updateOverallData("totalBalance",totalBalance);
    }
}
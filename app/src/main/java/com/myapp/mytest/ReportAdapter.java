package com.myapp.mytest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends ListAdapter<ReportItem, ReportAdapter.ViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private static final int VIEW_TYPE_EXPENSE = 1;
    private static final int VIEW_TYPE_INCOME = 2;
    public ReportAdapter(RecyclerViewInterface recyclerViewInterface) {
        super(new ReportItemDiffCallback());
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    public int getItemViewType(int position) {
        ReportItem currentItem = getItem(position);
        return currentItem.getType().equals("Expense") ? VIEW_TYPE_EXPENSE : VIEW_TYPE_INCOME;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportItem currentItem = getItem(position);
        holder.category_id.setText(currentItem.getCategory());
        holder.account_id.setText(currentItem.getAccount());

        if (getItemViewType(position) == VIEW_TYPE_EXPENSE) {
            holder.money_id.setText("-".concat(currentItem.getMoney()));
            holder.money_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.expenseColor));
        } else {
            holder.money_id.setText("+".concat(currentItem.getMoney()));
            holder.money_id.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.incomeColor));
        }

        holder.icon_id.setImageResource(currentItem.getIcon());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category_id, account_id, money_id;
        ImageView icon_id;

        public ViewHolder(View view, RecyclerViewInterface recyclerViewInterface) {
            super(view);
            category_id = view.findViewById(R.id.categoryText);
            account_id = view.findViewById(R.id.accountText);
            money_id = view.findViewById(R.id.moneyText);
            icon_id = view.findViewById(R.id.categoryIcon);

            view.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });
        }
    }

    public void removeItemById(int itemId) {
        List<ReportItem> currentList = new ArrayList<>(getCurrentList());
        for (ReportItem item : currentList) {
            if (item.getId() == itemId) {
                currentList.remove(item);
                submitList(currentList);
                break;
            }
        }
    }

    private static class ReportItemDiffCallback extends DiffUtil.ItemCallback<ReportItem> {
        @Override
        public boolean areItemsTheSame(@NonNull ReportItem oldItem, @NonNull ReportItem newItem) {
            return oldItem.getId() == newItem.getId(); // Assuming you have an ID field in ReportItem
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReportItem oldItem, @NonNull ReportItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}

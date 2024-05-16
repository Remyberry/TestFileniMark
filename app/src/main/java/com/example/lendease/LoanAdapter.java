package com.example.lendease;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {

    private final List<Loan> loanList;

    public LoanAdapter(List<Loan> loanList) {
        this.loanList = loanList;
    }

    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout for each loan
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);
        return new LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoanViewHolder holder, int position) {
        // Get the loan object for the current position
        Loan loan = loanList.get(position);

        // Set the data to the views in the item layout
        holder.borrowerNameTextView.setText(loan.getBorrowerName());
        holder.dueDateTextView.setText(loan.getDueDate());
        holder.amountTextView.setText(String.format(Locale.US,"%.2f", loan.getAmount())); // Format amount with 2 decimal places
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }

    public static class LoanViewHolder extends RecyclerView.ViewHolder {

        TextView borrowerNameTextView;
        TextView dueDateTextView;
        TextView amountTextView;

        public LoanViewHolder(View itemView) {
            super(itemView);

            borrowerNameTextView = itemView.findViewById(R.id.borrowerName); // Replace with your TextView IDs from recycler_view_item.xml
            dueDateTextView = itemView.findViewById(R.id.due_date_text_view);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
        }
    }
}

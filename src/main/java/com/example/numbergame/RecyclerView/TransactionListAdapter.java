package com.example.numbergame.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numbergame.R;

import org.w3c.dom.Text;

import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionListItemViewHolder> {
    private final List<TransactionListItem> mDataList;

    public TransactionListAdapter(List<TransactionListItem> list) {
        mDataList = list;
    }

    @NonNull
    @Override
    public TransactionListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item, parent);
        return new TransactionListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListItemViewHolder holder, int position) {
//        holder.mBetAmt.setText(mDataList.get(position).getBet());
//        holder.mWonAmt.setText(mDataList.get(position).getWon());
        holder.mRemGrants.setText(mDataList.get(position).getRemGrants());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class TransactionListItemViewHolder extends RecyclerView.ViewHolder {
        final TextView mBetAmt;
        final TextView mWonAmt;
        final TextView mRemGrants;
        public TransactionListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mBetAmt = itemView.findViewById(R.id.bet_amt);
            mWonAmt = itemView.findViewById(R.id.win_amt);
            mRemGrants = itemView.findViewById(R.id.rem_add);
        }
    }

}
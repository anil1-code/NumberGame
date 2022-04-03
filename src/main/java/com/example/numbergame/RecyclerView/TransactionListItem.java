package com.example.numbergame.RecyclerView;

public class TransactionListItem {
    private final int mRemGrants;
    private final double bet;
    private final double won;

    public TransactionListItem(int mRemGrants, double bet, double won) {
        this.mRemGrants = mRemGrants;
        this.bet = bet;
        this.won = won;
    }

    public int getRemGrants() {
        return mRemGrants;
    }

    public double getBet() {
        return bet;
    }

    public double getWon() {
        return won;
    }
}

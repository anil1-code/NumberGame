package com.example.numbergame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.numbergame.RecyclerView.TransactionListItem;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private final int mNumberOfTurns = 15;
    private int mRemTurns = mNumberOfTurns;

    private final int mNumberOfWishes = 3;
    private int mRemWishes = 3;


    private double mCurrentScore = 0;

    private final double mMaxBet = 100.34;

    private final int[] mNumIds = {R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.zero, R.id.decimal};
    private final List<TransactionListItem> transactionListItems = new ArrayList<>();

    private TextView mDropAmt;
    private TextView mDropAmtHint;
    private TextView mRemTurnsTV;
    private TextView mScoreTV;
    private MaterialButton mDropBtn;
    private LinearLayout mTransactionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mDropAmt = findViewById(R.id.drop_amount);
        for(int id : mNumIds) {
            View v = findViewById(id);
            v.setOnClickListener(this);
        }
        View clrBtn = findViewById(R.id.clear);
        clrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int l = mDropAmt.getText().length();
                if(l < 2) {
                    mDropAmt.setText("");
                } else {
                    mDropAmt.setText(mDropAmt.getText().subSequence(0, l - 1));
                }
            }
        });
        mDropAmtHint = findViewById(R.id.drop_amount_hints);
        mDropBtn = findViewById(R.id.drop_btn);
        mTransactionLayout = findViewById(R.id.transactions_layout);
        mRemTurnsTV = findViewById(R.id.rem_turns);
        mScoreTV = findViewById(R.id.score_text);
        fillDp();
        mDropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    double num = Double.parseDouble(mDropAmt.getText().toString());
                    if(num <= 0 || num > mMaxBet) {
                        throw new NumberFormatException();
                    }
                    boolean p = play(num);
                    View v = LayoutInflater.from(PlayActivity.this).inflate(R.layout.transaction_list_item, mTransactionLayout, false);
                    TextView btv = v.findViewById(R.id.bet_amt);
                    TextView wtv = v.findViewById(R.id.win_amt);
                    TextView rwtv = v.findViewById(R.id.rem_add);
                    btv.setText("-" + num);
                    mCurrentScore -= num;
                    if(p) {
                        wtv.setText("+" + (2 * num));
                        mCurrentScore += 2 * num;
                        mRemWishes--;
                    } else {
                        wtv.setText("+0");
                    }
                    rwtv.setText(mRemWishes + "");
                    mTransactionLayout.addView(v);

                    mDropAmt.setText("");
                    mRemTurns--;
                    mRemTurnsTV.setText(mRemTurns + " turns");
                    mScoreTV.setText(mCurrentScore+"");
//                    String w = mCurrentScore + "";
//                    w.substring()
//                    if(w.length() > 4)
                } catch (NumberFormatException e) {
                    return;
                }
            }
        });
    }

    private final double[][] dp = new double[mNumberOfTurns + 1][mNumberOfTurns + 1];
    private void fillDp() {
        dp[1][1] = mMaxBet;
        // dp[i][j] is the maximum score if alice plays i turns and bob has to add j times out of that
        for(int i = 2; i <= mNumberOfTurns; i++) {
            for(int j = 1; j < i; j++) {
                // lets say alice chooses x
                // if bob adds x, dp[i][j] = (dp[i - 1][j - 1] + x)
                // else dp[i][j] = (dp[i - 1][j] - x)
                // bob will chose x optimally in such a way that both of above are equal otherwise
                // bob will chose the smaller one
                dp[i][j] = (dp[i - 1][j - 1] + dp[i - 1][j]) / 2;
            }
            dp[i][i] = mMaxBet * i;
        }
        for(int i = mNumberOfTurns - 1; i >= 0; i--) {
            for(int j = i + 1; j <= mNumberOfTurns; j++) {
                dp[i][j] = 2 * dp[i + 1][j] - dp[i][j - 1];
            }
        }
    }

    // return true if wish granted
    private boolean play(double num) {
        if((mRemWishes == 0) || (mRemTurns > mRemWishes && dp[mRemTurns - 1][mRemWishes] + mCurrentScore - num < dp[mRemTurns - 1][mRemWishes - 1] + mCurrentScore + num)) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        MaterialButton btn = (MaterialButton) view;
        String dropTxt = mDropAmt.getText().toString();
        if(dropTxt.isEmpty() && btn.getText().charAt(0) == '.') {
            // prepend 0
            mDropAmt.append("0.");
            return;
        }
        if(dropTxt.length() > 7) {
            // show invalid
            mDropAmtHint.setAlpha(1);
            mDropAmtHint.setText("Max length exceeded");
            mDropAmtHint.setTextColor(getResources().getColor(R.color.red));
            mDropAmtHint.animate().alpha(0).setDuration(3000).start();
            return;
        }
        dropTxt += btn.getText();
        double ft = Double.parseDouble(dropTxt);
        if(ft > mMaxBet) {
            // show max bet limit
            mDropAmtHint.setAlpha(1);
            mDropAmtHint.setText("Must be <= " + mMaxBet);
            mDropAmtHint.setTextColor(getResources().getColor(R.color.red));
            mDropAmtHint.animate().alpha(0).setDuration(3000).start();
            return;
        }
        mDropAmt.append(btn.getText());
    }
}
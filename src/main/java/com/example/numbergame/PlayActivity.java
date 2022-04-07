package com.example.numbergame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    // we can change these parameters by rebuilding the apk or using remote config
    private final int mNumberOfTurns = 6;
    private int mRemTurns = mNumberOfTurns;
    private final int mNumberOfWishes = 3;
    private int mRemWishes = 3;
    private final double mMaxBet = 10;

    // current score of the user
    private double mCurrentScore = 0;

    // ui elements
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

        // init global ui elements
        initUi();

        // fill dp
        fillDp();

        // ids of num buttons
        final int[] mNumIds = {R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.zero, R.id.decimal};
        // setting click listener to all num Btn
        for (int id : mNumIds) {
            View v = findViewById(id);
            v.setOnClickListener(this);
        }

        // setting click listener to backspace
        View clrBtn = findViewById(R.id.clear);
        clrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int l = mDropAmt.getText().length();
                if (l < 2) {
                    mDropAmt.setText("");
                } else {
                    mDropAmt.setText(mDropAmt.getText().subSequence(0, l - 1));
                }
            }
        });

        // setting drop btn listener
        mDropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if animation is still running then stop it
                if (animation != null && animation.isRunning()) {
                    animation.cancel();
                }
                try {
                    // if not a double then it will land on go into catch block
                    double num = Double.parseDouble(mDropAmt.getText().toString());
                    // out of range
                    if (num < 0 || num > mMaxBet) {
                        throw new NumberFormatException();
                    }
                    // decide whether to grant the wish or not
                    boolean p = play(num);
                    // add transaction item view and inserting data into them
                    View v = LayoutInflater.from(PlayActivity.this).inflate(R.layout.transaction_list_item, mTransactionLayout, false);
                    TextView btv = v.findViewById(R.id.bet_amt);
                    TextView wtv = v.findViewById(R.id.win_amt);
                    TextView rmtv = v.findViewById(R.id.rem_add);
                    btv.setText("-" + num);
                    mCurrentScore -= num;
                    if (p) {
                        wtv.setText("+" + (2 * num));
                        mCurrentScore += 2 * num;
                        mRemWishes--;
                    } else {
                        wtv.setText("+0");
                    }
                    rmtv.setText(mRemWishes + "");
                    mTransactionLayout.addView(v);

                    mDropAmt.setText("");
                    mRemTurns--;
                    mRemTurnsTV.setText(mRemTurns + "");
                    mScoreTV.setText(mCurrentScore + "");

                    // see if game over
                    if (mRemTurns == 0) {
                        Intent intent = new Intent(PlayActivity.this, StartActivity.class);
                        intent.putExtra("played", true);
                        intent.putExtra("score", mCurrentScore);
                        startActivity(intent);
                        finish();
                    }
                } catch (NumberFormatException e) {
                    // show invalid
                    mDropAmtHint.setAlpha(1);
                    mDropAmtHint.setText("Invalid");
                    mDropAmtHint.setTextColor(getResources().getColor(R.color.red));
                    mDropAmtHint.animate().alpha(0).setDuration(3000).start();
                }
            }
        });
    }

    private void initUi() {
        mDropAmt = findViewById(R.id.drop_amount);
        mDropAmtHint = findViewById(R.id.drop_amount_hints);
        mDropBtn = findViewById(R.id.drop_btn);
        mTransactionLayout = findViewById(R.id.transactions_layout);
        mRemTurnsTV = findViewById(R.id.rem_turns);
        mRemTurnsTV.setText(String.valueOf(mRemTurns));
        mScoreTV = findViewById(R.id.score_text);
    }

    private ValueAnimator animation;

    // this function handles the angel appearance animation
    private void startStarAnimation() {
        int[] star_ids = {R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5, R.id.star6, R.id.star7, R.id.star8, R.id.star9};
        // reset everything
        for (int id : star_ids) {
            ImageView imageView = findViewById(id);
            imageView.setScaleX(1);
            imageView.setScaleY(1);
            imageView.setTranslationX(0);
            imageView.setTranslationY(0);
            imageView.setAlpha(1.0f);
        }
        ImageView angelView = findViewById(R.id.angel);
        angelView.setVisibility(View.VISIBLE);
        findViewById(R.id.star_container).setVisibility(View.VISIBLE);

        // using ValueAnimator to animate the stars translation, their alpha and angel's alpha
        int limit = findViewById(R.id.star1).getTop();
        animation = ValueAnimator.ofFloat(0f, limit);
        animation.setDuration(5000);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue = (float) updatedAnimation.getAnimatedValue();
                float fraction = animatedValue / 100;
                for (int id : star_ids) {
                    ImageView imageView = findViewById(id);
                    imageView.setScaleX(1.0f - fraction);
                    imageView.setScaleY(1.0f - fraction);
                    imageView.setAlpha(1.0f - fraction);
                    imageView.setTranslationY(-animatedValue);
                }
                angelView.setAlpha(Math.min(fraction, 1 - fraction) * 2);
            }
        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                angelView.setVisibility(View.INVISIBLE);
                findViewById(R.id.star_container).setVisibility(View.INVISIBLE);
            }
        });
        animation.start();
        // play sound effect
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.wish_granted_sound_effect);
        mp.start();
    }

    private final double[][] dp = new double[mNumberOfTurns + 1][mNumberOfTurns + 1];

    private void fillDp() {
        dp[1][1] = mMaxBet;
        // dp[i][j] is the maximum score if alice plays i turns and bob has to add j times out of that
        for (int i = 2; i <= mNumberOfTurns; i++) {
            for (int j = 1; j < i; j++) {
                // lets say alice chooses x
                // if bob adds x, dp[i][j] = (dp[i - 1][j - 1] + x)
                // else dp[i][j] = (dp[i - 1][j] - x)
                // bob will chose x optimally in such a way that both of above are equal otherwise
                // bob will chose the smaller one
                dp[i][j] = (dp[i - 1][j - 1] + dp[i - 1][j]) / 2;
            }
            dp[i][i] = mMaxBet * i;
        }
        for (int i = mNumberOfTurns - 1; i >= 0; i--) {
            for (int j = i + 1; j <= mNumberOfTurns; j++) {
                dp[i][j] = 2 * dp[i + 1][j] - dp[i][j - 1];
            }
        }
    }

    // return true if wish granted
    private boolean play(double num) {
        if ((mRemWishes == 0) || (mRemTurns > mRemWishes && dp[mRemTurns - 1][mRemWishes] + mCurrentScore - num < dp[mRemTurns - 1][mRemWishes - 1] + mCurrentScore + num)) {
            return false;
        }
        startStarAnimation();
        return true;
    }

    @Override
    public void onClick(View view) {
        MaterialButton btn = (MaterialButton) view;
        String dropTxt = mDropAmt.getText().toString();
        if (dropTxt.isEmpty() && btn.getText().charAt(0) == '.') {
            // prepend 0
            mDropAmt.append("0.");
            return;
        }
        if (dropTxt.length() > 7) {
            // show invalid
            mDropAmtHint.setAlpha(1);
            mDropAmtHint.setText("Max length exceeded");
            mDropAmtHint.setTextColor(getResources().getColor(R.color.red));
            mDropAmtHint.animate().alpha(0).setDuration(3000).start();
            return;
        }
        dropTxt += btn.getText();
        try {
            double ft = Double.parseDouble(dropTxt);
            if (ft > mMaxBet) {
                // show max bet limit
                mDropAmtHint.setAlpha(1);
                mDropAmtHint.setText("Must be <= " + mMaxBet);
                mDropAmtHint.setTextColor(getResources().getColor(R.color.red));
                mDropAmtHint.animate().alpha(0).setDuration(3000).start();
                return;
            }
            mDropAmt.append(btn.getText());
        } catch (NumberFormatException e) {
            mDropAmtHint.setAlpha(1);
            mDropAmtHint.setText("Invalid");
            mDropAmtHint.setTextColor(getResources().getColor(R.color.red));
            mDropAmtHint.animate().alpha(0).setDuration(3000).start();
        }
    }

    // hide navigation and status bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animation != null && animation.isRunning()) {
            animation.cancel();
        }
    }
}
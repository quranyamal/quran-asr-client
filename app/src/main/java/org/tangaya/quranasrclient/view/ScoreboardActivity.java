package org.tangaya.quranasrclient.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.tangaya.quranasrclient.MyApplication;
import org.tangaya.quranasrclient.R;
import org.tangaya.quranasrclient.databinding.ActivityScoreboardBinding;
import org.tangaya.quranasrclient.navigator.ScoreboardNavigator;
import org.tangaya.quranasrclient.viewmodel.ScoreboardViewModel;

public class ScoreboardActivity extends AppCompatActivity implements ScoreboardNavigator {

    ScoreboardViewModel mViewModel;
    ActivityScoreboardBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        mViewModel = new ScoreboardViewModel(getApplication());
        mViewModel.onActivityCreated(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scoreboard);
        mBinding.setViewmodel(mViewModel);
    }

    @Override
    public void showDetail() {
        Intent intent = new Intent(this, EvalDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void retryChapter() {
        Intent intent = new Intent(this, MurojaahActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void nextChapter() {

        SharedPreferences sharedPref = ((MyApplication) getApplication()).getPreferences();
        int chapterNum = sharedPref.getInt("CURRENT_CHAPTER_NUM", -1);

        if (chapterNum==115) {
            exit();
        } else {
            chapterNum++;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("CURRENT_CHAPTER_NUM", chapterNum);
            editor.commit();

            Intent intent = new Intent(this, MurojaahActivity.class);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void selectAnotherChapter() {
        Intent intent = new Intent(this, ChapterSelectionActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void exit() {
        finish();
    }
}
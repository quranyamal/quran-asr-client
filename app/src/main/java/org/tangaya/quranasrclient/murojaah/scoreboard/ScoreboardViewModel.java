package org.tangaya.quranasrclient.murojaah.scoreboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import org.tangaya.quranasrclient.MyApplication;
import org.tangaya.quranasrclient.data.source.QuranScriptRepository;

public class ScoreboardViewModel extends AndroidViewModel {

    ScoreboardNavigation mNavigator;

    public final ObservableField<String> currentChapter = new ObservableField<>();
    public final ObservableField<String> nextChapter = new ObservableField<>();

    public ScoreboardViewModel(@NonNull Application application) {
        super(application);
    }

    public void onActivityCreated(ScoreboardNavigation navigator) {
        mNavigator = navigator;

        SharedPreferences preferences = ((MyApplication) getApplication()).getPreferences();
        int chapterNum = preferences.getInt("CURRENT_CHAPTER_NUM", -1)+1;

        currentChapter.set(QuranScriptRepository.getChapter(chapterNum).getTitle());
        nextChapter.set(QuranScriptRepository.getChapter(chapterNum+1).getTitle());
    }

    public void showDetail() {
        mNavigator.showDetail();
    }

    public void retryChapter() {
        mNavigator.retryChapter();
    }

    public void nextChapter() {
        mNavigator.nextChapter();
    }

    public void selectAnotherChapter() {
        mNavigator.selectAnotherChapter();
    }

    public void exit() {
        mNavigator.exit();
    }

}

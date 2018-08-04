package org.tangaya.quranasrclient.view;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.tangaya.quranasrclient.MyApplication;
import org.tangaya.quranasrclient.ViewModelFactory;
import org.tangaya.quranasrclient.data.EvaluationOld;
import org.tangaya.quranasrclient.data.source.RecordingRepository;
import org.tangaya.quranasrclient.data.source.TranscriptionsRepository;
import org.tangaya.quranasrclient.R;
import org.tangaya.quranasrclient.databinding.ActivityMurojaahBinding;
import org.tangaya.quranasrclient.navigator.MurojaahNavigator;
import org.tangaya.quranasrclient.viewmodel.MurojaahViewModel;

import java.util.ArrayList;

import timber.log.Timber;

public class MurojaahActivity extends Activity implements LifecycleOwner, MurojaahNavigator {

    private int CHAPTER_NUM;

    public MurojaahViewModel mViewModel;
    private ActivityMurojaahBinding mMurojaahDataBinding;
    private LifecycleRegistry mLifecycleRegistry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);

        mMurojaahDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_murojaah);
        mMurojaahDataBinding.setLifecycleOwner(this);


        SharedPreferences sharedPref = ((MyApplication) getApplication()).getPreferences();

        CHAPTER_NUM = sharedPref.getInt("CURRENT_CHAPTER_NUM", -1) + 1;
        Log.d("MA", "chapter num:"+CHAPTER_NUM);

        if (mViewModel==null) {
            Timber.d("obtaining viewmodel");
            mViewModel = obtainViewModel(this);

            mViewModel.onActivityCreated(this, CHAPTER_NUM);
        }

        mMurojaahDataBinding.setViewmodel(mViewModel);

        Timber.d("queue size ==> " + mViewModel.getQueueSize());

        final Observer<Integer> numWorkerObserver = new Observer<Integer>() {

            @Override
            public void onChanged(@Nullable Integer numAvailWorkers) {
                Timber.d("num worker has been changed ==> " + numAvailWorkers);
                Timber.d("queue size ==> " + mViewModel.getQueueSize());
                mViewModel.numAvailableWorkers.set(numAvailWorkers);
                if (numAvailWorkers>0) {
                    if (mViewModel.getQueueSize()>0) {
                        mViewModel.dequeueRecognitionTasks();
                    } else {
                        Timber.d("recognition task queue empty. do nothing");
                    }
                } else {
                    Timber.d("no worker available. do nothing");
                }
            }
        };

        mViewModel.getStatusListener().getNumWorkersAvailable().observe(this, numWorkerObserver);

        final Observer<ArrayList<EvaluationOld>> evalsObserver = new Observer<ArrayList<EvaluationOld>>() {
            @Override
            public void onChanged(@Nullable ArrayList<EvaluationOld> evaluations) {
                Timber.d("eval set has changed");
            }
        };

        mViewModel.getEvalsMutableLiveData().observe(this, evalsObserver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    public static MurojaahViewModel obtainViewModel(Activity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        TranscriptionsRepository transcriptionRepo = new TranscriptionsRepository();
        RecordingRepository recordingRepo = new RecordingRepository();

        return new MurojaahViewModel(activity.getApplication(), transcriptionRepo, recordingRepo);
        //return ViewModelProviders.of(activity, factory).get(MurojaahViewModel.class);
    }

    @Override
    public void gotoResult() {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        finish();
        startActivity(intent);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}

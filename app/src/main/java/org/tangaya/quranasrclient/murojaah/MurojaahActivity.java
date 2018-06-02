package org.tangaya.quranasrclient.murojaah;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.tangaya.quranasrclient.ControlFragment;
import org.tangaya.quranasrclient.ViewModelFactory;
import org.tangaya.quranasrclient.data.Quran;
import org.tangaya.quranasrclient.data.source.TranscriptionsRepository;
import org.tangaya.quranasrclient.R;
import org.tangaya.quranasrclient.service.WavAudioRecorder;
import org.tangaya.quranasrclient.databinding.ActivityMurojaahBinding;

import java.io.File;

public class MurojaahActivity extends AppCompatActivity implements MurojaahNavigator {

    private int SURAH_NUM = 0;
    private String SURAH_NAME = "not-set";

    Button recordButton, clearButton;
    WavAudioRecorder mRecorder;

    private MurojaahViewModel mViewModel;
    private ActivityMurojaahBinding mMurojaahDataBinding;

    String mRecordFilePath = "/storage/emulated/0/DCIM/bismillah.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_murojaah);
        mMurojaahDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_murojaah);
        mMurojaahDataBinding.setLifecycleOwner(this);

        SURAH_NUM = getIntent().getExtras().getInt("SURAH_NUM");
        SURAH_NAME = getIntent().getExtras().getString("SURAH_NAME");
        TextView surahTv = findViewById(R.id.surah_name);
        surahTv.setText(SURAH_NUM+"."+SURAH_NAME);

        setupRecordButton();
        setupClearButton();

        ControlFragment controlFragment = obtainViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.murojaah_control_frame, controlFragment);
        transaction.commit();

        mViewModel = obtainViewModel(this);
        mMurojaahDataBinding.setViewmodel(mViewModel);

        Quran.init(getApplicationContext());
        mViewModel.currentSurahNum.set(SURAH_NUM);
        mViewModel.currentAyahNum.set(1);
    }

    private void setupRecordButton() {
        recordButton = (Button) findViewById(R.id.record);
        recordButton.setText("Start");
        mRecorder = WavAudioRecorder.getInstanse();
        mRecorder.setOutputFile(mRecordFilePath);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WavAudioRecorder.State.INITIALIZING == mRecorder.getState()) {
                    mRecorder.prepare();
                    mRecorder.start();
                    recordButton.setText("Stop");
                } else if (WavAudioRecorder.State.ERROR == mRecorder.getState()) {
                    mRecorder.release();
                    mRecorder = WavAudioRecorder.getInstanse();
                    mRecorder.setOutputFile(mRecordFilePath);
                    recordButton.setText("Start");
                } else {
                    mRecorder.stop();
                    mRecorder.reset();
                    recordButton.setText("Start");
                }
            }
        });
    }

    private void setupClearButton() {

        clearButton = (Button) findViewById(R.id.reset);
        clearButton.setText("Clear");
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File pcmFile = new File(mRecordFilePath);
                if (pcmFile.exists()) {
                    pcmFile.delete();
                }
            }
        });
    }

    public static MurojaahViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        TranscriptionsRepository repo = new TranscriptionsRepository();

        return new MurojaahViewModel(activity.getApplication(), repo);
        //return ViewModelProviders.of(activity, factory).get(MurojaahViewModel.class);
    }

    @NonNull
    private ControlFragment obtainViewFragment() {
        // View Fragment
        ControlFragment controlFragment = (ControlFragment) getSupportFragmentManager()
                .findFragmentById(R.id.murojaah_control_frame);

        if (controlFragment == null) {
            controlFragment = ControlFragment.newInstance();
        }
        return controlFragment;
    }

    @Override
    public void onClickHint() {
        mViewModel.showHint();
    }
}

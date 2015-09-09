package felipe.cl.spm.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import felipe.cl.spm.R;
import felipe.cl.spm.actividades.extras.Constantes;
import felipe.cl.spm.actividades.extras.CustomStateDialog;
import felipe.cl.spm.actividades.extras.CustomStateToast;
import felipe.cl.spm.actividades.extras.Utilities;

public class FormValidacionAuditiva extends Activity implements View.OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    boolean playing =false;
    Context mContext;

    ImageButton btn_back, btn_save, btn_rec, btn_preview;
    SeekBar playProgress;
    TextView duration_reprod, record_time, state_record;
    long startRecording = 0;


    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP3 = ".mp3";
    private static final String AUDIO_RECORDER_FOLDER = "SPM";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,             MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP3, AUDIO_RECORDER_FILE_EXT_3GP };
    private String FILENAME;
    private File file;
    private MediaPlayer mp;
    private Utilities utils;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_form_validacion_auditiva);

        mContext = this;
        playProgress = (SeekBar)findViewById(R.id.seekBar);
        playProgress.setEnabled(false);
        duration_reprod = (TextView)findViewById(R.id.duratio_reprod);
        record_time = (TextView)findViewById(R.id.tv_record_time);
        state_record = (TextView)findViewById(R.id.tv_estado);

        btn_back = (ImageButton) findViewById(R.id.ib_back);
        btn_save = (ImageButton) findViewById(R.id.ib_save);
        btn_rec = (ImageButton) findViewById(R.id.ib_rec);
        btn_preview = (ImageButton) findViewById(R.id.ib_play_recorded);
        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_preview.setOnClickListener(this);
        btn_preview.setEnabled(false);

        btn_rec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("AUDIORECORDING", "Start Recording");
                        startRecording = System.currentTimeMillis();
                        mHandler.postDelayed(mUpdateRecTimeTask, 100);
                        mHandler.postDelayed(mUpdateRecStateTask, 100);
                        state_record.setText("GRABANDO");
                        startRecording();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("AUDIORECORDING", "stop Recording");
                        btn_preview.setEnabled(true);
                        mHandler.removeCallbacks(mUpdateRecTimeTask);
                        mHandler.removeCallbacks(mUpdateRecStateTask);
                        record_time.setText("");
                        state_record.setText("");
                        stopRecording();
                        break;
                }
                return false;
            }
        });

        utils = new Utilities();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            finish();
        }
        if (v.getId() == R.id.ib_save) {
            CustomStateDialog t = new CustomStateDialog(this, R.drawable.ic_state_ok, "REGISTRO INGRESADO EXISTOSAMENTE", Constantes.STATE_OK);
            t.setCanceledOnTouchOutside(true);
            t.show();
            TimeoutOperation async = new TimeoutOperation(t);
            async.execute();
        }
        if(v.getId() == R.id.ib_play_recorded){
            if(file.exists() && !playing){
                playing = true;
                audioPlayer(FILENAME);
            }
        }
    }

    private class TimeoutOperation extends AsyncTask<String, Void, Void> {
        CustomStateDialog dialog;

        public TimeoutOperation(CustomStateDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            try{
                FormValidacionVisual.activity.finish();

            Planes.activity.finish();}catch (Exception e){e.printStackTrace();}
            ((Activity)mContext).finish();
        }
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }
        FILENAME = file.getAbsolutePath() + "/" + "respuesta" + file_exts[currentFormat];

        return (FILENAME);
    }

    public void audioPlayer(String fileName){
        //set up MediaPlayer

        mp = new MediaPlayer();
        try {
            mp.setDataSource(fileName);
            mp.setOnCompletionListener(this);
            mp.prepare();
            mp.start();

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playing = false;
                }
            });

            playProgress.setProgress(0);
            playProgress.setMax(100);
            updateProgressBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private MediaRecorder.OnErrorListener errorListener = new        MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.i("AUDIORECORDING", "Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.i("AUDIORECORDING", "Warning: " + what + ", " + extra);
        }
    };

    private void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private int flag = 0;
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            //songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            duration_reprod.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            playProgress.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    private Runnable mUpdateRecTimeTask = new Runnable() {
        public void run() {
            long currentDuration = System.currentTimeMillis() -startRecording;

            record_time.setText(""+utils.milliSecondsToTimer(currentDuration));

            mHandler.postDelayed(this, 100);
        }
    };
    private Runnable mUpdateRecStateTask = new Runnable() {
        public void run() {
            switch (flag){
                case 0:
                    state_record.setText("GRABANDO.");
                    flag++;
                    break;
                case 1:
                    state_record.setText("GRABANDO..");
                    flag++;
                    break;
                case 2:
                    state_record.setText("GRABANDO...");
                    flag=0;
                    break;
                default:
                    break;
            }

            mHandler.postDelayed(this, 800);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

}

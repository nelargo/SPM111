package felipe.cl.spm.actividades;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    boolean asd = false;

    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP3 = ".mp3";
    private static final String AUDIO_RECORDER_FOLDER = "SPM";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,             MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP3, AUDIO_RECORDER_FILE_EXT_3GP };
    private String FILENAME;
    private String NAME;
    private File file;
    private MediaPlayer mp;
    private Utilities utils;
    private Handler mHandler = new Handler();
    String rut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_form_validacion_auditiva);

        rut = getIntent().getStringExtra("RUT");
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
                        asd = true;
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
            if(asd) {
                UploadImage up = new UploadImage(mContext, null, FILENAME, NAME);
                up.execute();
            }else{
                Toast.makeText(mContext, "Debe grabar la declaracion", Toast.LENGTH_LONG).show();
            }
        }
        if(v.getId() == R.id.ib_play_recorded){
            if(file.exists() && !playing){
                playing = true;
                audioPlayer(FILENAME);
            }
        }
    }

    private class UploadImage extends AsyncTask<String, String, String> {

        private Context mContext;
        ProgressDialog dialog;
        View view;
        String path;
        String name;
        int flag= 0;

        public UploadImage(Context mContext, View v, String path, String n) {
            name = n;
            this.path = path;
            this.view = v;
            this.mContext = mContext;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            dialog.setMessage(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";


            try {
                String fileName = name;

                publishProgress(fileName);
                Log.i("ENVIANDO", fileName);
                HttpURLConnection conn;
                DataOutputStream dos;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;

                File done = new File(path);


                if (!done.isFile())
                    Log.e("DownloadManager", "no existe");
                else {
                    FileInputStream fileInputStream = new FileInputStream(done);
                    URL url = new URL("http://madgoatstd.com/SPM/upper.php");

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    //conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    //conn.setRequestProperty("uploadedfile", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, 1 * 1024 * 1024);
                    byte[] buf = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buf, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buf, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, 1 * 1024 * 1024);
                        bytesRead = fileInputStream.read(buf, 0, bufferSize);

                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();


                    Log.i("UploadManager", "HTTP response is: " + serverResponseMessage + ": " + serverResponseCode);

                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    InputStream responseStream = new BufferedInputStream(conn.getInputStream());

                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = responseStreamReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    responseStreamReader.close();

                    response = stringBuilder.toString();

                    if (response.contains("<MESSAGE>OK</MESSAGE>")) {
                        Log.d("ENVIANDO", "OK");
                    } else {
                        Log.d("ENVIANDO", "NOK");
                    }
                }


            } catch (Exception e) {
                Log.d("TAG", "Error: " + e.getMessage());
                response = "ERROR";
            }

            return response;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(mContext);
            dialog.setTitle("Subiendo Archivo de Audio");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing())
                dialog.dismiss();
            CustomStateDialog t = new CustomStateDialog(mContext, R.drawable.ic_state_ok, "REGISTRO INGRESADO EXISTOSAMENTE", Constantes.STATE_OK);
            t.setCanceledOnTouchOutside(true);
            t.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    try{
                        Planes.activity.finish();
                        FormValidacionVisual.activity.finish();
                        ((Activity)mContext).finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            t.show();

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
        NAME = rut+"_respuesta" + file_exts[currentFormat];
        FILENAME = file.getAbsolutePath() + "/" + NAME;

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

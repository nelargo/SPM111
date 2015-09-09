package felipe.cl.spm.actividades.extras;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.reflect.TypeVariable;

import felipe.cl.spm.R;
import felipe.cl.spm.actividades.DetalleCliente;
import felipe.cl.spm.actividades.Form_Cliente;

/**
 * Created by Felipes on 17-07-2015.
 */
public class Utilities {

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    private class DialogTimeoutOperation extends AsyncTask<String, Void, Void> {
        Class activity;
        Context context;
        int duration;
        Dialog dialog;
        Intent intent;

        public DialogTimeoutOperation(Context context,Dialog d, int duration, Class activity) {
            intent = new Intent(context,activity);
            this.activity = activity;
            this.context = context;
            this.duration = duration;
            this.dialog = d;
        }

        public void putExtra(String field, int VALUE){
            if(intent != null){
                   intent.putExtra(field, VALUE);
            }else{
                Log.e("Utilities", "Debe crear el task primero");
            }
        }


        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            context.startActivity(intent);
            dialog.dismiss();
        }
    }
}

package aac.mhr.baraye;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private TextView output;
    private StringBuilder text = new StringBuilder();
    private AssetFileDescriptor afd = null;
    private MediaPlayer player = new MediaPlayer();
    private String filename = "aac/Baraye.aac";

    public Button bt;
    public CountDownTimer cdt;
    public PowerManager.WakeLock wk;
    public int bloop = 2;
    public String mypref = "My_delay";
    public String mytime = "My_time";
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    public SharedPreferences prefs_time;
    public SharedPreferences.Editor editor_time;

    @SuppressLint("InvalidWakeLockTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BufferedReader reader = null;
        bt = findViewById(R.id.button2);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wk = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyWakeLock");
        wk.acquire();

        prefs = getSharedPreferences(mypref, MODE_PRIVATE);
        editor = getSharedPreferences(mypref, MODE_PRIVATE).edit();

        prefs_time = getSharedPreferences(mytime, MODE_PRIVATE);
        editor_time = getSharedPreferences(mytime, MODE_PRIVATE).edit();
        //    editor.putInt(mypref, 0);
        //}
        //SharedPreferences.Editor editor = getSharedPreferences(mypref, MODE_PRIVATE).edit();
        //editor.putString(mypref, "Elena");
        //editor.putInt(mypref, 12);
        //editor.apply();
        //String name = prefs.getString(mypref, "No name defined");//"No name defined" is the default value.
        //prefs.getInt(mypref, 0); //0 is the default value.


        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("bb1.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
                //text.append("time= " +getNetworkTime() + '\n');
                //DateFormat df = new SimpleDateFormat("mmssSSS");
                //String date = df.format(System.currentTimeMillis());
                //text.append("time= " +nntime() + '\n');
                //text.append("systemnanotime= " +System.nanoTime() + '\n');
                //text.append("systemcurenttimemili= " +System.currentTimeMillis() + '\n');
                //text.append("date gettime= " +new Date().getTime() + '\n');
            }
        } catch (IOException e) {
            //makeText(getApplicationContext(), "Error reading file!", LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
            output = (TextView) findViewById(R.id.textview);
            output.setMovementMethod(new ScrollingMovementMethod());
            output.setHorizontalScrollBarEnabled(true);
            //output.setText(String.valueOf( ct));
            //text.append(nntime() +"\n");
            output.setText((CharSequence) text);
        }
        try {
            afd = getResources().getAssets().openFd(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert afd != null;
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*

       // int pp = Math.toIntExact(getNetworkTime());
        output = (TextView) findViewById(R.id.textview);
        output.setMovementMethod(new ScrollingMovementMethod());
        output.setHorizontalScrollBarEnabled(true);
        //output.setText(String.valueOf( ct));
        text.append(nntime() +"\n");
        output.setText((CharSequence) text);

 */
//SNTPClient.getDate(TimeZone.getTimeZone(String.valueOf(Calendar.getInstance().getTimeZone())), new SNTPClient.Listener() {
            SNTPClient.getDate(new SNTPClient.Listener() {
                @Override
                public void onTimeResponse(long longtime) {
                    long nn = System.currentTimeMillis() - longtime;
                    //Toast.makeText(MainActivity.this, "nn= " + longtime, Toast.LENGTH_LONG).show();
                    if (nn > 10000 || nn < -10000 ) {//1
                        if (prefs.getInt(mypref, 0) == 0) {
                            //nn = prefs.getInt(mypref, 0);
                            Toast.makeText(MainActivity.this, "SYNC ERROR??", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }else {//2
                            longtime = System.currentTimeMillis() - prefs_time.getLong(mytime ,0);//3
                            //Toast.makeText(MainActivity.this, "kk= " + nn, Toast.LENGTH_LONG).show();
                            //Toast.makeText(MainActivity.this, "longtime= " + longtime, Toast.LENGTH_LONG).show();
                            if(longtime > (3600000) || longtime < (-3600000)){//4
                                Toast.makeText(MainActivity.this, "SYNC IS EXPIRED%%!!" + longtime , Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }else {//5
                                longtime = (long) prefs.getInt(mypref, 0);//6
                                Toast.makeText(MainActivity.this, "OLD SYNC OK ##?? " + longtime, Toast.LENGTH_LONG).show();
                                startmp(longtime);
                            }
                        }
                    }

                    else {//2
                            //editor.clear();
                            //editor.commit();
                            //editor.remove(mypref);
                            editor_time.putLong(mytime , System.currentTimeMillis()).apply();//3
                            editor.putInt(mypref, (int) nn ).apply();//4
                            //editor.apply();
                            Toast.makeText(MainActivity.this, "SYNC OK !!!! " + nn, Toast.LENGTH_LONG).show();
                            startmp((int) nn );
                    }
                }

                @Override
                public void onTimeReceived(String rawDate) {

                    //Toast.makeText(MainActivity.this, "reciv" +rawDate, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Exception ex) {
                    // Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            });

        //startmp(();
        //silent();

        //bt.setText("play");//132135 end 150047
        bt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                    onBackPressed();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startmp (long longtime) {
/*
        new CountDownTimer(100000000 , 10000  ){
            //long uutime = System.currentTimeMillis();
            @Override
            public void onTick(long l) {
                bt.setText(String.valueOf( nntime()));
            }

            @Override
            public void onFinish() {

            }
        }.start();

 */

        cdt = new CountDownTimer(150000 , 1 ){
            //long uutime = System.currentTimeMillis();
            @Override
            public void onTick(long l) {
                // bt.setText((int) l);
            }

            @Override
            public void onFinish() {
                bloop -= 1;
                if (bloop > 0) {
                    //long gh = System.currentTimeMillis();
                    player.seekTo(0);
                    player.start();
                    //Toast.makeText(MainActivity.this, "uutime= " + (gh -  uutime)  , Toast.LENGTH_LONG).show();
                    cdt.start();
                    //text.append(nntime() + "\n");
                    //output.setText((CharSequence) text);
                    //Toast.makeText(MainActivity.this, "uutime= " + (System.currentTimeMillis() -  uutime)  , Toast.LENGTH_LONG).show();
                }else{
                    onBackPressed();
                }
            }
        };

        if (!player.isPlaying()) {
            /*
            if(longtime > 15000  || longtime < -15000){
                longtime = 0;}

             */
            //player.setLooping(true);
            int ttime = (int) (nntime() - longtime);
            player.seekTo(ttime);
            player.start();
            ttime = 150000 - ttime ;
            new CountDownTimer(ttime, 1) {

                public void onTick(long l) {
                    //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                    // logic to set the EditText could go here
                }

                public void onFinish() {
                    //long gh = System.currentTimeMillis();
                    player.seekTo(0);
                    player.start();
                    cdt.start();
                    //text.append(nntime() +"\n");
                    //output.setText((CharSequence) text);
                    //Toast.makeText(MainActivity.this, "end timer  "  , Toast.LENGTH_LONG).show();
                }

            }.start();
            bt.setText("exit");  //changing text
        }

    }

    public int nntime() {
        long gh = System.currentTimeMillis();
        DateFormat dm = new SimpleDateFormat("mm");
        String min = dm.format(gh).substring(1);
        DateFormat ds = new SimpleDateFormat("ss");
        String sec = ds.format(gh);
        DateFormat dms = new SimpleDateFormat("SSS");
        String msec = dms.format(gh);
        int ct = (Integer.parseInt(min)) *60 *1000 + (Integer.parseInt(sec) * 1000) + (Integer.parseInt(msec));//Integer.valueOf()
        //Toast.makeText(MainActivity.this, min +" " +sec +" "+msec +"ct = "+ct  , Toast.LENGTH_LONG).show();
        for(;ct <= 150000;)
            return ct ;
        for(;ct <= 300000;)
            return (ct - 150000);
        for(;ct <= 450000;)
            return (ct - 300000);
        for(;ct <= 450000;)
            return (ct - 300000);

            return (ct - 450000);
    }

    //NTP server list: http://ir.pool.ntp.org
    @Override
    protected void onPause() {
        super.onPause();
     /*   if(player != null){
            player.pause();
            bt.setText("play");
        }

      */
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this.getApplication().onTerminate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wk.release();
        /*
        if(player != null){
            player.pause();
            bt.setText("play");
        }

         */
       // this.getApplication().onTerminate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(player != null){
            player.stop();
        }
        finish();
    }

}
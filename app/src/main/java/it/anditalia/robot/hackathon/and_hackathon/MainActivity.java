package it.anditalia.robot.hackathon.and_hackathon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qihancloud.opensdk.base.TopBaseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends TopBaseActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/octet-stream");
    private CountDownTimer countDownTimer;
    MediaRecorder recorder;
    private boolean istimeRunning = false;
    String root = Environment.getExternalStorageDirectory().toString();;
    private boolean isMicRecording = false;
    private long timeLeftInMilleseconds = 5000;
    Button start_btn, start_voice,zaavar;
    TextView textView;
    Dialog dialog;
    Request request;
    TextView attempText, amjilt;
    boolean isWin = false;

    OkHttpClient okHttpClient;
    MediaPlayer high, low, won, ex;
    int secretNumber;
    TextToSpeech mTts;
    private SpeechRecognizer speechRecognizer;
    int attempt =7;
    private String ttsUrl = "https://api.chimege.com/v1.2/synthesize";
    private String ttsToken = "d5da4a441d724f59227246957b4493644d2919ad7d65fb0bbdc5ccc7914acd71";
    private String sttUrl = "https://api.chimege.com/v1.2/transcribe";
    private String sttToken = "6f0a59cf46b150b75fa6fdd9c404955a37f1e950d4732033fb0d700cadd034ec";
    private static final int RecordAudioRequestCode =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this line is mandatory for the robot framework
        register(MainActivity.class);
        // This line permits to keep alive your app on the screen of the robot.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        }catch(NullPointerException e){}

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            checkPermissionReadStorage();
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            checkPermissionWriteStorage();
        }
        setContentView(R.layout.activity_main);
        start_btn = (Button) findViewById(R.id.start_btn);
        start_voice = (Button) findViewById(R.id.start_voice);
        start_btn = (Button) findViewById(R.id.start_btn);
        high= MediaPlayer.create(this, R.raw.high);
        low= MediaPlayer.create(this, R.raw.low);
        won= MediaPlayer.create(this, R.raw.won);
        ex= MediaPlayer.create(this, R.raw.ex);
        zaavar = (Button) findViewById(R.id.start_zaavar);
        textView = (TextView) findViewById(R.id.textView);
        dialog = new Dialog(this);
        attempText = (TextView) findViewById(R.id.attemptText);
        secretNumber = generateSecretNumber();
        zaavar.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                zaavarDialog();
            }
        });
        start_voice.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isWin){
                if(attempt<=7&&attempt>=1){
                    openVoiceInputDialog(attempt);
                    //voice();
                }else if(attempt == 0){
                    openLoseDialog();
                }
                }else{
                    openWinDialog(attempt);
                }
            }
        }));
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isWin){
                    if(attempt<=7&&attempt>=1){
                        inputDialog(attempt);
                    }else if(attempt == 0){
                        openLoseDialog();
                    }
                }
                else{
                    openWinDialog(attempt);
                }
            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }
    private void checkPermissionReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
    }
    private void checkPermissionWriteStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
        }
    }

    public int generateSecretNumber() {
        Random r = new Random();
        int secretNumber1 = r.nextInt(100) + 1;
        return secretNumber1;
    }


    private void zaavarDialog(){
            dialog.setContentView(R.layout.zaavar_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnstart = dialog.findViewById(R.id.zaavarButton);
        ImageView imageViewClose2 = dialog.findViewById(R.id.imageViewCloseZaavar);
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        imageViewClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void inputDialog(final int lolattempt) {
        dialog.setContentView(R.layout.input_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView inputAttemptText = dialog.findViewById(R.id.inputAttemptText);
        TextView result2 = dialog.findViewById(R.id.result2);
        result2.setText(""+ secretNumber);
        inputAttemptText.setText("ТАНЬД НИЙТ "+ lolattempt + " БОЛОМЖ БАЙНА.");
        final TextView inputResultext = dialog.findViewById(R.id.inputResultText);
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose2);
        Button btnInsert = dialog.findViewById(R.id.inputButton);
        final EditText inputNumber = dialog.findViewById(R.id.inputNumber);
        inputNumber.setFilters(new InputFilter[]{new InputFilterMinMax("1","100")});
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int inputNumbo = Integer.parseInt(String.valueOf(inputNumber.getText()));
                    if(secretNumber == inputNumbo) {

                        dialog.dismiss();
                        openWinDialog(attempt);
                    }
                    else if(secretNumber > inputNumbo) {
                        if(attempt == 1){
                            openLoseDialog();
                        }
                        else{
                            low.start();
                            inputResultext.setText("Taны тоо бага байна");
                            attempt--;
                            attempText.setText("ТАНЬД НИЙТ "+ attempt + " БОЛОМЖ БАЙНА.");
                            inputDialog(attempt);
                        }
                    }
                    else if(secretNumber < inputNumbo) {
                        if(attempt == 1){
                            openLoseDialog();
                        }
                        else{
                            high.start();
                            inputResultext.setText("Taны тоо их байна");
                            attempt--;
                            attempText.setText("ТАНЬД НИЙТ "+ attempt + " БОЛОМЖ БАЙНА.");
                            inputDialog(attempt);
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Тоогоо оруулна уу",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Тоогоо оруулна уу",Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openLoseDialog() {
        dialog.setContentView(R.layout.lose_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose2);
        TextView result = dialog.findViewById(R.id.result);
        result.setText("Миний санасан тоо бол : "+ secretNumber);
        Button btnOk = dialog.findViewById(R.id.loseButton);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                MainActivity.this.recreate();
            }
        });
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void voiceToChimege() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("",root + "/Wav/record.mp3",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(root +"/Wav/record.mp3")))
                .build();
        Request request = new Request.Builder()
                .url("https://api.chimege.com/v1.2/transcribe")
                .method("POST", body)
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("token", "6f0a59cf46b150b75fa6fdd9c404955a37f1e950d4732033fb0d700cadd034ec")
                .build();
        Response response = client.newCall(request).execute();
        Log.i("CHIMEGEGEEGEGE : " , response.body().string());
        //voiceDelete();
    }
    public void aacToWav() {
        File dir = new File(root+ "/Wav/");
        File file = new File(dir, "record.aac");
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Log.i("CHIMEGEGEEGEGE : " , convertedFile.getPath());
                //wavToMp3();
                //Toast.makeText(MainActivity.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception error) {
                Log.i("CHIMEGEGEEGEGE : " , error.getMessage());
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(file)

                // Your desired audio format
                .setFormat(AudioFormat.WAV)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }
    public void wavToMp3() {
        File dir = new File(root+ "/Wav/");
        File file = new File(dir, "record.wav");
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Log.i("CHIMEGEGEEGEGE : " , convertedFile.getPath());
                //Toast.makeText(MainActivity.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception error) {
                Log.i("CHIMEGEGEEGEGE : " , error.getMessage());
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(file)

                // Your desired audio format
                .setFormat(AudioFormat.MP3)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }
    private void voiceDelete(){
        File dir = new File(root+ "/Wav/");
        File file = new File(dir, "record.aac");
        boolean deleted = file.delete();
        if(deleted){
            Log.i("CHIMEGEGEEGEGE : " , "DELETED record.aac");
        }else{
            Log.i("CHIMEGEGEEGEGE : " , "NOTDELETED record.aac");
        }
    }

    public void startThread() {
        new Thread() {
            @Override
            public void run() {
                try {
                    voiceToChimege();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

        private void openVoiceInputDialog(final int lolattempt){
            dialog.setContentView(R.layout.voiceinput_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose2);
            final ImageView mic = dialog.findViewById(R.id.mic);
            TextView inputAttemptText = dialog.findViewById(R.id.inputAttemptText);
            final TextView inputStatus = dialog.findViewById(R.id.status);
            final TextView countdown = dialog.findViewById(R.id.countdown);
            final Button inputVoiceButton = dialog.findViewById(R.id.inputVoiceBtn);
            inputAttemptText.setText("ТАНЬД НИЙТ "+ lolattempt + " БОЛОМЖ БАЙНА.");
            inputVoiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isMicRecording){
                        Toast.makeText(MainActivity.this, "Бичиж байна...", Toast.LENGTH_SHORT).show();
                    }else{
                        //startRecording
                        inputStatus.setText("СОНСОЖ БАЙНА...");
                        countdown.setTextColor(Color.parseColor("#5271ff"));
                        startVoiceThread(countdown,inputStatus, mic);
                        mic.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording));
                        isMicRecording = true;
                    }
                }
            });
            imageViewClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
//voice record hiingee, timer aa buuruulangaa, 5 sec-n daraa automataar zogsooh code
    public void startVoiceThread(final TextView countdown, final TextView inputStatus, final ImageView mic) {
        new Thread() {
            @Override
            public void run() {
                startTiming(countdown);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                stopRecording(inputStatus,countdown,mic);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    startRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public void startRecording() throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(256000);
        recorder.setOutputFile(root + "/Wav/record.mp3");
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.prepare();
        recorder.start();
    }

    public void stopRecording(final TextView inputStatus, final TextView countdown, final ImageView mic){
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recorder.stop();
                        recorder.reset();
                        recorder.release();
                        inputStatus.setText("ХЭЛЭХ ТОВЧИЙГ ДАРЖ ТООГОО ХЭЛНЭ ҮҮ");
                        countdown.setTextColor(Color.parseColor("#3E3F55"));
                        mic.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped));
                        isMicRecording = false;
                        startThread();
                        //aacToWav();
                        //wavToMp3();
                    }
                });

            }

        }, 5000); //<-- Execute code after 5000 ms i.e after 5 Seconds.
    }


    public void startTiming(final TextView countdown) {
        for (int i = 5; i >= 0; i--) {
            final int finalI1 = i;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countdown.setText("00:0"+finalI1);
                }
            });
            SystemClock.sleep(1000);
        }
        //countdown.setText("00:05");
    }


    private void textToChimege(){
//        String data = "Сайн байна уу";
//        byte[] b = data.getBytes();
        okHttpClient = new OkHttpClient();
        request = new Request.Builder().url(ttsUrl)
                .header("Content-Type", "plain/text")
                .addHeader("token",ttsToken).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("CHIMEGE FAILURE:",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("CHIMEGE :",response.body().string());
            }
        });
        try{
            Response response = okHttpClient.newCall(request).execute();
            Log.i("CHIMEGE :",response.body().string());
        }catch (IOException e){
            e.printStackTrace();
        }
    }





//    WIN DIALOG
    private void openWinDialog(int attemptQR) {
        isWin = true;
        dialog.setContentView(R.layout.win_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        won.start();
        TextView attemptWin = dialog.findViewById(R.id.attemptWin);
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose2);
        ImageView QRcode = dialog.findViewById(R.id.QRcode);
        Button btnOk = dialog.findViewById(R.id.winButton);
        switch (attemptQR){
            case 7 : QRcode.setImageDrawable(getResources().getDrawable(R.drawable.a4999)); attemptWin.setText("Та нэг дэхь оролдлогоороо тааж 4,999₮ хожлоо"); break;
            case 6 : QRcode.setImageDrawable(getResources().getDrawable(R.drawable.a4999)); attemptWin.setText("Та хоёр дахь оролдлогоороо тааж 4,999₮ хожлоо");break;
            case 5 : QRcode.setImageDrawable(getResources().getDrawable(R.drawable.a3999)); attemptWin.setText("Та гурав дахь оролдлогоороо тааж 3,999₮ хожлоо");break;
            case 4 : QRcode.setImageDrawable(getResources().getDrawable(R.drawable.a2999)); attemptWin.setText("Та дөрөв дахь оролдлогоороо тааж 2,999₮ хожлоо");break;
            case 3 : QRcode.setImageDrawable(getResources().getDrawable(R.drawable.a1999)); attemptWin.setText("Та тав дахь оролдлогоороо тааж 1,999₮ хожлоо");break;
            case 2 : QRcode.setImageDrawable(getResources().getDrawable(R.drawable.a499)); attemptWin.setText("Та зургаа дахь оролдлогоороо тааж 499₮ хожлоо");break;
            default: QRcode.setImageDrawable(getResources().getDrawable(R.drawable.a499)); attemptWin.setText("Та сүүлийн оролдлогоороо тааж 499₮ хожлоо");
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isWin = false;
                dialog.dismiss();
                won.stop();
                MainActivity.this.recreate();
            }
        });
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                won.stop();
            }
        });
        dialog.show();
    }


// VOICE
    private void voice() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "ТООГОО ХЭЛНЭ ҮҮ");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int inputNumbo =0;
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if (resultCode == RESULT_OK && null!=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //textView.setText(result.get(0));
                    try {
                        inputNumbo = Integer.parseInt(result.get(0));
                        bodoh(inputNumbo);
                    }catch (Exception e){
                            ex.start();
                        voice();
                    }
                }
                break;
            }
        }
    }





//
    public void bodoh(int inputNumbo){
        if(inputNumbo<100){
            if(secretNumber == inputNumbo) {
                dialog.dismiss();
                openWinDialog(attempt);
            }
            else if(secretNumber > inputNumbo) {
                low.start();
                attempt--;
                attempText.setText("ТАНЬД НИЙТ "+ attempt + " БОЛОМЖ БАЙНА.");
                voice();
                //dialog.dismiss();
            }
            else if(secretNumber < inputNumbo) {
                high.start();
                attempt--;
                attempText.setText("ТАНЬД НИЙТ "+ attempt + " БОЛОМЖ БАЙНА.");
                voice();
                //dialog.dismiss();
            }else{
                Toast.makeText(MainActivity.this, "Тоогоо оруулна уу",Toast.LENGTH_SHORT).show();
            }
        }else{
            ex.start();
            voice();
        }

    }

    @Override
    protected void onMainServiceConnected() {
        // This funzione is executed before onCreate (is mandatory)
    }

}

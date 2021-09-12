package it.anditalia.robot.hackathon.and_hackathon;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qihancloud.opensdk.base.TopBaseActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends TopBaseActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    Button start_btn, start_voice,zaavar;
    TextView textView;
    Dialog dialog;
    TextView attempText, amjilt;
    boolean isWin = false;
    MediaPlayer high, low, won, ex;
    int secretNumber;
    private SpeechRecognizer speechRecognizer;
    int attempt =7;
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
                    voice();
                }else if(attempt == 0){
                    openLoseDialog();
                }
                }else{
                    openWinDialog();
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
                    openWinDialog();
                }
            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
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


    private void inputDialog(int lolattempt) {
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
                        openWinDialog();
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




    private void openWinDialog() {
        isWin = true;
        dialog.setContentView(R.layout.win_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        won.start();
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose2);
        Button btnOk = dialog.findViewById(R.id.winButton);
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
                    }
                }
                break;
            }
        }
    }

    public void bodoh(int inputNumbo){
        if(inputNumbo<100){
            if(secretNumber == inputNumbo) {
                dialog.dismiss();
                openWinDialog();
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

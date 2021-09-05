package it.anditalia.robot.hackathon.and_hackathon;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qihancloud.opensdk.base.TopBaseActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends TopBaseActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    Button start_btn, start_voice;
    TextView textView;
    Dialog dialog;
    TextView attempText;
    MediaPlayer high, low, won, ex;
    int secretNumber;
    int attempt =7;
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
        setContentView(R.layout.activity_main);
        start_btn = (Button) findViewById(R.id.start_btn);
        start_voice = (Button) findViewById(R.id.start_voice);
        start_btn = (Button) findViewById(R.id.start_btn);
        high= MediaPlayer.create(this, R.raw.high);
        low= MediaPlayer.create(this, R.raw.low);
        won= MediaPlayer.create(this, R.raw.won);
        ex= MediaPlayer.create(this, R.raw.ex);
        textView = (TextView) findViewById(R.id.textView);
        dialog = new Dialog(this);
        attempText = (TextView) findViewById(R.id.attemptText);
        secretNumber = generateSecretNumber();
        start_voice.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(attempt<=7&&attempt>=1){
                    voice();
                }else if(attempt == 0){
                    openLoseDialog();
                }
            }
        }));
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //voice();
                if(attempt<=7&&attempt>=1){
                    inputDialog(attempt);
                }else if(attempt == 0){
                    openLoseDialog();
                }
            }
        });


    }

    public int generateSecretNumber() {
        Random r = new Random();
        int secretNumber1 = r.nextInt(100) + 1;
        return secretNumber1;
    }

    private void inputDialog(int lolattempt) {
        dialog.setContentView(R.layout.input_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView inputAttemptText = dialog.findViewById(R.id.inputAttemptText);
        TextView result2 = dialog.findViewById(R.id.result2);
        result2.setText(""+ secretNumber);
        inputAttemptText.setText("ТАНЬД НИЙТ "+ lolattempt + " БОЛОМЖ БАЙНА.");
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
                        low.start();
                        attempt--;
                        attempText.setText("ТАНЬД НИЙТ "+ attempt + " БОЛОМЖ БАЙНА.");
                        dialog.dismiss();
                    }
                    else if(secretNumber < inputNumbo) {
                        high.start();
                        attempt--;
                        attempText.setText("ТАНЬД НИЙТ "+ attempt + " БОЛОМЖ БАЙНА.");
                        dialog.dismiss();
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
        dialog.setContentView(R.layout.win_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        won.start();
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose2);
        Button btnOk = dialog.findViewById(R.id.winButton);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        if(secretNumber == inputNumbo) {
            dialog.dismiss();
            openWinDialog();
        }
        else if(secretNumber > inputNumbo) {
            low.start();
            attempt--;
            attempText.setText("ТАНЬД НИЙТ "+ attempt + " БОЛОМЖ БАЙНА.");
            dialog.dismiss();
        }
        else if(secretNumber < inputNumbo) {
            high.start();
            attempt--;
            attempText.setText("ТАНЬД НИЙТ "+ attempt + " БОЛОМЖ БАЙНА.");
            dialog.dismiss();
        }else{
            Toast.makeText(MainActivity.this, "Тоогоо оруулна уу",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onMainServiceConnected() {
        // This funzione is executed before onCreate (is mandatory)
    }
}

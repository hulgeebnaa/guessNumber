package it.anditalia.robot.hackathon.and_hackathon;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.qihancloud.opensdk.base.TopBaseActivity;

public class SplashActivity extends TopBaseActivity {

    ImageView logo;
    Animation logoanim;
    int index;
    long delay= 200;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this line is mandatory for the robot framework
        register(MainActivity.class);
        try {
            this.getSupportActionBar().hide();
        }catch(NullPointerException e){}
        // This line permits to keep alive your app on the screen of the robot.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = (ImageView) findViewById(R.id.logo);
        //animation
        logoanim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        logo.setAnimation(logoanim);


        //startActivity(new Intent(SplashActivity.this, MainActivity.class));
        //Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        // Animatoo.animateFade(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);
    }
    @Override
    protected void onMainServiceConnected() {

    }
}
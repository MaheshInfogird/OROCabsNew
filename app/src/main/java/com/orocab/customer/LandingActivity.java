package com.orocab.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class LandingActivity extends AppCompatActivity {

    VideoView video;
    Uri uri;
    Button Skip;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        session = new UserSessionManager(getApplicationContext());

        if (session.isUserLoggedIn())
        {
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);
            finish();
        }

        video = (VideoView)findViewById(R.id.video_view);
        Skip = (Button)findViewById(R.id.skip);

        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.oro_video);
        video.setVideoURI(uri);
        video.start();

        Skip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
               // video.stopPlayback();
               // finish();
            }
        });

      /*  video.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mp.reset();
                video.setVideoURI(uri);
                video.start();
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

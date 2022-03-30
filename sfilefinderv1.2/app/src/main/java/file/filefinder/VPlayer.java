package file.filefinder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;

public class VPlayer extends Activity {
    MediaController mediaControls;
    VideoView simpleVideoView;

    protected void onCreate(Bundle bundle) {
    try{
        super.onCreate(bundle);        
        this.requestWindowFeature(1);
        this.setContentView(R.layout.vv);
        this.simpleVideoView = (VideoView)this.findViewById(R.id.v);
        ((LinearLayout)this.findViewById(R.id.ll)).setGravity(17);
        this.simpleVideoView.setTop(50);
        this.mediaControls = new MediaController(this);
        this.mediaControls.setAnchorView(this.simpleVideoView);
        this.simpleVideoView.setMediaController(this.mediaControls);
        if(bundle != null && bundle.getString("path").equals(this.getIntent().getData().toString())){
          this.simpleVideoView.setVideoURI(this.getIntent().getData());
          this.simpleVideoView.seekTo(bundle.getInt("current"));
          Toast.makeText(this, "onRestoreInstanceState executed" , 0).show();
        } else this.simpleVideoView.setVideoURI(this.getIntent().getData());
        this.simpleVideoView.start();
        this.simpleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
            }
        });
        this.simpleVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener(){

            public boolean onError(MediaPlayer mediaPlayer, int n, int n2) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", 1).show();
                return false;
            }
        });
        } catch(Exception e){
          Toast.makeText(this, "Error: " + e.getMessage(), 0).show();
        }
    }

@Override
protected void onSaveInstanceState(Bundle outState) {
    //    super.onSaveInstanceState(outState);
        outState.putInt("current", simpleVideoView.getCurrentPosition());
        outState.putString("path", getIntent().getData().toString());
        Toast.makeText(this, "onSaveInstanceState executed" , 0).show();
    }
    @Override
protected void onRestoreInstanceState(Bundle outState) {
        //super.onRestoreInstanceState(outState);
        //Toast.makeText(this, "onRestoreInstanceState executed" , 0).show();
   }
}


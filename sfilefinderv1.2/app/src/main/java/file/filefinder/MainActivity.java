package file.filefinder;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import java.io.File;
import android.content.Context;
import android.content.res.Configuration;

public class MainActivity extends Activity {

  ViewAdder addView;

  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    Toast.makeText(this, "onCreate", 0).show();
    // DBOperation.init(this);
    addView = new ViewAdder(this);
    this.setContentView(addView.sView);
    try {
    NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    /*  if (bundle.getString("current") != null)
        addView.current = new File(bundle.getString("current"));
      Toast.makeText(this, bundle.getString("current") + addView.current.getAbsolutePath(), 0);
      runOnUiThread( new Thread(new Runnable(){
    public void run(){
      MediaController mdc = new MediaController(getApplicationContext(), false);
      mdc.setAnchorView(addView.sView);
      mdc.setMediaPlayer(new MediaController.MediaPlayerControl() {

        public void    start(){
          
        }
 
        public void    pause(){
          
        }
 
       public int     getDuration(){
          return 0;
        }
 
        public int     getCurrentPosition(){
          return 0;
        }
 
        public void    seekTo(int pos){
          
        }
 
        public boolean isPlaying(){
          return true;
        }
 
       public int getBufferPercentage() {
          return 0;
        }
 
       public boolean canPause(){
          return true;
        }
 
      public  boolean canSeekBackward(){
          return true;
        }
 
      public boolean canSeekForward(){
          return true;
        }
 
     public int getAudioSessionId(){
          return 0;
        }
 
    });
    }
  }));*/
//  NotificationChannel notiChannel = new NotificationChannel("thisnotiid", "notiChannel", 1);
//  notiChannel.setShowBadge(true);
// notiChannel.setLockscreenVisibility(1);
//  NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//  nm.createNotificationChannel(notiChannel);
  Notification noti = new Notification.Builder(this )
  .setContentTitle("Simple Player")
  .setSmallIcon(R.drawable.logo1)
  .setStyle(new Notification.MediaStyle())
//  .setCustomContentView(new RemoteViews("file.filefinder", R.layout.remotely))
.build();
  nm.notify(3214, noti);
    } catch (Exception e) {
    Toast.makeText(this, e.getMessage() + "", 1).show();
    new AlertDialog.Builder(this).
    setTitle("Error")
     .setIcon(R.drawable.logo1)
    .setMessage(Arrays.toString(e.getStackTrace()))
    .show();
    }
  }

  @Override
  public void onBackPressed() {
    if (!addView.current.getPath().equals("/")) {
      addView.refresh(addView.parentPath);
      new Thread(
              new Runnable() {
                public void run() {
                  try {
                    Thread.sleep(100);
                  } catch (Exception e) {

                  }
                  addView.sView.setScrollY(addView.previouslyPressed);
                }
              })
          .start();

      // message(previouslyPressed + "");
    } else finish();
  }

  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("current", addView.current.getAbsolutePath());
    Toast.makeText(this, "onSaveInstanceState", 0).show();
  }

  @Override
  protected void onRestoreInstanceState(Bundle outState) {
    super.onRestoreInstanceState(outState);
    Toast.makeText(this, "onRestoreInstanceState", 0).show();
  }

  @Override
  protected void onPause() {
    super.onPause();
    Toast.makeText(this, "onPause", 0).show();
    /*SharedPreferences.Editor ed = new Share;
    ed.putInt("view_mode", mCurViewMode);
    ed.commit();*/
  }

  @Override
  protected void onStart() {
    super.onStart();
    Toast.makeText(this, "onStart", 0).show();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Toast.makeText(this, "onDestroy", 0).show();
  }

  @Override
  protected void onResume() {
    super.onResume();
    Toast.makeText(this, "onResume", 0).show();
  }
  /*@Override
  public boolean onCreateActionBar(Menu menu) {
      super.onCreateActionBar(menu);
    }*/
  @Override
  public void onConfigurationChanged(Configuration config) {
    super.onConfigurationChanged(config);
    Toast.makeText(this, "onConfigurationChange", 0).show();
  }
}

package file.filefinder;

import android.app.*;
import android.content.*;
import android.media.*;
import android.os.*;
import android.net.Uri;
import android.widget.*;
import java.util.*;
import java.io.*;

public class AudioPlayerService {
  MediaPlayer player;

  Context context;
  Handler handler;
  List<String> history = new ArrayList<>();
  List<File> playlist = new ArrayList<>();
  Iterator<File> it;
  Button parentBtn;

  public AudioPlayerService(Context c, File source, Button anchor) {
    try {
      context = c;
      parentBtn = anchor;
      handler = new Handler(context.getMainLooper());
      player = MediaPlayer.create(context, Uri.fromFile(source));
      player.setOnCompletionListener(new PlayerListener());
      player.setOnErrorListener(new PlayerListener());
      player.start();
      toast("Playing: " + source.getName());
      initPlaylist(source);
      Collections.sort(playlist, new AlphabeticalComparator<>());
      it = playlist.iterator();
      while (it.hasNext()) {
        if (it.next().getName().equals(source.getName())) break;
      }
      // NotificationChannel notiChannel = new NotificationChannel("thisnotiid", "notiChannel", NotificationManager.IMPORTANCE_HIGH);
//  notiChannel.setShowBadge(true);
//  notiChannel.setLockscreenVisibility(1);
//NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  //nm.createNotificationChannel(notiChannel);
  //Notification noti = new Notification.Builder(c, "thisnotiid")
 // .setContentTitle("Simple Player")
//  .setSmallIcon(R.drawable.logo1)
//  .setCustomContentView(new RemoteViews("file.filefinder", R.layout.remotely))
// .build();
 // nm.notify(998811, noti);
    } catch (Exception e) {
      toast("Error from AudioSercice construction: " + e.getMessage());
    }
  }

  public void initPlaylist(File file) {
    try {
      if (!history.contains(file.getParent())) {
        for (File f : file.getParentFile().listFiles()) {
          if (f.getName().endsWith("mp3")
              || f.getName().endsWith("ogg")
              || f.getName().endsWith("amr")
              || f.getName().endsWith("m4a")
              || f.getName().endsWith("wav")) playlist.add(f);
        }
        history.add(file.getParent());
        toast("Music(s) in this playlist is " + playlist.size());
      }
    } catch (Exception e) {
    }
  }

  public void toast(String alert) {
    handler.post(
        new Runnable() {
          public void run() {
            Toast.makeText(context, alert, 1).show();
          }
        });
  }

  public void play(File source) {
    try {
      player.reset();
      player.setDataSource(source.getAbsolutePath());
      player.prepare();
      player.start();
      toast("Playing: " + source.getName());
      initPlaylist(source);
      Collections.sort(playlist, new AlphabeticalComparator<>());
      it = playlist.iterator();
      while (it.hasNext()) {
        if (it.next().getName().equals(source.getName())) break;
      }
    } catch (Exception e) {
      toast("Error: " + e.getMessage());
    }
  }

  public class PlayerListener
      implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    public void onCompletion(MediaPlayer p) {
      try {
        if (it.hasNext()) {
          File f = it.next();
          p.reset();
          p.setDataSource(f.getAbsolutePath());
          p.prepare();
          p.start();
          toast("Playing: " + f.getName());
        } else player.release();
      } catch (Exception e) {
        toast(e.getMessage());
      }
    }

    public boolean onError(MediaPlayer playerl, int a, int b) {
      playerl.release();
      toast("Error code: " + a + " and " + b);
      return true;
    }
  }
}

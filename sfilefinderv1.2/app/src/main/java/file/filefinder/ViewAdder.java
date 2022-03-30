package file.filefinder;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import file.filefinder.VPlayer;
import static file.filefinder.ButtonFactory.Builder.buildFrom;
import static file.filefinder.LinearLayoutFactory.Builder;
import java.io.File;
import java.util.*;

public class ViewAdder {

  Context c;
  File current;
  int lastChildIndex;
  LinearLayout parent;
  Button parentBtn;
  File parentPath;
  ScrollView sView;
  AudioPlayerService player;
  int previouslyPressed;
  Button btnTemplate;
  LinearLayout llTemplate;

  public ViewAdder(Context ct) {
    c = ct;
    this.parentBtn =
        new ButtonFactory.Builder(c)
            .setSize(-2, -2)
            .setGravity(3)
            .setTextSize(20)
            .setText("parent")
            .build();
    this.parentBtn.setOnClickListener(
        new View.OnClickListener() {
          public void onClick(View view) {
            refresh(parentPath);
          }
        });
    btnTemplate =
        new ButtonFactory.Builder(c).setGravity(8).setBackground(c.getDrawable(R.color.d)).build();
    llTemplate = new LinearLayoutFactory.Builder(c).setSize(-1, -1).setGravity(5).build();
    this.parent =
        new LinearLayoutFactory.Builder(c)
            .setSize(-1, -1)
            .setOrientation(1)
            .addChild(this.parentBtn)
            .build();
    this.sView = new ScrollView(c);
    this.sView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
    this.sView.addView(this.parent);

    // sView.setEdgeEffectColor(R.color.grey50transparent);
    File sdcard = new File("/storage/sdcard1");
    this.parentPath = sdcard.exists() ? sdcard : Environment.getExternalStorageDirectory();
    refresh(current == null ? parentPath : current);
  }

  public void addView(File file, String name) {
    boolean isFolder = (!file.isFile() && file.isDirectory());
    Button button = buildFrom(c, btnTemplate);
    if (!isFolder) button.setBackground(c.getDrawable(R.color.f));
    button.setText(name);
    LinearLayout linearLayout = new Builder(c).setGravity(8).build();
    linearLayout.addView(button);
    parent.addView(linearLayout);
    button.setOnClickListener(
        new View.OnClickListener() {
          public void onClick(View view) {
            try {
              previouslyPressed = sView.getScrollY();
            } catch (Exception e) {
              message(Arrays.toString(e.getStackTrace()));
            }
            if (isFolder) {
              refresh(file);
            } else {
              Intent intent = new Intent();
              intent.setAction("android.intent.action.VIEW");
              intent.addCategory("android.intent.category.DEFAULT");
              if ((file.getName().endsWith("mp3")
                  || file.getName().endsWith("ogg")
                  || file.getName().endsWith("amr")
                  || file.getName().endsWith("m4a")
                  || file.getName().endsWith("wav"))) {
                new Thread(
                        new Runnable() {
                          public void run() {
                            try {
                              if (player == null) {
                                player = new AudioPlayerService(c, file, parentBtn);
                              } else {
                                player.play(file);
                              }
                            } catch (Exception e) {
                              message(Arrays.toString(e.getStackTrace()));
                            }
                          }
                        })
                    .start();

              } else if (file.getName().endsWith("3gp") || file.getName().endsWith("mp4")) {
                playV(Uri.fromFile(file));
              } else if (file.getName().toLowerCase().endsWith("txt")
                  || file.getName().toLowerCase().endsWith("java")
                  || file.getName().toLowerCase().endsWith("js")
                  || file.getName().toLowerCase().endsWith("html")
                  || file.getName().toLowerCase().endsWith("css")
                  || file.getName().toLowerCase().endsWith("xml")) {
                Intent i = new Intent(c, TReader.class);
                i.setData(Uri.fromFile(file));
                c.startActivity(i);
              } else if (file.getName().endsWith("docx") || file.getName().endsWith("doc")) {
                intent.setDataAndType(Uri.fromFile(file), "application/doc*");
                c.startActivity(Intent.createChooser(intent, "Pick Appropriately: "));
              } else if (file.getName().toLowerCase().endsWith("pdf")) {
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                c.startActivity(Intent.createChooser(intent, "Pick Appropriately: "));
              } else if (file.getName().toLowerCase().endsWith("zip")
                  || file.getName().toLowerCase().endsWith("x-zip")
                  || file.getName().toLowerCase().endsWith("zip-x")
                  || file.getName().toLowerCase().endsWith("rar")
                  || file.getName().toLowerCase().endsWith("gzip")
                  || file.getName().toLowerCase().endsWith("lz")) {
                intent.setDataAndType(Uri.fromFile(file), "application/zip*");
                c.startActivity(Intent.createChooser(intent, "Pick Appropriately: "));
              } else {
                intent.setDataAndType(Uri.fromFile(file), "*/*");
                c.startActivity(Intent.createChooser(intent, "Pick Appropriately: "));
              }
            }
          }
        });
    button.setOnLongClickListener(
        new View.OnLongClickListener() {
          public boolean onLongClick(View v) {
            AlertDialog d = new AlertDialog.Builder(c).setTitle("Message").show();
            return true;
          }
        });
  }

  public void refresh(File file) {

    try {
      File[] arrFile = file.listFiles();
      if (arrFile != null) {
        current = file;
        // setTitle(this.current.getAbsolutePath());
        parentPath = current.getParent() == null ? current : current.getParentFile();
        parentBtn.setText(parentPath.getPath());
        Map<String, File> folder = new TreeMap<String, File>(new AlphabeticalComparator<>());
        Map<String, File> notFolder = new TreeMap<String, File>(new AlphabeticalComparator<>());
        for (File f : arrFile) {
          if (f.isDirectory()) {
            folder.put(f.getName(), f);
          } else notFolder.put(f.getName(), f);
        }

        remove(lastChildIndex);
        this.lastChildIndex = 0;
        for (Map.Entry<String, File> f : folder.entrySet()) {
          this.addView(f.getValue(), f.getKey());
          this.parentPath =
              this.current.getParent() == null ? this.current : this.current.getParentFile();
          this.parentBtn.setText(this.parentPath.getPath());
          this.lastChildIndex++;
        }
        for (Map.Entry<String, File> f : notFolder.entrySet()) {
          this.addView(f.getValue(), f.getKey());
          this.parentPath =
              this.current.getParent() == null ? this.current : this.current.getParentFile();
          this.parentBtn.setText(this.parentPath.getPath());
          this.lastChildIndex++;
        }

      } else toast("cannot get child files");
    } catch (Exception e) {
      message(Arrays.toString(e.getStackTrace()));
    }
  }

  void remove(int n) {
    for (int i = 0; i < n; ++i) {
      try {
        this.parent.removeViewAt(1);
      } catch (Exception e) {
        message(Arrays.toString(e.getStackTrace()));
      }
    }
  }

  public void playV(Uri uri) {
    Intent intent = new Intent(c, VPlayer.class);
    intent.setData(uri);
    c.startActivity(intent);
  }

  void toast(String str) {
    Toast.makeText(c, str, 1).show();
  }

  void message(String message) {
    new AlertDialog.Builder(c).setMessage(message).setTitle("Message").show();
  }
}

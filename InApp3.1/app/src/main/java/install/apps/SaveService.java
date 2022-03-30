/*package install.apps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SaveService extends Service {
    Context c;

    @Override
    public void onCreate() {
        c = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Bundle b = intent.getExtras();
        new Thread(
                new Save(
                        b.getInt("refCount"),
                        b.getInt("count"),
                        b.getStringArrayList("labels"),
                        b.getStringArrayList("pkgName"),
                        b.getString("FILENAME")))
                .start();
        return START_NOT_STICKY;
    }

    void toast(final Object str) {
        new Handler(c.getMainLooper()).post(new Thread(new Runnable() {
            public void run() {
                Toast.makeText(c, str.toString(), Toast.LENGTH_LONG).show();
            }
        }));
    }

    public class Save implements Runnable {
        int refCount;
        int count;
        ArrayList<String> labels;
        ArrayList<String> pkgName;
        String FILENAME;

        public Save(
                int refCount,
                int count,
                ArrayList<String> labels,
                ArrayList<String> pkgName,
                String FILENAME) {
            this.refCount = refCount;
            this.count = count;
            this.labels = labels;
            this.pkgName = pkgName;
            this.FILENAME = FILENAME;
        }

        public void run() {
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILENAME, false));
                out.writeObject(labels);
                out.writeObject(pkgName);
                out.writeInt(refCount);
                out.writeInt(count);
                out.close();
            } catch (Exception e) {
                toast(e.getMessage());
            }
        }
    }
}
*/
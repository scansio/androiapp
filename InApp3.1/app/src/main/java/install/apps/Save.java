package install.apps;

import java.util.ArrayList;
import java.io.*;

public class Save implements Runnable {
    int refCount;
    int count;
    ArrayList<String> labels;
    ArrayList<String> pkgName;
    String fileName;

    public Save(
            int refCount,
            int count,
            ArrayList<String> labels,
            ArrayList<String> pkgName,
            String fileName) {
        this.refCount = refCount;
        this.count = count;
        this.labels = labels;
        this.pkgName = pkgName;
        this.fileName = fileName;
    }

    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName, false));
            out.writeObject(labels);
            out.writeObject(pkgName);
            out.writeInt(refCount);
            out.writeInt(count);
            out.close();
        } catch (Exception e) {
          appDetail.getInstance().toast(e.getMessage());
        }
    }
}

package com.klye.ime.latin;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import java.util.Map.Entry;

public class IESet extends IE {
    private static final String d1 = "__";
    private static final String d2 = "::";

    public void onCreate(Bundle savedInstanceState) {
        String s1 = "MLK_Settings_Begin";
        for (Entry<String, ?> entry : PreferenceManager.getDefaultSharedPreferences(this).getAll().entrySet()) {
            s1 = s1 + "\n__" + ((String) entry.getKey()) + d2 + entry.getValue().toString().replace(" ", "%20").replace("\n", "%0A") + d2 + entry.getValue().getClass().getSimpleName().charAt(0);
        }
        this.s = s1 + "\nMLK_Settings_End\n";
        super.onCreate(savedInstanceState);
    }

    static String parse(String s2, Context c) {
        Editor ed = PreferenceManager.getDefaultSharedPreferences(c).edit();
        String[] sp = s2.split("MLK_Settings_");
        if (sp.length != 3) {
            return "Invalid data / error";
        }
        sp = sp[1].split(d1);
        for (int i = 1; i < sp.length; i++) {
            String[] sp2 = sp[i].split(d2);
            if (sp2.length != 3) {
                return "error: " + sp[i];
            }
            switch (sp2[2].charAt(0)) {
                case 'B':
                    ed.putBoolean(sp2[0], sp2[1].charAt(0) == 't');
                    break;
                case 'I':
                    ed.putInt(sp2[0], Integer.parseInt(sp2[1]));
                    break;
                case 'L':
                    ed.putLong(sp2[0], Long.parseLong(sp2[1]));
                    break;
                case 'S':
                    ed.putString(sp2[0], sp2[1].replace(" ", "").replace("\n", "").replace("%0A", "\n").replace("%20", " "));
                    break;
                default:
                    return "unknown type: " + sp[i];
            }
        }
        ed.commit();
        return "Saved";
    }

    private void parse() {
        M.msg(this, parse(this.s.toString(), this));
    }

    protected void onSave() {
        try {
            parse();
        } catch (Throwable e) {
            M.msg(this, "Invalid settings");
            M.l(e);
        }
    }
}

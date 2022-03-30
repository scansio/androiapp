package install.apps;

import java.io.Serializable;
import java.util.Comparator;

public class AlphabeticalComparator<T> implements Comparator<T>, Serializable {
    private static final long serialVersionUID = 99287993L;

    public AlphabeticalComparator() {
    }

    public int compare(T str1, T str2) {
        int res = String.CASE_INSENSITIVE_ORDER.compare(str1.toString(), str2.toString());
        if (res == 0) {
            res = str1.toString().compareTo(str2.toString());
        }
        return res;

    }

}

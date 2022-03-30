package file.filefinder;

import java.util.*;

public class AlphabeticalComparator<T extends Object> implements Comparator<T>
{

    public AlphabeticalComparator(){}
    
    public int compare(T str1, T str2) {
        int res = String.CASE_INSENSITIVE_ORDER.compare(str1.toString(), str2.toString());
        if (res == 0) {
            res = str1.toString().compareTo(str2.toString());
        }
        return res;
    
     }
    
}

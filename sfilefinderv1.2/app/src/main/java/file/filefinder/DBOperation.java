package file.filefinder;

import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.database.*;
import android.content.*;
import android.widget.*;
import java.io.File;

public final class DBOperation {
  String error;
  static Context context;
  static String PATH;
  static SQLiteDatabase DB;
  
  public static void get(Context c){
    context = c;
    Cursor cursor = null;
  }
  
  public static void init(Context c){
  try{
    PATH = Environment.getExternalStorageDirectory() + "/sDBDir";
    new File(PATH).mkdir();
    DB = SQLiteDatabase.openDatabase(PATH + "/sDatabase.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
    DB.execSQL("CREATE TABLE file (path TEXT, frequency TEXT)");
    DB.execSQL("CREATE TABLE video (vpath TEXT, position LONG);");
  }catch(Exception e){
    
    Toast.makeText(c, "Error: " + e.getMessage(), 1).show();
  }
        
  }
  
  public static void put(String key, Object o){
    
  }
}

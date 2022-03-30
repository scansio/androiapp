package mem.info;

import android.app.*;
import android.os.*;
import android.widget.*;

import mem.info.R;

public class MainActivity extends Activity {
TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         tv = ((TextView)findViewById(R.id.tv));
         new Thread(new Runnable(){
            public void run(){
              memInfo();
            }
          }).start();
    }
    
    public void memInfo() {
        ActivityManager activityManager = (ActivityManager)getSystemService("activity"); 
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        
        try{
        Handler h = new Handler(Looper.getMainLooper());
        
        while(true){
         Thread.sleep(100);
          h.post(new Runnable(){
          
            public void run(){
            activityManager.getMemoryInfo(memoryInfo);
              tv.setText((memoryInfo.availMem/1000000) + " MB");
            }
          });
        }}catch(Exception e){
          
        }
     }
}

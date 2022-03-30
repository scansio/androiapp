package com.syber.treader;

import android.app.Activity;
import android.os.*;
import android.widget.*;

import java.io.*;
import java.util.*;
//import java.util.concurrent.*;

import com.syber.treader.R;

public class TReader extends Activity {
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_main);
        
        if(getIntent().getData() == null){
            Toast.makeText(this, "No file to read", 0).show();
            finish();
        }
        final TextView tv = findViewById(R.id.tv);
        final Handler h = new Handler(Looper.getMainLooper());
        try{
        
            Scanner sc = new Scanner(new FileInputStream(new File(getIntent().getData().getPath())));
            
             new Thread(new Runnable(){
             public void run(){
             try{
               while(sc.hasNextLine()){
                String str = sc.nextLine();
                String t = "";
                int i = str.length()/2;
                if(str.length() > 100){
                  t = str.substring(0, i);
                  h.post(new a(("\n" + t), tv));
                  h.post(new a(((str.substring(i, str.length() - 1))), tv));
                } else h.post(new a("\n" + str, tv));
                Thread.sleep(10);
                 
               }
               sc.close();
             }catch(Exception e){}
             
             
             }
           }).start();
           
           
            /*new Thread(new Runnable(){
              public void run(){
                 for(;;){
                 try{
                   if(!v.isEmpty())
                   h.post(new a(v.take(), tv));
                 }catch(Exception e){}
                   
                 }
                    
                }
            }).start();*/
          }catch(Exception e){
            tv.append("\n\n" + e.getMessage());
          }
    }
    
    public class a implements Runnable{
      TextView tv;
      String str;
      
      public a(String s, TextView ttv){
        tv = ttv;
        str = s;
      }
      public void run(){
        tv.append(str);
      }
    }
}

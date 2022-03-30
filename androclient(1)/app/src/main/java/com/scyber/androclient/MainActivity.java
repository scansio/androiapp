package com.scyber.androclient;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.View;

import com.scyber.androclient.R;

public class MainActivity extends Activity{
Handler h = new Handler(Looper.getMainLooper());
EditText et;
TextView tv;
ClientSide s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         et = findViewById(R.id.et);
         tv = findViewById(R.id.tv);
        Button btn = findViewById(R.id.btn);
        new Thread(new Runnable(){
          public void run(){
              s = new ClientSide(h, tv);
              s.connect(btn);
          }
        }).start();
        btn.setOnClickListener(new View.OnClickListener(){
          public void onClick(View v){
          if(!"".equals(et.getText().toString())){
            h.post(new a(et.getText().toString(), s));
            et.setText("");
          }
            
          }
        });
}

@Override
    protected void onDestroy(){
      h.post(new b(s));
    }
    
  public class a implements Runnable{
  ClientSide server ;
    String o = "";
    public a(String e, ClientSide c){
      o = e;
      server = c;
    }
     public void run(){
       server.send(o);
     }
   }
   
    public class b implements Runnable{
  ClientSide server ;
    public b(ClientSide c){
      server = c;
    }
     public void run(){
       server.close();
     }
   }
   
}
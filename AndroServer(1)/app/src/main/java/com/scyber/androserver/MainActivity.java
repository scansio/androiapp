package com.scyber.androserver;

import android.app.Activity;
import android.os.*;
import android.widget.*;
import android.view.View;

import com.scyber.androserver.R;

public class MainActivity extends Activity{
Handler h = new Handler(Looper.getMainLooper());
EditText et;
TextView tv;
ServerSide s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         et = findViewById(R.id.et);
         tv = findViewById(R.id.tv);
        Button btn = findViewById(R.id.btn);
        new Thread(new Runnable(){
          public void run(){
              s = new ServerSide(h, tv, "scansio");
              s.listen(btn);
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
  ServerSide server ;
    String o = "";
    public a(String e, ServerSide c){
      o = e;
      server = c;
    }
     public void run(){
       server.send(o);
     }
   }
   
    public class b implements Runnable{
  ServerSide server ;
    public b(ServerSide c){
      server = c;
    }
     public void run(){
       server.close();
     }
   }
   
}
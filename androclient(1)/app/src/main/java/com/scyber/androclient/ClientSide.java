package com.scyber.androclient;

import java.io.*;
import android.net.*;
import android.os.*;
import android.widget.*;
import android.view.View;

public class ClientSide
{
  //initialize socket and input stream
  private LocalSocket socket = null;
  private DataInputStream in = null;
  private DataOutputStream out = null;
  Handler handler;
  
  private TextView t;
  //constructor with port
  public ClientSide(Handler h, TextView tv )
  {
      t = tv;
      handler = h;
  }
  
  public void connect(View v){
  
  try{
  handler.post(new a(v, false)); 
   socket = new LocalSocket();
   handler.post(new Thread(t, "Connecting to ..."));
   //socket.bind(new LocalSocketAddress("scansio"));
    socket.connect(new LocalSocketAddress("scansio"));
 
     
      in = new DataInputStream(socket.getInputStream());
      out = new DataOutputStream(socket.getOutputStream());
      handler.post(new a(v, true));
      handler.post(new Thread(t, "Connected"));
        
      while(socket.isConnected()){
        handler.post(new Thread(t, in.readUTF()));
      }
      //handler.post(new Thread(t, "Connection cancelled"));
      
    }
    catch(Exception i)
    {
      handler.post(new Thread(t, i.getMessage()));
    }
  }
  
  public void close(){
  
    handler.post(new Thread(t, "Closing connection"));
      //close connection
      try
      {
      if(socket != null) socket.close();
      if(in != null)in.close();
      }catch(Exception e){
        handler.post(new Thread(t, e.getMessage()));
      }
  }
  
  public void send(String v){
    
    try{
      out.writeUTF(v);
    }catch(Exception e){
      handler.post(new Thread(t, e.getMessage()));
    }
  }
  
  public  class Thread implements Runnable{
    TextView tv;
     String o;
    public Thread (TextView tv, String o){
      this.tv = tv;
      this.o = o;
    }
    
    public void run(){
      tv.append("\n" + this.o);
    }
  }
  
  public  class a implements Runnable{
    View v;
     boolean o;
    public a (View v, boolean o){
      this.v = v;
      this.o = o;
    }
    
    public void run(){
      v.setEnabled(o);
    }
  }
  
}
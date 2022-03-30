package com.scyber.androserver;

import java.io.*;
import android.net.*;
import android.os.*;
import android.widget.*;
import android.view.View;

public class ServerSide
{
  //initialize socket and input stream
  private LocalSocket socket = null;
  private LocalServerSocket server = null;
  private DataInputStream in = null;
  private DataOutputStream out = null;
  
  private TextView t;
  Handler handler;
  //constructor with port
  public ServerSide(Handler mhandler, TextView tv, String name )
  {
   
  try{
      
      handler = mhandler;
      t = tv;
            //socket = new LocalSocket(LocalSocket.SOCKET_SEQPACKET);
      server = new LocalServerSocket(name);
     // handler.post(new Thread(t, "\nServer Local Address: " + server.getLocalSocketAddress().getName()));
   //   handler.post(new Thread(t, "Waiting for a client ..."));
      }catch(Exception s){
        handler.post(new Thread(t, s.getMessage()));
      }
  }
  
  public void listen(View v){
  
  try{
  
    handler.post(new a(v, false));
    handler.post(new Thread(t, "\nServer Local Address: " + server.getLocalSocketAddress().getName()));
      handler.post(new Thread(t, "Waiting for a client ..."));

    socket = server.accept();
      //handler.post(new Thread(t, " Remote Address: " + socket.getRemoteSocketAddress() +  "\nClient accepted"));
      //takes input from the client socket
     
      in = new DataInputStream(socket.getInputStream());
      out = new DataOutputStream(socket.getOutputStream());
      handler.post(new a(v, true));
      handler.post(new Thread(t, "Connected"));
      //reads message from client until "Over" is sent
      for(;;){
        handler.post(new Thread(t, in.readUTF()));
      }
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
      tv.append("\n" + o);
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
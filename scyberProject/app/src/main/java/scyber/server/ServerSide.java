package scyber.server;

import java.net.*;
import java.io.*;
public class ServerSide
{
  //initialize socket and input stream
  private Socket socket = null;
  private ServerSocket server = null;
  private DataInputStream in = null;
  //constructor with port
  public ServerSide(int port)
  {
    //starts server and waits for a connection
    try
    {
      System.out.println("Server stet3. 444darted at port 5100");
      socket = new Socket("localhost",port);
      server = new ServerSocket(port);
      System.out.println("Waiting for a client ...");
      socket = server.accept();
      System.out.println("Client accepted");
      //takes input from the client socket
      in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      String line = " ";
      //reads message from client until "Over" is sent
      while (!line.equals("Over"))
      {
        try
        {
          line = in.readUTF();
          System.out.println(line);
        }
        catch(Exception i)
        {
          System.out.println(i);
        }
      }
      System.out.println("Closing connection");
      //close connection
      socket.close();
      in.close();
    }
    catch(IOException i)
    {
      System.out.println(i);
    }
  }
  public static void main(String args[]){
    ServerSide server = new ServerSide(51050);
  }
}
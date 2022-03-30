package scyber.test;

import java.io.*;

public class Deserialization {

  public static void main(String[] args) {
  Deserialization t = new Deserialization();
    int  j = 0;
    for(String e : t.deserial("")){
      System.out.println(e);
      j+=Integer.parseInt(e);
    }
    System.out.println("Total = " + j);
  }
    
    public <A> scyber.util.Stack<A> deserial(A t){
      scyber.util.Stack<A> obj = null;
      File file = new File("/storage/emulated/0/" + PostfixEvaluation.class.getName() + ".jo");
    try{
      if(file.createNewFile())System.out.println("File created...");
      System.out.println("File existed...");
    }catch(IOException fioe){
      System.out.println("File error: " + fioe.getLocalizedMessage());
    }
    try(
      FileInputStream fin = new FileInputStream(file);
      ObjectInputStream out = new ObjectInputStream(fin)
    ){
        obj = (scyber.util.Stack<A>)out.readObject();
       // out.writeObject(s);
      } catch(Exception ioe){
        System.out.println("Error says: " + ioe.getMessage());
      }
      return obj;
    }
}

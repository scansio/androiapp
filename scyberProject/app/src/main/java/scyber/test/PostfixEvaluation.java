package scyber.test;

import scyber.util.*;
import java.util.Arrays;
import java.io.*;

public class PostfixEvaluation implements Serializable{

  private static final long serialVersionUID = 6819288105193937581L;


  public static void main(String[] args) {
    int  j = 0;
    CStack<String> t = evaluate("5 2 D d a d a A 10");
    for(String e : t){
      System.out.println(e);
      j+=Integer.parseInt((String)e);
    }
    System.out.println("Total = " + j);
    
  }
  
  public static  CStack<String> evaluate(String e){
    CStack<String> s = new Stack<>(e.length());
    CStack<String> si = new Stack<>(s.capacity());
    String[] i = e.split(" ");
    String number = "[0-9]*";
    
    for(String a:i){
      s.push(a);
    }
    System.out.println("S before: "+ Arrays.toString(s.elements()));
    s = s.transpose();
    System.out.println("S after: " + Arrays.toString(s.elements()));
    sa: while(!s.isEmpty()){
      if(s.peek().matches(number)){
        si.push(s.pop());
        //System.out.println("si peek: "+si.peek());
      }else if(s.peek().matches("[Dd]")){
        s.pop();
        String p1 = si.pop();
        String p2 = si.pop();
        si.push(p1);
        si.push(p2);
        si.push(Integer.toString(Integer.parseInt(p1) * Integer.parseInt(p2)));
        System.out.println("Results: "+Integer.toString(Integer.parseInt(p1) * Integer.parseInt(p2)));
        
      }else if(s.peek().matches("[cC]")){
        System.out.println("popped by C: "+si.pop());
        s.pop();
      }else if(s.peek().matches("[Aa]")){
        int t = 0;
        s.pop();
        for(Object a:si.elements()){
          t+=Integer.parseInt((String)a);
          System.out.println("t from a: "+t);
        } 
        
        si.push(Integer.toString(t));
        System.out.println("Si from a: "+ Arrays.toString(si.elements()));
        System.out.println("S from a: "+ Arrays.toString(s.elements()) + " Size: " + s.size());
      }  else {
        System.out.println("Breaking at elements " + s.peek());
        break sa;
      }
    }
    File file = new File("/storage/emulated/0/" + PostfixEvaluation.class.getName() + ".jo");
    try{
      if(file.createNewFile())System.out.println("File created...");
    }catch(IOException fioe){
      System.out.println("File error: " + fioe.getLocalizedMessage());
    }
    try(
      FileOutputStream fout = new FileOutputStream(file);
      ObjectOutputStream out = new ObjectOutputStream(fout)
    ){
        out.writeObject(si);
       // out.writeObject(s);
      } 
      catch(IOException ioe){
        System.out.println("Error says: " + ioe.getMessage());
      }
    System.out.println("S after while: " + Arrays.toString(s.elements()));
    return si;
    
  }

}

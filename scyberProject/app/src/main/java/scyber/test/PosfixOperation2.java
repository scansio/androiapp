package scyber.test;

import scyber.util.*;
import java.util.Arrays;
import java.io.Serializable;

public class PosfixOperation2 implements Serializable{

  private static final long serialVersionUID = 6819288105593937581L;


  public static void main(String[] args) {
    int  j = 0;
    for(Object e:evaluate("5 2 5 * * 3 4 6 - + 10")){
      System.out.println(e);
      j+=Integer.parseInt((String)e);
    }
    System.out.println("Total = " + j);
  }
  public static Object[] evaluate(String e){
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
      }else if(s.peek().matches("[*]")){
        s.pop();
        String p1 = null;
        String p2 = null;
        si.push(p1);
        si.push(p2);
        si.push(Integer.toString(Integer.parseInt(p1) * Integer.parseInt(p2)));
        System.out.println("Results: "+Integer.toString(Integer.parseInt(p1) * Integer.parseInt(p2)));
        
      }else if(s.peek().matches("[-]")){
        s.pop();
        String p1 = si.pop();
        String p2 = si.pop();
        si.push(p1);
        si.push(p2);
        si.push(Integer.toString(Integer.parseInt(p1) - Integer.parseInt(p2)));
        System.out.println("Results: "+Integer.toString(Integer.parseInt(p1) * Integer.parseInt(p2)));
        
      }else if(s.peek().matches("[+]")){
        s.pop();
        String p1 = si.pop();
        String p2 = si.pop();
        si.push(p1);
        si.push(p2);
        si.push(Integer.toString(Integer.parseInt(p1) + Integer.parseInt(p2)));
        System.out.println("Results: "+Integer.toString(Integer.parseInt(p1) * Integer.parseInt(p2)));
        System.out.println("Si from a: "+ Arrays.toString(si.elements()));
        System.out.println("S from a: "+ Arrays.toString(s.elements()) + " Size: " + s.size());
      }  else {
        System.out.println("Breaking at element " + s.peek());
        break sa;
      }
    }
    System.out.println("S after while: " + Arrays.toString(s.elements()));
    return si.elements();
    
  }

}

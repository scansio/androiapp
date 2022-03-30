package scyber.test;

import scyber.util.*;

public class ParenthesisMatch2 {

  public static void main(String[] args) {
    System.out.println(isValid("{[]}([]})"));
  }
  public static boolean isValid(String e){
    CStack<Character> s = new scyber.util.Stack<>(e.length());
    String left = "[\\[\\(\\{]";
    String right = "[\\]\\)\\}]";
    boolean valid = false;
    
    for(String a : e.split("")){
      if(a.matches(left)) System.out.println("Pushed: " + a + " = "+ s.push(a.charAt(0)));
      if(a.matches(right)){
        System.out.println("matched right");
        boolean _true = true;
        
        if(!s.isEmpty()){
          System.out.println("is not empty");
          if((s.peek() == '{' & a.equals("}")) |
            (s.peek() == '(' & a.equals(")")) |
            (s.peek() == '[' & a.equals("]"))) 
              System.out.println(" Match found for " + s.pop());
           else _true = false;
        }
        valid = _true; 
      } break;
    }
    
    return valid ? s.isEmpty() : false;
  }
}

package scyber.test;

import java.util.regex.*;
import scyber.util.*;

public class ParenthesisMatch {

  public static void main(String[] args) {
    System.out.println(isValid("{[]}([]'*z')"));
  }
  public static boolean isValid(String e){
    CStack<Character> s = new scyber.util.Stack<>(e.length());
    char[] i = e.toCharArray();
    String left = "[\\[\\(\\{]";
    String right = "[\\]\\)\\}]";
    boolean valid = false;
    
    for(char a:i){
      if(Pattern.matches(left, Character.toString(a))){
        s. push(a);
      } else if(Pattern.matches(right, Character.toString(a))){
        boolean _true = true;
        
        if(!s.isEmpty()){
          if(s.peek() == '{' & a == '}'){
            s.pop();
          }else if(s.peek() == '(' & a == ')'){
            s.pop();
          }else if(s.peek() == '[' & a == ']'){
            s.pop();
          }else {
            _true = false;
            break;
          }
        }else {
          _true = false;
          break;
        }
        valid = _true; 
      }{}
    }
    
    return valid?s.isEmpty():false;
  }
}

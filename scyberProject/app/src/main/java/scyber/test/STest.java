package scyber.test;

import scyber.util.*;

public class STest {

  public static void main(String[] args) {
    CStack<Integer> s = new Stack<>(300);
    int i = 1;
    while(!s.isFull()){
      s.push(i++);
    }
    System.out.println(s.size());
    while(!s.isEmpty()){
      System.out.println(s.peek());
      System.out.println(s.pop());
    }
    
  }

}

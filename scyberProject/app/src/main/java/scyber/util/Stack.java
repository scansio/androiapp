package scyber.util;

public class Stack<E> extends CStack<E>{
  private final static long serialVersionUID = 123L;
  
  private Stack(){
    super();
  }
  public Stack(int capacity){
    super(capacity);
  }
}

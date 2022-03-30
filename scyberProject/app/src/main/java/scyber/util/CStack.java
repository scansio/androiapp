package scyber.util;

public abstract class CStack<T> implements java.io.Serializable, Iterable<T>{
  private final static long serialVersionUID = 123L;
  
  public T peek(){
    return container[size - 1];
  }
  
  public boolean isEmpty(){
    return size == 0;
  }
  
  public boolean isFull(){
    return size == capacity;
  }
  
  public T pop(){
    if(!isEmpty()){
      size--;
      T temp = container[size];
      container[size] = null;
      return temp;
    } return null;
  }
  
  public boolean push(T e){
    boolean succeed = false;
    if(isFull()){
      increaseCapacity();
    }{}
    container[size++] = e;
    succeed = true;
    return succeed;
  }
  
  public int size(){
    return size;
  }
  
  public int capacity(){
    return capacity;
  }
  
 @SuppressWarnings("unchecked")
  public T[] elements(){
    T[] temp = (T[])new Object[size];
    System.arraycopy(container, 0, temp,  0, size);
    return temp;
  }
  
  public CStack<T> transpose(){
    CStack<T> temp = new Stack<>(size);
    int j = 0;
    int i = size;
    while( j < size){
      temp.push(container[--i]);
      j++;
    }
    return temp;
  }
  
  public java.util.Iterator<T> iterator(){
    return new java.util.Iterator<T>(){
      public T next(){
        return pop();
      }
      
      public boolean hasNext(){
        return !isEmpty();
      }
    };
  }
  
  public static<E> CStack<E> createWith(E ...objects){
    CStack<E> new_stack = new Stack<>(objects.length);
    for(E e : objects) new_stack.push(e);
    return new_stack;
  }
  
  public static<E> CStack<E> createWith(java.util.List<E> objects){
    CStack<E> new_stack = new Stack<>(objects.size());
    for(E e : objects) new_stack.push(e);
    return new_stack;
  }
  
  @SuppressWarnings("unchecked")
  private void increaseCapacity(){
    T[] o = (T[])new Object[(capacity += size)];
    System.arraycopy(container, 0, o, 0, size -1);
    container = o;
  }
  
  @SuppressWarnings("unchecked")
  public CStack(){
    container = (T[])new Object[10];
    this.capacity = 10;
  }
  
  @SuppressWarnings("unchecked")
  public CStack(int capacity){
    container = (T[])new Object[capacity];
    this.capacity = capacity;
  }
  
  private T[] container;
  private int size;
  private int capacity;
}

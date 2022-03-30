package com.summing;

//Note that this program run faster than that of SumN
//The runtime is roughly O(logN) as n gets very large

public class SumN1 implements iSumN{

long result;

public SumN1(){
  
}

public SumN1(long n){
  this.result = this.sumN(n);
}

@Override
public long sumN(long n){
long tmpN = ZERO;

  for (long i = ZERO; i <= n; i++){
     tmpN += i;
  }
  return tmpN;
}

public static void main(String commandLineArgument[]){
iSumN resultOfSummation = new SumN1();
  System.out.println("Result = " + new SumN1(999999999).result);
  System.out.println("Result Init. with non parameter constructor = " + resultOfSummation.sumN(999999999));
}
}

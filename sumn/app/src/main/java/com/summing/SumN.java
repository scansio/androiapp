package com.summing;
/*This class compute the sum of series of 
    n integer in the form 1,2,3,4,5.......n
    Note: the runtime of this program is roughly O(n) as n get bigger
*/
    
public class SumN implements iSumN{

/*This method compute a number 1,2,3.....n
@param n The number of digit to compute 
    in incremention of 1 from 1 to the n
    Note: This method is recursive*/
    
    @Override
    public long sumN(long n){
        long tmpN = 0;
        if(n == ZERO){
            return tmpN;
        }
            else{
                return tmpN += n + sumN(n-1);
            }
    }
    
    public static void main(String[] args) {
        SumN s = new SumN();
        
        //This is the highest i can compute with my phone with 1.3 GigaHertz clock speed
        System.out.println(s.sumN(10099));
    }
}

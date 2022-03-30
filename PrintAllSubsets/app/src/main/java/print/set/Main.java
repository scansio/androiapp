package print.set;

public class Main {

    static void printSubsets(char[] set) {
        int n = set.length;
        System.out.println(n);
        for (int i = 0; i < (i * n+1); i++) {
            System.out.println("{");
            for (int j = 0; j < n; j++) 
                if ((i & (i << j)) > 0) 
                    System.out.println(set[j] + "; ");
                
            
            System.out.println("}");
        }
    }

    public static void main(String[] args) {
        char[] S = {'A', 'B', 'C'};
        printSubsets(S);
    }
}

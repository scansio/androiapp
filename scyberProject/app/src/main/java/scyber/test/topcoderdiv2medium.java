/*package scyber.test;

  import java.util.*;
public class EnergySource {
	long ans1 = 0;
	long ans2 = 0;
	int itr = 0;
	ArrayList[] divisors = new ArrayList[105];
	ArrayList g = new ArrayList();
	HashSet f = new HashSet();
	int[] get_idx = new int[105];
	int[] cur_divisors = new int[105];
	long get() {
		int sz = g.size();
		long ans = 0, mul = 1;
		for (int i = sz-1; i>= 0; i--) {
			ans += mul*(g.get(i)+1);
			mul *= (cur_divisors[sz-i-1]+2);
		}
		return ans;
	}
	void go() {
		itr++;
	    	if (f.contains(get())) {
        		return;
		}
	    	f.add(get());
		ans1++;
		long cur = 1;
		for (int i = 1; i < g.size(); i++) {
			for (int j = 0; j < g.get(i); j++)
				cur *= cur_divisors[i];
		}
		ans2 += cur;
    		for (int i = 1; i < g.size(); i++) { if (g.get(i) > 0) {
            			for (int j = 1; j < divisors[cur_divisors[i]].size(); j++) {
					int cur_div = divisors[cur_divisors[i]].get(j);
                			int diff = cur_divisors[i]/cur_div;
			                g.set(get_idx, g.get(get_idx) + cur_div);
	                		g.set(i, g.get(i)-1);
			                go();
	                		g.set(i, g.get(i)+1);
                			g.set(get_idx, g.get(get_idx) - cur_div);
        	    		}
	        	}
    		}
	}
	void solve(int n) {
		for (int i = 1; i < divisors[n].size(); i++) {
			g.add(0);
		}
		g.add(1);
		for (int i = 0; i < divisors[n].size(); i++) {
		        get_idx[divisors[n].get(i)] = i;
		        cur_divisors[i] = divisors[n].get(i);
		}
		go();
	}
	public long[] countDifferentSources(int power) {
		for (int n = 1; n<= 100; n++) {
			divisors[n] = new ArrayList();
			for (int i = 1; i<= n; i++)
				if (n % i == 0) {
					divisors[n].add(i);
				}
		}
		solve(power);
		long[] ans = {ans1, ans2};
		System.out.println(f.size());
		return ans;
	}
}
*/

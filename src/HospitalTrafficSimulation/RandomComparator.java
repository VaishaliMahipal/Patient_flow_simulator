package HospitalTrafficSimulation;

import java.util.Comparator;

public class RandomComparator implements Comparator<Test> {
	
	public int compare(Test a, Test b){
		{ 	
	    	if(Math.random()<0.5){
	    		return -1;
	    	}
	    	return 1;
	    }
	}
}

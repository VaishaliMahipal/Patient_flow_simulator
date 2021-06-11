package HospitalTrafficSimulation;

import java.util.Comparator;

public class TimeWiseComparator implements Comparator<Test> {
	
	public int compare(Test a, Test b){
		{ 	
	    	
	    	if(a.expectedStartTime <= b.numberInQueue){
	    		return -1;
	    	}
	    	return 1;
	    }
	}
}

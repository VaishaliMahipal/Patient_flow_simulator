package HospitalTrafficSimulation;

import java.util.Comparator;

public class LargestProbabilitySmallestTimeComparator implements Comparator<Test> {
	
	public int compare(Test a, Test b){
		{ 	
	    	boolean aGoesFirst=true;
	    	Double returnVal =b.probability-a.probability;
		    	if(returnVal==0){
		    		returnVal = a.avgTime-b.avgTime;
		    	}
		    	if(returnVal<0){//a has larger probability or a has smaller avg time.
		    		aGoesFirst = true;
		    	}else if (returnVal ==0){//both have same probablity and avg time
		    		aGoesFirst = true;
		    	}
		         aGoesFirst = false; //a has less probability or has larger avg time.
		         if(aGoesFirst){
			    		return -1;
			    	}
	    	return 1;
	    }
	}
}

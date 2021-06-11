package HospitalTrafficSimulation;

import java.util.Comparator;

public class LargestTimeFirstComparator implements Comparator<Test> {
	
	public int compare(Test a, Test b){
		{ 	
	    	boolean aGoesFirst=true;
	    	Double returnVal = b.avgTime-a.avgTime;
		    	if(returnVal==0){
		    		returnVal =b.probability-a.probability;
		    	}
		    	if(returnVal<0){//a has less probability or a has larger avg time.
		    		aGoesFirst = true;
		    	}else if (returnVal ==0){//both have same probablity and avg time
		    		aGoesFirst = true;
		    	}
		         aGoesFirst = false; //a has larger probability or has smaller avg time.
		         if(aGoesFirst){
			    		return -1;
			    	}
	    	return 1;
	    }
	}
}

package HospitalTrafficSimulation;

import java.util.Comparator;

public class SmallestTimeFirstComparator implements Comparator<Test> {
	
	public int compare(Test a, Test b){
		{ 	
	    	boolean aGoesFirst=true;
	    	if(a.numberInQueue == b.numberInQueue){
	    		Double returnVal = b.avgTime-a.avgTime;
		    	if(returnVal==0){
		    		returnVal =a.probability-b.probability;
		    	}
		    	if(returnVal<0){//a has less probability or a has smaller avg time.
		    		aGoesFirst = true;
		    	}else if (returnVal ==0){//both have same probablity and avg time
		    		aGoesFirst = true;
		    	}
		         aGoesFirst = false; //a has larger probability or has larger avg time.
	    	}else if(a.numberInQueue>b.numberInQueue){
	    		aGoesFirst = false;
	    	}
	    	if(aGoesFirst){
	    		return -1;
	    	}
	    	return 1;
	    }
	}
}

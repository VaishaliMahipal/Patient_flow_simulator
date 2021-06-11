package HospitalTrafficSimulation;

import java.util.Comparator;

class ResourceTestComparator implements Comparator<Test> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(Test a, Test b) 
    { 	
    	int retVal=-1;//a goes first
    	Double startTimeDifference = a.expectedStartTime - b.expectedStartTime;
    	if(startTimeDifference>0){
    		retVal= 1;//b goes first
    	}else if(startTimeDifference==0 && a.labTestsRequired){
    		//both can start at the same time, and a needs lab tests. If both need lab test, then we just let b start first.
    		retVal =1;
    		
    	}
    	//A can start first, or both can start at same time and a does not require lab tests.
    	return retVal;
    	
    }

} 
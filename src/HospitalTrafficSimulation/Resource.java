package HospitalTrafficSimulation;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Resource {
	int testId;
	int capacity;
	int unitsAvailable;
	double startTime =0;
	int totalPolledUnits=0;
	LinkedList<Test> testsUsingResource= new LinkedList<>();
	private Comparator<? super Test> testComparator = new ResourceTestComparator();
	PriorityQueue<Test> orderedTestRequests = new PriorityQueue<Test>(10, testComparator);
	int testsForPatientsWithLabTests=0;
	public Resource(int id, int i) {
		this.testId=id;
		this.capacity=i;
		this.unitsAvailable=i;
	}
	public boolean areUnitsAvailable() {
		
		return unitsAvailable>0;
	}
	public boolean takePatient(Test test) {
		// TODO Auto-generated method stub
		if(unitsAvailable>0){
			testsUsingResource.add(test);
			unitsAvailable--;
			if (test.labTestsRequired){
				testsForPatientsWithLabTests++;
			}
			return true;
		}
		return false;
	}
	//returns units available
	public int testDone(Test test){
		boolean removedtest = this.testsUsingResource.remove(test);
		if (removedtest){
				unitsAvailable++;
				if (test.labTestsRequired){
					testsForPatientsWithLabTests--;
				}
		}else{
			return -1;
		}
		return unitsAvailable;
	}
	
	public boolean removeTest(Test testToRemove){
		boolean result = testsUsingResource.remove(testToRemove);
		if (result){
				unitsAvailable++;
				if (testToRemove.labTestsRequired){
					testsForPatientsWithLabTests--;
				}
		}
		return result;
	}
	
	
	public void schedule(Test test) {
		orderedTestRequests.add(test);
		
	}

}


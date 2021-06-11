package HospitalTrafficSimulation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
public class Patient {
	
	Test[] testList;
	boolean labTestsRequired = false;
	boolean inTest = false;
	int id;
	Double expectedTotalTime = new Double(0);
	Double nonLabActualTotalTime = new Double(0);
	Double finalActualTotalTime = null;
	
	private double startTimeOffset;
	private Comparator<? super Test> testComparator = null;
	PriorityQueue<Test> orderedTestRequests ;
	
	public Patient(int id, Test[] testList2, int comparatorId, double startTimeOffset) {
		this.id = id;
		testList=testList2;
		this.setStartTimeOffset(startTimeOffset);
		this.nonLabActualTotalTime=startTimeOffset;
		switch(comparatorId){
			case 1:
				this.testComparator=new LeastProbSmallestTimeComparator();
				break;
			case 2:
				this.testComparator=new LeastProbLargestTimeComparator();
				break;
			case 3:
				this.testComparator=new LargestTimeFirstComparator();
				break;				
			case 4:
				this.testComparator=new RandomComparator();
		}
	//	System.out.println("Using "+ this.testComparator.getClass());
		orderedTestRequests= new PriorityQueue<Test>(10, testComparator);
		//if lab tests are required and the total time for non lab tests is less than lab test then actual time = lab test time.
		//"urine-analysis" id is 3
		if (testList[3]!=null){
			this.labTestsRequired = true;
			this.expectedTotalTime = testList[3].avgTime+this.getStartTimeOffset();
			
		}
		//blood tests id is 0
		if (testList[0]!=null){
			this.labTestsRequired = true;
			//this uses the knowledge that blood tests require longer that urine analysis, this can be made smarter by adding avg time comparison.
			this.expectedTotalTime = testList[0].avgTime+getStartTimeOffset();
		}
	}
	
	
	@Override
	public String toString(){
		String patient = "id: "+id +" finalActualTotalTime "+finalActualTotalTime+" startOffset "+startTimeOffset+" expectedMinimumTotalTime: "+expectedTotalTime+" labTestsRequired: "+labTestsRequired+" nonLabTotalTime "+nonLabActualTotalTime ;
		if (orderedTestRequests!=null && !orderedTestRequests.isEmpty()){
			patient+=" tests:";
			patient+=Arrays.toString(orderedTestRequests.toArray());
			
		}
		return patient;
	}
	
	
	public void sortTests(Test[] pRMatrix, boolean useTestList, Test t){
		
			
			
			Double otherTestsTotalTime = new Double(0);
			
			//this is used only for the patient creation part
			if(useTestList && testList!=null){
				for (int i =0;i<testList.length; i++) {
					if(testList[i]!=null){
						testList[i].labTestsRequired=this.labTestsRequired;
						if(i != 0 && i != 3){ //lab tests are ids 0 and 3
							orderedTestRequests.add(testList[i]);
							
							
						}
						
					}

				}
			}
			if(t!=null){
				this.inTest=false;
			}
			
			
			PriorityQueue<Test> backup= new PriorityQueue<Test>(10, testComparator);
			while(!orderedTestRequests.isEmpty()){
				
				Test test = orderedTestRequests.poll();
			//	System.out.println("Removing test --------------------"+test.id);
				
				if(t==null ||(t!=null && t!=test)){
					test.expectedStartTime=startTimeOffset+otherTestsTotalTime;
					otherTestsTotalTime+=test.avgTime;
					pRMatrix[test.id]=test;
					backup.add(test);
				}
		//		System.out.println("non lab tests total time "+otherTestsTotalTime);
		//		System.out.println(Arrays.toString(pRMatrix));
				
			}
			orderedTestRequests=backup;
			if(otherTestsTotalTime>expectedTotalTime){
				this.expectedTotalTime=otherTestsTotalTime+startTimeOffset;
				}
		
		testList=pRMatrix;
		
	}
	
	public Double getExpectedWaitTime() {
		return expectedTotalTime;
	}
	public void setExpectedWaitTime(Double expectedWaitTime) {
		this.expectedTotalTime = expectedWaitTime;
	}
	public Double getActualWaitTime() {
		return nonLabActualTotalTime;
	}
	public void setActualWaitTime(Double actualWaitTime) {
		this.nonLabActualTotalTime = actualWaitTime;
	}
	public PriorityQueue<Test> getOrderedTestRequests() {
		return orderedTestRequests;
	}
	public void setOrderedTestRequests(PriorityQueue<Test> orderedTestRequests) {
		this.orderedTestRequests = orderedTestRequests;
	}
	public boolean isLabTestsRequired() {
		return labTestsRequired;
	}

	public void setLabTestsRequired(boolean labTestsRequired) {
		this.labTestsRequired = labTestsRequired;
	}


	public double getStartTimeOffset() {
		return startTimeOffset;
	}


	public void setStartTimeOffset(double startTimeOffset) {
		this.startTimeOffset = startTimeOffset;
	}





}

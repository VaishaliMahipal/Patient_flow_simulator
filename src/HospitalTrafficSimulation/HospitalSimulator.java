package HospitalTrafficSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;

public class HospitalSimulator {
	Resource xRayRooms = new Resource(7,2);
	Resource ultrasoundRooms = new Resource(2,2);
	Resource eyeExamEquipment = new Resource(6,5);
	Resource catScanRoom = new Resource(4,2);
	Resource MRIRoom = new Resource(1,1);
	Resource woundCulture = new Resource(5,1);
	Resource[] rooms;
	BufferedReader bfs;
	File file;
	Double[][] testProbs;
	int patientsAtAtime=10;
	int timePeriod=60;
	PriorityQueue<Test> totalTestQ=new PriorityQueue<Test>(10, new TimeWiseComparator());
	public HospitalSimulator( File file, Double[][] testProbs, int patientsAtATime2, int timePeriod2 ){
		try {
			bfs = new BufferedReader(new FileReader(file));
			@SuppressWarnings("unused")
			String line = bfs.readLine();//this is the head line
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.testProbs = testProbs;
		this.patientsAtAtime=patientsAtATime2;
		this.timePeriod=timePeriod2;
		
		rooms = new Resource[8];
		rooms[1]=MRIRoom;
		rooms[2]=ultrasoundRooms;
		rooms[4]=catScanRoom;		
		rooms[5]=woundCulture;
		rooms[6]=eyeExamEquipment;
		rooms[7]=xRayRooms;
	}
	public void skipInitialLines(int lines){
		String line = "";
		for (int i=0; i< lines&&line!=null; i++){
			try {
				line =bfs.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public  int generateAndSchedulePatients(Test[][] PRMatrix, Patient[] patientList, int patientListOffset,  int comparatorId) {			
		int totalTests =0;
		try {
			
			String line = bfs.readLine();	
			
			for(int i=patientListOffset*patientsAtAtime;(i<patientListOffset*patientsAtAtime+patientsAtAtime && line!=null);i++){
				String[] testRequired=line.split(",");
				Patient patient = generatePatientFromFile(testRequired, i,  comparatorId, patientListOffset, rooms);//generatePatient(testProbs, i);
				line = bfs.readLine();
				patient.sortTests(PRMatrix[i], true, null);
				totalTests+=(patient.orderedTestRequests.size());
				System.out.println("Patient "+i+":" +patient.toString());
				for(Test test: patient.testList){
					if (test!=null){
						Resource currentResource = null;
						switch(test.id){//blood tests
							case 1:
								currentResource=MRIRoom;
								break;
							case 2:
								currentResource=ultrasoundRooms;
								break;
							case 4:
								currentResource=catScanRoom;
								break;
												
							case 5:
								currentResource=woundCulture;
								break;	
				
							case 6:
								currentResource=eyeExamEquipment;
								break;				
							case 7:
								currentResource=xRayRooms;
								break;
							default:
									break;
						}
						
						if(currentResource!=null){
							test.numberInQueue = currentResource.orderedTestRequests.size();
							currentResource.schedule(test);
							totalTestQ.add(test);
						}
					}
				}
				
				patientList[i]=patient;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return totalTests;
		
	}
	public void optimizeAndRunSchedule(Patient[] patientList, Test[][] PRMatrix, int totalTests){
		System.out.println("Optimizing test schedule and updating actual time required to perform "+totalTests+" tests");
		//we have the optimal order of tests per patient, now optimize across resources 
		Resource currentResource = null;
		int currentPolledUnit=0;
		for(int i =0; i<totalTests; i++){
			currentResource=null;
			while(currentResource==null){
				currentResource=getCurrentResource(currentPolledUnit, totalTests);
				if (currentResource==null){//no resource for this poll number
					currentPolledUnit++;
					printPRMatrix(PRMatrix);
				}
			}
			//System.out.println("currentPolledUnit "+currentPolledUnit+" currentResource "+currentResource.testId);
			if(currentResource!=null){
				PriorityQueue<Test> currentQ =currentResource.orderedTestRequests;
				
				int capacity = currentResource.capacity;
				
				if(!currentQ.isEmpty()){
					Test t = currentQ.poll();
					currentResource.totalPolledUnits++;
					//if patient can come before scheduled start time, then expected start time=scheduled start time. otherwise, expected start time=when patient can come.
					t.expectedStartTime=(patientList[t.patientId].nonLabActualTotalTime>currentResource.startTime)?patientList[t.patientId].nonLabActualTotalTime:currentResource.startTime;
					
					if(currentResource.testsUsingResource.size()==capacity){						
						Test completedTest = currentResource.testsUsingResource.remove();
						currentResource.startTime += completedTest.getAvgTime();
						if(t.expectedStartTime<currentResource.startTime)
							t.expectedStartTime=currentResource.startTime;
						
					}
					currentResource.testsUsingResource.add(t);
					patientList[t.patientId].sortTests(PRMatrix[t.patientId],false, t);
					
					PRMatrix[t.patientId][t.id]=t;
					if(patientList[t.patientId].nonLabActualTotalTime<t.expectedStartTime){
						patientList[t.patientId].nonLabActualTotalTime=t.expectedStartTime+t.avgTime;
					}else{
						patientList[t.patientId].nonLabActualTotalTime+=t.avgTime;
					}
					
				}
				
				
			}
			
		}
	}

	private void printPRMatrix(Test[][] PRMatrix) {
		System.out.println("Polled number incremented-------------------------------------------------------");
		for(int k=0; k<PRMatrix[0].length; k++){
			String PatientsListForTest="Test id "+k;
			//if lab tests are required and the total time for non lab tests is less than lab test then actual time = lab test time.
			for(int j=0;j<PRMatrix.length;j++){
				
				
				PatientsListForTest+=((PRMatrix[j][k]!=null)?PRMatrix[j][k].toString():"")+" ";
			}
			System.out.println(PatientsListForTest);
		}
		
	}
	private Resource getCurrentResource(int currentPolledUnit, int totalTests) {
		Resource currentResource = null; 
		
		//get resource that is not empty, pick first test from it.
		for(int i =0; i<6&&currentResource==null; i++){
			switch(i){
				case 0://test id 1
					currentResource=MRIRoom;
					break;
				case 1://test id 2
					currentResource=ultrasoundRooms;
					break;
				case 2://4
					currentResource=catScanRoom;
					break;
									
				case 3://5
					currentResource=woundCulture;
					break;		
				case 4://6
					currentResource=eyeExamEquipment;
					break;
				
				case 5://7
					currentResource=xRayRooms;
					break;
				default:
						break;
			}
			if (currentResource.totalPolledUnits!=currentPolledUnit || currentResource.orderedTestRequests.isEmpty()){
				currentResource=null;
			}
		}
		return currentResource;
	}
	public  Patient generatePatientFromFile(String[] testRequired, int id,  int comparatorId, int patientListOffset, Resource[] rooms2) {
		
		//order of items is: Day,Order time,Patient id,sex,age,,blood tests,MRI,ultrasound,urine-analysis,Cat scan,wound culture,eye exam,x-ray
		Test[] testList = new Test[8];
		int csvOffset=6;
		for(int i=csvOffset;i<testRequired.length; i++) {
			boolean isTestNeeded = "1".equals(testRequired[i]);
			int testId=i-csvOffset; //removing file offset
			int numberInQueue=(rooms[testId]!=null)?rooms[testId].orderedTestRequests.size():0;
			if(isTestNeeded){
				testList[testId] = new Test(testId, id, testProbs[testId][0], testProbs[testId][1], numberInQueue);
			}
		}
		Patient patient = new Patient(id, testList, comparatorId, (double)patientListOffset *timePeriod);
		
		return patient;
	}

	public  void generateRandomRun() {
		// TODO Auto-generated method stub
		
	}

	/*public  Patient generatePatient(Double[][] testProbs, int id, int comparatorId,Resource[] rooms2){

		Test[] testList = new Test[8];
		for(int i=0;i<testProbs.length; i++) {
			boolean isTestNeeded = (Math.random()<testProbs[i][0]);
			if(isTestNeeded){
				testList[i] = new Test(i, id, testProbs[i][0], testProbs[i][1], rooms2[i].orderedTestRequests.size());
			}
		}
		Patient patient = new Patient(id, testList, comparatorId, 0);
		
		return patient;
	}*/

}

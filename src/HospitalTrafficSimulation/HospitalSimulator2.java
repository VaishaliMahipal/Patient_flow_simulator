package HospitalTrafficSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class HospitalSimulator2 {
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
	int numberOfPatientSets; 
	int comparatorId;
	PriorityQueue<Test> totalTestQ=new PriorityQueue<Test>(10, new TimeWiseComparator());
	public double result;
	public HospitalSimulator2(Double[][] testProbs, int patientsAtATime2, int timePeriod2, int numberOfPatientSets, int comparatorId, int linesToSkip ){
		this.testProbs = testProbs;
		this.patientsAtAtime=patientsAtATime2;
		this.timePeriod=timePeriod2;
		this.numberOfPatientSets = numberOfPatientSets;
		this.comparatorId=comparatorId;
		rooms = new Resource[8];
		rooms[1]=MRIRoom;
		rooms[2]=ultrasoundRooms;
		rooms[4]=catScanRoom;		
		rooms[5]=woundCulture;
		rooms[6]=eyeExamEquipment;
		rooms[7]=xRayRooms;
		Patient[] patientList = new Patient[numberOfPatientSets*patientsAtAtime];
		Test[][] PRMatrix = new Test[numberOfPatientSets*patientsAtAtime][8];
	//	System.out.println("Generating patients and tests");
		File file = new File("Book1.csv");
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

		skipInitialLines(linesToSkip);
			
		
		
		
		optimizeAndRunSchedule(patientList, PRMatrix);
		//System.out.println("Total time required for patients to finish tests has been calculated. Results are as follows");
		
		printMatrix(PRMatrix);
		result = calculateFinalAverageTime(PRMatrix, patientList);
		
		

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
	public  int generateAndSchedulePatients(Test[][] PRMatrix, Patient[] patientList,  int patientWave) {			
		int totalTests =0;
		try {
			
			String line = bfs.readLine();	
			int patientIdStart=patientWave*patientsAtAtime;
			for(int i=patientIdStart;(i<patientIdStart+patientsAtAtime && line!=null);i++){
				String[] testRequired=line.split(",");
				Patient patient = generatePatientFromFile(testRequired, i,  comparatorId, patientWave, rooms);//generatePatient(testProbs, i);
				line = bfs.readLine();
				patient.sortTests(PRMatrix[i], true, null);
				totalTests+=(patient.orderedTestRequests.size());
				//System.out.println("Patient "+i+":" +patient.toString());
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
	public void optimizeAndRunSchedule(Patient[] patientList, Test[][] PRMatrix){
		
		int totalTests=0;
		int patientWave=0;
		
		System.out.println("Optimizing test schedule and updating actual time required to perform "+totalTests+" tests");
		
		//we have the optimal order of tests per patient, now optimize across resources 
		Resource currentResource = null;
		int testsPerformed=0;
		double timeUnit=2.5; //gcd of all the avg times;
		int timeCounter=0;
		double totalTime=0;
		LinkedList<Test> testsInProgress=new LinkedList<Test>();
		while(testsPerformed<totalTests || patientWave<numberOfPatientSets){
			if(totalTime%timePeriod==0 && patientWave<numberOfPatientSets){
				totalTests+=generateAndSchedulePatients(PRMatrix, patientList, patientWave);
				patientWave++;
			}
			
			for(int i=0;i<testsInProgress.size();i++){
				Test test = testsInProgress.get(i);
				test.totalElapsedTime+=timeUnit;
				if(test.totalElapsedTime>test.avgTime){
					//mark test done, mark patient.inTest=false
					//remove from patient queue
					patientList[test.patientId].sortTests(PRMatrix[test.patientId], false, test);
					
					//remove from resource
					int unitsAvailable = rooms[test.id].testDone(test);
					if(unitsAvailable<0){
						System.out.println("Something is wrong. tried to finish test that wasnt in progress");
					}
					testsInProgress.remove(test);
					testsPerformed++;
					//System.out.println("TEST DONE"+patientList[test.patientId].toString());
				}
				
			}
			int resourcesToSchedule=6;
			while(resourcesToSchedule>0){
				//for Each type of resource
				for(int resourceType=0; resourceType<6;resourceType++){
					currentResource=getCurrentResource(resourceType);
							//for each Free Resource
					if(currentResource.areUnitsAvailable() &&currentResource.orderedTestRequests.size()>0){
						//assign test from queue where patient is available
						Test currentResourceTopTest = currentResource.orderedTestRequests.poll();
						
						if(currentResourceTopTest!=null){
							boolean success=	currentResource.takePatient(currentResourceTopTest);
							if(success){
								currentResourceTopTest.actualStartTime=totalTime;
								testsInProgress.add(currentResourceTopTest);
								patientList[currentResourceTopTest.patientId].inTest=true;
							}else{
								//System.out.println("could not take patient for test");
							}
						}					
							
					}else{
						resourcesToSchedule--;
					}
				
				}
			}
			
			//for each patient, increment wait time & test time
			for(Patient patient: patientList){
				//add wait/test time if there are still pending tests
				if(patient!=null && patient.orderedTestRequests.size()>0){
					patient.nonLabActualTotalTime+=timeUnit;
				}

			}
			//f
			totalTime=timeCounter*timeUnit;
			timeCounter++;
		}
		
	}


	
	private Resource getCurrentResource(int resourceType) {
		Resource currentResource = null; 
		
		//get resource that is not empty, pick first test from it.
		
			switch(resourceType){
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



	private static void printMatrix(Test[][] PRMatrix) {
		for(int k=0; k<PRMatrix[0].length; k++){
			String PatientsListForTest="Test id "+k;
			//if lab tests are required and the total time for non lab tests is less than lab test then actual time = lab test time.
			for(int j=0;j<PRMatrix.length;j++){
				
				
				PatientsListForTest+=((PRMatrix[j][k]!=null)?PRMatrix[j][k].toString():"")+" ";
			}
			System.out.println(PatientsListForTest);
		}
		
	}
	private double calculateFinalAverageTime(Test[][] PRMatrix, Patient[] patientList) {
		double sum = 0;
		for(int i=0; i<PRMatrix.length; i++){
			//if lab tests are required and the total time for non lab tests is less than lab test then actual time = lab test time.
			
			if(patientList[i].isLabTestsRequired() && patientList[i].expectedTotalTime>patientList[i].nonLabActualTotalTime){
			
				patientList[i].finalActualTotalTime=patientList[i].expectedTotalTime - patientList[i].getStartTimeOffset();
			}else{
				patientList[i].finalActualTotalTime=patientList[i].nonLabActualTotalTime- patientList[i].getStartTimeOffset();
			}
		
			System.out.println(patientList[i].toString() + " Test list: "+Arrays.toString(PRMatrix[i]).replace("null", ""));
			sum+=patientList[i].finalActualTotalTime;
			
		}
		for(int i=0; i<PRMatrix[0].length; i++){
			String PatientsListForTest="Test id "+i;
			//if lab tests are required and the total time for non lab tests is less than lab test then actual time = lab test time.
			for(int j=0;j<PRMatrix.length;j++){
				
				PatientsListForTest+=" "+((PRMatrix[j][i]!=null)?PRMatrix[j][i].toString():"-----")+" ";
			}
			System.out.println(PatientsListForTest);
		}
		result=sum/PRMatrix.length;
		System.out.println("average total time: "+result);
		return result;
	}
}

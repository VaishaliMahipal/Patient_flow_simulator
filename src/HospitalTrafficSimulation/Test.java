package HospitalTrafficSimulation;

public class Test{
	
	int id;
	int patientId;
	Double avgTime;
	Double probability;
	Boolean labTestsRequired = false;
	int numberInQueue;
	Double expectedStartTime = new Double(0);
	double actualStartTime=0;
	double totalElapsedTime=0;
	public Test(int testId, int patientId, Double probability, Double avgTime, int numberInQueue) {
		id = testId;
		this.patientId=patientId;
		this.probability = probability;
		this.avgTime=avgTime;
		this.numberInQueue=numberInQueue;
	}
	
	public Double getAvgTime() {
		return avgTime;
	}

	public void setAvgTime(Double avgTime) {
		this.avgTime = avgTime;
	}

	public Double getProbability() {
		return probability;
	}

	public void setProbability(Double probability) {
		this.probability = probability;
	}

	public Boolean getLabTestsRequired() {
		return labTestsRequired;
	}

	public void setLabTestsRequired(Boolean labTestsRequired) {
		this.labTestsRequired = labTestsRequired;
	}


	

	
	public int isThereIsAWait() {
		return numberInQueue;
	}

	public void setThereIsAWait(int thereIsAWait) {
		this.numberInQueue = thereIsAWait;
	}


	public String toString2(){
		String test = "patient "+patientId+" test id: "+this.id+ " expectedStartTime:"+expectedStartTime+"number in Queue"+numberInQueue;
		return test;
	}
	public String toString(){
		return this.patientId+":"+this.actualStartTime;
	}
	
}

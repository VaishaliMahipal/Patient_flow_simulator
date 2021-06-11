package HospitalTrafficSimulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class SimulationController {

	public static void main(String[] args) {
		final float startTime = System.nanoTime();
			
			//read in test probabilities
			Double[][] testProbs = new Double[8][3];
			//these are ordered by duration of test
			testProbs[0] = new Double[]{0.9,57.5, 5.0}; //blood tests
			testProbs[1] = new Double[]{0.001,52.5, 1.0};//MRI
			testProbs[2] = new Double[]{0.5,52.5, 2.0};//ultrasound
			testProbs[3] = new Double[]{0.75,37.5, 5.0};//"urine-analysis"
			testProbs[4] = new Double[]{0.5,22.5, 2.0};//catscan
			testProbs[5]= new Double[]{0.1,17.5, 1.0};//("wound culture",		
			testProbs[6]=  new Double[]{0.1,7.5, 5.0};//"eye exam"
			testProbs[7] = new Double[]{0.4,7.5,2.0};//"x-ray"
	
			
			//if  mri or ultra sound required, those are done first. Order by most time consuming first.
			//if there is wait for most time consuming, bump it one lower. 
			//if two people need same test, the one waiting for blood or urine goes later.
			
	
	
			//Generate n patients (n is input)
			
//			int comparatorId=Integer.parseInt(args[0]);
//			int numberOfPatientSets = Integer.parseInt(args[1]);
//			int linesToSkip= Integer.parseInt(args[2]);
//			int patientsAtATime=Integer.parseInt(args[3]);
//			int timePeriod = Integer.parseInt(args[4]);
			BufferedReader bfs=null;
			
			File file = new File("InputVariables4.csv");
			File result = new File("Results"+System.currentTimeMillis()+".csv");
			String line="";
			try {
				bfs = new BufferedReader(new FileReader(file));
				result.createNewFile();
				line = bfs.readLine();//this is the head line
				BufferedWriter bfw = new BufferedWriter(new FileWriter(result));
				bfw.write(line+"\n");
				
				line = bfs.readLine();
				while(line!=null){
					
					String[] inputs = line.split(",");
					
					HospitalSimulator2 simulator = new HospitalSimulator2(testProbs, Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), Integer.parseInt(inputs[1]),Integer.parseInt(inputs[0]), Integer.parseInt(inputs[2]));
					bfw.write(line+","+simulator.result+"\n");
					//double averaageTime = simulator.run();
					line = bfs.readLine();
					
				}
				bfw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("generic error happened for "+line);
			}
			
			

			final float duration = (System.nanoTime() - startTime)/1000000000;
			System.out.println("running time of program =" + duration+"seconds");

				

	}
	
	

	
}

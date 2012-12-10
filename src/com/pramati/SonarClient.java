package com.pramati;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.sonar.wsclient.Host;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.TimeMachine;
import org.sonar.wsclient.services.TimeMachineCell;
import org.sonar.wsclient.services.TimeMachineQuery;

@Path("SonarWS")
public class SonarClient {
	public final String propertiesFile = "sonar-server-config.properties";
	public String projectKey;
	@GET
	@Produces("text/xml")
	public String retrieveCodeChurnMeasures(){
		Sonar sonar = null;
		TimeMachine t = null;
		TimeMachineCell eachCell = null;
		TimeMachineCell[] allCells = null;
		Object[] allValues = null;
		try{
			//Establish Connection with Sonar Server
			sonar = establishConnectionWithSonar();
			//In order to retrieve metrics of a project,pass the parameters - "Project Key" and "Metric" keys which are needed
			//Time Machine API
			t = sonar.find(TimeMachineQuery.createForMetrics(projectKey, "lines","complexity","class_complexity","file_complexity","statements","classes","files"));
			eachCell = null;
			allCells= t.getCells();
			for(int i=0 ; i<allCells.length ; i++){ // for each Version/Analysis
				eachCell = allCells[i];
				System.out.println(eachCell.getDate()); // Version Date
				allValues = eachCell.getValues();
				for(int j=0 ; j < allValues.length ; j++ ){//for each Metric specified					
					System.out.println(allValues[j]); // Metric Values				
				}			
			}				
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "<Response>Succesfully Parsed JSON Object from SONAR</Response>";
	}
	
	public Sonar establishConnectionWithSonar(){
		//Input is fetched from properties file
		Properties prop = new Properties();
		String sonarServerURL = "";
		String userName = "";
		String pwd = "";
		Sonar sonar = null;
		InputStream inpStream = getClass().getResourceAsStream(propertiesFile);
		try{
			prop.load(inpStream);
			sonarServerURL = prop.getProperty("sonar-server");
			userName = prop.getProperty("sonar-user");
			pwd = prop.getProperty("sonar-pwd");
			projectKey = prop.getProperty("sonar-project-key");
			sonar = new Sonar(new HttpClient4Connector(new Host(sonarServerURL,userName,pwd)));
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}		
		return sonar;
	}
	
}

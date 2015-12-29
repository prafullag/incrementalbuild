package com.incrementalbuild;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.w3c.dom.Document;

/**
 * 
 * @author prafulla.gupta
 *
 */
public class MavenExecutor {
	
	private BuildProperties properties;
		
	public MavenExecutor(BuildProperties properties){
		this.properties = properties;
	}

	public void executeMaven(String pomFilePath) throws Exception{
		System.out.println("Executing maven for : " + pomFilePath);
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setJavaHome(new File(System.getenv("JAVA_HOME")));
		
		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
		request.setOffline(BooleanUtils.isTrue(properties.getIsOffline()));
		
		File pomFile = setPom(pomFilePath, request);
		
		setGoals(request);
		
		setProperties(request);
		
		setProfiles(request);
		
		invoker.execute(request);
		
		copyJarFiles(pomFilePath, pomFile);
	}

	private void copyJarFiles(String pomFilePath, File pomFile) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(pomFile);
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		String artifactId = xpath.compile("/project/artifactId").evaluate(document);
		String version = xpath.compile("/project/version").evaluate(document);
		
		if(StringUtils.isEmpty(version)){
			version = xpath.compile("/project/parent/version").evaluate(document);
		}
		if(version.trim().startsWith("$")){
			String versionExp = version.substring(version.indexOf("{") + 1,version.indexOf("}")).trim();
			if(StringUtils.isNotEmpty(xpath.compile(versionExp).evaluate(document))){
				version = xpath.compile(versionExp).evaluate(document);
			}else{
				String parentPom = xpath.compile("/project/parent/relativePath").evaluate(document);
				File parentPomFile = new File(pomFilePath + "/" + parentPom);
				
				DocumentBuilderFactory dbfParent = DocumentBuilderFactory.newInstance();
				DocumentBuilder dbParent = dbfParent.newDocumentBuilder();
				Document documentParent = dbParent.parse(parentPomFile);
				version = xpath.compile("/project/properties/" + versionExp).evaluate(documentParent);
			}
		}
		
		String jarPath = pomFilePath + "/target/" + artifactId.trim() + "-" + version.trim() + ".jar";
		
		for(String copyPath : properties.getCopyPaths().split(",")){
			System.out.println("Copying " + jarPath + " to " + copyPath); 
			//copy only if file exists so that we are copying to libs who are using that jar
			if(Files.exists(Paths.get(copyPath, artifactId.trim() + "-" + version.trim() + ".jar"))){
				if(SystemUtils.IS_OS_WINDOWS){
					jarPath = jarPath.replaceAll("/", "\\\\");
					copyPath = copyPath.replaceAll("/", "\\\\");
					Runtime.getRuntime().exec("copy /Y " + jarPath + " " + copyPath);
				}else{
					Runtime.getRuntime().exec("cp " + jarPath + " " + copyPath);
				}
			}
		}
	}

	private File setPom(String filePath, InvocationRequest request) {
		if(StringUtils.isNotBlank(properties.getModulePomFile())){
			request.setPomFile(new File(filePath + "/" + properties.getModulePomFile()));
			System.out.println("Executing pom file : " + request.getPomFile().getAbsolutePath());
		}else{
			request.setPomFile(new File(filePath + "/pom.xml"));
		}
		return request.getPomFile();
	}
	
	private void setProperties(InvocationRequest request){
		if(StringUtils.isNotEmpty(properties.getBuildProperties())){
			String[] props = properties.getBuildProperties().split(" ");
			Properties property = new Properties();
			for(String prop : props){
				String[] attr = prop.split(":");
				property.setProperty(attr[0], attr[1]);
			}
			request.setProperties(property);
		}
	}
	
	private void setProfiles(InvocationRequest request){
		if(StringUtils.isNotEmpty(properties.getBuildProfiles())){
			String[] profiles = properties.getBuildProfiles().split(" ");
			List<String> profilesList = new ArrayList<String>();
			for(String profile : profiles){
				profilesList.add(profile);
			}
			request.setProfiles(profilesList);
		}
	}
	
	private void setGoals(InvocationRequest request){
		String[] goals = properties.getBuildGoals().split(" ");
		List<String> goalsList = new ArrayList<String>();
		for(String goal : goals){
			goalsList.add(goal);
		}
		request.setGoals(goalsList);
	}
	
	public static void main(String[] args) throws Exception{
	}
}

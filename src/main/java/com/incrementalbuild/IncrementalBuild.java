package com.incrementalbuild;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Goal which builds incrementally.
 *
 * @goal incremental
 * 
 * @phase compile
 * 
 * @author prafulla.gupta
 */
public class IncrementalBuild extends AbstractMojo {
	
	/**
     * @parameter
     */
	
	private String buildDirectory;
	
	/**
     * @parameter
     */
	private String buildFileName;
	
	/**
     * @parameter
     */
	private String copyPaths;
	
	/**
     * @parameter
     */
	private String buildGoals;
	
	/**
     * @parameter
     */
	private String buildProfiles;
	
	/**
     * @parameter
     */
	private String buildProperties;
	
	/**
     * @parameter
     */
	private String modulePomFile;
	
	/**
     * @parameter
     */
	private Boolean offline;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Starting Incremental Build");
		
		getLog().info("JAVA_HOME:  " +  System.getenv("JAVA_HOME"));
		
		getLog().info("MAVEN_HOME:  " +  System.getenv("MAVEN_HOME"));
		
		getLog().info("Directories will be copied to " +  copyPaths);
		
		getLog().info("Build All pom path " +  buildDirectory);
		
		Path path = Paths.get(buildDirectory, buildFileName);
		
		BuildProperties buildProps = getBuildProperties();
		try{
			
			MavenExecutor executor = new MavenExecutor(buildProps);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(path.toFile());
			
			NodeList nodeList = document.getElementsByTagName("module");
			
			for(int i=0; i<nodeList.getLength(); i++){
				String modulePath = buildDirectory + "/" + nodeList.item(i).getTextContent();
				
				DirectoryChangeDetector changeDetector = new DirectoryChangeDetector(modulePath);
				if(changeDetector.hasChanged()){
					System.out.println("Building module path " + modulePath);
					executor.executeMaven(modulePath);
				}
			}
		}catch(Exception ex){
			getLog().error(ex);
			throw new MojoFailureException("Build Failed", ex);
		}	
	}

	private BuildProperties getBuildProperties() {
		BuildProperties buildProps = new BuildProperties();
		buildProps.setBuildGoals(this.buildGoals);
		buildProps.setBuildProfiles(this.buildProfiles);
		buildProps.setBuildProperties(this.buildProperties);
		buildProps.setCopyPaths(this.copyPaths);
		buildProps.setModulePomFile(this.modulePomFile);
		buildProps.setIsOffline(this.offline);
		return buildProps;
	}

}

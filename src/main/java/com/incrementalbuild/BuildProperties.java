package com.incrementalbuild;

/**
 * 
 * @author prafulla.gupta
 *
 */
public class BuildProperties {

	private String copyPaths;
	
	private String buildGoals;
	
	private String buildProfiles;
	
	private String buildProperties;
	
	private String modulePomFile;
	
	private Boolean isOffline;
	
	private String mavenHome;
	
	private String javaHome;

	public String getCopyPaths() {
		return copyPaths;
	}

	public void setCopyPaths(String copyPaths) {
		this.copyPaths = copyPaths;
	}

	public String getBuildGoals() {
		return buildGoals;
	}

	public void setBuildGoals(String buildGoals) {
		this.buildGoals = buildGoals;
	}

	public String getBuildProfiles() {
		return buildProfiles;
	}

	public void setBuildProfiles(String buildProfiles) {
		this.buildProfiles = buildProfiles;
	}

	public String getBuildProperties() {
		return buildProperties;
	}

	public void setBuildProperties(String buildProperties) {
		this.buildProperties = buildProperties;
	}

	public String getModulePomFile() {
		return modulePomFile;
	}

	public void setModulePomFile(String modulePomFile) {
		this.modulePomFile = modulePomFile;
	}

	public Boolean getIsOffline() {
		return isOffline;
	}

	public void setIsOffline(Boolean isOffline) {
		this.isOffline = isOffline;
	}

	public String getMavenHome() {
		return mavenHome;
	}

	public void setMavenHome(String mavenHome) {
		this.mavenHome = mavenHome;
	}

	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}
}

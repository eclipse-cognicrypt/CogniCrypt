/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.models;

import java.nio.file.Path;

/**
 * @author rajiv
 *
 */
public class ModelAdvancedMode {
	private String nameOfTheTask;	
	private Path locationOfCustomLibrary;
	private Path locationOfClaferFile;
	private Path locationOfXSLFile;
	private Path locationOfJSONFile;
	private boolean isCustomLibraryRequired;
	private boolean isGuidedModeChosen;
	private boolean isGuidedModeForced;
	
	/**
	 * @param nameOfTheTask
	 * @param locationOfCustomLibrary
	 * @param locationOfClaferFile
	 * @param locationOfXSLFile
	 * @param locationOfJSONFile
	 * @param isCustomLibraryRequired
	 * @param isGuidedModeChosen
	 * @param isGuidedModeForced
	 */
	public ModelAdvancedMode(
			String nameOfTheTask, 
			Path locationOfCustomLibrary, 
			Path locationOfClaferFile, 
			Path locationOfXSLFile, 
			Path locationOfJSONFile, 
			boolean isCustomLibraryRequired, 
			boolean isGuidedModeChosen, 
			boolean isGuidedModeForced
			) {
		super();
		this.setNameOfTheTask(nameOfTheTask);
		this.setLocationOfCustomLibrary(locationOfCustomLibrary);
		this.setLocationOfClaferFile(locationOfClaferFile);
		this.setLocationOfXSLFile(locationOfXSLFile);
		this.setLocationOfJSONFile(locationOfJSONFile);
		this.setCustomLibraryRequired(isCustomLibraryRequired);
		this.setGuidedModeChosen(isGuidedModeChosen);
		this.setGuidedModeForced(isGuidedModeForced);
	}




	/**
	 * @return the nameOfTheTask
	 */
	public String getNameOfTheTask() {
		return nameOfTheTask;
	}
	/**
	 * @param nameOfTheTask the nameOfTheTask to set
	 */
	private void setNameOfTheTask(String nameOfTheTask) {
		this.nameOfTheTask = nameOfTheTask;
	}
	/**
	 * @return the locationOfCustomLibrary
	 */
	public Path getLocationOfCustomLibrary() {
		return locationOfCustomLibrary;
	}
	/**
	 * @param locationOfCustomLibrary the locationOfCustomLibrary to set
	 */
	private void setLocationOfCustomLibrary(Path locationOfCustomLibrary) {
		this.locationOfCustomLibrary = locationOfCustomLibrary;
	}
	/**
	 * @return the locationOfClaferFile
	 */
	public Path getLocationOfClaferFile() {
		return locationOfClaferFile;
	}
	/**
	 * @param locationOfClaferFile the locationOfClaferFile to set
	 */
	private void setLocationOfClaferFile(Path locationOfClaferFile) {
		this.locationOfClaferFile = locationOfClaferFile;
	}
	/**
	 * @return the locationOfXSLFile
	 */
	public Path getLocationOfXSLFile() {
		return locationOfXSLFile;
	}
	/**
	 * @param locationOfXSLFile the locationOfXSLFile to set
	 */
	private void setLocationOfXSLFile(Path locationOfXSLFile) {
		this.locationOfXSLFile = locationOfXSLFile;
	}
	/**
	 * @return the locationOfJSONFile
	 */
	public Path getLocationOfJSONFile() {
		return locationOfJSONFile;
	}
	/**
	 * @param locationOfJSONFile the locationOfJSONFile to set
	 */
	private void setLocationOfJSONFile(Path locationOfJSONFile) {
		this.locationOfJSONFile = locationOfJSONFile;
	}
	/**
	 * @return the isGuidedModeChosen
	 */
	public boolean isGuidedModeChosen() {
		return isGuidedModeChosen;
	}
	/**
	 * @param isGuidedModeChosen the isGuidedModeChosen to set
	 */
	private void setGuidedModeChosen(boolean isGuidedModeChosen) {
		this.isGuidedModeChosen = isGuidedModeChosen;
	}
	/**
	 * @return the isGuidedModeForced
	 */
	public boolean isGuidedModeForced() {
		return isGuidedModeForced;
	}
	/**
	 * @param isGuidedModeForced the isGuidedModeForced to set
	 */
	private void setGuidedModeForced(boolean isGuidedModeForced) {
		this.isGuidedModeForced = isGuidedModeForced;
	}

	/**
	 * @return the isCustomLibraryRequired
	 */
	public boolean isCustomLibraryRequired() {
		return isCustomLibraryRequired;
	}

	/**
	 * @param isCustomLibraryRequired the isCustomLibraryRequired to set
	 */
	private void setCustomLibraryRequired(boolean isCustomLibraryRequired) {
		this.isCustomLibraryRequired = isCustomLibraryRequired;
	}
}

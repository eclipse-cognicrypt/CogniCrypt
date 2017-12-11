/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.models;

import java.io.File;

/**
 * @author rajiv
 *
 */
public class ModelAdvancedMode {
	private String nameOfTheTask;	
	private File locationOfCustomLibrary;
	private File locationOfClaferFile;
	private File locationOfXSLFile;
	private File locationOfJSONFile;
	private boolean isCustomLibraryRequired;
	private boolean isGuidedModeChosen;
	//private boolean isGuidedModeForced;
	

	/**
	 * @return the nameOfTheTask
	 */
	public String getNameOfTheTask() {
		return nameOfTheTask;
	}
	/**
	 * @param nameOfTheTask the nameOfTheTask to set
	 */
	public void setNameOfTheTask(String nameOfTheTask) {
		this.nameOfTheTask = nameOfTheTask;
	}
	/**
	 * @return the locationOfCustomLibrary
	 */
	public File getLocationOfCustomLibrary() {
		return locationOfCustomLibrary;
	}
	/**
	 * @param locationOfCustomLibrary the locationOfCustomLibrary to set
	 */
	public void setLocationOfCustomLibrary(File locationOfCustomLibrary) {
		this.locationOfCustomLibrary = locationOfCustomLibrary;
	}
	/**
	 * @return the locationOfClaferFile
	 */
	public File getLocationOfClaferFile() {
		return locationOfClaferFile;
	}
	/**
	 * @param locationOfClaferFile the locationOfClaferFile to set
	 */
	public void setLocationOfClaferFile(File locationOfClaferFile) {
		this.locationOfClaferFile = locationOfClaferFile;
	}
	/**
	 * @return the locationOfXSLFile
	 */
	public File getLocationOfXSLFile() {
		return locationOfXSLFile;
	}
	/**
	 * @param locationOfXSLFile the locationOfXSLFile to set
	 */
	public void setLocationOfXSLFile(File locationOfXSLFile) {
		this.locationOfXSLFile = locationOfXSLFile;
	}
	/**
	 * @return the locationOfJSONFile
	 */
	public File getLocationOfJSONFile() {
		return locationOfJSONFile;
	}
	/**
	 * @param locationOfJSONFile the locationOfJSONFile to set
	 */
	public void setLocationOfJSONFile(File locationOfJSONFile) {
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
	public void setGuidedModeChosen(boolean isGuidedModeChosen) {
		this.isGuidedModeChosen = isGuidedModeChosen;
	}
/*	*//**
	 * @return the isGuidedModeForced
	 *//*
	public boolean isGuidedModeForced() {
		return isGuidedModeForced;
	}
	*//**
	 * @param isGuidedModeForced the isGuidedModeForced to set
	 *//*
	private void setGuidedModeForced(boolean isGuidedModeForced) {
		this.isGuidedModeForced = isGuidedModeForced;
	}*/

	/**
	 * @return the isCustomLibraryRequired
	 */
	public boolean isCustomLibraryRequired() {
		return isCustomLibraryRequired;
	}

	/**
	 * @param isCustomLibraryRequired the isCustomLibraryRequired to set
	 */
	public void setCustomLibraryRequired(boolean isCustomLibraryRequired) {
		this.isCustomLibraryRequired = isCustomLibraryRequired;
	}
}

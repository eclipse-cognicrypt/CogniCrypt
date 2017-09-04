/**
 * 
 */
package crossing.e1.taskintegrator.models;

import java.nio.file.Path;

/**
 * @author rajiv
 *
 */
public class ModelAdvancedMode {
	private String nameOfTheTask;
	private boolean requiresCustomLibrary;
	private Path locationOfCustomLibrary;
	private Path locationOfClaferFile;
	private Path locationOfXSLFile;
	private Path locationOfJSONFile;
	/**
	 * Disabling all the setters for now.
	 * 
	 * @param nameOfTheTask
	 * @param requiresCustomLibrary
	 * @param locationOfCustomLibrary
	 * @param locationOfClaferFile
	 * @param locationOfXSLFile
	 * @param locationOfJSONFile
	 */
	public ModelAdvancedMode(String nameOfTheTask, Path locationOfCustomLibrary, Path locationOfClaferFile, Path locationOfXSLFile, Path locationOfJSONFile) {
		super();
		this.setNameOfTheTask(nameOfTheTask);
		//this.setRequiresCustomLibrary(requiresCustomLibrary);
		this.setLocationOfCustomLibrary(locationOfCustomLibrary);
		this.setLocationOfClaferFile(locationOfClaferFile);
		this.setLocationOfXSLFile(locationOfXSLFile);
		this.setLocationOfJSONFile(locationOfJSONFile);
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
	public void setNameOfTheTask(String nameOfTheTask) {
		this.nameOfTheTask = nameOfTheTask;
	}
	/**
	 * @return the requiresCustomLibrary
	 */
	/*public boolean isRequiresCustomLibrary() {
		return requiresCustomLibrary;
	}
	/**
	 * @param requiresCustomLibrary the requiresCustomLibrary to set
	 */
	/*public void setRequiresCustomLibrary(boolean requiresCustomLibrary) {
		this.requiresCustomLibrary = requiresCustomLibrary;
	}*/
	/**
	 * @return the locationOfCustomLibrary
	 */
	public Path getLocationOfCustomLibrary() {
		return locationOfCustomLibrary;
	}
	/**
	 * @param locationOfCustomLibrary the locationOfCustomLibrary to set
	 */
	public void setLocationOfCustomLibrary(Path locationOfCustomLibrary) {
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
	public void setLocationOfClaferFile(Path locationOfClaferFile) {
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
	public void setLocationOfXSLFile(Path locationOfXSLFile) {
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
	public void setLocationOfJSONFile(Path locationOfJSONFile) {
		this.locationOfJSONFile = locationOfJSONFile;
	}
}

package crossing.e1.configurator.beginner.tasks;

import java.util.HashSet;

public final class TaskUtils {

	public static HashSet<CryptoTask> getAvailableTasks(){
		HashSet<CryptoTask> availableTasks = new HashSet<CryptoTask>();
		availableTasks.add(new SymmEncrTask());
		
		return availableTasks;
	}
}

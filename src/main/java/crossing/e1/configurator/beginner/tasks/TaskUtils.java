package crossing.e1.configurator.beginner.tasks;

import java.util.HashSet;

public final class TaskUtils {

	public static HashSet<CryptoTask> getAvailableTasks(){
		HashSet<CryptoTask> availableTasks = new HashSet<CryptoTask>();
		//TODO: get all subclasses of CryptoTask instead of adding them here
		availableTasks.add(new SymmEncrTask());
		availableTasks.add(new PwdBasedEncryptionTask());
		availableTasks.add(new SecurePwdQuestion());
		return availableTasks;
	}
}

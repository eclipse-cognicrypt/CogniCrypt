package de.cognicrypt.codegenerator.primitive.clafer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PrimitiveClaferGeneratorFile {

	private File file;
	private String path;

	public PrimitiveClaferGeneratorFile(File file, String path) {
		this.file = file;
		this.path=path;
	}

	public File createClaferFile(String source) {
		try {
			if (isNewFileCreated()) {
			FileWriter writer = new FileWriter(file);
			writer.write(source);
			writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public boolean isNewFileCreated() {
		try {
			file = new File(path);
			if (!file.createNewFile()) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	public String getFilePath(){
		return file.getAbsolutePath();
	}

}

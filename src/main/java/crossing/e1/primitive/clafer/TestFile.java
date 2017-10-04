package crossing.e1.primitive.clafer;

import java.io.File;

public class TestFile {

	public static void main(String args[]){
		
		File file = null;
		String path="TESTFILE.txt";
		PrimitiveClaferGenerator claf=new PrimitiveClaferGenerator(file,path);
	
			
			claf.createClaferFile("Helloooooooooooo");
			System.out.println(claf.getFilePath());
		
		
	}
	
}

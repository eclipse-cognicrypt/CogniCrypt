package clafer;

import java.io.File;

import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.clafer.ClaferGenerator;

public class CopyClaferHeaderTest {

	@Test
	public void test() {
		try {
			File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeader, Constants.claferFooter);
			assert (finalClafer.exists());

			File finalClafer1 = ClaferGenerator.copyClaferHeader(Constants.claferHeaderTest, Constants.claferFooter);
			assert (!finalClafer1.exists());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

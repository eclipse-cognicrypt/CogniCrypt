package de.cognicrypt.codegenerator.primitive.clafer;

import java.io.IOException;
import java.io.OutputStream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
public class TestFile{
public static void main(String[] args) {
	 
	

    OutputStream output = new OutputStream() {
        private StringBuilder sb = new StringBuilder();

        @Override
        public void write(int b) throws IOException {
            this.sb.append((char) b);
        }

        @Override
        public String toString() {
            return this.sb.toString();
        }
    };

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    /*
     * The third argument is OutputStream err, where we use our output object
     */
    
   
//    compiler.run(null, null, output, "C:\\Users\\Ahmed\\issues\\CogniCrypt\\plugins\\de.cognicrypt.codegenerator\\src\\main\\resources\\Primitives\\XSL\\TransformedFiles\\AhmedCipher.java");
//    String error = output.toString(); //Compile error get written into String
}
}
package crossing.e1.primitive.clafer;


public class ClaferDetails {
	private String name;
	private String description;
	private String[] mode;
	private String padding;
	private int[] keySize;
	private int blockSize;
	
	public String getName(){
		return this.name;
	}
	public String getDescription(){
		return this.description;
	}
	public String[] getMode(){
		return this.mode;
	}
	public int[] getKeySize(){
		return this.keySize;
	}
	public int getBlockSize(){
		return this.blockSize;
	}
	
}

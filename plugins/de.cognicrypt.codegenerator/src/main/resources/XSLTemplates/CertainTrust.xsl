<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>




<xsl:if test="//task[@description='CertainTrust']">
<xsl:result-document href="CertainTrustUtils.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class CertainTrustUtils {
	public static CertainTrust[] createMultipleCertainTrustObj(int count, int n){
		
		if(count &lt; 1) throw new IllegalArgumentException("count should be greater than 0. Entered n = " + count + "\n");
		
		CertainTrust[] ctObjArray = new CertainTrust[count];
		for(int i = 0; i &lt; ctObjArray.length; i++){
			ctObjArray[i]=new CertainTrust(n);
		}
		return ctObjArray;
	}
	
	public static CertainTrust AND(CertainTrust[] ctObjArray){
		if(ctObjArray.length &lt; 2) throw new IllegalArgumentException("Array need at least 2 CertainTrust objects. Operation not allowed. \n\n");
		
		return ctObjArray[0].AND(Arrays.copyOfRange(ctObjArray, 1, ctObjArray.length));
	}
	
	
	public static CertainTrust OR(CertainTrust[] ctObjArray){
		if(ctObjArray.length &lt; 2) throw new IllegalArgumentException("Array need at least 2 CertainTrust objects. Operation not allowed. \n\n");

		return ctObjArray[0].OR(Arrays.copyOfRange(ctObjArray, 1, ctObjArray.length));
	}
	
	
	public static CertainTrust cFUSION(CertainTrust[] ctObjArray, int[] weights){
		if(ctObjArray.length &lt; 2) throw new IllegalArgumentException("Array need at least 2 CertainTrust objects. Operation not allowed. \n\n");
		if(weights.length != ctObjArray.length) throw new IllegalArgumentException("Different lengths of arrays. Operation not allowed. \n\n");
	
		return CertainTrust.cFusion(ctObjArray, weights);
	}
	
	
	public static CertainTrust wFUSION(CertainTrust[] ctObjArray, int[] weights){
		if(ctObjArray.length &lt; 2) throw new IllegalArgumentException("Array need at least 2 CertainTrust objects. Operation not allowed. \n\n");
		if(weights.length != ctObjArray.length) throw new IllegalArgumentException("Different lengths of arrays. Operation not allowed. \n\n");
	
		return CertainTrust.wFusion(ctObjArray, weights);
	}
}
</xsl:result-document>


<xsl:choose><xsl:when test="//task/code/HTI='true'">
<xsl:result-document href="CertainTrustView.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class CertainTrustView extends JFrame{

	private static final long serialVersionUID = -8391833921510126856L;
	private CertainTrust[] ctObjArray;
	private CertainTrust result;
	
	public CertainTrustView(CertainTrust ctObject) {			
		setTitle("CertainTrust");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());
		this.add(new CertainTrustHTI(ctObject));
		this.pack();
		this.setVisible(true);	
	}
		
	public CertainTrustView(Operator op, CertainTrust[] ctObjArray) {
		
		if(op == null || (op != Operator.AND &amp;&amp; op != Operator.OR)) throw new IllegalArgumentException("Operator have to be AND or OR operator. Operation not allowed. \n");
		
		setLocationRelativeTo(null);
		setTitle("CertainTrust - "+op.name());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setSize(800, 600);
		
		this.ctObjArray = ctObjArray;
		for(int i = 0; i &lt; ctObjArray.length; i++){
			this.add(new CertainTrustHTI(ctObjArray[i]));
		}
		
		this.result = new CertainTrust(ctObjArray[0].getN());
		Map&lt;String,String&gt; htiConfig = new HashMap&lt;String, String&gt;();
		htiConfig.put("readonly", "true");
		add(new CertainTrustHTI(result,htiConfig));
		
		setVisible(true);	
		setObserver(op);
	}
	
	public CertainTrustView(Operator op, CertainTrust[] ctObjArray, int[] weights) {
		
		if(op == null || (op != Operator.wFUSION &amp;&amp; op != Operator.cFUSION)) throw new IllegalArgumentException("Operator have to be wFusion or cFusion operator. Operation not allowed. \n");
		
		setLocationRelativeTo(null);
		setTitle("CertainTrust - "+op.name());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setSize(800, 600);
		
		this.ctObjArray = ctObjArray;
		for(int i = 0; i &lt; ctObjArray.length; i++){
			this.add(new CertainTrustHTI(ctObjArray[i]));
		}
		
		this.result = new CertainTrust(ctObjArray[0].getN());
		Map&lt;String,String&gt; htiConfig = new HashMap&lt;String, String&gt;();
		htiConfig.put("readonly", "true");
		add(new CertainTrustHTI(result,htiConfig));
		
		setVisible(true);	
		setObserver(op, weights);
	}

	
	private void setObserver(Operator op){
		HTIObserver HTIObserver = new HTIObserver(ctObjArray, result, op);
		
		for(int i = 0; i &lt; ctObjArray.length; i++){
			ctObjArray[i].addObserver(HTIObserver);

		}
		HTIObserver.update(null, null);
	}
	
	private void setObserver(Operator op, int[] weights){
		HTIObserver HTIObserver = new HTIObserver(ctObjArray,weights, result, op);
		
		for(int i = 0; i &lt; ctObjArray.length; i++){
			ctObjArray[i].addObserver(HTIObserver);

		}
		HTIObserver.update(null, null);
	}
}
</xsl:result-document>

<xsl:result-document href="HTIObserver.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class HTIObserver implements Observer{

	private CertainTrust[] ctObjArray;
	private CertainTrust tempResult;
	private CertainTrust finaleResult;
	private Operator op;
	private int[] weights;
	
	public HTIObserver(CertainTrust[] ctObjArray, CertainTrust result, Operator op) {	
		this.ctObjArray = ctObjArray;
		this.finaleResult = result;
		this.op = op;
	}
	
	public HTIObserver(CertainTrust[] ctObjArray, int[] weights, CertainTrust result, Operator op) {	
		this.ctObjArray = ctObjArray;
		this.finaleResult = result;
		this.op = op;
		this.weights = weights;
	}

	@Override
	public void update(Observable o, Object arg) {
		switch(op){
		case AND: tempResult = CertainTrustUtils.AND(ctObjArray); break;
		case OR: tempResult = CertainTrustUtils.OR(ctObjArray); break;
		case wFUSION : tempResult = CertainTrustUtils.wFUSION(ctObjArray, weights); break;
		case cFUSION : tempResult =  CertainTrustUtils.cFUSION(ctObjArray, weights); break;
		default: break;
		}
		
		this.finaleResult.setF(tempResult.getF());
		this.finaleResult.setTC(tempResult.getT(), tempResult.getC());
	}
	
}

</xsl:result-document>

<xsl:result-document href="Operator.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
public enum Operator {
	NOT,AND,OR,wFUSION,cFUSION
}
</xsl:result-document>

</xsl:when>
</xsl:choose>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

	public void templateUsage() {
		<xsl:choose>
		<xsl:when test="//task/code/operator='NONE'">
		CertainTrust opinion = new CertainTrust(<xsl:value-of select="//task/code/n"/>);
		
		/*-----------------------
		 opinion.setRS(positive evidence, negative evidence);	&lt;-- set evidence
		 opinion = opinion.NOT();								&lt;-- negate opinion
		 ------------------------*/
		
		double expectation = opinion.getExpectation();
		<xsl:choose>
		<xsl:when test="//task/code/HTI='true'">
    	CertainTrustView view = new CertainTrustView(opinion);
		</xsl:when>
		</xsl:choose>
		</xsl:when>
		
		<xsl:otherwise>
		CertainTrust[] opinions = CertainTrustUtils.createMultipleCertainTrustObj(<xsl:value-of select="//task/code/amountOpinions"/>, <xsl:value-of select="//task/code/n"/>);
		<xsl:choose>
		<xsl:when test="//task/code/operator='wFUSION' or //task/code/operator='cFUSION'">
		int[] weights = new int[<xsl:value-of select="//task/code/amountOpinions"/>];
		</xsl:when>
		</xsl:choose>
		
		/*-----------------------
		  opinions[x].setRS(positive evidence, negative evidence);	&lt;-- set evidence
		  opinions[x] = opinions[x].NOT();							&lt;-- negate opinion
		 ------------------------*/
		
		CertainTrust result = CertainTrustUtils.<xsl:value-of select="//task/code/operator"/>(opinions<xsl:choose><xsl:when test="//task/code/operator='wFUSION' or //task/code/operator='cFUSION'">,weights</xsl:when></xsl:choose>);
		double expectation = result.getExpectation();
		<xsl:choose><xsl:when test="//task/code/HTI='true'">
		CertainTrustView view = new CertainTrustView(Operator.<xsl:value-of select="//task/code/operator"/>, opinions<xsl:choose><xsl:when test="//task/code/operator='wFUSION' or //task/code/operator='cFUSION'">,weights</xsl:when></xsl:choose>);
		</xsl:when></xsl:choose>
		</xsl:otherwise>
		</xsl:choose>
	}
}

</xsl:if>


</xsl:template>

<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>


</xsl:stylesheet>

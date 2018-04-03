<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">
<xsl:variable name="class"><xsl:value-of select="//SymmetricBlockCipher/name" />Cipher</xsl:variable>
<xsl:variable name="OutputFile"><xsl:value-of select="$class" />.java</xsl:variable>
<xsl:result-document href="{$OutputFile}">

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import java.util.Arrays;

public class <xsl:value-of select="$class"></xsl:value-of>  extends CipherSpi {

 @Override
	 protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
	 String[] allowedModes="<xsl:value-of select="SymmetricBlockCipher/mode"></xsl:value-of>".split("\\|");
	 if (!Arrays.asList(allowedModes).contains(mode))
	  throw new NoSuchAlgorithmException();
	 }

	@Override
	 protected void engineSetPadding(String padding) throws NoSuchPaddingException {
	  String[] allowedPaddings="<xsl:value-of select="SymmetricBlockCipher/Padding"></xsl:value-of>".split("\\|");
	 if (!Arrays.asList(allowedPaddings).contains(padding))
	  throw new NoSuchPaddingException();
	 }

	@Override
	 protected int engineGetBlockSize() {
	  return <xsl:value-of select="SymmetricBlockCipher/blocksize" />;
	 }
	 
	@Override
	protected byte[] engineDoFinal(byte[] arg0, int arg1, int arg2) throws IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int engineDoFinal(byte[] arg0, int arg1, int arg2, byte[] arg3, int arg4) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected byte[] engineGetIV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int engineGetOutputSize(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected AlgorithmParameters engineGetParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void engineInit(int arg0, Key arg1, SecureRandom arg2) throws InvalidKeyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void engineInit(int arg0, Key arg1, AlgorithmParameterSpec arg2, SecureRandom arg3) throws InvalidKeyException, InvalidAlgorithmParameterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void engineInit(int arg0, Key arg1, AlgorithmParameters arg2, SecureRandom arg3) throws InvalidKeyException, InvalidAlgorithmParameterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected byte[] engineUpdate(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int engineUpdate(byte[] arg0, int arg1, int arg2, byte[] arg3, int arg4) throws ShortBufferException {
		// TODO Auto-generated method stub
		return 0;
	}
	
 
 
}
</xsl:result-document>

</xsl:template>
</xsl:stylesheet>

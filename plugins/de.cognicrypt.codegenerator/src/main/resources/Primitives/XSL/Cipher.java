


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

public class hiuhCipher extends CipherSpi {

 @Override
 protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
 String[] allowedModes="PCBC".split("||");
 if (!Arrays.asList(allowedModes).contains(mode))
  throw new NoSuchAlgorithmException();
 }

 @Override
 protected void engineSetPadding(String padding) throws NoSuchPaddingException {
  String[] allowedPaddings="PKCS7|ZeroPadding".split("\\|");
 if (!Arrays.asList(allowedPaddings).contains(padding))
  throw new NoSuchPaddingException();
 }
  @Override
 protected int engineGetBlockSize() {
  return 25;
 }
 
 
}


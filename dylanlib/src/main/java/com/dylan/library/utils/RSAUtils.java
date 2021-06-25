package com.dylan.library.utils;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

public class RSAUtils {
	
	public static HashMap<String,Object> generateKey() throws NoSuchAlgorithmException{
		HashMap<String,Object> map=new HashMap<String, Object>();
		KeyPairGenerator keypairGen;
			keypairGen = KeyPairGenerator.getInstance("RSA");
			keypairGen.initialize(1024);
			KeyPair keyPari=keypairGen.generateKeyPair();
			RSAPublicKey publicKey=(RSAPublicKey) keyPari.getPublic();
			RSAPrivateKey privateKey=(RSAPrivateKey) keyPari.getPrivate();
			map.put("public", publicKey);
			map.put("private", privateKey);
			return map;
	}
	
	
	public static void printModulusAndExponnent(HashMap<String,Object> map){
		RSAPublicKey publicKey=(RSAPublicKey) map.get("public");
		RSAPrivateKey privateKey= (RSAPrivateKey) map.get("private");
	    String publicModulus=publicKey.getModulus().toString();
		String publicExponent=publicKey.getPublicExponent().toString();
		System.out.println("publicModulus��"+publicModulus+"\n"+"publicExponent��"+publicExponent);
		String privateModulus=privateKey.getModulus().toString();
		String privateExponent=privateKey.getPrivateExponent().toString();
		System.out.println("privateModulus��"+privateModulus+"\n"+"privateExponent��"+privateExponent);
	}

	public static RSAPublicKey getRSAPublicKey(String modulus,String exponent){
		try {
			BigInteger bim=new BigInteger(modulus);
			BigInteger bie=new BigInteger(exponent);
			KeyFactory keyfactory=KeyFactory.getInstance("RSA");
			RSAPublicKeySpec spec=new RSAPublicKeySpec(bim, bie);
			RSAPublicKey publicKey=(RSAPublicKey) keyfactory.generatePublic(spec);
			return publicKey;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static RSAPrivateKey getRSAPrivateKey(String modulus,String exponent){
		try {
			BigInteger bim=new BigInteger(modulus);
			BigInteger bie=new BigInteger(exponent);
			KeyFactory keyfactory=KeyFactory.getInstance("RSA");
			RSAPrivateKeySpec spec=new RSAPrivateKeySpec(bim, bie);
			RSAPrivateKey privateKey=(RSAPrivateKey) keyfactory.generatePrivate(spec);
			return privateKey;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
/**
 * ��Կ����
 * @param publicKey
 * @param plainText
 * @return
 */
public static byte[] encryptByPublicKey(Key publicKey,String plainText){
	try {
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] input=plainText.getBytes();
		byte[] debyte=cipher.doFinal(input);
		return debyte;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
	
}

/**
 * ˽Կ����
 * @param privateKey
 * @param cipherByte
 * @return
 */
public static String decryptByPrivateKey(Key privateKey,byte[] cipherByte){
	Cipher cipher;
	try {
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryByte=cipher.doFinal(cipherByte);
		return new String(decryByte);
	} catch (Exception e) {
		e.printStackTrace();
	} 
	return null;
}

/**
 * 
private void test(){
	HashMap<String, Object> keyMap;
	try {
		keyMap = RSAUtils.generateKey();
		RSAUtils.printModulusAndExponnent(keyMap);
	    String modulus="106270254936848551221205655193719058068468429493717226668532158476440052462738389423114405381648204214678148443512475862265068090136957827143158839339031628226453966585989573808236669073728779416893240361103313508509293822975245064866380368943670237075571709034786101681928758678475027249399552504762882760063";
		String exponent="65537";
	    RSAPublicKey publicKey=RSAUtils.getRSAPublicKey(modulus, exponent);
		String password="wang123456";
		byte[] cipherByte=RSAUtils.encryptByPublicKey(publicKey,password);
		System.out.println("RSA���ܺ�"+new String(cipherByte));
		String cipherText=new String(Base64.encodeBase64(cipherByte));
		System.out.println("Base64�������ģ�"+cipherText);
		String modulus2="106270254936848551221205655193719058068468429493717226668532158476440052462738389423114405381648204214678148443512475862265068090136957827143158839339031628226453966585989573808236669073728779416893240361103313508509293822975245064866380368943670237075571709034786101681928758678475027249399552504762882760063";
		String exponent2="12605779359431475917323843988525137821753720354672287717185238872634465536190674571239015936599678648166805407630284989444872962337682685326013043273595552397806275576103921419412105938879096450707504945621998296891389047108717923733222608822725550773920710026379090541330598236480994210920993075457610965553";
		RSAPrivateKey privateKey=RSAUtils.getRSAPrivateKey(modulus2, exponent2);
		cipherByte=Base64.decodeBase64(cipherText.getBytes());
		System.out.println("Base64��ԭ���ģ�"+new String(cipherByte));
		System.out.println("RSA���ܺ�"+RSAUtils.decryptByPrivateKey(privateKey, cipherByte));
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	}
}
*/
}

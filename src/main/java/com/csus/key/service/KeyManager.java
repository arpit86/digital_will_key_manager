package com.csus.key.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

public class KeyManager {
	
	private EmailManager emailManager = null;
	
	/*
	 *  This function generate private-public key pair using RSA algorithm for Registered User.
	 */
	public byte[] generateKeyPair(String userEmail) throws NoSuchAlgorithmException {
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
        byte[] privateKey = keyGen.genKeyPair().getPrivate().getEncoded();
        //user.setUser_publicKey(publicKey);
        
        System.out.println("Public key is saved in database and the Private key is emailed to user.");
        // The public-private key is saved to KeyPair folder: <keyType>_<userEmail>
        writeToFile("KeyPair/publicKey_" + userEmail + ".txt", publicKey);
        writeToFile("KeyPair/privateKey_" + userEmail  + ".txt", privateKey);
        
        //Send an email to user with private key to the user email
        emailManager = new EmailManager();
        emailManager.sendEmailContainingThePrivateKey(privateKey, userEmail);
        
        return publicKey;
	}
	
	/*
	 *  This function generate private-public key pair using RSA algorithm for Authorized User.
	 */
	public byte[] generateKeyPairForAuthorizedUser(String userEmail, int willId) throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(512);
		byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
		byte[] privateKey = keyGen.genKeyPair().getPrivate().getEncoded();
		
		System.out.println("Public key is saved in database and the Private key is emailed to user.");
		// The public-private key is saved to KeyPair folder: <keyType>_<userEmail>
		writeToFile("KeyPair/publicKey_" + userEmail + ".txt", publicKey);
		writeToFile("KeyPair/privateKey_" + userEmail + ".txt", privateKey);
			
		// Send an email to user with private key to the user email
		emailManager = new EmailManager();
		emailManager.sendEmailAuthorizeUserToRegister(privateKey, userEmail, willId);
		return publicKey;
	}
	
	/*
	 *  This function generates session key using AES algorithm for the Will owner.
	 */
	public void generateSecretKey(String userEmail) {
    	SecureRandom rnd = new SecureRandom();
        byte [] key = new byte [16];
        rnd.nextBytes(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        writeToFile("SecretKey/symKey_"+ userEmail + ".txt", secretKey.getEncoded());
    }
	
	/*
	 * Obtain the private key from the file uploaded by the user.
	 */
	public PrivateKey getPrivate(String userEmail) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File("KeyPair/privateKey_" + userEmail + ".txt").toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

	/*
	 * Obtain the public key associated with the email of the user.
	 */
    public PublicKey getPublic(String userEmail) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File("KeyPair/publicKey_" + userEmail + ".txt").toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
	
    /*
	 * Obtain the session key associated with the email of the user.
	 */
    public SecretKeySpec getSecretKey(String userEmail) throws IOException{
        byte[] keyBytes = Files.readAllBytes(new File("SecretKey/symKey_"+ userEmail + ".txt").toPath());
        return new SecretKeySpec(keyBytes, "AES");
    }
	
	/*
	 *  This function writes the byte[] data to the file path provided.
	 */
	private void writeToFile(String path, byte[] key) {
		try {
			File f = new File(path);
			f.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(key);
	        fos.flush();
	        fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

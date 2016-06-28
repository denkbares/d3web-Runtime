package de.d3web.io.tests;

import java.io.File;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.utils.Streams;

public class EncryptionTest {

	private static final File original = new File(
			"src/test/resources/kbs/original/MMInfo/MMInfo-Original.jar");
	// "src/test/resources/kbs/original/car/car-Original.jar");

	private static final File enrypted = new File(
			"target/encryptedKBs/MMInfo-Encrypted.jar");

	// "target/encryptedKBs/car-Encrypted.jar");

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
		Cipher cipher = createCipher(Cipher.ENCRYPT_MODE);
		KnowledgeBase base = PersistenceManager.getInstance().load(original);
		PersistenceManager.getInstance(cipher).save(base, enrypted);
	}

	private static Cipher createCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		byte[] keyBytes = "1234123412341234".getBytes(); // example
		final byte[] ivBytes = new byte[] {
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
				0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f }; // example

		final SecretKey key = new SecretKeySpec(keyBytes, "AES");
		final IvParameterSpec IV = new IvParameterSpec(ivBytes);
		final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
		cipher.init(mode, key, IV);
		return cipher;
	}

	@Test
	public void encrypted() throws Exception {
		ZipFile zipFile1 = new ZipFile(original);
		ZipFile zipFile2 = new ZipFile(enrypted);
		byte[] basic1 = Streams.getBytesAndClose(zipFile1.getInputStream(zipFile1.getEntry("kb/basic.xml")));
		byte[] basic2 = Streams.getBytesAndClose(zipFile2.getInputStream(zipFile2.getEntry("kb/basic.xml")));
		Assert.assertFalse("text is not encrypted", Arrays.equals(basic1, basic2));
		// doesn't work in all java implementations (e.g. in windows)
		// Assert.assertTrue("text has not same length", basic1.length ==
		// basic2.length);
		zipFile1.close();
		zipFile2.close();
	}

	@Test
	public void unreadable() throws Exception {
		ZipFile zip = new ZipFile(enrypted);
		String basic = Streams.getTextAndClose(zip.getInputStream(zip.getEntry("kb/basic.xml")));
		Assert.assertFalse("text is not encrypted", basic.contains("KnowledgeBase"));
		Assert.assertFalse("text is not encrypted", basic.contains("Diagnoses"));
		Assert.assertFalse("text is not encrypted", basic.contains("Diagnosis"));
		Assert.assertFalse("text is not encrypted", basic.contains("Children"));
		Assert.assertFalse("text is not encrypted", basic.contains("Child"));
		Assert.assertFalse("text is not encrypted", basic.contains("rootQASet"));
		Assert.assertFalse("text is not encrypted", basic.contains("rootSolution"));
		zip.close();
	}

	@Test
	public void decryptable1() throws Exception {
		Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
		ZipFile zip = new ZipFile(enrypted);
		InputStream in = zip.getInputStream(zip.getEntry("kb/basic.xml"));
		in = new CipherInputStream(in, cipher);
		String basic = Streams.getTextAndClose(in);

		Assert.assertTrue("text can be decrypted", basic.contains("KnowledgeBase"));
		Assert.assertTrue("text can be decrypted", basic.contains("Diagnoses"));
		Assert.assertTrue("text can be decrypted", basic.contains("Diagnosis"));
		Assert.assertTrue("text can be decrypted", basic.contains("Children"));
		Assert.assertTrue("text can be decrypted", basic.contains("Child"));
		Assert.assertTrue("text can be decrypted", basic.contains("rootQASet"));
		Assert.assertTrue("text can be decrypted", basic.contains("rootSolution"));
		zip.close();
	}

	@Test
	public void decryptable2() throws Exception {
		Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
		ZipFile zip = new ZipFile(enrypted);
		InputStream in = zip.getInputStream(zip.getEntry("kb/settings.xml"));
		in = new CipherInputStream(in, cipher);
		String basic = Streams.getTextAndClose(in);

		Assert.assertTrue("text can be decrypted", basic.contains("settings"));
		Assert.assertTrue("text can be decrypted", basic.contains("plugins"));
		Assert.assertTrue("text can be decrypted", basic.contains("psmethods"));
		zip.close();
	}

	@Test
	public void decrypted() throws Exception {
		Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
		KnowledgeBase base1 = PersistenceManager.getInstance().load(original);
		KnowledgeBase base2 = PersistenceManager.getInstance(cipher).load(enrypted);
		Set<?> all1 = new HashSet<Object>(base1.getManager().getAllTerminologyObjects());
		Set<?> all2 = new HashSet<Object>(base2.getManager().getAllTerminologyObjects());
		Assert.assertEquals("disjoint objects", all1, all2);
	}

	@Test
	public void resources() throws Exception {
		ZipFile zip = new ZipFile(original);
		byte[] png1 = Streams.getBytesAndClose(zip.getInputStream(zip.getEntry("multimedia/d3web logo.png")));

		Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
		KnowledgeBase base = PersistenceManager.getInstance(cipher).load(enrypted);
		Resource resource = base.getResource("d3web logo.png");
		byte[] png2 = Streams.getBytesAndClose(resource.getInputStream());

		Assert.assertTrue("corrupted image file", Arrays.equals(png1, png2));

		zip.close();
	}
}

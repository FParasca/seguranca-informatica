import java.io.*;
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CypherManager {
    private String username;
    private char[] password;
    private KeyStore keyStore;

    public CypherManager(String username, String password) throws Exception {
        this.username = username;
        this.password = password.toCharArray();
        loadKeyStore();
    }
    private void loadKeyStore() throws Exception {
        String ksPath = "keystore." + username; 
        FileInputStream kfile = new FileInputStream(ksPath);
        this.keyStore = KeyStore.getInstance("PKCS12");
        this.keyStore.load(kfile, password);
        
    }
    public void encryptFile(String fileName, String targetUser) throws Exception {
    encryptFile(fileName, targetUser,  false);
    }
    public void encryptFile(String fileName, String targetUser , boolean ace ) throws Exception {

        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        SecretKey key = kg.generateKey();

        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);

        FileInputStream fis;
        FileOutputStream fos;
        CipherOutputStream cos;
        String extension = ace ? ".envelope" : ".cifrado";

        fis = new FileInputStream(fileName);
        fos = new FileOutputStream(fileName + extension);
        cos = new CipherOutputStream(fos, c);
        

        byte[] b = new byte[16];  
        int i = fis.read(b);
        while (i != -1) {
            cos.write(b, 0, i);
            i = fis.read(b);
        }
        java.security.cert.Certificate cert = keyStore.getCertificate(targetUser);
        if (cert == null) {
            cos.close();
            fis.close();
            fos.close();
            throw new Exception("Error: Certificate for user '" + targetUser + "' does not exist.");
        }
        Cipher c2 = Cipher.getInstance("RSA");
        c2.init(Cipher.WRAP_MODE, cert);
        byte[] keyCifrada = c2.wrap(key);

        FileOutputStream kos = new FileOutputStream(fileName + ".chave." + targetUser);
        kos.write(keyCifrada);
        kos.close();
        cos.close();
        fis.close();
        fos.close();
    }
    public void signFile(String fileName) throws Exception {

        PrivateKey pkey = (PrivateKey) keyStore.getKey(this.username, this.password);
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(pkey);
        FileInputStream myfile = new FileInputStream(fileName);
        byte[] b = new byte[2048];
        int i = myfile.read(b);
        while(i != -1) {
        	s.update(b, 0 ,i);
        	i = myfile.read(b);
        }
        
        byte[] assinatura = s.sign();
        
        FileOutputStream filesign = new FileOutputStream(fileName + ".assinatura." + this.username);
        filesign.write(assinatura);
        filesign.close();
        myfile.close();
    }
}


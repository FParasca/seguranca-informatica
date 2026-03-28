package mySaudeClient;

import java.util.List;
import java.io.File;
import java.util.ArrayList;

public class terminalHandler {
    private String serverAddr, username, password, target, command;
    private List<String> files;
    private List<String> sendfiles;

    public terminalHandler(String s, String u, String p, String t, String c, List<String> f) {
        this.serverAddr = s;
        this.username = u;
        this.password = p;
        this.target = t;
        this.command = c;
        this.files = f;
        this.sendfiles = new ArrayList<String> ();    
    }
    
   public void execute() {
        switch (command) {
            case "-e":   handleSend(); break;
            case "-c":   handleEncrypt(); break;
            case "-a":   handleSign(); break;
            
            case "-ce": 
                handleEncrypt(); 
                handleSend();
                break;
                
            case "-ae":
                handleSign();
                handleSend();
                break;

            case "-ace":
                handleSign();
                handleEncrypt(true);
                handleSend();
                break;

            case "-r":   handleReceive(); break;
            case "-d":   handleDecrypt(); break;
            case "-v":   handleVerify(); break;

            case "-rd": 
                handleReceive();
                handleDecrypt();
                break;

            case "-rv":
                handleReceive();
                handleVerify();
                break;  
            
            case "-rdv":
                handleReceive();        
                handleDecrypt();
                handleVerify();
                break;
        }
    }
   private void handleSend() {
	    try {
	        System.out.println("1. a entrar no handleSend");

	        serverConnec sc = new serverConnec(serverAddr);
	        System.out.println("2. ligou ao servidor");

	        sc.sendUser(command,username, target);
	        System.out.println("3. enviou user e target");

	        List<String> toSend = sendfiles.isEmpty() ? files : sendfiles;

	        for (String file : toSend) {
	            System.out.println("4. a enviar ficheiro: " + file);
	            sc.sendFile(file);
	        }

	        sc.close();
	        System.out.println("5. terminou envio");

	    } catch (Exception e) {
	        System.err.println("Error during send: " + e.getMessage());
	    }
	}
   private void handleReceive() {
	    try {
	        serverConnec sc = new serverConnec(serverAddr);
	        sc.sendUser(command, username, username);
	        
	        for (String file : files) {
	            File localFile = new File(file);
	            if (localFile.exists()) {
	                System.err.println("Error: ficheiro '" + file + "' já existe localmente");
	                continue;
	            }
	            sc.requestFile(file);
	            sc.receiveFile(file);
	        }
	        
	        sc.close();
	    } catch (Exception e) {
	        System.err.println("Error during receive: " + e.getMessage());
	    }
	}
    private void handleEncrypt() {
        handleEncrypt(false);
    }
    private void handleEncrypt(boolean ace) {
        try {
            cypherManager cm = new cypherManager(username, password);
            for (String file : files) {
                cm.encryptFile(file, target, ace); 
                String extension = ace ? ".envelope" : ".cifrado";
                sendfiles.add(file + extension);
                sendfiles.add(file + ".chave." + target);
            }
        } catch (Exception e) {
            System.err.println("Error during encryption: " + e.getMessage());
        }
    }
    private void handleDecrypt() {
        try {
            cypherManager cm = new cypherManager(username, password);
            for (String file : files) {
                cm.decryptFile(file); 
            }
        } catch (Exception e) {
            System.err.println("Error during decryption: " + e.getMessage());
        }
    }
    private void handleSign() {
        try {
            cypherManager cm = new cypherManager(username, password);
            for (String file : files) {
                cm.signFile(file);
                sendfiles.add(file);
                sendfiles.add(file + ".assinatura." + this.username);
            }
        } catch (Exception e) {
            System.err.println("Error during signing: " + e.getMessage());
        }
    }
    private void handleVerify() {
        try {
            cypherManager cm = new cypherManager(username, password);
            for (String file : files) {
                cm.verifySignature(file, target);
            }
        } catch (Exception e) {
            System.err.println("Error during signature verification: " + e.getMessage());
        }
    }

}    


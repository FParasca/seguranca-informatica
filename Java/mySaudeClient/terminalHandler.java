import java.util.List;

public class TerminalHandler {
    private String serverAddr, username, password, target, command;
    private List<String> files;

    public TerminalHandler(String s, String u, String p, String t, String c, List<String> f) {
        this.serverAddr = s;
        this.username = u;
        this.password = p;
        this.target = t;
        this.command = c;
        this.files = f;
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
    }
    private void handleReceive() {
    }
    private void handleEncrypt() {
        handleEncrypt(false);
    }
    private void handleEncrypt(boolean ace) {
        try {
            CypherManager cm = new CypherManager(username, password);
            for (String file : files) {
                cm.encryptFile(file, target, ace); 
            }
        } catch (Exception e) {
            System.err.println("Error during encryption: " + e.getMessage());
        }
    }
    private void handleDecrypt() {
    }
    private void handleSign() {
        try {
            CypherManager cm = new CypherManager(username, password);
            for (String file : files) {
                cm.signFile(file); 
            }
        } catch (Exception e) {
            System.err.println("Error during signing: " + e.getMessage());
        }
    }
    private void handleVerify() {
    }

}    


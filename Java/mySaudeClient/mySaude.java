package mySaudeClient;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MySaude {
    public static void main(String[] args) {
        String serverAddr = null; 
        String username = null;   
        String password = null;   
        String target = null;     
        String command = null;    
        List<String> files = new ArrayList<>();

        for (int i = 0; i < args.length; i++) { 

            switch (args[i]) {
                case "-s":
                    serverAddr = args[++i]; 
                    break;
                case "-u":
                    username = args[++i]; 
                    break;
                case "-p":
                    password = args[++i];
                    break;
                case "-t":
                    target = args[++i]; 
                    break;
                case "-e": case "-r": case "-c": case "-d": case "-ce": case "-rd": case "-a": case "-v": case "-ae": case "-rv": case "-ace": case "-rdv":
                    command = args[i];
                    while (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        files.add(args[++i]);
                    }
                    break;
            }
        }

        if (username == null || command == null) {
            System.err.println("Error -u or action missing");
            return;
        }

        if (command.contains("e") || command.contains("c") || command.contains("a")) {
            for (String fileName : files) {
                File f = new File(fileName);
                if (!f.exists()) {
                    System.err.println("Error '" + fileName + "' does not exist.");
                }
            }
        }
        TerminalHandler handler = new TerminalHandler(serverAddr, username, password, target, command, files);
        handler.execute();
        //executar o objeto, tava a dar erro sem executar 
    }
}
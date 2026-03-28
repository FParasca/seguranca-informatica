package mySaudeServer;


import java.net.ServerSocket;
import java.net.Socket;

public class mySaudeServer {

    public static void main(String[] args) {
    	
        if (args.length < 1) {
            System.err.println("Error: java mySaudeServer <porto>");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);

            ServerSocket ss = new ServerSocket(port);

            System.out.println("Server running on port " + port);

            while (true) {
                Socket s = ss.accept();

                new clientHandler(s).start();
            }

        } catch (NumberFormatException e) {
            System.err.println("Porto inválido: " + args[0]);
        }
        catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
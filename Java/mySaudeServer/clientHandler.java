package mySaudeServer;
import java.net.Socket;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
public class clientHandler extends Thread {
    private Socket socket;
    public clientHandler(Socket s) {
        this.socket = s;
    }
    public void run() {
        try {
            System.out.println("cliente entrou na thread");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String command = (String) in.readObject();
            String username = (String) in.readObject();
            String target = (String) in.readObject();
            System.out.println("Command: " + command);
            System.out.println("User: " + username);
            System.out.println("Target: " + target);
            if (command.equals("-e") || command.equals("-ce") || command.equals("-ae") || command.equals("-ace")) {
                File userDir = new File("serverFiles/" + target);
                if (!userDir.exists()) {
                    System.err.println("Error: pasta do utilizador '" + target + "' não existe no servidor");
                    return;
                }
                while (true) {
                    String fileName = (String) in.readObject();
                    long fileSize = in.readLong();
                    File destFile = new File("serverFiles/" + target + "/" + fileName);
                    if (destFile.exists()) {
                        System.err.println("Error: ficheiro '" + fileName + "' já existe no servidor");
                        continue;
                    }
                    FileOutputStream fos = new FileOutputStream("serverFiles/" + target + "/" + fileName);
                    byte[] buffer = new byte[2048];
                    int n;
                    while (fileSize > 0) {
                        n = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize));
                        fos.write(buffer, 0, n);
                        fileSize -= n;
                    }
                    fos.close();
                    System.out.println("File received: " + fileName);
                }
            } else if (command.equals("-r") || command.equals("-rd") || command.equals("-rv") || command.equals("-rdv")) {
                while (true) {
                    String fileName = (String) in.readObject();
                    File file = new File("serverFiles/" + username + "/" + fileName);
                    if (!file.exists()) {
                        System.err.println("Error: ficheiro '" + fileName + "' não existe");
                        out.writeBoolean(false);
                        out.flush();
                        continue;
                    }
                    out.writeBoolean(true);
                    out.flush();
                    out.writeLong(file.length());
                    out.flush();
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[2048];
                    int n;
                    while ((n = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                    }
                    out.flush();
                    fis.close();
                    System.out.println("File sent: " + fileName);
                }
            }
        } catch (EOFException e) {
            System.out.println("cliente terminou envio");
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
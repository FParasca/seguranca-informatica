package mySaudeServer;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class clientHandler extends Thread {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public clientHandler(Socket s) {
        this.socket = s;
    }
    @Override
    public void run() {
        try {
            System.out.println("cliente entrou na thread");
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());

            String command = (String) in.readObject();
            String username = (String) in.readObject();
            String target = (String) in.readObject();

            System.out.println("Command: " + command);
            System.out.println("User: " + username);
            System.out.println("Target: " + target);

            switch (command) {
                case "-e":
                    handlein(target, 1);
                    break;
                case "-ce":
                    handlein(target, 2);
                    break;
                case "-ae":
                    handlein(target, 2);
                    break;
                case "-ace":
                    handlein(target, 3); 
                    break;
                case "-r":
                    handlein(username, 1);
                    break;
                case "-rd":
                case "-rv":
                    handleout(username, 2);
                    break;
                case "-rdv":
                    handleout(username, 3);
                    break;
                default:
                    System.err.println("Comando inválido: " + command);
            }
            } catch (EOFException e) {
            System.out.println("cliente terminou a sessão");
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private void handlein(String username, int fileCount) throws Exception {
        File userDir = new File("serverFiles/" + username);
        if (!userDir.exists()) {
            System.err.println("Error: pasta do utilizador '" + username + "' não existe no servidor");

            return;
        }
        for (int i = 0; i < fileCount; i++) {
            String fileName = (String) in.readObject();
            long fileSize = in.readLong();
            File destFile = new File("serverFiles/" + username + "/" + fileName);
            if (destFile.exists()) {
                System.err.println("Error: ficheiro '" + fileName + "' já existe no servidor");
                continue;
            }

            FileOutputStream fos = new FileOutputStream("serverFiles/" + username + "/" + fileName);
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
        }
    
    private void handleout(String username, int fileCount) throws Exception {
        for (int i = 0; i < fileCount; i++) {
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
    }

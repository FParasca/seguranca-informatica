package mySaudeClient;
import java.io.*;
import java.net.Socket;
public class serverConnec {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public serverConnec(String serverAddr) throws Exception {
        String[] parts = serverAddr.split(":");
        this.socket = new Socket(parts[0], Integer.parseInt(parts[1]));
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
    }
    public ObjectOutputStream getOut() { return out; }
    public ObjectInputStream getIn() { return in; }
    public void close() throws Exception {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
    }
    public void sendUser(String command, String user, String targetOrMode) throws IOException {
        out.writeObject(command);
        out.writeObject(user);
        out.writeObject(targetOrMode);
    }
    public void sendFile(String filePath) throws IOException {
        File f = new File(filePath);
        long filesize = f.length();
        out.writeObject(f.getName());
        out.writeLong(filesize);
        FileInputStream fis = new FileInputStream(f);
        byte[] buffer = new byte[2048];
        int n;
        while ((n = fis.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        out.flush();
        fis.close();
    }
    public void receiveFile(String targetPath) throws IOException {
        boolean exists = in.readBoolean();
        if (!exists) {
            System.err.println("Error: ficheiro não existe no servidor");
            return;
        }
        long filesize = in.readLong();
        FileOutputStream fileStream = new FileOutputStream(targetPath);
        byte[] buffer = new byte[2048];
        int n;
        while (filesize > 0) {
            n = in.read(buffer, 0, (int)(filesize > 2048 ? 2048 : filesize));
            fileStream.write(buffer, 0, n);
            filesize -= n;
        }
        fileStream.close();
        System.out.println("Ficheiro recebido: " + targetPath);
    }
    public void requestFile(String fileName) throws IOException {
        out.writeObject(fileName);
        out.flush();
    }
}
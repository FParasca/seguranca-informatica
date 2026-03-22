import java.io.*;
import java.net.Socket;

public class ServerConnec {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ServerConnec(String serverAddr) throws Exception{
        String[] parts = serverAddr.split(":");
        this.socket = new Socket(parts[0], Integer.parseInt(parts[1]));
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }
    public ObjectOutputStream getOut() { return out; }
    public ObjectInputStream getIn() { return in; }

    public void close() throws Exception {
        out.close();
        in.close();
        socket.close();
    }

    public void sendUser(String user, String targetOrMode) throws IOException {
        out.writeObject(user);
        out.writeObject(targetOrMode);
    }
    public void sendFile(String filePath) throws IOException {
        File f = new File(filePath);
        int filesize = (int) f.length();
        out.writeLong(filesize); 
        byte[] buffer = new byte[2048];
        int n = 0;
        while (filesize>0) {
            out.write(buffer, 0, n);
            filesize -= n;
        }
        out.flush();
    }

    public void receiveFile(String targetPath) throws IOException {
        long filesize = in.readLong();
        FileOutputStream fileStream = new FileOutputStream(targetPath);
        byte[] buffer = new byte[2048];
        int n;
        while (filesize>0) {
            n=in.read(buffer, 0, (int)(filesize>2048?2048:filesize));
            fileStream.write(buffer, 0, n);
            filesize-=n;
        }
        fileStream.close();
    }
    

}


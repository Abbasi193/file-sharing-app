import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author ABDULLAH ABBASI
 */
public class Server extends Thread {

   
    private int portNo;
    private String filePath;
    private int fileSize;
    private String fileName;
    private String ipAddress;
    private final int partSize = 256000;
    private int parts = 0;
    private int lastPart = 0;
    private int currentPart = 1;
    private ServerSocket welcomeSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private BufferedReader bufferedReader;
    private BufferedInputStream bufferedInputStream;
    private InputStreamReader inputStreamReader;
    private Socket connectionSocket;
    private boolean running=false; 

    public Server(int portNo,String path)
    {   this.portNo=portNo;
        filePath=path;
    }

    @Override
    public void run(){
        byte[] byteChunkPart;
        int readSize;
        try {
            welcomeSocket = new ServerSocket(portNo);
            running=true;

            while (true) {
                serve();
                recieveInfo();                
                int choice=JOptionPane.showConfirmDialog(null,"Download "+fileName+"("+fileSize+"bytes) from "+ipAddress);
                outputStream.write(choice);
                if (parts > 0&&choice==JOptionPane.YES_OPTION) {

                    for (int i = 1; i <= parts; i++) {
                        readSize=partSize;
                        if (i == parts) {
                            readSize = lastPart;
                        } 
                        if(fileSize<=partSize)
                        {
                            readSize = fileSize ;
                        }
                        byteChunkPart = new byte[readSize];
                        saveFile(recieveFile(byteChunkPart));

                    }
                    merge();
                    break;
                }
            }
            close();
        }  
        /*catch (SocketException ex) {
                JOptionPane.showMessageDialog(null,"Connection Reset\nPlease restart server");
                ex.printStackTrace();
        }*/
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Port Already in use\nPlease select another port");
            //ex.printStackTrace();
        }
        catch (Exception e) {
           // e.printStackTrace();
        }
    }

    public void serve() throws IOException {
        connectionSocket = welcomeSocket.accept();
        inputStream = new DataInputStream(connectionSocket.getInputStream());
        inputStreamReader = new InputStreamReader(inputStream);
        outputStream = new DataOutputStream(connectionSocket.getOutputStream());
        bufferedReader = new BufferedReader(inputStreamReader);
        bufferedInputStream = new BufferedInputStream(inputStream);
        
    }

    public void saveFile(byte[] byteChunkPart) throws FileNotFoundException, IOException {
        
        FileOutputStream filePart = new FileOutputStream(new File(filePath + fileName + ".part" + currentPart));
        filePart.write(byteChunkPart);
        filePart.flush();
        filePart.close();
        filePart = null;
        currentPart++;
        outputStream.write(1);
    }

    public void recieveInfo() throws IOException {
        String info = bufferedReader.readLine();
        currentPart = 1;
        String[] infoArray = info.split("-");
        fileName = infoArray[0];
        fileSize = Integer.parseInt(infoArray[1]);
        parts = fileSize / partSize;
        lastPart = fileSize % partSize;
        ipAddress=connectionSocket.getInetAddress().getHostAddress();
        if (lastPart > 0) {
            parts++;
        }
    }

    public byte[] recieveFile(byte[] byteChunkPart) throws IOException {
        int read;
        read=bufferedInputStream.read(byteChunkPart, 0,byteChunkPart.length);
        
        int current = read;
        while(current<byteChunkPart.length) 
        {
            read = bufferedInputStream.read(byteChunkPart, current, (byteChunkPart.length)-current);
            current += read;
        }
        System.out.println(current);
        return byteChunkPart;
    }

    public void merge() {
        File outputFile = new File(filePath + fileName);
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        byte[] fileBytes;
        try {
            fileOutputStream = new FileOutputStream(outputFile, true);

            for (int i = 1; i <= parts; i++) {

                File file = new File(filePath + fileName + ".part" + i);
                fileInputStream = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                fileInputStream.read(fileBytes, 0, (int) file.length());
                fileOutputStream.write(fileBytes);
                fileOutputStream.flush();

                fileBytes = null;
                fileInputStream.close();
                fileInputStream = null;
                file.delete();
                file = null;
            }
            fileOutputStream.close();
            fileOutputStream = null;
        } catch (Exception exception) {
            //exception.printStackTrace();
        }
    }
    public void close() throws IOException
    {   running=false;
        welcomeSocket.close();
        inputStream.close();
        outputStream.close();
        bufferedReader.close();
        bufferedInputStream.close();
        inputStreamReader.close();
        connectionSocket.close();
    }
    
    public boolean isRunning()
    {
        return running;
    }
 
}

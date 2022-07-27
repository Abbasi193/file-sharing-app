import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author ABDULLAH ABBASI
 */
public class Client {
    private String filePath;
    private int portNo;
    private String ip;
    private String fileName;
    private int fileSize;
    private final int partSize =256000;
    private int parts=0;
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public Client(int portNo,String ip,String filePath) throws IOException
    {   this.portNo=portNo;
        this.filePath=filePath;
        this.ip=ip;
        startApp();
    }
    public void startApp() throws FileNotFoundException, IOException {
        byte[] bytes;
        FileInputStream fileInputStream =openFileStream(filePath);
        if(fileSize==0)
        {
           JOptionPane.showMessageDialog(null,"Empty File"); 
        }
        else if(fileSize>0)
        {
            createClient();
            sendInfo();
            if(inputStream.read()==0)
            {    
                while(fileSize>0)
                {   
                    bytes=getFileByte(fileInputStream);
                    sendFile(bytes);            
                }
//                JOptionPane.showMessageDialog(null,"File Sent Successfully");
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Server refused to accept");
            }
        }
    }
        
    public FileInputStream openFileStream(String fileName) throws FileNotFoundException
    {   File inputFile = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        fileSize = (int)inputFile.length();
        this.fileName=inputFile.getName();
        return fileInputStream;
    }
    public byte[] getFileByte(FileInputStream inputStream) throws IOException {
        int readLength = partSize;
        int read;
        byte[] byteChunkPart;
        if (fileSize <= partSize) {
            readLength = fileSize;
        }
        byteChunkPart = new byte[readLength];
        read = inputStream.read(byteChunkPart, 0, readLength);
        fileSize -= read;
        return byteChunkPart;
    }
    
    public void createClient() throws IOException
    {   
        clientSocket=new Socket(ip,portNo);
        outputStream= new DataOutputStream(clientSocket.getOutputStream());
        inputStream= new DataInputStream(clientSocket.getInputStream());
        JOptionPane.showMessageDialog(null,"Connection Succesfull");
    }
    public void sendFile(byte[] byteChunkPart) throws FileNotFoundException, IOException
    {   
        outputStream.write(byteChunkPart,0,byteChunkPart.length);
        outputStream.flush();
        inputStream.read();       
    }
    public void sendInfo() throws IOException
    {   String info=fileName.replace("-","_")+"-"+fileSize;
        outputStream.writeBytes(info+"\n");   
    }

}

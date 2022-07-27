import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author ABDULLAH ABBASI
 */
public class Front {
    private String fileName;
    private String sharedFilePath = "C:\\Shared";
    private String trackerIP ="localhost";
    private int trackerPortNo=10001;
    private final int partSize =256000;
    private String ipAddress;
    private int port;
    
    public void create()
    {   
        updateTracker();
        JFrame jframe = new JFrame("Client App");
        jframe.setBounds(300,200,570, 370);
        JPanel jPanel =new JPanel();
        JLabel label1 = new JLabel("Port No");
        JLabel label2 = new JLabel("Ip Address");
        JLabel label3 = new JLabel("File");
        JTextField jTextField1 = new JTextField("6789");
        JTextField jTextField2 =new JTextField("localhost");
        JButton jButton1 =new JButton("Choose File");
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                chooseFile();
            }
            });
        JButton jButton2 =new JButton("Send File");
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                
                try {
                    ipAddress=jTextField2.getText();
                    port=Integer.parseInt(jTextField1.getText());
                    if(fileName==null||ipAddress==null)
                    {
                        throw new NullPointerException();
                    }
                    Client client = new Client(port,ipAddress,fileName);
                    client=null;                    
                }
                catch(NumberFormatException e)
                {   
                    JOptionPane.showMessageDialog(null,"Invalid Port Number");
                }
                catch(ConnectException ex)
                {
                    //ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Connection Error\nRestart Server");   
                }
                catch (SocketException ex) {
                    JOptionPane.showMessageDialog(null,"Connection Reset\nPlease restart server");
                    //ex.printStackTrace();
                }
                catch (IOException ex) {
                    //ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Connection Error\nRestart Server");
                }
                catch(NullPointerException e)
                {
                    JOptionPane.showMessageDialog(null,"Please fill the input fields");  
                }
            }
            });
        
        
        jframe.add(jPanel);
        jPanel.add(label1);
        jPanel.add(jTextField1);
        jPanel.add(label2);
        jPanel.add(jTextField2);
        jPanel.add(label3);
        jPanel.add(jButton1);
        jPanel.add(jButton2);
        jframe.setResizable(false);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true);

         jframe.pack();
            
    }
    public void chooseFile()
    {
        JFrame jframe = new JFrame("Client App");
        jframe.setBounds(300,100,570, 370);
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                fileName=jFileChooser.getSelectedFile().getAbsolutePath();
                jframe.dispose();
            }
            });
        
        jframe.add(jFileChooser);
        jframe.setVisible(true);
        
    }
    public static void main(String[] args){
        Front f = new Front();
        f.create();
    }
    public void updateTracker()
    {
        try {
            File folder =new File(sharedFilePath);
            File[] fileList = folder.listFiles();
            for(int i=0;i<fileList.length;i++)
            {   
                Socket clientSocket=new Socket(trackerIP,trackerPortNo);
                DataOutputStream clientoutputStream= new DataOutputStream(clientSocket.getOutputStream());
                int fileSize = (int)fileList[i].length();
                int parts = fileSize / partSize;
                int lastPart = fileSize % partSize;
                if (lastPart > 0) {
                    parts++;
                }
                String query="insert into tracker(IP_ADDRESS,FILE_NAME,FILE_SIZE,CHUNKS) values (\'"
                +InetAddress.getLocalHost().getHostAddress()+"\',\'"+fileList[i].getName()+"\',"+fileSize+","+parts+")";
                clientoutputStream.writeBytes(query+"\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Tracker server not found");
        }
    }
}

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class FrontServer {
    private String fileName;
    private int portNo;
    private Server server;
    
    public void create()
    {  
        JFrame jframe = new JFrame("Server App");
        jframe.setBounds(300,100,570, 370);
        JPanel jPanel =new JPanel();
        JLabel label1 = new JLabel("Port No");
        JLabel label3 = new JLabel("Save to");
        JTextField jTextField1 = new JTextField("6789");
        
        JButton jButton1 =new JButton("Choose Folder");
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                chooseFile();
            }
            });

        JButton jButton2 =new JButton("Start");
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                
                try {
                    portNo=Integer.parseInt(jTextField1.getText());
                    if(fileName==null)
                    {
                        throw new NullPointerException();
                    }
                    server = new Server(portNo,fileName+"\\");
                    server.start(); 
                }
                catch(NumberFormatException e)
                { // e.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Invalid Port Number");
                }                
                              
                catch(NullPointerException e)
                {   //e.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Please fill the input fields");  
                }
            }
            });  
        
        jframe.add(jPanel);
        jPanel.add(label1);
        jPanel.add(jTextField1);
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
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                fileName=jFileChooser.getSelectedFile().getPath();
                jframe.dispose();
            }
            });
        
        jframe.add(jFileChooser);
        jframe.setVisible(true);
        
    }
    public static void main(String[] args) {
        FrontServer f = new FrontServer();
        f.create();
    }
}

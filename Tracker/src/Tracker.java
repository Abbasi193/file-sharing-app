
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Abdullah Abbasi
 */
public class Tracker {
    private JTable jtable;
    public static void main(String[] args) {
        Tracker tracker = new Tracker();
        tracker.frame();
        tracker.serve();
    }
    public void serve()
    {
        try {
            ServerSocket server =new ServerSocket(10001);
            while(true){
                Socket connectionSocket = server.accept();
                DataInputStream inputStream = new DataInputStream(connectionSocket.getInputStream());
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String query=bufferedReader.readLine();
                updateDb(query);
                ((DefaultTableModel)jtable.getModel()).setRowCount(0);
                jtable.setModel(getTableData());
                
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Port Already in use\nPlease select another port");
        }
    }
    public void updateDb(String query) {        
        
        try {
            Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/computer", "software", "hotmail");
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,"Database not connected\nPlease retry");
        }
    }        
    public DefaultTableModel getTableData() {        
        Vector<Vector<String>> myVector = new Vector<Vector<String>>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/computer", "software", "hotmail");
            PreparedStatement statement = connection.prepareStatement("select * from tracker");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Vector v = new Vector<>();
                v.add(result.getString(2));
                v.add(result.getString(3));
                v.add(result.getString(4));
                v.add(result.getString(5));
                myVector.add(v);
            }      
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        Vector<String> v2 = new Vector<String>();
        v2.add("IP_ADDRESS");
        v2.add("FILE_NAME");
        v2.add("FILE_SIZE");
        v2.add("CHUNKS");
        return new DefaultTableModel(myVector, v2);
    }


    public void frame() {
        JFrame jframe = new JFrame("Tracker");
        jframe.setBounds(300, 300, 450, 230);
        jtable =new JTable(getTableData());
        jtable.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(jtable);
        jframe.add(scrollPane);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(false);
        
    }
    
}

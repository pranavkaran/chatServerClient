package ChatServerClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ChatServerClient.ChatStatus;

public class Client {
	
	BufferedReader clientBufferReader;
    PrintWriter clientPrintWriter;
    Socket clientSocket;
	
    String strScreenName;
	JFrame frame = new JFrame(strScreenName);
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    JTextArea userArea = new JTextArea(2, 40);
    
    
    public Client() {
    	// Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        userArea.setEditable(false);
        frame.getContentPane().add(textField, "South");
        frame.getContentPane().add(new JScrollPane(userArea), "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clientPrintWriter.println(textField.getText());
                textField.setText("");
			}
        });
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(frame, 
                    "Are you sure to close this window?", "Really Closing?", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                	clientPrintWriter.println(ChatStatus.SCREEN_NAME_EXIT);
                	JOptionPane.showMessageDialog(frame, "You have exit the chat room");
                    System.exit(0);
                }
            }
        });
        this.frame.setVisible(true);
    }
    
    public void processInput(String input) {
    	String code = input.substring(0,3);
    	switch (code) {
		case ChatStatus.SCREEN_NAME_SUBMIT:
			strScreenName = JOptionPane.showInputDialog(frame,"Enter screen name: ");
	    	frame.setTitle(strScreenName);
	        clientPrintWriter.println(strScreenName);
			break;
	    case ChatStatus.SCREEN_NAME_SUBMIT_OK:
	    	textField.setEditable(true);
	    	JOptionPane.showMessageDialog(frame, "Start chating..");
	    	break;
	    case ChatStatus.MESSAGE_TEXT:
	    	messageArea.append(input.substring(3) + "\n");
	    	break;
	    case ChatStatus.SCREEN_NAME_JOIN:
	    	messageArea.append(input.substring(3) + " joined chat"  + "\n");
	    	break;
	    case ChatStatus.SCREEN_NAME_EXIT:
	    	messageArea.append(input.substring(3) + " exit chat"   + "\n");
	    	break;
	    case ChatStatus.USER_NAME_LIST:
	    	userArea.setText(input.substring(3) + "\n");
	    	break;
	        
		default:
			break;
		}
    }
    

    public void run() throws IOException{
		try {
			String serverAddress = JOptionPane.showInputDialog(frame,
					"Enter IP Address \n" + "port 9001:");
			//String serverAddress = "127.0.0.1";
			clientSocket = new Socket(serverAddress, 9001);
			
			clientBufferReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			clientPrintWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			
			while (true) {
			    processInput(clientBufferReader.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
            } catch (IOException e) {
            }
		}

    }
	
	public static void main(String[] args) throws IOException {
		Client objClient = new Client();
		objClient.run();
	}
}

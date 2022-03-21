import java.awt.*;
import java.io.*;
import java.net.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.*;
import java.util.*;

public class Server extends JFrame {
    private JFrame frame;
    private JTextArea chat_frame;
    private JTextField text;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server_sock; // [SERVER-SOCK-WAIT]
    private Socket client_sock; // [INCOMING-CONN]


    public Server(){ //[CONSTRUCTOR]
        super("Simple GUI ChatServer. ");

        System.out.println("Simple GUI ChatServer. ");
        // [OBJs]
        text = new JTextField();
        text.setEditable(false);
        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                text.setText("");
            }
        });
        add(text, BorderLayout.NORTH);
        chat_frame = new JTextArea();
        add(new JScrollPane(chat_frame));
        setSize(500, 500);
        setVisible(true);
    }
    public void Run_Server(){
        try{
            System.out.println("[+] Initiating Server.. "); // [BINDING]
            server_sock = new ServerSocket(6101, 12);
            while(true){
                try{
                    System.out.println("[!] Listening on port [6101] ");
                    waitForConnection();
                    chatStream();
                    inChat();
                }catch(EOFException eofException){
                    showMessage("\n client disoncceted from sock");
                }finally{ closeSocket(); }
            }
        }catch(IOException ioException){ ioException.printStackTrace(); }
    }


    // [METHODs] --> [sock.conn] --> [Stream (io)] --> [GUI-Display]
    private void waitForConnection() throws IOException { // [LISTENING]
        showMessage("[!] Server Hanging.. --> [No New Connection]");
        System.out.println("[!] Server Hanging.. --> [No New Connection] \n\t" + server_sock.getInetAddress());
        client_sock = server_sock.accept();
        showMessage("[+] Client Connected.. --> [Connection Found!]");
        System.out.println("[+] Client Connected.. --> [Connection Found!]\n\t" + client_sock.getInetAddress().getHostName());
    }
    private void chatStream() throws IOException{ // [Stream (io)] pickelization
        output = new ObjectOutputStream(client_sock.getOutputStream());
        System.out.println("[!] Chat Stream.. [LOG]-->  \n\t" + output);
        showMessage(""+ output);

        output.flush();

        input = new ObjectInputStream(client_sock.getInputStream());
        System.out.println("[!] Chat Stream.. [LOG]-->  \n\t" + input);
        showMessage("\n \t\t [+] Stream has Initiated! \n");
    }

    private void inChat() throws IOException { // [CONVERSATION] --> read/write of IO
        String message = " [+] Connection Initiated!";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) input.readObject(); // [Incoming Pickle to Server]
                sendMessage("\n " + message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n Cannot Parse [Non String Obj Returned] ");
            }
        }while(!message.equals("CLIENT - END")); // [KILL SIGNAL]-- key = END
    }
    private void ableToType(){
        showMessage("\n [ERROR]--> Not Allowed To Type ");
        System.out.println("\n [ERROR]--> Not Allowed To Type ");

    }
    private void sendMessage(String send_message){
        System.out.println("\n [SEND-MSG]--> Sent! ");
        try{
            System.out.println("[+] Server] -->" + send_message);
            output.writeObject("[+] Server] -->" + send_message); // decode
            output.flush();
            showMessage("\n[+] Server] -->" + send_message); // dispalys to sever

        }catch(IOException ioException){
            chat_frame.append("\n [!]: Cannot Process Msg..!  ");
        }
    }
    private void showMessage(final String show_message){ // [UPDATES THE CHAT MAIN-WINDOW]
        System.out.println("\n [SHOW-MSG]--> Sent! ");

        // ASYNC UPDATE FOR UI
        SwingUtilities.invokeLater(
          new Runnable(){ //[CREATES THREAD]<--
              public void run(){
                  chat_frame.append(show_message);
              }
          }
        );
    }
    private void ableToType(final boolean permissions){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        text.setEditable(permissions);
                    }
                });
    }

    private void closeSocket(){
        System.out.println("\n [!]--> [CLOSE-SOCKET]! ");
        showMessage("\n [!]--> [Closing Socket]!");
        ableToType(false);
        try{
            output.close();
            input.close();
            client_sock.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }

    }
}

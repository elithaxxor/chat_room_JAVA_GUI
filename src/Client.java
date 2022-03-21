import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Client extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    public Client(String host) { //[CONSTRUCTOR]
        super("[!] --[Client]--");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                userText.setText("");
            }
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(450, 450);
        setVisible(true);
    }

    // ConnectTo Socket
    public void Start_Client() {
        try {
            Run_Client();
            setIOStream();
            inChat();
        } catch (EOFException eofException) {
            System.out.println("[!] Client Terminated Session [!]");
            showMessage("\n[!] Client Terminated Session [!]");
        } catch (IOException ioException) {
        } finally {
            Close_Socket();
        }
    }

    public void Run_Client() throws IOException {
        connection = new Socket(InetAddress.getByName(serverIP), 6101);
        System.out.println("\n[!] Connecting... " + connection.getInetAddress().getHostName());
        showMessage("\n[!] Connecting... " + connection.getInetAddress().getHostName());

    }

    private void setIOStream() throws IOException {
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());

            showMessage("[+]--> Socket Connected.. ");
            System.out.println("\n[+]--> Socket Connected.. \n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void inChat() throws IOException { // [CONVERSATION] --> read/write of IO
        String message = " [+] Connection Initiated!";
        sendMessage(message);
        ableToType(true);
        do {
            try {
                message = (String) input.readObject(); // [Incoming Pickle to Server]
                sendMessage("\n " + message);
            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\n Cannot Parse [Non String Obj Returned] ");
            }
        } while (!message.equals("CLIENT - END")); // [KILL SIGNAL]-- key = END
    }

    private void ableToType() {
        showMessage("\n [ERROR]--> Not Allowed To Type ");
        System.out.println("\n [ERROR]--> Not Allowed To Type ");

    }

    public void Close_Socket() {
        System.out.println("\n [!]--> [CLOSE-SOCKET]! ");
        showMessage("\n [!]--> [Closing Socket]!");
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void sendMessage(String send_message) {
        System.out.println("\n [SEND-MSG]--> Sent! ");
        try {
            send_message = "+";
            System.out.println("[+] CLIENT] -->" + send_message);
            output.writeObject("[+] Server] -->" + send_message); // decode
            output.flush();
            showMessage("\n[+] Server] -->" + send_message); // dispalys to sever
        } catch (IOException ioException) {
            chatWindow.append("\n [!]: Cannot Process Msg..!  ");
        }
    }
    private void showMessage(final String message) { // [UPDATES THE CHAT MAIN-WINDOW]
        System.out.println("\n [SHOW-MSG]--> Sent! " + message);

        // ASYNC UPDATE FOR UI
        //[CREATES THREAD]<--
        SwingUtilities.invokeLater(
                () -> chatWindow.append(message)
        );
    }


    private void ableToType(final boolean onOff) {
            SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        
                    }

                    public void Run() {
                        userText.setEditable(onOff);
                    }
                });
    }
}



import javax.swing.*;
import javax.swing.JFrame.*;



import javax.swing.JFrame;
import java.io.IOException;

public class ClientTest {
    public static void main(String[] args){
        Client second;
        second = new Client("127.0.0.1");
        second.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            second.Run_Client();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}


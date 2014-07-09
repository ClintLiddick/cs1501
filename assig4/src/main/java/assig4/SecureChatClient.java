package assig4;

/* CS 1501
   Original code: Professor Ramirez, ImprovedChatClient.java
   Secure modification: Clinton Liddick

   Connects to SecureChatServer and securely receives/transmits client messages via
   Add128 or Substitute cipher after RSA handshake.
 */
import java.io.*;
import java.math.BigInteger;
import java.net.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

  public static final int PORT = 8765;

  ObjectInputStream myReader;
  ObjectOutputStream myWriter;
  JTextArea outputArea;
  JLabel prompt;
  JTextField inputField;
  JScrollPane scrollPane;
  JScrollBar scrollBar;
  String myName, serverName;
  Socket connection;

  BigInteger E;
  BigInteger N;
  String cipherName;
  SymCipher symCipher;

  public SecureChatClient ()
  {
    try {
      // get user and connection information
      myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
      serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
      InetAddress addr = InetAddress.getByName(serverName);
      connection = new Socket(addr, PORT);   // Connect to server with new
      // Socket  
      // get output stream from server
      myWriter = new ObjectOutputStream(connection.getOutputStream());
      myWriter.flush();

      // get input stream from server
      myReader = new ObjectInputStream(connection.getInputStream());

      // get RSA public key and public mod from server
      E = (BigInteger) myReader.readObject();
      System.out.println("E received: " + E);
      N = (BigInteger) myReader.readObject();
      System.out.println("N received: " + N);

      // get cipher to use as String object
      cipherName = (String) myReader.readObject();

      // set cipher object
      switch (cipherName) {
      case "Sub":
        symCipher = new Substitute();
        System.out.println("Using Substitute cipher");
        break;
      case "Add":
        symCipher = new Add128();
        System.out.println("Using Add128 cipher");
        break;
      default:
        System.out.println("Unknown cipher");
        System.exit(1);
      }

      // encrypt key with RSA and send as positive BigInteger
      myWriter.writeObject(new BigInteger(1,symCipher.getKey()).modPow(E, N));
      myWriter.flush();

      // end of handshaking

      // cache key
      byte[] byteKey = symCipher.getKey();
      System.out.print("Key: ");
      for (int i = 0; i < byteKey.length; i++)
        System.out.print(byteKey[i] + " ");
      System.out.println();

      this.setTitle(myName);      // Set title to identify chatter

      myWriter.writeObject(symCipher.encode(myName)); // send user name to server
      myWriter.flush();

      Box b = Box.createHorizontalBox();  // Set up graphical environment for
      outputArea = new JTextArea(8, 30);  // user
      outputArea.setEditable(false);
      // wrap long messages at word border
      outputArea.setLineWrap(true);
      outputArea.setWrapStyleWord(true);
      scrollPane = new JScrollPane(outputArea);
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      b.add(scrollPane);
      scrollBar = scrollPane.getVerticalScrollBar();
      outputArea.append("Welcome to the Chat Group, " + myName + "\n");

      inputField = new JTextField("");  // This is where user will type input
      inputField.addActionListener(this);

      prompt = new JLabel("Type your messages below:");
      Container c = getContentPane();

      c.add(b, BorderLayout.NORTH);
      c.add(prompt, BorderLayout.CENTER);
      c.add(inputField, BorderLayout.SOUTH);

      Thread outputThread = new Thread(this);  // Thread is to receive strings
      outputThread.start();                    // from Server

      addWindowListener(
          new WindowAdapter()
          {
            // close window and send "logoff" signal
            public void windowClosing(WindowEvent e)
            { 
              try {
                myWriter.writeObject(symCipher.encode("CLIENT CLOSING"));
                myWriter.flush();
              } catch (IOException ex) {
                System.out.println("Problem closing client!");
              } finally {
                System.exit(0);
              }
            }
          }
          );

      setSize(500, 200);
      setVisible(true);

    }
    catch (Exception e)
    {
      System.out.println("Problem starting client!");
      e.printStackTrace();
    }
  }


  public void run()
  {
    // continually read messages from server
    while (true)
    {
      try {
        byte[] cipher = (byte[]) myReader.readObject();
        String currMsg = symCipher.decode(cipher);
        outputArea.append(currMsg+"\n");
        scrollBar.setValue(scrollBar.getMaximum());  // always scroll to bottom
        System.out.print("Cipher received: ");
        for (int i=0; i<cipher.length; i++)
          System.out.print(cipher[i] + " ");
        System.out.println();
        System.out.print("Decoded Bytes: ");
        byte[] decodedBytes = currMsg.getBytes();
        for (int i=0; i<decodedBytes.length; i++)
          System.out.print(decodedBytes[i] + " ");
        System.out.println();
        System.out.println("Msg received: " + currMsg);
        System.out.println();
      }
      catch (Exception e)
      {
        System.out.println(e +  ", closing client!");
        break;
      }
    }
    System.exit(0);
  }

  // user entered message to send
  public void actionPerformed(ActionEvent e)
  {
    String currMsg = e.getActionCommand();      // Get input value
    inputField.setText("");
    try {
      String msg = myName + ": " + currMsg;
      byte[] cipher = symCipher.encode(msg);
      myWriter.writeObject(cipher);   // Add name and send encrypted message
      myWriter.flush();
      System.out.println("Msg sent: " + msg);
      System.out.print("Cipher sent: ");
      for (int i=0; i<cipher.length; i++)
        System.out.print(cipher[i] + " ");
      System.out.println("\n");
    } catch (IOException ex) {
      outputArea.append("Error sending message\n");
    }
  }                                               

  public static void main(String [] args)
  {
    SecureChatClient JR = new SecureChatClient();
    JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  }
}



package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ArrosageInterface {
    private static final String SERVER_IP = "127.0.0.1"; // Adresse IP du serveur
    public static final int SERVER_PORT = 999; // Port du serveur

    public JFrame frame;
    private JTextField portTextField;
    private JComboBox<String> vanneComboBox;
    private JComboBox<String> ordreComboBox;
    private JTextArea logTextArea;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PortVerificationPage portVerificationPage = new PortVerificationPage();
                    portVerificationPage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ArrosageInterface() {
        initialize();
        connectToServer();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblVanne = new JLabel("Vanne :");
        lblVanne.setBounds(10, 47, 46, 14);
        frame.getContentPane().add(lblVanne);

        vanneComboBox = new JComboBox<String>();
        vanneComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "ALL" }));
        vanneComboBox.setBounds(66, 44, 86, 20);
        frame.getContentPane().add(vanneComboBox);

        JLabel lblOrdre = new JLabel("Ordre :");
        lblOrdre.setBounds(10, 83, 46, 14);
        frame.getContentPane().add(lblOrdre);

        ordreComboBox = new JComboBox<String>();
        ordreComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "ON", "OFF" }));
        ordreComboBox.setBounds(66, 80, 86, 20);
        frame.getContentPane().add(ordreComboBox);

        JButton btnEnvoyer = new JButton("Envoyer");
        btnEnvoyer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String vanne = (String) vanneComboBox.getSelectedItem();
                String ordre = (String) ordreComboBox.getSelectedItem();
                sendCommandToServer("vanne " + vanne + " " + ordre);
            }
        });
        btnEnvoyer.setBounds(10, 118, 89, 23);
        frame.getContentPane().add(btnEnvoyer);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(198, 11, 226, 239);
        frame.getContentPane().add(scrollPane);

        logTextArea = new JTextArea();
        scrollPane.setViewportView(logTextArea);
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Création d'un thread pour recevoir les réponses du serveur
            Thread receiveThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        String response;
                        while ((response = reader.readLine()) != null) {
                            processServerResponse(response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendCommandToServer(String command) {
        writer.println(command);
    }

    private void processServerResponse(String response) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                logTextArea.append(response + "\n");
            }
        });
    }
}

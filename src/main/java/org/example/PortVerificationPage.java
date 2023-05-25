package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PortVerificationPage {
    private JFrame frame;
    private JTextField portTextField;

    public PortVerificationPage() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblPort = new JLabel("Numéro de port :");
        lblPort.setBounds(10, 11, 114, 14);
        frame.getContentPane().add(lblPort);

        portTextField = new JTextField();
        portTextField.setBounds(134, 8, 86, 20);
        frame.getContentPane().add(portTextField);
        portTextField.setColumns(10);

        JButton btnConnexion = new JButton("Connexion");
        btnConnexion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String port = portTextField.getText();
                if (isValidPort(port)) {
                    showControlPage();
                    frame.dispose(); // Fermez la fenêtre de vérification du port
                } else {
                    showErrorAlert("Port incorrect. Veuillez réessayer.");
                }
            }
        });
        btnConnexion.setBounds(10, 40, 114, 23);
        frame.getContentPane().add(btnConnexion);
    }

    private boolean isValidPort(String port) {
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber == ArrosageInterface.SERVER_PORT;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showErrorAlert(String message) {
        JOptionPane.showMessageDialog(frame, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showControlPage() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ArrosageInterface window = new ArrosageInterface();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void show() {
        frame.setVisible(true);
    }
}

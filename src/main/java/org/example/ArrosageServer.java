package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ArrosageServer {
    private static final int SERVER_PORT = 999; // Port du serveur

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Serveur en attente de connexions...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouvelle connexion: " + clientSocket);

                // Création d'un thread pour gérer la connexion client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;
        private BufferedReader reader;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    processCommand(line);
                }

                writer.close();
                reader.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processCommand(String command) {
            String[] parts = command.split(" ");
            if (parts.length >= 2) {
                String vanneNum = parts[1];
                String ordre = parts[2];

                if (vanneNum.equals("ALL")) {
                    // Appliquer l'ordre à toutes les vannes
                    for (int i = 0; i < 4; i++) {
                        String gpio = getGPIO(i);
                        if (gpio != null) {
                            writeToFile(gpio, "out");
                            writeToFile(gpio, ordre.equals("ON") ? "1" : "0");
                        }
                    }
                } else {
                    int vanneIndex = Integer.parseInt(vanneNum);
                    String gpio = getGPIO(vanneIndex);
                    if (gpio != null) {
                        writeToFile(gpio, "out");
                        writeToFile(gpio, ordre.equals("ON") ? "1" : "0");
                    }
                }

                // Envoyer la réponse au client
                if (command.startsWith("vanne")) {
                    writer.println("vanne " + vanneNum + " " + ordre + " OK");
                }
            } else if (parts.length == 3 && parts[2].equals("STATE")) {
                String vanneNum = parts[1];

                int vanneIndex = Integer.parseInt(vanneNum);
                String gpio = getGPIO(vanneIndex);
                if (gpio != null) {
                    writeToFile(gpio, "in");
                    String etat = readFromFile(gpio);
                    writer.println("vanne " + vanneNum + " STATE " + etat);
                }
            }
        }

        private String getGPIO(int vanneIndex) {
            switch (vanneIndex) {
                case 0:
                    return "/sys/class/gpio/gpio11";
                case 1:
                    return "/sys/class/gpio/gpio13";
                case 2:
                    return "/sys/class/gpio/gpio24";
                case 3:
                    return "/sys/class/gpio/gpio27";
                default:
                    return null;
            }
        }

        private void writeToFile(String filePath, String data) {
            try {
                FileWriter fileWriter = new FileWriter(filePath + "/direction");
                fileWriter.write("out");
                fileWriter.close();

                FileWriter valueWriter = new FileWriter(filePath + "/value");
                valueWriter.write(data);
                valueWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String readFromFile(String filePath) {
            try {
                FileReader fileReader = new FileReader(filePath + "/value");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = bufferedReader.readLine();
                bufferedReader.close();
                return line;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}

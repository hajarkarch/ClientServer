package projet;

import java.io.*;
import java.net.*;
import java.util.*;

public class MultiClientServer {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            out.println("Bienvenue ! Veuillez vous connecter.");
            out.println("Entrez votre nom d'utilisateur :");
            String username = in.readLine();
            out.println("Entrez votre mot de passe :");
            String password = in.readLine();

            if (authenticate(username, password)) {
                out.println("Connexion réussie !");
            } else {
                out.println("Échec de la connexion. Nom d'utilisateur ou mot de passe incorrect.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean authenticate(String username, String password) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}


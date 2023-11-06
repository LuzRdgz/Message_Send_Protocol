import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {

    public static final String CLASS_NAME = ConnectionHandler.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
    public static boolean isConnected;
    public static ArrayList<String> lista = new ArrayList<String>();


    private UserManager users;
    private Socket clientSocket = null;

    private BufferedReader input;
    private PrintWriter output;


    public ConnectionHandler(UserManager u, Socket s) {
        users = u;
        clientSocket = s;

        try {
            input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String buffer = null;
        String command;
        while (true) {
            try {
                buffer = input.readLine();
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                e.printStackTrace();
            }
            command = buffer.trim();
            // CONNECT Juan
            if (command.startsWith("CONNECT")) {
                String userName = command.substring(command.indexOf(' ')).trim();
                System.out.println(userName);
                isConnected = users.connect(userName, clientSocket);
                if (isConnected) {
                    output.println("OK");
                    lista.add(userName);
                } else {
                    output.println("FAIL");
                }
            }

            // SEND #<mensaje>@<usuario>
            if (command.startsWith("SEND")) {
                String message = command.substring(command.indexOf('#') + 1,
                        command.indexOf('@'));
                System.out.println(message);
                String userName = command.substring(command.indexOf('@') + 1).trim();
                System.out.println(userName);
                users.send(message);
                //output.println(message);
            }

            // DISCONNECT <usuario>
            if (command.startsWith("DISCONNECT")) {
                try {
                    input.close();
                    output.close();
                    clientSocket.close();
                    lista.remove(0);
                    System.out.println("Desconectado");
                    System.exit(1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                output.println("FAIL");
            }

            // LIST
            if (command.startsWith("LIST")) {
                System.out.println("Lista de Usuarios Conectados");
                for (int i = 0; i < lista.size(); i++) {
                    System.out.println(lista.get(i).toString());
                }

            }
        }



    }


    }


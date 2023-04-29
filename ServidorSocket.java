import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorSocket {
    private static final int PUERTO = 11000;
    private static List<ObjectOutputStream> clientes = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(PUERTO);
        System.out.println("Servidor iniciado en el puerto " + PUERTO);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Nuevo cliente conectado");
            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            clientes.add(salida);
            Thread t = new Thread(new ManejadorCliente(socket, salida));
            t.start();
        }
    }

    private static void broadcast(String mensaje) {
        System.out.println("Mensaje recibido: " + mensaje);
        for (ObjectOutputStream cliente : clientes) {
            try {
                cliente.writeObject(mensaje);
            } catch (IOException e) {
                // Si hay un error al escribir en un ObjectOutputStream, suprimimos la excepción
                // y continuamos con el siguiente ObjectOutputStream.
            }
        }
    }

    private static class ManejadorCliente implements Runnable {
        private Socket socket;
        private ObjectOutputStream salida;
        private ObjectInputStream entrada;

        public ManejadorCliente(Socket socket, ObjectOutputStream salida) throws IOException {
            this.socket = socket;
            this.salida = salida;
            this.entrada = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            try {
                String nombreCliente = (String) entrada.readObject();
                System.out.println(nombreCliente + " se ha unido al chat.");
                while (true) {
                    String mensaje = (String) entrada.readObject();
                    if (mensaje.equalsIgnoreCase("exit")) {
                        System.out.println(nombreCliente + " se ha desconectado.");
                        clientes.remove(salida);
                        broadcast(nombreCliente + " se ha desconectado.");
                        break;
                    }
                    broadcast(nombreCliente + " = " + mensaje);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Cliente desconectado");
                clientes.remove(salida);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Si hay un error al cerrar el socket, suprimimos la excepción.
                }
            }
        }
    }
}
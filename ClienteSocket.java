import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteSocket {
    
    private static final String DNSAWS = "ec2-44-213-65-61.compute-1.amazonaws.com";
    private static final int PUERTO = 11000;

    private static boolean isRunning = true;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(DNSAWS, PUERTO);
        System.out.println("Conexión establecida con el servidor");

        ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su nombre de usuario: ");
        String nombreUsuario = scanner.nextLine();
        salida.writeObject(nombreUsuario);
        System.out.println(nombreUsuario + ", estás en el chat");

        Thread t = new Thread(new ManejadorServidor(entrada));
        t.start();

        while (isRunning) {
            String mensaje = scanner.nextLine();
            salida.writeObject(mensaje);
        }
    }

    private static class ManejadorServidor implements Runnable {
        
        private ObjectInputStream entrada;

        public ManejadorServidor(ObjectInputStream entrada) {
            this.entrada = entrada;
        }
    
        @Override
        public void run() {
            try {
                while (true) {
                    String mensaje = (String) entrada.readObject();
                    System.out.println(mensaje);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Te desconectaste satisfactoriamente");
            }
        }
    }
}

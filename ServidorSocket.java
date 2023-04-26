import java.io.*;
import java.net.*;

public class ServidorSocket {
    
   // Declaro un int que contendrá el puerto a usar
    private static final int PORT = 11000;
    
    // Declaro una lista de sockets donde se irán metiendo los clientes
    private static List<Socket> clients = new ArrayList<>();

    // Declaro el main que lanza una IOException
    public static void main(String[] args) throws IOException {
        
        // Declaro un ServerSocket el cual es construido mediante el puerto
        ServerSocket serverSocket = new ServerSocket(PORT);
        // Lanzo mensaje para que quede claro desde consola el puerto a usar
        System.out.println("Server iniciado y escuchando en el puerto " + PORT);

        // Declaro bucle infinito para aceptar clientes y añadirlos a la lista conforme vayan conectandose
        while (true) {
            
            // Declaro un Socket y donde estara el cliente aceptado
            Socket clientSocket = serverSocket.accept();
            
            // Añado ese mismo socket a la lista de clientes
            clients.add(clientSocket);
            
            // Declaro un hilo construido con mi clase estatica creada, la cual necesita un socket
            Thread thread = new Thread(new ClientHandler(clientSocket));
            
            // Lanzo el hilo
            thread.start();
            // Lanzo mensaje de confirmacion de conexion y el total de clientes conectados hasta el momento
            System.out.println("Nuevo cliente conectado. Total de clientes: " + clients.size());
        }
    }

    // Declaro una clase para implementar la interfaz Runnable y que maneje un cliente por hilo
    static class ClientHandler implements Runnable {
        
        // Declaro el Socket cliente
        private Socket clientSocket;
        
        // Declaro objeto stream de entrada
        private ObjectInputStream entrada;
        
        // Declaro objeto stream de salida
        private ObjectOutputStream eixida;

        // Constructor de clase que necesita de un socket cliente, y lanza una IOException
        public ClientHandler(Socket clientSocket) throws IOException {
            
            // Asigno socket cliente pasado como parametro al constructor
            this.clientSocket = clientSocket;
            
            // Asigno el objeto stream de entrada de ese mismo socket
            entrada = new ObjectInputStream(clientSocket.getInputStream());
            
            // Asigno el objeto stream de salida de ese mismo socket
            eixida = new ObjectOutputStream(clientSocket.getOutputStream());
            
        }

        // Declaro metodo run el cual ejecutará cada hilo, un hilo por cliente
        public void run() {
            
            try {
                
                while (true) {
                    
                    // Declaro un String con la frase del cliente y le asigno el texto que venga por el objeto stream de entrada
                    String FraseCliente = (String) entrada.readObject();
                    
                    // Muestro por consola la dirección del cliente emisor y el mensaje recibido por el mismo
                    System.out.println("La frase recibida del cliente " + clientSocket.getInetAddress() + " es: " + FraseClient);

                    // Declaro una variable y le asigno ese mismo mensaje recibido pero lo paso a mayusculas todo
                    String FraseMajuscules = FraseClient.toUpperCase();
                    
                    // Muestro por consola el mensaje pasado a mayusculas
                    System.out.println("El server devuelve la frase en mayúsculas: " + FraseMajuscules);
                    
                    // Y envio el mensaje a todos mediante el método enviarMensajeATodos(String mensaje)
                    enviarMensajeATodos(FraseMajuscules);
                    
                }
                
            } catch (IOException | ClassNotFoundException e) {
                
                e.printStackTrace();
                
            } finally {
                
                try {
                    
                    // Cierro el socket cliente
                    clientSocket.close();
                    
                    // Lo quito de la lista de clientes conectados
                    clients.remove(clientSocket);
                    
                    // Mueestro por consola que un cliente se ha desconectado y muestro también el tamaño de la lista
                    System.out.println("Cliente desconectado. Total de clientes: " + clients.size());
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }

        // Declaro un método para enviar mensaje a todos que necesita de un String el cual sera el mensaje
        private void enviarMensajeATodos(String mensaje) throws IOException {
            
            // Declaro un bucle que recorra la lista de clientes conectados
            for (Socket client : clients) {
                
                // Y si ese cliente no es el emisor del mensaje se le envia el mensaje
                if (client != clientSocket) {
                    
                    // Declaro objeto stream de salida
                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                    
                    // Y escribo el mensaje recibido por el cliente emisor en el objeto stream de salida, el cual deberian leer todos los clientes conectados
                    out.writeObject(mensaje);
                }
            }
        }
    }
}
package cliente_Servidor_SMTP_Hilos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class Servidor {

    public static void main(String[] args) {

        // Puerto en el que escuchará el servidor SMTP
        final int PUERTO_SMTP = 587;

        try (ServerSocket servidor = new ServerSocket(PUERTO_SMTP)) {
            System.out.println("Servidor SMTP iniciado en el puerto: " + PUERTO_SMTP);

            while (true) {
                Socket sc = servidor.accept();

                // Crear un nuevo hilo para manejar el cliente
                Thread hiloCliente = new Thread(new ManejadorCliente(sc));
                hiloCliente.start();

                System.out.println("Nuevo hilo para cliente");
            }

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor SMTP: " + e.getMessage());
        }
    }
}

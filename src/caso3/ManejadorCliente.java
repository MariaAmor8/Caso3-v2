package caso3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorCliente extends Thread {

    private Socket clientSocket;
    private Deposito deposito;
    private boolean cifradoSimetrico;
    private Tiempo tiempo;
    String SSLPath;

    public ManejadorCliente(Socket clientSocket, Deposito deposito, boolean cifradoSimetrico, Tiempo tiempo, String SSLPath) {
        this.clientSocket = clientSocket;
        this.deposito = deposito;
        this.cifradoSimetrico = cifradoSimetrico;
        this.tiempo = tiempo;
        this.SSLPath=SSLPath;
    }

    @Override
    public void run() {
        try (
            PrintWriter escritor = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            System.out.println("Delegado iniciado para el cliente ");

            // Ejecuta el protocolo para manejar la solicitud del cliente es iteartivo creo que sirve igual
            ProtocoloServidorIterativo protocolo = new ProtocoloServidorIterativo();
            protocolo.procesar(lector, escritor, deposito, cifradoSimetrico, tiempo,SSLPath);

            System.out.println("Cliente procesado y desconectado ");

        } catch (Exception e) {
            System.err.println("Error en el delegado del cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Cierra el socket después de procesar la solicitud supongamos sirve
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
            }
        }
    }
}
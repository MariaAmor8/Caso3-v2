package caso3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteDelegado extends Thread {

    private int idCliente;
    private int idPaquete;

    public ClienteDelegado(int idCliente, int idPaquete) {
        this.idCliente = idCliente;
        this.idPaquete = idPaquete;
    }

    @Override
    public void run() {
        try (
            Socket socket = new Socket("localhost", ServidorDelegado.PUERTO);
            PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Cliente " + idCliente + " conectado al servidor.");

            // Iterativo creo que sirve
            ProtocoloClienteIterativo protocolo = new ProtocoloClienteIterativo();
            protocolo.procesar(new BufferedReader(new InputStreamReader(System.in)), lector, escritor, idCliente, idPaquete);

            System.out.println("Cliente " + idCliente + " procesado y desconectado.");

        } catch (Exception e) {
            System.err.println("Error en el cliente " + idCliente + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
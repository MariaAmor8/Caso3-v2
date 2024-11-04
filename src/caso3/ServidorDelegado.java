package caso3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorDelegado {

    public static final int PUERTO = 3400;
    private Deposito deposito;
    private boolean cifradoSimetrico;
    private Tiempo tiempo;
    private int maxDelegados; // Número máximo de delegados concurrentes
    String SSLPath;
    public ServidorDelegado(Deposito deposito, boolean cifradoSimetrico, Tiempo tiempo, int maxDelegados,String SSLPath) {
        this.deposito = deposito;
        this.cifradoSimetrico = cifradoSimetrico;
        this.tiempo = tiempo;
        this.maxDelegados = maxDelegados;
        this.SSLPath=SSLPath;
    }

    public void iniciar() {
        try (ServerSocket ss = new ServerSocket(PUERTO)) {
            System.out.println("Servidor con delegados iniciado en el puerto " + PUERTO);

            int delegadosActivos = 0;

            while (delegadosActivos < maxDelegados) {
                Socket clientSocket = ss.accept();
                System.out.println("Nuevo cliente conectado ");

                // Crear un delegado para manejar la conexión del cliente
                ManejadorCliente delegado = new ManejadorCliente(clientSocket, deposito, cifradoSimetrico, tiempo,SSLPath);
                delegado.start(); // Iniciar el hilo para manejar al cliente
                delegadosActivos++;
            }

            System.out.println("Número máximo de delegados alcanzado.");

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void setSSLPath(String SSLPath){
		this.SSLPath = SSLPath;
	}
}

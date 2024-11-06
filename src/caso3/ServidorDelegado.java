package caso3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorDelegado {

    public static final int PUERTO = 3400;
    private Deposito deposito;
    private boolean cifradoSimetrico;
    private int maxDelegados; // Número máximo de delegados concurrentes
    String SSLPath;
    public ServidorDelegado(Deposito deposito, boolean cifradoSimetrico, int maxDelegados,String SSLPath) {
        this.deposito = deposito;
        this.cifradoSimetrico = cifradoSimetrico;
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
                Tiempo tiempoConsulta = new Tiempo();
                ManejadorCliente delegado = new ManejadorCliente(clientSocket, deposito, cifradoSimetrico, tiempoConsulta,SSLPath);
                delegado.start(); // Iniciar el hilo para manejar al cliente
                delegadosActivos++;
                //imprimirResultados();
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
    public void imprimirResultados() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados.txt", true))) { 
        writer.write("Tiempos para "+ maxDelegados+" delegados\n");
    } catch (IOException e) {
        System.out.println("Error al escribir en el archivo: " + e.getMessage());
    }
}
}
package caso3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorIterativo extends Thread {
	
	public static final int PUERTO = 3400;
	public static Deposito deposito;
	boolean cifradoSimetrico;
	String SSLPath;
	
	public void run() {
		//System.out.println("Servidor I");
		try {
			iniciar();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


		public void iniciar() throws IOException {
			ServerSocket ss = null;
	
			try {
				ss = new ServerSocket(PUERTO);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
	
			// Espera y acepta la conexión de un cliente
			Socket socket = ss.accept();
	
			try (
				PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()))
			) {
				// Se ejecuta el protocolo en el lado servidor
				ProtocoloServidorIterativo psi = new ProtocoloServidorIterativo();
	
				// Bucle para procesar múltiples consultas del cliente
				int consultas = 0;
				while (consultas < 32) {
					// Procesa una consulta
					Tiempo tiempoConsulta = new Tiempo();
					psi.procesar(lector, escritor, deposito, this.cifradoSimetrico, tiempoConsulta, this.SSLPath);
	
					// Espera un mensaje del cliente para saber si debe continuar
					String mensaje = lector.readLine();
					tiempoConsulta.imprimirResultados();
					if ("TERMINAR".equals(mensaje)) {
						consultas++; 
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// Cierra el socket después de que el cliente haya terminado todas sus consultas
				socket.close();
				//System.out.println("Conexión cerrada");
			}
		}
	
	public void setDeposito(Deposito dep) {
		deposito = dep;
	}

	public void setSimetrico(boolean cifradoSimetrico) {
		this.cifradoSimetrico = cifradoSimetrico;
	}
	
	public void setSSLPath(String SSLPath){
		this.SSLPath = SSLPath;
	}
}

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
	Tiempo tiempo;
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

	    //System.out.println("Main Server ...");

	    try {
	        ss = new ServerSocket(PUERTO);
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.exit(-1);
	    }

	    
	    // crear el socket en el lado servidor
	    // queda bloqueado esperando a que llegue un cliente
	    Socket socket = ss.accept();

	    try {
	    	// se conectan los flujos, tanto de salida como de entrada
	    	PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
	    	BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	    	// se ejecuta el protocolo en el lado servidor
	    	ProtocoloServidorIterativo psi = new ProtocoloServidorIterativo();
	    	psi.procesar(lector, escritor, deposito, this.cifradoSimetrico, this.tiempo, this.SSLPath);

	    	// se cierran los flujos y el socket
	    	escritor.close();
	    	lector.close();
	    	socket.close();
	 
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

	}
	
	public void setDeposito(Deposito dep) {
		deposito = dep;
	}

	public void setSimetrico(boolean cifradoSimetrico) {
		this.cifradoSimetrico = cifradoSimetrico;
	}
	
	public void setTiempo(Tiempo tiempo) {
		this.tiempo = tiempo;
	}

	public void setSSLPath(String SSLPath){
		this.SSLPath = SSLPath;
	}
}

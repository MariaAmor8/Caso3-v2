package caso3;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClienteIterativo extends Thread{

	public static final int PUERTO = 3400;
	public static final String SERVIDOR = "localhost";
	int idCliente;
	int idPaquete;
	boolean esIterativo= false;
	
	public void run() {
		//System.out.println("Cliente I ");
		try {
			iniciar();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	
	public void iniciar() throws Exception {
		
		Socket socket = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;
		//System.out.println("Comienza cliente");

		try {
			// crea el socket en el lado cliente
			socket = new Socket(SERVIDOR, PUERTO);
			// se conectan los flujos, tanto de salida como de entrada

			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// crea un flujo para leer lo que escribe el cliente por el teclado
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// se ejecuta el protocolo en el lado cliente
		ProtocoloClienteIterativo pci = new ProtocoloClienteIterativo();
		for (int i = 0; i < 32; i++) {
			System.out.println("Realizando consulta " + (i + 1) + " de 32.");
			System.out.println("boom");
			pci.procesar(stdIn, lector, escritor, this.idCliente, this.idPaquete);
		}
		// se cierran los flujos y el socket
		stdIn.close();
		escritor.close();
		lector.close();
		socket.close();
	
	}
	
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
		this.idPaquete = idCliente; //por facilidad, el id del paquete del cliente es igual a su id
	}

    public void setEsIterativo(boolean esIterativo) {
        this.esIterativo = esIterativo;
    }
	



}

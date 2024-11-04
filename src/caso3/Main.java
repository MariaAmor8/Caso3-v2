package caso3;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class Main {
	public static RSAKey llaveRSA = new RSAKey();

	public void servidorIterativo(boolean cifradoSimetrico, String SSLPath) {
		Iterativo opcionIterativa = new Iterativo();
		
		try {
			opcionIterativa.iniciarIterativo(cifradoSimetrico, SSLPath);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	public void imprmirMenu() {
		System.out.println("------------ Menu -----------");
		System.out.println("Elija una opcion: ");
		System.out.println("1. Generar llaves asimetricas del servidor");
		System.out.println("2. Generar servidor iterativo");
		System.out.println("3. Generar servidor con delegados");
		
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		Main main = new Main();
		boolean continuar = true;
		
		System.out.println("Ingrese la ruta donde se encuentra la carpeta para el SSL: ");
		String SSLPath = scanner.nextLine();

		while(continuar) {
			main.imprmirMenu();
			int opcion = scanner.nextInt();
			scanner.nextLine();
			if(opcion==1) {
				llaveRSA.generarLlaves();
				System.out.println("Rescatemos llaves :))");
				Key llavePrivada = llaveRSA.rescatarLlave("privada");
				System.out.println("Llave privada: " + llavePrivada.getFormat());
				Key llavePublica  =llaveRSA.rescatarLlave("publica");
				System.out.println("Llave publica: " + llavePublica.getFormat());
				continuar = false;
			}
			else {
				System.out.println("Primero debe generar las llaves");
			}
		}

		main.imprmirMenu();
		int opcion = scanner.nextInt();
		boolean cifradoSimetrico = false;
		scanner.nextLine();
		if(opcion == 2) {
			System.out.println("------ servidor iterativo ------");
			System.out.println("1. Cifrado del paquete con llave simetrica");
			System.out.println("2. Cifrado del paquete con llave asimetrica");
			int opcionCif = scanner.nextInt();
			scanner.nextLine();
			
			if(opcionCif == 1) {
				cifradoSimetrico = true;
			}
			main.servidorIterativo(cifradoSimetrico, SSLPath);
			
		}
		else if(opcion == 3) {
			System.out.println("------ Servidor con delegados ------");
			
			// Solicita el número máximo de delegados y el número de clientes
			System.out.print("Ingrese el número máximo de delegados concurrentes: ");
			int maxDelegados = scanner.nextInt();
			scanner.nextLine(); // Limpiar el buffer
			System.out.print("Ingrese el número máximo de clientes concurrentes: ");
			int maxClientes = scanner.nextInt();
			scanner.nextLine(); // Limpiar el buffer
			
			// Instancia las clases necesarias para el servidor
			Deposito deposito = new Deposito(); // Asegúrate de que Deposito esté correctamente inicializado
			Tiempo tiempo = new Tiempo();  
			boolean cifraSimet= false;     // Asegúrate de que Tiempo esté correctamente inicializado
			//boolean cifradoSimetrico = false;    // O ajusta según la opción de cifrado deseada
			
			// Crear e iniciar el servidor en un hilo separado
			ServidorDelegado servidor = new ServidorDelegado(deposito, cifraSimet, tiempo, maxDelegados,SSLPath);
			Thread servidorThread = new Thread(() -> servidor.iniciar());
			servidorThread.start();
			// Inicia los clientes delegados con la cantidad especificada
			ClienteConDelegados.iniciar(maxClientes);
		
			// Espera a que el servidor termine (opcional)
			try {
				servidorThread.join();
			} catch (InterruptedException e) {
				System.err.println("Error al esperar que el servidor termine.");
				e.printStackTrace();
			}
		}
		

		else {
			System.out.println("opcion no valida");
		}	

		scanner.close();
	}
	
	

	
}

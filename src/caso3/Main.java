package caso3;
import java.io.IOException;
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
			System.out.println("---------------- Servidor iterativo ----------------");
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
			System.out.print("Ingrese el número máximo de delegados concurrentes: ");
			int maxDelegados = scanner.nextInt();
			scanner.nextLine();
			System.out.print("Ingrese el número máximo de clientes concurrentes: ");
			int maxClientes = scanner.nextInt();
			scanner.nextLine();
			Deposito deposito = new Deposito(); 
			Tiempo tiempo = new Tiempo(); 
			System.out.println("1. Cifrado del paquete con llave simetrica");
			System.out.println("2. Cifrado del paquete con llave asimetrica");
			int opcionCif = scanner.nextInt();
			scanner.nextLine();
			if(opcionCif == 1) {
				cifradoSimetrico = true;
			}
			ServidorDelegado servidor = new ServidorDelegado(deposito, cifradoSimetrico, tiempo, maxDelegados, SSLPath);
			Thread servidorThread = new Thread(new Runnable() {
				@Override
				public void run() {
					servidor.iniciar();
				}
			});
			servidorThread.start();
			ClienteConDelegados.iniciar(maxClientes);
			try {
				servidorThread.join();
				System.out.println("TERMINO");
				System.out.println("Tiempos: ");
				tiempo.imprimirResultados();
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

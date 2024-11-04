package caso3;

public class Iterativo {

	public void iniciarIterativo(boolean cifradoSimetrico, String SSLPath) throws InterruptedException {

		ServidorIterativo si = new ServidorIterativo();
		ClienteIterativo clienteI = new ClienteIterativo();
		Deposito depo = new Deposito();
		si.setDeposito(depo);
		si.setSimetrico(cifradoSimetrico); // para sabe si el estado del paquete debe ser cifrado simetricamente o
											// no
		si.setSSLPath(SSLPath);

		Tiempo tiempo = new Tiempo();
		si.setTiempo(tiempo);

		si.start();
		clienteI.start();

		si.join();
		clienteI.join();
		System.out.println("TERMINO");
		System.out.println("Tiempos: ");
		tiempo.imprimirResultados();

	}

}
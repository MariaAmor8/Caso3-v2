package caso3;

public class Iterativo {
	
	ServidorIterativo si = new ServidorIterativo();
	ClienteIterativo clienteI = new ClienteIterativo();
	
	public void iniciarIterativo(boolean cifradoSimetrico, String SSLPath) throws InterruptedException {
		//System.out.println("vamo a ver que pasa");
		Tiempo tiempo = new Tiempo();
		Deposito depo = new Deposito();
		si.setDeposito(depo);	
		si.setSimetrico(cifradoSimetrico); // para sabe si el estado del paquete debe ser cifrado simetricamente o no
		si.setTiempo(tiempo);
		si.setSSLPath(SSLPath);
		
		si.start();
		clienteI.start();
		
		si.join();
		clienteI.join();
		System.out.println("TERMINO");
		System.out.println("Tiempos: ");
		tiempo.imprimirResultados();
	}

}

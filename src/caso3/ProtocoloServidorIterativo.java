package caso3;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

import java.math.BigInteger;

public class ProtocoloServidorIterativo {
	RSAKey llaveRSA = new RSAKey();
	private PrivateKey privateKeyServidor;
	private PublicKey publicKeyServidor;
	BigInteger llaveSecretaDH;
	SecretKey llaveAES;
	SecretKey llaveHMAC;
	CifradoAES cifradoAES = new CifradoAES();
	CifradoHMAC cifradoHMAC = new CifradoHMAC();
	Deposito depostito;
	boolean cifradoSimetrico;
	Tiempo tiempo;
	String SSLPath;
	
	public void procesar(BufferedReader pIn, PrintWriter pOut, Deposito deposito, boolean cifradoSimetrico, Tiempo tiempo, String SSLPath) throws Exception {
		this.SSLPath = SSLPath;
		this.tiempo = tiempo;
		this.depostito = deposito;
		this.cifradoSimetrico = cifradoSimetrico;

		boolean primeraParte = comunicacionReto(pIn, pOut);
		if(primeraParte) {
			boolean isDh = diffieHellman(pOut, pIn);
			if(isDh) {
				byte[] digest = calcularSHA512(llaveSecretaDH);
				llaveAES = this.cifradoAES.generarLlaveAES(digest);
				llaveHMAC = this.cifradoHMAC.generarLlaveHMac(digest);
				
				// mandar vector de inicialización vi
				byte[] vi = this.cifradoAES.generarIV();
				String viString = Base64.getEncoder().encodeToString(vi);
				System.out.println("S: Envío iv ");
				pOut.println(viString);
				
				//medir el tiempo que se demora en verificar la consulta
				tiempo.iniciar(Tiempo.VERIFICACION_CONSULTA);
				verificarConsulta(pIn, pOut, vi);
				tiempo.detener(Tiempo.VERIFICACION_CONSULTA);
				
			}
		}
		else {
			System.out.println("S: conexión no establecida correctamente. TUKI TUKI bye bye ");
		}
	}


	private void verificarConsulta(BufferedReader pIn, PrintWriter pOut, byte[]vi) throws Exception {
		//leer id del cliente cifrado
		String uidCifrado = pIn.readLine();

		//Leer hash del id del cliente
		String hashUid = pIn.readLine();

		//Descifrar id del cliente
		String uidDescifrado = this.cifradoAES.descifrarAES(llaveAES, uidCifrado);

		//Verificar que el id del cliente si es calculando y comparando su hash
		boolean isIdCliente = this.cifradoHMAC.verificarHMac(llaveHMAC,uidDescifrado,hashUid);
		//System.out.println("S: is id cliente?: " + isIdCliente);

		//leer id del paquete del cliente cifrado
		String idPaqueteCifrado = pIn.readLine();

		//Leer hash del id del paquete del cliente
		String hashPaqueteId = pIn.readLine();

		//Descifrar id del paquete del cliente
		String idPaqueteDescifrado = this.cifradoAES.descifrarAES(llaveAES, idPaqueteCifrado);

		//Verificar que el id del cliente si es calculando y comparando su hash
		boolean isIdPaquete = this.cifradoHMAC.verificarHMac(llaveHMAC,idPaqueteDescifrado,hashPaqueteId);
		//System.out.println("S: is id paquete?: " + isIdPaquete);

		//buscar estado del paquete
		//Si no coinciden el id del cliente con el hash, o el paquete con el hash retorna desconocido
		String estadoPaquete;
		if(!isIdCliente || !isIdPaquete) {
			estadoPaquete = "DESCONOCIDO";
		}
		else {
			estadoPaquete = this.depostito.obtenerEstadoPaquete(Integer.parseInt(uidDescifrado), Integer.parseInt(idPaqueteDescifrado));
		}

		System.out.println("S: Estado del paquete: " + estadoPaquete);	

		//cifrar estado del paquete y enviar
		//si usa cifrado asimetrico

		if(!this.cifradoSimetrico) {
			tiempo.iniciar(Tiempo.CIFRAR_PAQUETE_ASIMETRICO);
			this.llaveRSA.cifrarMensaje(publicKeyServidor, estadoPaquete);
			tiempo.detener(Tiempo.CIFRAR_PAQUETE_ASIMETRICO);
		}

		String estadoCifrado = this.cifradoAES.cifrarMensajeAES(llaveAES, estadoPaquete, vi);
		pOut.println(estadoCifrado);




		//hacer hash del estado del paquete y eviar
		String hashEstado = this.cifradoHMAC.cifrarConHMAC(estadoPaquete, llaveHMAC);
		pOut.println(hashEstado); 

	}


	private boolean comunicacionReto(BufferedReader pIn, PrintWriter pOut) throws Exception {

		//leer llaves de arhivo
		PublicKey llavePublica = (PublicKey) llaveRSA.rescatarLlave("publica");
		PrivateKey llavePrivada = (PrivateKey) llaveRSA.rescatarLlave("privada");
		this.privateKeyServidor = llavePrivada;
		this.publicKeyServidor = llavePublica;

		String inputLine = pIn.readLine();
		System.out.println("S: palabra de inicio recibida: "+ inputLine);

		//Recibir R  y calcular Rta > rta = descifrar con llave privada R
		String R = pIn.readLine();
		System.out.println("S: Descifrando R...");
		
		//medir tiempo que se demora en descifrar RETO
		tiempo.iniciar(Tiempo.VERIFICACION_RETO);
		String rta = llaveRSA.descifrarMensaje(llavePrivada, R);
		tiempo.detener(Tiempo.VERIFICACION_RETO);
		
		//Enviar rta
		pOut.println(rta);
		System.out.println("S: Envío Rta = "+rta);

		//Leer "OK" o "ERROR"
		String respuestaCliente = pIn.readLine();
		if(respuestaCliente.equals("OK")) {
			System.out.println("S: Recibido: " + respuestaCliente);
			return true;
		}
		else {
			System.out.println("S: Recibido: " + respuestaCliente);
			return false;

		}

	}


	private boolean diffieHellman(PrintWriter pOut, BufferedReader pIn) throws Exception {
		DiffieHellman dh = new DiffieHellman();
		//medir tiempo que se demora en generar P, G y Gx
		System.out.println("S: Generando G, P y Gx...");
		tiempo.iniciar(Tiempo.GENERAR_PG_GX);
		//dh.generarPyG(SSLPath);
		dh.generarPyGPorDefault();
		tiempo.detener(Tiempo.GENERAR_PG_GX);
		
		BigInteger P = dh.darP();
		BigInteger G = dh.darG();
		BigInteger X = dh.generarXAleatorio(P);
		//System.out.println("S: mi X");
		BigInteger Y = dh.calcularY(G, X, P);
		//System.out.println("S: G^x mod p (o sea Y)");

		//mandar G
		pOut.println(G);

		//mandar P
		pOut.println(P);

		//mandar G^x mod p = Y
		pOut.println(Y);

		System.out.println("S: Envío G, P y Gx");

		//crear firma
		byte[] firmaCifrado = this.llaveRSA.firmarValores(P, G, Y, this.privateKeyServidor);
		String firmaCifrada = Base64.getEncoder().encodeToString(firmaCifrado);
		//mandar firma
		pOut.println(firmaCifrada);

		//leer si diffie hellman funcionó
		String isDH = pIn.readLine();
		//System.out.println("S: dh funcionó? "+isDH);

		if(isDH.equals("OK")) {
			//calcular llave secreta diffie hellman
			String YCliente = pIn.readLine();
			BigInteger yCliente = new BigInteger(YCliente);

			this.llaveSecretaDH = dh.calcularY(yCliente, X, P);
			//System.out.println("S: Mi llave secreta de DH");
			return true;
		}
		else {
			System.out.println("S: Diffie hellman no funcinó");
			return false;
		}

	}

	private byte[] calcularSHA512(BigInteger llaveMaestra) throws Exception {
		// Convertir la clave maestra de BigInteger a bytes
		byte[] inputBytes = llaveMaestra.toByteArray();

		// Crear una instancia de MessageDigest para SHA-512
		MessageDigest md = MessageDigest.getInstance("SHA-512");

		// Calcular el digest (hash)
		byte[] digest = md.digest(inputBytes);

		return digest;
	}
}

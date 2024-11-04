package caso3;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

import java.math.BigInteger;

public class ProtocoloClienteIterativo {
	RSAKey llaveRSA = new RSAKey();
	PublicKey publicKeyServidor;
	BigInteger P;
	BigInteger G;
	BigInteger YServidor;
	BigInteger X;
	DiffieHellman dh = new DiffieHellman();
	BigInteger llaveSecretaDH;
	SecretKey llaveAES;
	SecretKey llaveHMAC;
	CifradoAES cifradoAES = new CifradoAES();
	CifradoHMAC cifradoHMAC = new CifradoHMAC();
	int idCliente;
	int idPaquete;

	public void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut, int idCliente, int idPaquete) throws Exception {
		this.idCliente = idCliente;
		this.idPaquete = idPaquete;
		boolean primeraParte = comunicacionReto(pIn, pOut);
		if(primeraParte) {
			boolean segundaParte = diffieHellman(pIn, pOut);
			if(segundaParte) {
				pOut.println("OK");
				finalizarDiffieHellman(pIn, pOut);
				byte[] digest = calcularSHA512(llaveSecretaDH);
				llaveAES = this.cifradoAES.generarLlaveAES(digest);
				llaveHMAC = this.cifradoHMAC.generarLlaveHMac(digest);
				verificarConsulta(pIn, pOut);
				
				
			}
			else {
				System.out.println("C: DH - conexión no establecida correctamente. TUKI TUKI bye bye ");
			}
		}
		else {
			System.out.println("C: conexión no establecida correctamente. TUKI TUKI bye bye ");
		}
		
	}
	

	private void verificarConsulta(BufferedReader pIn, PrintWriter pOut) throws Exception {
		//leer vector de inicializacion
		String vi = pIn.readLine();
		byte[] VI = Base64.getDecoder().decode(vi);
		//System.out.println("C: VI");
		
		System.out.println("C: id cliente" + this.idCliente);
		
		//mandar cifrado el id del Cliente con la llave EAS
		String uidCifrado = this.cifradoAES.cifrarMensajeAES(llaveAES, this.idCliente+"", VI);
		pOut.println(uidCifrado);
		//System.out.println("C: uidCifrado");
		
		//mandar el hash con el usuario del id - HMAC
		String hashUid = this.cifradoHMAC.cifrarConHMAC(this.idCliente+"", this.llaveHMAC);
		pOut.println(hashUid);
		//System.out.println("C: HMAC uidCliente");
		
		//mandar cifrado el id del paquete del cliente
		String idPaqueteCifrado = this.cifradoAES.cifrarMensajeAES(llaveAES, this.idCliente+"", VI);
		pOut.println(idPaqueteCifrado);
		//System.out.println("C: id Paquete cifrado");
		
		//mandar hash del id del paquete del cliente
		String hashPaqueteId = this.cifradoHMAC.cifrarConHMAC(this.idPaquete+"", this.llaveHMAC);
		pOut.println(hashPaqueteId);
		//System.out.println("C: HMAC paquete id");
		
		//leer estado cifrado, descifrar y comparar con el hash
		String estadoCifrado = pIn.readLine();
		String estado = this.cifradoAES.descifrarAES(llaveAES, estadoCifrado);
		String hashEstado = pIn.readLine();
		//boolean isEqual = this.cifradoHMAC.verificarHMac(llaveHMAC, estado, hashEstado);
		this.cifradoHMAC.verificarHMac(llaveHMAC, estado, hashEstado);
		//System.out.println("C: es igual? " + isEqual);
		//System.out.println("C: estado del paquete " + estado);
		
		//Enviar "TERMINAR"
		pOut.println("TERMINAR");
	}


	private boolean comunicacionReto(BufferedReader pIn, PrintWriter pOut) throws Exception {
		
		//leer llave (publica) de arhivo
		PublicKey llavePublica = (PublicKey) llaveRSA.rescatarLlave("publica");
		this.publicKeyServidor = llavePublica;
		System.out.println("C: Llave publica rescatada");

		//enviar palabra de inicio
		String palabraInic = "SECINIT";
		pOut.println(palabraInic);
		System.out.println("C: palabra de inicio enviada");

		//cifrar RETO y mandarlo
		String RetoCifrado = llaveRSA.cifrarMensaje(llavePublica, "Reto");
		pOut.println(RetoCifrado);
		//System.out.println("C: Envió RETO cifrado");

		//leer rta y comprar
		String rta = pIn.readLine();
		//System.out.println("C: Rta recibida");
		if(rta.equals("Reto")) {
			pOut.println("OK");
			System.out.println("C: OK");
			return true;
		}
		else {
			pOut.println("ERROR");
			System.out.println("C: ERROR");
			return false;

		}

	}

	private boolean diffieHellman(BufferedReader pIn, PrintWriter pOut) throws Exception {
		//leer G
		String g = pIn.readLine();
		BigInteger G = new BigInteger(g);
		//leer P
		String p = pIn.readLine();
		BigInteger P = new BigInteger(p);
		//leer Y
		String gx = pIn.readLine();
		BigInteger Y = new BigInteger(gx);
		
		//System.out.println("C: Recibido G");
		//System.out.println("C: Recibido p");
		//System.out.println("C: Recibido g^x mod p");
		
		//leer firma
		String firmaCifrada = pIn.readLine();
		byte[] firmaCifradabytes = Base64.getDecoder().decode(firmaCifrada);
		
		
		//verificar Firma
		BigInteger[] resultado = this.llaveRSA.verificarFirma(P, G, Y, this.publicKeyServidor, firmaCifradabytes);
		//System.out.println("C: Firma descifrada P");
		//System.out.println("C: Firma descifrada G");
		//System.out.println("C: Firma descifrada Y");
		
		this.P = resultado[0];
		this.G = resultado[1];
		this.YServidor = resultado[2];
		
		
		if(resultado[0].equals(P) && resultado[1].equals(G) && resultado[2].equals(Y)) {
			System.out.println("C: Diffie-Hellman OK");
			return true;
		}
		else {
			System.out.println("C: diffieHellman ERROR");
			return false;
			
		}
		

	}

	private void finalizarDiffieHellman(BufferedReader pIn, PrintWriter pOut) {
		
		//Calcular (G^x)^y
		this.X = dh.generarXAleatorio(this.P);
		//System.out.println("C: mi X");
		BigInteger YCliente = dh.calcularY(this.G, this.X, this.P);
		
		//comunicar YCliente
		pOut.println(YCliente);
		
		//calcular Llave secreta Cliente > z = (YServidor)^x mod p
		this.llaveSecretaDH = dh.calcularY(this.YServidor, this.X, this.P);
		//System.out.println("C: Mi llave secreta de DH");
		
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
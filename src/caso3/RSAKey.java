package caso3;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSAKey {
	
	/**
	 * Método que genera y guarda en archivos las llaves asimetricas del servidor usando RSA
	 * @return las 2 llaves generadas
	 * @throws NoSuchAlgorithmException
	 */
	public Key[] generarLlaves() throws NoSuchAlgorithmException {
		 KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
         
         // Establece el tamaño de las llaves, 2048 bits es común y seguro para RSA
         keyGen.initialize(1024);

         // Genera el par de llaves (pública y privada)
         KeyPair keyPair = keyGen.generateKeyPair();
         
         // Obtén la llave pública y la llave privada del par
         PublicKey publicKey = keyPair.getPublic();
         PrivateKey privateKey = keyPair.getPrivate();

         // Muestra las llaves en formato codificado
         //System.out.println("Public Key: " + publicKey.toString());
         //System.out.println("Private Key: " + privateKey.toString());
         
         try {
         	
         	String folderPath = "src/Llaves";
     		String filePath = folderPath + "/LlavePrivadaServidor.txt";
     		String filePath2 = folderPath + "/LlavePublicaServidor.txt";
     		File folder = new File(folderPath);

     		if (!folder.exists()) {
     			folder.mkdirs();
     		}
     		
     		Files.write(Paths.get(filePath), privateKey.getEncoded());
     		Files.write(Paths.get(filePath2), publicKey.getEncoded());
 			

 			System.out.println("Llaves guardadas con éxito");

 		} catch (IOException e) {
 			e.printStackTrace();
 		}
         
         return new Key[]{publicKey, privateKey};
        
	}
	/**
	 * Metodo para leer una llave y rescatar su valor desde el archivo donde quedó guardado
	 * @param tipoLlave
	 * @return la llave como objeto
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public Key rescatarLlave(String tipoLlave) throws NoSuchAlgorithmException, InvalidKeySpecException {

		try {
			// Determina la ruta del archivo según el tipo de llave
			
			String filePath = tipoLlave.equalsIgnoreCase("publica") 
				    ? "src/Llaves/LlavePublicaServidor.txt" 
				    : "src/Llaves/LlavePrivadaServidor.txt";
			
			byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			// Lee y retorna la clave pública
			if (tipoLlave.equalsIgnoreCase("publica")) {
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
				return keyFactory.generatePublic(keySpec);
			}
			// Lee y retorna la clave privada
			else if (tipoLlave.equalsIgnoreCase("privada")) {
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
				return keyFactory.generatePrivate(keySpec);
			} else {
				System.out.println("Tipo de llave no válido. Use 'publica' o 'privada'.");
				return null;
			}

		} catch (IOException e) {
			System.out.println("Ocurrió un error al leer el archivo de la llave.");
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Metodo para cifrar un mensaje con una llave publica RSA
	 * @param publicKey
	 * @param mensaje
	 * @return mensaje cifrado > C(k_w+,Reto)
	 * @throws Exception
	 */
	public String cifrarMensaje(PublicKey publicKey, String mensaje) throws Exception {
        // Inicializa el cifrador con el algoritmo RSA
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // Cifra el mensaje y lo convierte en un arreglo de bytes
        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));

        // Devuelve el mensaje cifrado en formato Base64
        return Base64.getEncoder().encodeToString(mensajeCifrado);
    }
	
	/**
	 * Metodo para descifrar un mensaje con la llave privada RSA
	 * @param privateKey
	 * @param mensajeCifrado
	 * @return mensaje descifrado
	 * @throws Exception
	 */
	public String descifrarMensaje(PrivateKey privateKey, String mensajeCifrado) throws Exception {
        // Inicializa el cifrador con el algoritmo RSA
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Decodifica el mensaje cifrado de Base64 a bytes
        byte[] bytesCifrados = Base64.getDecoder().decode(mensajeCifrado);

        // Descifra el mensaje
        byte[] mensajeDescifrado = cipher.doFinal(bytesCifrados);

        // Convierte los bytes descifrados a texto plano
        return new String(mensajeDescifrado, "UTF-8");
    }
	
	/**
	 * Metodo para mandar la firma de G, P y G a la X (gx) cifrada con la llave privada del servidor
	 * @param p
	 * @param g
	 * @param gx
	 * @param privateKey
	 * @return arreglo de bytes cifrado
	 * @throws Exception
	 */
	public byte[] firmarValores(BigInteger p, BigInteger g, BigInteger gx, PrivateKey privateKey) throws Exception {
        // Inicializa el objeto Signature con el algoritmo SHA1withRSA
        Signature firma = Signature.getInstance("SHA1withRSA");
        firma.initSign(privateKey);

        // Convierte los valores p, g y gx a bytes y actualiza la firma con ellos
        firma.update(p.toByteArray());
        firma.update(g.toByteArray());
        firma.update(gx.toByteArray());

        // Genera la firma
        return firma.sign();
    }
	
	/**
	 * Método para descifrar la Firma que contiene P, G y G^x con la llave publica del servidor (que la tiene el cliente)
	 * @param p
	 * @param g
	 * @param gx
	 * @param publicKey
	 * @param firma
	 * @return arreglo de BigIntegers > [P, G, G^x]
	 * @throws Exception
	 */
	
	public BigInteger[] verificarFirma(BigInteger p, BigInteger g, BigInteger gx, PublicKey publicKey, byte[] firma) throws Exception {
        // Inicializa el objeto Signature con el algoritmo SHA1withRSA para verificación
        Signature verificador = Signature.getInstance("SHA1withRSA");
        verificador.initVerify(publicKey);

        // Convierte los valores p, g y gx a bytes y actualiza el verificador con ellos
        verificador.update(p.toByteArray());
        verificador.update(g.toByteArray());
        verificador.update(gx.toByteArray());

        // Verifica la firma
        boolean esValido = verificador.verify(firma);

        // Si la firma es válida, devuelve un arreglo con p, g y gx; de lo contrario, null
        if (esValido) {
            return new BigInteger[]{p, g, gx};
        } else {
            return null;
        }
    }

}

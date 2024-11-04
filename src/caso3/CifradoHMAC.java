package caso3;

import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class CifradoHMAC {
	
	public SecretKey generarLlaveHMac(byte[] digestSHA512) {
        // Extrae los últimos 256 bits
        byte[] claveHMacBytes = new byte[32];
        System.arraycopy(digestSHA512, 32, claveHMacBytes, 0, 32);

        // Crea una clave HMAC a partir de los 256 bits extraídos
        return new SecretKeySpec(claveHMacBytes, "HmacSHA256"); // "HmacSHA256" para una clave HMAC
    }
	
	/**
	 * Metodo para cifrar usando HMAC
	 * @param mensaje
	 * @param clave
	 * @return mensaje cifrado con HMAC
	 * @throws Exception
	 */
	public String cifrarConHMAC(String mensaje, SecretKey clave) throws Exception {
        // Crear una instancia de Mac para HMACSHA384
        Mac mac = Mac.getInstance("HmacSHA384");
        mac.init(clave);  // Inicializar Mac con la clave SecretKey
        
        // Calcular el HMAC del mensaje
        byte[] hmacBytes = mac.doFinal(mensaje.getBytes("UTF-8"));
        
        // Convertir el HMAC a una cadena en Base64 para facilitar la lectura
        return Base64.getEncoder().encodeToString(hmacBytes);
    }
	
	/**
	 * Metodo para verificar si un mensaje encriptado usando HMAC es correcto
	 * @param clave
	 * @param mensaje
	 * @param hmacAComparar
	 * @return
	 * @throws Exception
	 */
	public boolean verificarHMac(SecretKey clave, String mensaje, String hmacAComparar) throws Exception {
        // Calcula el HMAC del mensaje dado usando la misma clave
        String hmacGenerado = cifrarConHMAC(mensaje, clave);

        // Compara el HMAC generado con el HMAC proporcionado
        return hmacGenerado.equals(hmacAComparar);
    }


}
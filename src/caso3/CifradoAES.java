package caso3;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CifradoAES {
	
    public SecretKey generarLlaveAES(byte[] digestSHA512) {
        // Extrae los primeros 256 bits
        byte[] claveCifradoBytes = new byte[32];
        System.arraycopy(digestSHA512, 0, claveCifradoBytes, 0, 32);

        // Crea una clave simétrica a partir de los 256 bits extraídos
        return new SecretKeySpec(claveCifradoBytes, "AES"); // "AES" indica un algoritmo de clave simétrica
    }
    
    /**
     * Metodo para generar el vector de inicializacion
     * @return iv
     */
    public byte[] generarIV() {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
    
    /**
     * Metodo para cifrar usando AES, CBC, relleno PKCS5 y llave de 256 bits
     * @param llaveAES
     * @param mensaje
     * @param iv
     * @return Mensaje cifrado en String
     * @throws Exception
     */
    
    public String cifrarMensajeAES(SecretKey llaveAES, String mensaje, byte[] iv) throws Exception {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Configura el cifrador en modo AES/CBC/PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, llaveAES, ivSpec);

        // Cifra el mensaje
        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));

        // Codifica el mensaje cifrado en Base64 para enviar o almacenar
        String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);

        // Devuelve el IV y el mensaje cifrado juntos
        return Base64.getEncoder().encodeToString(iv) + ":" + mensajeCifradoBase64;
    }
    
    /**
     * Metodo para descifrar un mensaje encriptado con AES
     * @param llaveAES
     * @param mensajeCifrado
     * @return mensaje descifrado en String
     * @throws Exception
     */
    public String descifrarAES(SecretKey llaveAES, String mensajeCifrado) throws Exception {
        // Separa el IV y el mensaje cifrado en Base64
        String[] partes = mensajeCifrado.split(":");
        if (partes.length != 2) {
            throw new IllegalArgumentException("El formato del mensaje cifrado no es válido.");
        }
        byte[] iv = Base64.getDecoder().decode(partes[0]);
        byte[] mensajeCifradoBytes = Base64.getDecoder().decode(partes[1]);

        // Configura el descifrador en modo AES/CBC/PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, llaveAES, new IvParameterSpec(iv));

        // Descifra el mensaje
        byte[] mensajeDescifradoBytes = cipher.doFinal(mensajeCifradoBytes);

        // Convierte el resultado a texto plano
        return new String(mensajeDescifradoBytes, "UTF-8");
    }


}
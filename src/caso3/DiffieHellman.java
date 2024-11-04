package caso3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {
	
	private BigInteger P;
	private BigInteger G;
	
	/**
	 * Metodo par calcular G^x mod p, es decir Y
	 * @param g
	 * @param x
	 * @param p
	 * @return g^x mod p
	 */
	public BigInteger calcularY(BigInteger g, BigInteger x, BigInteger p) {
        return g.modPow(x, p);
    }
	
	public void generarPyG(String SSLPath) throws Exception {
		Process process = Runtime.getRuntime().exec(SSLPath+"\\openssl dhparam -text 1024");
		// Leer la salida del commando
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		StringBuilder output = new StringBuilder();
		// Almacena toda la salida para procesarla después
		while ((line = reader.readLine()) != null) {
			output.append(line).append("\n");
		}
		reader.close();
		process.waitFor();
		
		//System.out.println(output.toString());
		
		// Extraer prime y generator a partir de output
        BigInteger prime = extractPrime(output.toString());
        BigInteger generator = extractGenerator(output.toString());

        //System.out.println("Prime (BigInteger): " + prime);
        //System.out.println("Generator (BigInteger): " + generator);
        
        this.P = prime;
        this.G = generator;

	}
	
	private BigInteger extractPrime(String output) {
		StringBuilder primeHex = new StringBuilder();
		boolean primeSection = false;

		// Procesar la cadena de output para extraer el valor de prime
		String[] lines = output.split("\n");
		for (String line : lines) {
			if (line.trim().startsWith("prime:")) {
				primeSection = true;
				continue;
			}
			if (primeSection) {
				if (line.trim().startsWith("generator")) {
					break; // Finalizar la sección de prime al encontrar "generator"
				}
				primeHex.append(line.trim().replace(":", "")); // Remover ":" y espacios
			}
		}

		// Convertir el valor hexadecimal de prime a BigInteger
		return new BigInteger(primeHex.toString(), 16);
	}

	private BigInteger extractGenerator(String output) {
		// Procesar la cadena de output para extraer el valor de generator
		String[] lines = output.split("\n");
		for (String line : lines) {
			if (line.trim().startsWith("generator")) {
				String[] parts = line.trim().split(" ");
				return new BigInteger(parts[1]); // Retornar el valor de generator
			}
		}
		return null; // Retornar null si no se encuentra el valor
	}
	
	public BigInteger generarXAleatorio(BigInteger p) {
		SecureRandom random = new SecureRandom();
        BigInteger clavePrivada;

        do {
            // Genera un número aleatorio de 1024 bits
            clavePrivada = new BigInteger(1024, random);
        } while (clavePrivada.compareTo(p) >= 0); // Asegura que sea menor que p

        return clavePrivada;
	}
	
	public BigInteger darP() {
		return this.P;
	}
	
	public BigInteger darG() {
		return this.G;
	}
}

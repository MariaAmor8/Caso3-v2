package caso3;

import java.util.HashMap;
import java.util.Random;

public class Deposito {
	
	private HashMap<Integer, Paquete> paquetes;
	
    public Deposito() {
        this.paquetes = new HashMap<>();
        cargarBodega();
    }
    
    
    /**
     * Método que carga la bodega con 32 paquetes, cada uno con un estado aleatorio.
     */
    private void cargarBodega() {
        // Estados posibles para los paquetes
        int[] estados = {1, 2, 3, 4, 5};
        Random random = new Random();

        // Crear 32 paquetes con ID de 0 a 31 y asignar un estado aleatorio
        for (int i = 0; i <= 31; i++) {
            int estado = estados[random.nextInt(estados.length)]; // Selecciona un estado aleatorio
            Paquete paquete = new Paquete(i, estado); // Crea el paquete con ID y estado
            paquetes.put(i, paquete); // Añade el paquete al HashMap con la clave ID
        }
    }
    
    public HashMap<Integer, Paquete> getPaquetes() {
        return paquetes;
    }
    
    /**
     * Devuelve el estado del paquete asociado a un userId y un paqueteId.
     * Si el userId no está en el HashMap o el paqueteId no coincide, retorna "Desconocido".
     *
     * @param userId    El ID del usuario en el HashMap.
     * @param paqueteId El ID del paquete que se desea verificar.
     * @return El estado del paquete si existe y coincide, o "Desconocido" en caso contrario.
     */
    public String obtenerEstadoPaquete(int userId, int paqueteId) {
        Paquete paquete = paquetes.get(userId);

        // Verifica si el paquete existe y el paqueteId coincide
        if (paquete != null && paquete.getId() == paqueteId) {
            return paquete.getEstado()+"";
        }

        return "6";
    }
    
    
}

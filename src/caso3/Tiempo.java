package caso3;

public class Tiempo {

    // Constantes para los diferentes procesos
    static final int VERIFICACION_RETO = 0;
    static final int GENERAR_PG_GX = 1;
    static final int VERIFICACION_CONSULTA = 2;
    static final int CIFRAR_PAQUETE_ASIMETRICO = 3;

    // Variables para almacenar los tiempos de inicio y los deltas
    private long[] startTimes = new long[4];
    private long[] deltas = new long[4];

    // Constructor: inicializa el delta de CIFRAR_PAQUETE_ASIMETRICO en 0
    public Tiempo() {
        deltas[CIFRAR_PAQUETE_ASIMETRICO] = 0;
    }

    // Método para iniciar el conteo del tiempo para un proceso específico
    public void iniciar(int proceso) {
        startTimes[proceso] = System.nanoTime();
    }

    // Método para detener el conteo del tiempo y calcular el delta para un proceso específico
    public void detener(int proceso) {
        long delta = (System.nanoTime() - startTimes[proceso]); // Convertir a milisegundos
        
        // Si el proceso es CIFRAR_PAQUETE_ASIMETRICO, sumar el delta a VERIFICACION_CONSULTA
        if (proceso == CIFRAR_PAQUETE_ASIMETRICO) {
            deltas[VERIFICACION_CONSULTA] += delta;
        } else {
            deltas[proceso] = delta;
        }
    }

    // Método para imprimir los resultados de los tiempos de cada proceso
    public void imprimirResultados() {
        System.out.println("Tiempos de cada proceso:");
        System.out.println("Verificación de Reto: " + deltas[VERIFICACION_RETO] + " ns");
        System.out.println("Generación de P, G y Gx: " + deltas[GENERAR_PG_GX] + " ns");
        System.out.println("Verificación de Consulta: " + deltas[VERIFICACION_CONSULTA] + " ns");
    }
}


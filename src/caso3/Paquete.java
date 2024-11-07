package caso3;

public class Paquete {

    // Atributos de la clase
    private int id;
    private int estado;

    // Constantes para los posibles estados del paquete
    public static final int EN_OFICINA = 0;
    public static final int RECOGIDO = 1;
    public static final int EN_CLASIFICACION = 2;
    public static final int DESPACHADO = 3;
    public static final int EN_ENTREGA = 4;
    public static final int ENTREGADO = 5;

    // Constructor
    public Paquete(int id, int estado) {
        this.id = id;
        this.estado = estado;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    // Método para obtener el nombre del estado como cadena
    public String obtenerNombreEstado() {
        switch (estado) {
            case EN_OFICINA:
                return "EN OFICINA";
            case RECOGIDO:
                return "RECOGIDO";
            case EN_CLASIFICACION:
                return "EN CLASIFICACION";
            case DESPACHADO:
                return "DESPACHADO";
            case EN_ENTREGA:
                return "EN ENTREGA";
            case ENTREGADO:
                return "ENTREGADO";
            default:
                return "DESCONOCIDO";
        }
    }

    @Override
    public String toString() {
        return "Paquete{" +
                "id=" + id +
                ", estado=" + obtenerNombreEstado() +
                '}';
    }

    // Método estático para obtener el nombre del estado a partir de un número
    public String obtenerEstadoPorNumero(int estado) {
        switch (estado) {
            case EN_OFICINA:
                return "EN OFICINA";
            case RECOGIDO:
                return "RECOGIDO";
            case EN_CLASIFICACION:
                return "EN CLASIFICACION";
            case DESPACHADO:
                return "DESPACHADO";
            case EN_ENTREGA:
                return "EN ENTREGA";
            case ENTREGADO:
                return "ENTREGADO";
            default:
                return "DESCONOCIDO";
        }
    }

}

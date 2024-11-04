package caso3;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClienteConDelegados {

    public static void iniciar(int numeroClientes) {
        List<ClienteDelegado> clientes = new ArrayList<>();

        // Crear y ejecutar múltiples instancias de ClienteDelegado
        for (int i = 0; i < numeroClientes; i++) {
            ClienteDelegado cliente = new ClienteDelegado(i, i);
            clientes.add(cliente);
            cliente.start();
        }

        for (ClienteDelegado cliente : clientes) {
            try {
                cliente.join(); 
            } catch (InterruptedException e) {
                System.err.println("Error esperando al cliente: " + cliente.getId());
                e.printStackTrace();
            }
        }

        System.out.println("Todos los clientes han terminado de procesar.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el número de clientes delegados a crear: ");
        int numeroClientes = scanner.nextInt();

        iniciar(numeroClientes);
        scanner.close();
    }
}

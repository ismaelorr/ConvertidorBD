import org.convertidor.controllers.*;


/**
 * Clase principal que inicia la aplicación.
 * Crea una instancia de MainController, que es el controlador principal de la aplicación.
 *
 *     @Author Ismael Orellana Bello
 *     @Date 12/06/2023
 *     @Version 1.0
 */
public class Main {

    /**
     * Punto de entrada de la aplicación.
     * Crea una instancia de MainController, que se encarga de iniciar y controlar el flujo de la aplicación.
     *
     * @param args Argumentos de línea de comandos (no se utilizan en esta aplicación).
     * @throws Exception Si ocurre alguna excepción durante la ejecución.
     */
    public static void main(String[] args) throws Exception{
        new MainController();
    }
}

package servidor;

import java.io.*;
import java.net.*;

public class Servidor {
    // porta onde o servidor vai estabelecer a conexao
    private static final int PORTA = 5040;

    public static void main(String[] args) {

        try {

            // cria o socket do servidor
            ServerSocket servidorSocket = new ServerSocket(PORTA);
            System.out.println("servidor rodando na porta " + PORTA);

            // espera conexoes de clientes
            while (true) {

                Socket clienteSocket = servidorSocket.accept();
                System.out.println("novo cliente conectado: " + clienteSocket.getInetAddress());

                // cria uma thread pra cada cliente
                GerenciadorCliente gerenciador = new GerenciadorCliente(clienteSocket);
                new Thread(gerenciador).start();
            }

        } catch (IOException e) {
            // mostra erro se der problema e diz qual e o erro
            System.out.println("erro no servidor: " + e.getMessage());

        }
    }
}
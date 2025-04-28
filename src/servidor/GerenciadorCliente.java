package servidor;

import java.io.*;
import java.net.*;

public class GerenciadorCliente implements Runnable {

    private Socket clienteSocket;
    private PrintWriter saida;
    private BufferedReader entrada;
    private String usuario;

    // usuarios e senhas fixos
    private static final String[][] USUARIOS = {
            {"italo", "senha123"},
    };

    public GerenciadorCliente(Socket socket) {

        this.clienteSocket = socket;

        try {

            // cria entrada e saida pra comunicar com cliente
            saida = new PrintWriter(clienteSocket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));

        } catch (IOException e) {

            System.out.println("erro ao criar entrada/saida: " + e.getMessage());

        }
    }

    @Override

    public void run() {

        try {

            // faz o login
            if (!fazerLogin()) {

                saida.println("login falhou");
                clienteSocket.close();
                return;

            }
            saida.println("login ok");


            // cria as pastas do usuario
            GerenciadorArquivos.criarPastasUsuario(usuario);

            // loop pra receber comandos do cliente
            String comando;
            while ((comando = entrada.readLine()) != null) {

                if (comando.equals("listar")) {
                    // lista os arquivos
                    String lista = GerenciadorArquivos.listarArquivos(usuario);
                    saida.println(lista);
                    saida.println(""); // envia linha vazia pra marcar fim da lista

                } else if (comando.startsWith("download ")) {
                    // faz download de um arquivo
                    String nomeArquivo = comando.substring(9); // pega nome completo
                    GerenciadorArquivos.enviarArquivo(usuario, nomeArquivo, clienteSocket.getOutputStream());

                } else if (comando.startsWith("upload ")) {
                    // faz upload de um arquivo
                    String nomeArquivo = comando.substring(7); // pega nome completo

                    try {
                        GerenciadorArquivos.receberArquivo(usuario, nomeArquivo, clienteSocket.getInputStream());
                        saida.println("upload concluido");

                    } catch (IOException e) {
                        saida.println("erro no upload: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("erro com cliente: " + e.getMessage());
        } finally {
            try {

                clienteSocket.close();

            } catch (IOException e) {

                System.out.println("erro ao fechar socket: " + e.getMessage());
            }
        }
    }

    // verifica usuario e senha
    private boolean fazerLogin() throws IOException {

        saida.println("digite usuario:");
        String usuario = entrada.readLine();
        saida.println("digite senha:");
        String senha = entrada.readLine();

        for (String[] credenciais : USUARIOS) {
            if (credenciais[0].equals(usuario) && credenciais[1].equals(senha)) {
                this.usuario = usuario;
                return true;
            }
        }
        return false;
    }
}
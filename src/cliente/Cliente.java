package cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Cliente {
    private static final String HOST = "localhost";
    private static final int PORTA = 5040;

    public static void main(String[] args) {

        try {
            // se conecta ao servidor usando o host e a porta criados na classe
            Socket socket = new Socket(HOST, PORTA);
            System.out.println("conectado ao servidor");


            // cria entrada e saida de dados com scanner, printwriter e buffered reader
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);


            // faz login com as credenciais que estao no servidor
            System.out.println(entrada.readLine()); // metodo pro digite usuario
            String usuario = scanner.nextLine();
            saida.println(usuario);
            System.out.println(entrada.readLine()); // metodo pro digite senha
            String senha = scanner.nextLine();
            saida.println(senha);



            // verifica se o login deu certo
            String resposta = entrada.readLine();


            if (!resposta.equals("login ok")) {

                System.out.println("falha no login"); // se der errado retorna falha
                socket.close();

                return;
            }
            System.out.println("login bem sucedido"); // se der certo retorna um ok

            // loop pra mostrar o menu
            while (true) {

                System.out.println("\n1. listar arquivos");
                System.out.println("2. fazer download");
                System.out.println("3. fazer upload");
                System.out.println("4. sair");
                System.out.print("escolha: ");
                String opcao = scanner.nextLine();

                if (opcao.equals("1")) {

                    // lista arquivos
                    saida.println("listar");
                    String linha;
                    System.out.println("arquivos disponiveis:");

                    while (!(linha = entrada.readLine()).isEmpty()) {
                        System.out.println(linha);
                    }


                } else if (opcao.equals("2")) {


                    // faz download
                    System.out.print("nome do arquivo: ");
                    String nomeArquivo = scanner.nextLine();
                    saida.println("download " + nomeArquivo);
                    receberArquivo(nomeArquivo, socket.getInputStream());

                } else if (opcao.equals("3")) {
                    // faz upload
                    System.out.print("caminho do arquivo: ");
                    String caminhoArquivo = scanner.nextLine();
                    File arquivo = new File(caminhoArquivo);

                    if (!arquivo.exists()) {
                        System.out.println("esse arquivo nao existe");

                        continue;
                    }

                    String nomeArquivo = arquivo.getName();
                    saida.println("upload " + nomeArquivo);
                    enviarArquivo(caminhoArquivo, socket.getOutputStream());
                    saida.flush(); // garante que comando foi enviado
                    System.out.println(entrada.readLine()); // upload concluido ou erro

                } else if (opcao.equals("4")) {
                    break;
                }

            }


            socket.close(); // fecha o socket pra evitar problemas (security & data use)

        } catch (IOException e) {

            System.out.println("erro no cliente: " + e.getMessage());
        }

    }

    // envia arquivo pro servidor e retorna com tratamento de erros
    private static void enviarArquivo(String caminhoArquivo, OutputStream saida) throws IOException {
        File arquivo = new File(caminhoArquivo);
        FileInputStream fis = new FileInputStream(arquivo);

        byte[] buffer = new byte[1024];
        int bytesLidos;
        while ((bytesLidos = fis.read(buffer)) != -1) {

            saida.write(buffer, 0, bytesLidos);
        }

        fis.close();
        saida.flush(); // garante que todos os dados foram enviados
    }



    // recebe arquivo do servidor
    private static void receberArquivo(String nomeArquivo, InputStream entrada) throws IOException {

        File arquivo = new File("downloads/" + nomeArquivo);
        arquivo.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(arquivo);

        byte[] buffer = new byte[1024];
        int bytesLidos;
        while ((bytesLidos = entrada.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesLidos);
        }


        fos.close();
        System.out.println("arquivo baixado: " + nomeArquivo);
    }
}
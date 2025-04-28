package servidor;

import java.io.*;

public class GerenciadorArquivos {
    private static final String DIRETORIO_BASE = "armazenamento/";

    // cria pastas pra um usuario
    public static void criarPastasUsuario(String usuario) {

        String[] tipos = {"pdf", "jpg", "txt"};

        for (String tipo : tipos) {

            File pasta = new File(DIRETORIO_BASE + usuario + "/" + tipo);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }
        }
    }

    // lista todos os arquivos do usuario
    public static String listarArquivos(String usuario) {

        StringBuilder lista = new StringBuilder();
        String[] tipos = {"pdf", "jpg", "txt"};

        for (String tipo : tipos) {
            File pasta = new File(DIRETORIO_BASE + usuario + "/" + tipo);
            File[] arquivos = pasta.listFiles();

            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    lista.append(tipo).append("/").append(arquivo.getName()).append("\n");
                }
            }
        }
        return lista.toString();
    }

    // envia um arquivo pro cliente
    public static void enviarArquivo(String usuario, String nomeArquivo, OutputStream saida) throws IOException {

        String tipo = nomeArquivo.split("\\.")[1].toLowerCase();
        File arquivo = new File(DIRETORIO_BASE + usuario + "/" + tipo + "/" + nomeArquivo);

        if (arquivo.exists()) {

            FileInputStream fis = new FileInputStream(arquivo);
            byte[] buffer = new byte[1024];
            int bytesLidos;
            while ((bytesLidos = fis.read(buffer)) != -1) {
                saida.write(buffer, 0, bytesLidos);


            }
            fis.close();
        }
    }

    // recebe um arquivo do cliente
    public static void receberArquivo(String usuario, String nomeArquivo, InputStream entrada) throws IOException {

        String tipo = nomeArquivo.split("\\.")[1].toLowerCase();
        File arquivo = new File(DIRETORIO_BASE + usuario + "/" + tipo + "/" + nomeArquivo);
        FileOutputStream fos = new FileOutputStream(arquivo);

        byte[] buffer = new byte[1024];
        int bytesLidos;
        while ((bytesLidos = entrada.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesLidos);


        }
        fos.close();
    }
}
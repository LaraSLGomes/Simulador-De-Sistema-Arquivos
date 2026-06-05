import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;

public class Shell {
    public static void main(String[] args) throws IOException {
        System.out.println("Inicializando Sistema de Arquivos...");
        FileSystemSimulator fs = new FileSystemSimulator(Paths.get("disco_journal.log"));
        fs.carregarJournal();
        
        System.out.println(" Simulador de SO: Journaling e File System ativo");
        System.out.println(" Comandos disponíveis: touch, copy, rm, mkdir, rmdir, mv, ls, log, exit");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        while (true) {
            System.out.print("usuario@simulador:~$ ");
            String linha = br.readLine();
            
            if (linha == null) break;
            linha = linha.trim();
            if (linha.isEmpty()) continue;
            
            String[] partes = linha.split(" ");
            String comando = partes[0].toLowerCase();
            
            try {
                switch (comando) {
                    case "touch":
                        if (partes.length < 2) { 
                            System.out.println("Uso: touch /caminho/arquivo [conteudo texto...]"); 
                            break; 
                        }
                        String conteudo = partes.length > 2 ? String.join(" ", Arrays.copyOfRange(partes, 2, partes.length)) : "";
                        fs.criarArquivo(partes[1], conteudo);
                        System.out.println("Arquivo criado com sucesso.");
                        break;

                    case "mkdir":
                        if (partes.length < 2) { System.out.println("Uso: mkdir /caminho/pasta"); break; }
                        fs.criarDiretorio(partes[1]);
                        System.out.println("Diretório criado com sucesso.");
                        break;

                    case "copy":
                        if (partes.length < 3) { System.out.println("Uso: copy /origem /destino"); break; }
                        fs.copiar(partes[1], partes[2]);
                        System.out.println("Arquivo copiado.");
                        break;

                    case "rm":
                    case "rmdir":
                        if (partes.length < 2) { System.out.println("Uso: " + comando + " /caminho"); break; }
                        fs.deletar(partes[1]);
                        System.out.println("Item removido.");
                        break;

                    case "mv":
                        if (partes.length < 3) { System.out.println("Uso: mv /caminho novo_nome"); break; }
                        fs.renomear(partes[1], partes[2]);
                        System.out.println("Item renomeado.");
                        break;

                    case "ls":
                        String caminhoLs = partes.length < 2 ? "/" : partes[1];
                        fs.listar(caminhoLs);
                        break;

                    case "log":
                        System.out.println("--- Início do Journal ---");
                        java.nio.file.Files.lines(Paths.get("disco_journal.log")).forEach(System.out::println);
                        System.out.println("--- Fim do Journal ---");
                        break;

                    case "exit":
                        System.out.println("Encerrando simulador...");
                        return;

                    default:
                        System.out.println("Comando desconhecido. Comandos suportados: touch, mkdir, copy, rm, rmdir, mv, ls, log, exit");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Erro na execução: " + e.getMessage());
            }
        }
    }
}
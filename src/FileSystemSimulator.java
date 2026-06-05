import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FileSystemSimulator {
    private final Directory raiz;
    private final Journal journal;

    public FileSystemSimulator(Path arquivoJournal) {
        this.raiz = new Directory("", null);
        this.journal = new Journal(arquivoJournal);
    }

    public void carregarJournal() throws IOException {
        for (String linha : journal.lerHistorico()) {
            if (linha.trim().isEmpty()) continue;
            
            String[] partes = linha.split("\\|");
            String operacao = partes[0];
            
            switch (operacao) {
                case "CRIAR_ARQUIVO":
                    String conteudo = partes.length > 2 ? partes[2] : "";
                    criarArquivoInterno(partes[1], conteudo);
                    break;
                case "CRIAR_DIRETORIO":
                    criarDiretorioInterno(partes[1]);
                    break;
                case "DELETAR":
                    deletarInterno(partes[1]);
                    break;
                case "COPIAR":
                    copiarInterno(partes[1], partes[2]);
                    break;
                case "RENOMEAR":
                    renomearInterno(partes[1], partes[2]);
                    break;
            }
        }
    }

    public void criarArquivo(String caminho, String conteudo) {
        journal.registrarOperacao("CRIAR_ARQUIVO|" + caminho + "|" + conteudo);
        criarArquivoInterno(caminho, conteudo);
    }

    public void criarDiretorio(String caminho) {
        journal.registrarOperacao("CRIAR_DIRETORIO|" + caminho);
        criarDiretorioInterno(caminho);
    }

    public void deletar(String caminho) {
        journal.registrarOperacao("DELETAR|" + caminho);
        deletarInterno(caminho);
    }

    public void copiar(String origem, String destino) {
        journal.registrarOperacao("COPIAR|" + origem + "|" + destino);
        copiarInterno(origem, destino);
    }

    public void renomear(String caminho, String novoNome) {
        journal.registrarOperacao("RENOMEAR|" + caminho + "|" + novoNome);
        renomearInterno(caminho, novoNome);
    }

    public void listar(String caminho) {
        FSNode no = resolverCaminho(caminho);
        if (no instanceof Directory) {
            Directory dir = (Directory) no;
            if (dir.getFilhos().isEmpty()) {
                System.out.println("(Diretório vazio)");
            } else {
                for (Map.Entry<String, FSNode> entrada : dir.getFilhos().entrySet()) {
                    FSNode filho = entrada.getValue();
                    String tipo = (filho instanceof Directory) ? "[DIR] " : "[ARQ] ";
                    System.out.println(tipo + filho.getNome());
                }
            }
        } else if (no instanceof File) {
            System.out.println(((File) no).getConteudo());
        } else {
            throw new IllegalArgumentException("Caminho não encontrado: " + caminho);
        }
    }

    private void criarArquivoInterno(String caminho, String conteudo) {
        String[] partes = quebrarCaminho(caminho);
        String nome = partes[partes.length - 1];
        Directory pai = obterOuCriarPais(partes);
        
        pai.adicionarFilho(new File(nome, pai, conteudo));
    }

    private void criarDiretorioInterno(String caminho) {
        obterOuCriarPais(quebrarCaminho(caminho + "/dummy"));
    }

    private void deletarInterno(String caminho) {
        Path p = Paths.get(caminho);
        String nome = p.getFileName().toString();
        String caminhoPai = p.getParent() == null ? "/" : p.getParent().toString().replace("\\", "/");
        
        Directory pai = (Directory) resolverCaminho(caminhoPai);
        if (pai != null) pai.removerFilho(nome);
    }

    private void copiarInterno(String origem, String destino) {
        FSNode noOrigem = resolverCaminho(origem);
        if (!(noOrigem instanceof File)) {
            throw new IllegalArgumentException("Apenas arquivos podem ser copiados no momento.");
        }
        
        String[] partes = quebrarCaminho(destino);
        String nomeDestino = partes[partes.length - 1];
        Directory paiDestino = obterOuCriarPais(partes);
        
        File arquivoOrigem = (File) noOrigem;
        paiDestino.adicionarFilho(new File(nomeDestino, paiDestino, arquivoOrigem.getConteudo()));
    }

    private void renomearInterno(String caminho, String novoNome) {
        Path p = Paths.get(caminho);
        String nomeAntigo = p.getFileName().toString();
        String caminhoPai = p.getParent() == null ? "/" : p.getParent().toString().replace("\\", "/");
        
        Directory pai = (Directory) resolverCaminho(caminhoPai);
        if (pai == null) return;
        
        FSNode no = pai.removerFilho(nomeAntigo);
        if (no instanceof File) {
            pai.adicionarFilho(new File(novoNome, pai, ((File) no).getConteudo()));
        } else if (no instanceof Directory) {
            Directory d = (Directory) no;
            d.nome = novoNome;
            pai.adicionarFilho(d);
        }
    }

    private FSNode resolverCaminho(String caminho) {
        if (caminho == null || caminho.equals("/") || caminho.isEmpty()) return raiz;
        String[] partes = quebrarCaminho(caminho);
        Directory atual = raiz;
        
        for (int i = 0; i < partes.length; i++) {
            FSNode filho = atual.getFilho(partes[i]);
            if (filho == null) return null;
            if (i == partes.length - 1) return filho;
            if (filho instanceof Directory) atual = (Directory) filho;
            else return null;
        }
        return atual;
    }

    private Directory obterOuCriarPais(String[] partes) {
        Directory atual = raiz;
        for (int i = 0; i < partes.length - 1; i++) {
            FSNode filho = atual.getFilho(partes[i]);
            if (filho == null) {
                Directory novoDir = new Directory(partes[i], atual);
                atual.adicionarFilho(novoDir);
                atual = novoDir;
            } else if (filho instanceof Directory) {
                atual = (Directory) filho;
            }
        }
        return atual;
    }

    private String[] quebrarCaminho(String caminho) {
        if (caminho == null) return new String[0];
        String p = caminho.startsWith("/") ? caminho.substring(1) : caminho;
        return p.isEmpty() ? new String[0] : p.split("/");
    }
}
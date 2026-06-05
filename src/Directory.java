import java.util.LinkedHashMap;
import java.util.Map;

public class Directory extends FSNode {

    private final Map<String, FSNode> filhos = new LinkedHashMap<>();

    public Directory(String nome, Directory pai) {
        super(nome, pai);
    }

    public Map<String, FSNode> getFilhos() { 
        return filhos; 
    }

    public void adicionarFilho(FSNode node) {
        filhos.put(node.getNome(), node);
    }

    public FSNode getFilho(String nome) { 
        return filhos.get(nome); 
    }

    public FSNode removerFilho(String nome) { 
        return filhos.remove(nome); 
    }
}
public class File extends FSNode {
    private String conteudo;

    public File(String nome, Directory pai, String conteudo) {
        super(nome, pai);
        this.conteudo = (conteudo == null) ? "" : conteudo;
    }

    public String getConteudo() { 
        return conteudo; 
    }

    public void setConteudo(String conteudo) { 
        this.conteudo = (conteudo == null) ? "" : conteudo; 
    }
}
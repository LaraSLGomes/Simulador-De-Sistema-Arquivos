public abstract class FSNode {
    protected String nome;
    protected Directory pai;

    public FSNode(String nome, Directory pai) {
        this.nome = nome;
        this.pai = pai;
    }

    public String getNome() { 
        return nome; 
    }

    public String getCaminho() {
        if (pai == null) return "/";
        String caminhoPai = pai.getCaminho();
        if ("/".equals(caminhoPai)) return "/" + nome;
        return caminhoPai + "/" + nome;
    }
}
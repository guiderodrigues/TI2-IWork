package model;

public class Prestador{
    private int ID;
	private String nome;
    private String nomePrestador;
	private String email;
	private String senha;
	private String telefone;
    private int categoria;
    private float nota;
    private int nAvaliacoes;
    private int nPedidos;
    private String descricao;
    private String imagens;
    private float preco;

    public Prestador(){
        this.ID = 0;
        this.nome = "";
        this.nomePrestador = "";
        this.email = "";
        this.senha = "";
        this.telefone = "";
        this.categoria = -1;
        this.nota = 0;
        this.nAvaliacoes = 0;
        this.nPedidos = 0;
        this.descricao = "";
        this.imagens = "";
        this.preco = 0;
    }

    public Prestador(String nome, String nomePrestador, String email, String senha, String telefone, int categoria, float nota, int nAvaliacoes, int nPedidos, String descricao, String imagens, float preco){
        this.ID = 0;
        this.nome = nome;
        this.nomePrestador = nomePrestador;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.categoria = categoria;
        this.nota = nota;
        this.nAvaliacoes = nAvaliacoes;
        this.nPedidos = nPedidos;
        this.descricao = descricao;
        this.imagens = imagens;
        this.preco = preco;
    }

    public Prestador(int ID, String nome, String nomePrestador, String email, String senha, String telefone, int categoria, float nota, int nAvaliacoes, int nPedidos, String descricao, String imagens, float preco){
        this.ID = ID;
        this.nome = nome;
        this.nomePrestador = nomePrestador;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.categoria = categoria;
        this.nota = nota;
        this.nAvaliacoes = nAvaliacoes;
        this.nPedidos = nPedidos;
        this.descricao = descricao;
        this.imagens = imagens;
        this.preco = preco;
    }

    public int getID(){
        return this.ID;
    }
    public void setID(int ID){
        this.ID = ID;
    }

    public String getNome(){
        return this.nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }

    public String getNomePrestador(){
        return this.nomePrestador;
    }
    public void setNomePrestador(String nomePrestador){
        this.nomePrestador = nomePrestador;
    }

    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getSenha(){
        return this.senha;
    }
    public void setSenha(String senha){
        this.senha = senha;
    }

    public String getTelefone(){
        return this.telefone;
    }
    public void setTelefone(String telefone){
        this.telefone = telefone;
    }

    public int getCategoria(){
        return this.categoria;
    }
    public void setCategoria(int categoria){
        this.categoria = categoria;
    }

    public float getNota(){
        return this.nota;
    }
    public void setNota(float nota){
        this.nota = nota;
    }

    public int getNAvaliacoes(){
        return this.nAvaliacoes;
    }
    public void setNAvaliacoes(int nAvaliacoes){
        this.nAvaliacoes = nAvaliacoes;
    }

    public int getNPedidos(){
        return this.nPedidos;
    }
    public void setNPedidos(int nPedidos){
        this.nPedidos = nPedidos;
    }

    public String getDescricao(){
        return this.descricao;
    }
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }

    public String getImagens(){
        return this.imagens;
    }
    public void setImagens(String imagens){
        this.imagens = imagens;
    }

    public float getPreco(){
        return this.preco;
    }
    public void setPreco(float preco){
        this.preco = preco;
    }


    public void incrementNPedidos(){
        this.nPedidos++;
    }

    public void addNewAvaliacao(int nota){
        this.nAvaliacoes++;
        this.nota = (this.nota + nota)/this.nAvaliacoes;
    }
    public void changePreviousAvaliacao(int previousNota, int newNota){
        this.nota = (this.nota*this.nAvaliacoes - previousNota + newNota)/this.nAvaliacoes;
    }
    public void removeAvaliacao(int nota){
        this.nAvaliacoes--;
        this.nota = (this.nota*this.nAvaliacoes - nota)/this.nAvaliacoes;
    }

    public boolean addNewImagem(String imagem){
        int nImagens = 0;
        for(int i=0;i<this.imagens.length();i++){
            if(this.imagens.charAt(i) == ';'){
                nImagens++;
            }
        }
        if(nImagens >= 4){
            return false;
        }
        this.imagens += imagem + ";";
        return true;
    }
    public boolean removeImagem(int index){
        String[] imagens = this.imagens.split(";");
        int nImagens = imagens.length;

        if(index >= nImagens){
            return false;
        }

        String newImagens = "";
        for(int i=0;i<nImagens;i++){
            if(i != index){
                newImagens += imagens[i] + ";";
            }
        }
        this.imagens = newImagens;
        return true;
    }

    public String toString(){
        return "ID: " + this.ID + "\nNome: " + this.nome + "\nNomePrestador: " + this.nomePrestador + "\nEmail: " + this.email + "\nSenha: " + this.senha + "\nTelefone: " + this.telefone + "\nCategoria: " + this.categoria + "\nNota: " + this.nota + "\nNAvaliacoes: " + this.nAvaliacoes + "\nNPedidos: " + this.nPedidos + "\nDescricao: " + this.descricao + "\nImagens: " + this.imagens + "\nPreco: " + this.preco;
    }

    public String toJson(){
        return "{\"ID\": " + this.ID + ", \"nome\": \"" + this.nome + "\", \"nomePrestador\": \"" + this.nomePrestador + "\", \"email\": \"" + this.email + "\", \"senha\": \"" + this.senha + "\", \"telefone\": \"" + this.telefone + "\", \"categoria\": " + this.categoria + ", \"nota\": " + this.nota + ", \"nAvaliacoes\": " + this.nAvaliacoes + ", \"nPedidos\": " + this.nPedidos + ", \"descricao\": \"" + this.descricao + "\", \"imagens\": \"" + this.imagens + "\", \"preco\": " + this.preco + "}";
    }
    public String toJsonSafe(){
        return "{\"ID\": " + this.ID + ", \"nome\": \"" + this.nome + "\", \"nomePrestador\": \"" + this.nomePrestador + "\", \"email\": \"" + this.email + "\", \"telefone\": \"" + this.telefone + "\", \"categoria\": " + this.categoria + ", \"nota\": " + this.nota + ", \"nAvaliacoes\": " + this.nAvaliacoes + ", \"nPedidos\": " + this.nPedidos + ", \"descricao\": \"" + this.descricao + "\", \"imagens\": \"" + this.imagens + "\", \"preco\": " + this.preco + "}";
    }

}
package model;

public class Servico{
    private int ID;
    private String data;
    private String hora;
    private int avaliacao;
    private int idConsumidor;
    private int idPrestador;
    private String comentario;
    private String endereco;
    
    public Servico(){
        this.ID = 0;
        this.data = "";
        this.hora = "";
        this.avaliacao = -1;
        this.idConsumidor = -1;
        this.idPrestador = -1;
        this.comentario = "";
        this.endereco = "";
    }

    public Servico(String data, String hora, int avaliacao, int idConsumidor, int idPrestador, String comentario, String endereco){
        this.ID = 0;
        this.data = data;
        this.hora = hora;
        this.avaliacao = avaliacao;
        this.idConsumidor = idConsumidor;
        this.idPrestador = idPrestador;
        this.comentario = comentario;
        this.endereco = endereco;
    }

    public Servico(int ID, String data, String hora, int avaliacao, int idConsumidor, int idPrestador, String comentario, String endereco){
        this.ID = ID;
        this.data = data;
        this.hora = hora;
        this.avaliacao = avaliacao;
        this.idConsumidor = idConsumidor;
        this.idPrestador = idPrestador;
        this.comentario = comentario;
        this.endereco = endereco;
    }

    public int getID(){
        return this.ID;
    }
    public void setID(int ID){
        this.ID = ID;
    }

    public String getData(){
        return this.data;
    }
    public void setData(String data){
        this.data = data;
    }

    public String getHora(){
        return this.hora;
    }
    public void setHora(String hora){
        this.hora = hora;
    }

    public int getAvaliacao(){
        return this.avaliacao;
    }
    public void setAvaliacao(int avaliacao){
        this.avaliacao = avaliacao;
    }

    public int getIdConsumidor(){
        return this.idConsumidor;
    }
    public void setIdConsumidor(int idConsumidor){
        this.idConsumidor = idConsumidor;
    }

    public int getIdPrestador(){
        return this.idPrestador;
    }
    public void setIdPrestador(int idPrestador){
        this.idPrestador = idPrestador;
    }

    public String getComentario(){
        return this.comentario;
    }
    public void setComentario(String comentario){
        this.comentario = comentario;
    }

    public String getEndereco(){
        return this.endereco;
    }
    public void setEndereco(String endereco){
        this.endereco = endereco;
    }

    public String toString(){
        return "ID: " + this.ID + "\nData: " + this.data + "\nHora: " + this.hora + "\nAvaliacao: " + this.avaliacao + "\nID Consumidor: " + this.idConsumidor + "\nID Prestador: " + this.idPrestador + "\nComentario: " + this.comentario + "\nEndereco: " + this.endereco;
    }

    public String toJson(){
        return "{\"ID\": " + this.ID + ", \"data\": \"" + this.data + "\", \"hora\": \"" + this.hora + "\", \"avaliacao\": " + this.avaliacao + ", \"idConsumidor\": " + this.idConsumidor + ", \"idPrestador\": " + this.idPrestador + ", \"comentario\": \"" + this.comentario + "\", \"endereco\": \"" + this.endereco + "\"}";
    }

}
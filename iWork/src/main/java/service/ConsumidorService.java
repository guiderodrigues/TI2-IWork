package service;

import model.Consumidor;
import dao.ConsumidorDAO;
import spark.Request;
import spark.Response;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ConsumidorService {
    private ConsumidorDAO consumidorDAO = new ConsumidorDAO();
    public ConsumidorService(){
        consumidorDAO.conectar();
    }

    public String generateHash(String str){
        String bcryptHashString = BCrypt.withDefaults().hashToString(12, str.toCharArray());
        return bcryptHashString;
    }

    public boolean verifyHash(String str, String hash){
        BCrypt.Result result = BCrypt.verifyer().verify(str.toCharArray(), hash);
        return result.verified;
    }

    public boolean inserir(Request req, Response res){
        try{
            Consumidor consumidor = new Consumidor();
            consumidor.setNome(req.queryParams("nome"));
            consumidor.setTelefone(req.queryParams("telefone"));
            consumidor.setEmail(req.queryParams("email"));
            String senha = req.queryParams("senha");
            consumidor.setSenha(generateHash(senha));
            consumidor.setCartao(req.queryParams("cartao"));
            consumidor.setValidade(req.queryParams("validade"));
            consumidor.setCvv(req.queryParams("cvv"));
            
            consumidorDAO.inserirElemento(consumidor);
            res.status(200);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return false;
        }
    } 

    public String get(Request req, Response res){
        try{
            String id = req.params(":id");
            Consumidor consumidor = consumidorDAO.getElemento(Integer.parseInt(id));
            res.status(200);
            return consumidor.toJsonSafe();
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

    public String getSafe(Request req, Response res){
        try{
            String id = req.params(":id");
            Consumidor consumidor = consumidorDAO.getElemento(Integer.parseInt(id));
            res.status(200);
            return consumidor.toJson();
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }


    public String login(Request req, Response res){
        try{
            String email = req.queryParams("email");
            String senha = req.queryParams("senha");
            Consumidor consumidor = consumidorDAO.getElemento(email);
            if(consumidor == null){
                res.status(404);
                return null;
            }
            if(verifyHash(senha, consumidor.getSenha())){
                res.status(200);
                res.type("application/json");
                return "{\"token\":\"" + generateHash(consumidor.getSenha()) + "\", \"id\": " + consumidor.getID() + ", \"type\": \"consumidor\"}";
            }else{
                res.status(401);
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }
}

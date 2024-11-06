package service;

import model.Prestador;
import dao.PrestadorDAO;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dao.HorariosDiaDAO;
import spark.Request;
import spark.Response;

public class PrestadorService {
    private PrestadorDAO prestadorDAO = new PrestadorDAO();
    

    public PrestadorService(){
        prestadorDAO.conectar();
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
            Prestador prestador = new Prestador();
            prestador.setNome("");
            prestador.setNomePrestador(req.queryParams("nome"));
            prestador.setEmail(req.queryParams("email"));
            String senha = req.queryParams("senha");
            prestador.setSenha(generateHash(senha));
            prestador.setTelefone(req.queryParams("telefone"));
            prestador.setCategoria(0);
            prestador.setNota(0);
            prestador.setNAvaliacoes(0);
            prestador.setNPedidos(0);
            prestador.setDescricao("");
            prestador.setImagens("");
            prestador.setPreco(0);
            
            prestadorDAO.inserirElemento(prestador);

            int newID = prestadorDAO.getLargestID();
            HorariosDiaDAO horariosDiaDAO = new HorariosDiaDAO();
            horariosDiaDAO.conectar();
            for(int i = 0; i < 7; i++){
                horariosDiaDAO.inserirElemento(newID, i, "");
            }

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
            Prestador prestador = prestadorDAO.getElemento(Integer.parseInt(id));
            res.status(200);
            return prestador.toJsonSafe();
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

    public String getPrestadores(Request req, Response res){
        try{
            String categoriaStr = req.queryParams("categoria");
            String search = req.queryParams("search");
            String minPriceStr = req.queryParams("minPrice");
            String maxPriceStr = req.queryParams("maxPrice");
            String orderStr = req.queryParams("order");

            int minPrice = -1;
            int maxPrice = -1;
            int order = 0;
            int categoria = 0;

            if(minPriceStr != null && !minPriceStr.equals("")){
                minPrice = Integer.parseInt(minPriceStr);
            }
            if(maxPriceStr != null && !maxPriceStr.equals("")){
                maxPrice = Integer.parseInt(maxPriceStr);
            }
            if(orderStr != null){
                order = Integer.parseInt(req.queryParams("order"));
            }
            if(categoriaStr != null){
                categoria = Integer.parseInt(req.queryParams("categoria"));
            }


            String prestadores = prestadorDAO.toJsonSafe(prestadorDAO.get(categoria, search, minPrice, maxPrice, order));
            res.status(200);
            return prestadores;
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return "Tá com um erro aq fi";
        }
    }

    public String login(Request req, Response res){
        try{
            String email = req.queryParams("email");
            String senha = req.queryParams("senha");
            String senhaHash = prestadorDAO.getSenhaFromEmail(email);
            if(senhaHash == null){
                res.status(404);
                return null;
            }
            if(verifyHash(senha, senhaHash)){
                res.status(200);
                res.type("application/json");
                return "{\"token\":\"" + generateHash(senhaHash) + "\", \"id\": " + prestadorDAO.getIDFromEmail(email) + ", \"type\": \"prestador\"}";
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

    public String update(Request req, Response res){
        try{
            String imagens = req.queryParams("imagens");
            String nome = req.queryParams("nome");
            String descricao = req.queryParams("descricao");
            String token = req.queryParams("token");

            String precoStr = req.queryParams("preco");
            String idStr = req.queryParams("id");
            String categoriaStr = req.queryParams("categoria");

            if(idStr == null || token == null){
                res.status(400);
                return "{\"status\":\"fail\"}";
            }


            int id = -1;
            float preco = -1.0f;
            int categoria = 0;

            if(precoStr != null && !precoStr.equals("")){
                preco = Float.parseFloat(precoStr);
            }
            if(idStr != null && !idStr.equals("")){
                id = Integer.parseInt(idStr);
            }
            if(categoriaStr != null && !categoriaStr.equals("")){
                categoria = Integer.parseInt(categoriaStr);
            }

            if(!verifyHash(prestadorDAO.getSenhaFromID(id), token)){
                res.status(401);
                return "{\"status\":\"not_authenticated\"}";
            }

            prestadorDAO.atualizarElemento(id, nome, imagens, descricao, preco, categoria);

            res.status(200);
            res.type("application/json");
            return "{\"status\":\"ok\"}";
            
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return "{\"status\":\"fail\"}";
        }
    }
}

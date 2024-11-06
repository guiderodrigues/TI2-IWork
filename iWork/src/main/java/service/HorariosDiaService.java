package service;

import model.HorariosDia;
import at.favre.lib.crypto.bcrypt.BCrypt;
import dao.HorariosDiaDAO;
import dao.PrestadorDAO;
import spark.Request;
import spark.Response;

public class HorariosDiaService {
    private HorariosDiaDAO horariosDiaDAO = new HorariosDiaDAO();
    private PrestadorDAO prestadorDAO = new PrestadorDAO();

    public HorariosDiaService(){
        horariosDiaDAO.conectar();
        prestadorDAO.conectar();
    }

    public boolean verifyHash(String str, String hash){
        BCrypt.Result result = BCrypt.verifyer().verify(str.toCharArray(), hash);
        return result.verified;
    }


    public boolean inserir(Request req, Response res){
        try{
            HorariosDia horariosDia = new HorariosDia();
            horariosDia.setIdPrestador(Integer.parseInt(req.queryParams("idPrestador")));
            horariosDia.setDiaDaSemana(Integer.parseInt(req.queryParams("diaDaSemana")));
            horariosDia.setHoras(req.queryParams("horas"));
            
            horariosDiaDAO.inserirElemento(horariosDia);
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
            HorariosDia horariosDia = horariosDiaDAO.getElemento(Integer.parseInt(id));
            res.status(200);
            return horariosDia.toJson();
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

    public String getHorariosPrestador(Request req, Response res){
        try{
            String id = req.params(":id");
            String horariosDiaJson = horariosDiaDAO.toJson(horariosDiaDAO.getHorariosPrestador(Integer.parseInt(id)));
            res.status(200);
            return horariosDiaJson;
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

    public String getHorariosPrestadorDia(Request req, Response res){
        try{
            String id = req.params(":id");
            String dia = req.params(":dia");

            String horariosDiaJson = horariosDiaDAO.toJson(horariosDiaDAO.getHorariosPrestadorDia(Integer.parseInt(id), Integer.parseInt(dia)));
            res.status(200);
            return horariosDiaJson;
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

    public String update(Request req, Response res){
        try{
            String idStr = req.queryParams("id");
            if(idStr == null) throw new Exception("id is null");
            int id = Integer.parseInt(idStr);
            String token = req.queryParams("token");
            String[] horas = new String[7];

            for(int i=0;i<7;i++){
                horas[i] = req.queryParams("dia"+i);
            }

            if(!verifyHash(prestadorDAO.getSenhaFromID(id), token)){
                res.status(401);
                return "{\"status\":\"not_authenticated\"}";
            }

            boolean status = horariosDiaDAO.updateHorarios(id, horas);
            if(!status){
                res.status(500);
                return "{\"status\":\"error\"}";
            }

            res.status(200);
            return "{\"status\":\"ok\"}";

        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

}

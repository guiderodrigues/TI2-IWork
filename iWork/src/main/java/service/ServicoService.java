package service;

import model.Consumidor;
import model.HorariosDia;
import model.Prestador;
import model.Servico;
import dao.ServicoDAO;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dao.ConsumidorDAO;
import dao.PrestadorDAO;
import dao.HorariosDiaDAO;
import model.HorariosDia;
import spark.Request;
import spark.Response;

import java.util.List;

public class ServicoService {
    private ServicoDAO servicoDAO = new ServicoDAO();
    private ConsumidorDAO consumidorDAO = new ConsumidorDAO();
    private HorariosDiaDAO horariosDiaDAO = new HorariosDiaDAO();
    private PrestadorDAO prestadorDAO = new PrestadorDAO();

    public ServicoService(){
        servicoDAO.conectar();
        consumidorDAO.conectar();
        horariosDiaDAO.conectar();
        prestadorDAO.conectar();
    }

    public boolean verifyHash(String str, String hash){
        BCrypt.Result result = BCrypt.verifyer().verify(str.toCharArray(), hash);
        return result.verified;
    }

    public boolean inserirCompleto(Request req, Response res){
        try{
            Servico servico = new Servico();
            servico.setData(req.queryParams("data"));
            servico.setHora(req.queryParams("hora"));
            servico.setAvaliacao(Integer.parseInt(req.queryParams("avaliacao")));
            servico.setIdConsumidor(Integer.parseInt(req.queryParams("idConsumidor")));
            servico.setIdPrestador(Integer.parseInt(req.queryParams("idPrestador")));
            servico.setComentario(req.queryParams("comentario"));
            servico.setEndereco(req.queryParams("endereco"));
            
            servicoDAO.inserirElemento(servico);
            res.status(200);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return false;
        }
    }

    public int getDayOfTheWeek(String date){
        String[] dateSplit = date.split("-");
        int year = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int day = Integer.parseInt(dateSplit[2]);

        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(year, month, day);
        int dayOfWeek = c.get(java.util.Calendar.DAY_OF_WEEK);

        return (dayOfWeek + 3) % 7;
    }

    public String inserir(Request req, Response res){
        try{
            String idPrestadorStr = req.queryParams("idPrestador");
            String idConsumidorStr = req.queryParams("idConsumidor");
            String data = req.queryParams("data");
            String hora = req.queryParams("hora");
            String endereco = req.queryParams("endereco");
            String token = req.queryParams("token");

            if(idPrestadorStr == null || idConsumidorStr == null || data == null || hora == null){
                res.status(400);
                return "{\"status\":\"error\"}";
            }

            if(endereco == null){
                endereco = "";
            }

            int idPrestador = Integer.parseInt(idPrestadorStr);
            int idConsumidor = Integer.parseInt(idConsumidorStr);

            if(!verifyHash(consumidorDAO.getElemento(idConsumidor).getSenha(), token)){
                res.status(401);
                return "{\"status\":\"not_authenticated\"}";
            }

            boolean isTimeAvaliable = false;

            HorariosDia hd = horariosDiaDAO.getHorariosPrestadorDia(idPrestador, getDayOfTheWeek(data));
            int[] horas = hd.getHorasArray();
            for(int h:horas){
                if(h == Integer.parseInt(hora.substring(0, 2))){
                    isTimeAvaliable = true;
                    break;
                }
            }

            if(!isTimeAvaliable){
                res.status(400);
                return "{\"status\":\"time_not_avaliable\"}";
            }

            List<Servico> servicosDia = servicoDAO.get(idPrestador, -1, data, data);
            for(Servico s:servicosDia){
                if(s.getHora().equals(hora)){
                    res.status(400);
                    return "{\"status\":\"time_not_avaliable\"}";
                }
            }


            servicoDAO.inserirElemento(idPrestador, idConsumidor, data, hora, endereco);
            prestadorDAO.incrementNPedidos(idPrestador);
            res.status(200);
            return "{\"status\":\"ok\"}";
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return "{\"status\":\"error\"}";
        }
    }

    public String get(Request req, Response res){
        try{
            String id = req.params(":id");
            Servico servico = servicoDAO.getElemento(Integer.parseInt(id));
            res.status(200);
            return servico.toJson();
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

    public String getServicosFromPrestador(Request req, Response res){
        try{
            String id = req.params(":id");
            String start = req.queryParams("start");
            String end = req.queryParams("end");

            String servicosJson = servicoDAO.toJson(servicoDAO.get(Integer.parseInt(id), -1, start, end));
            res.status(200);
            return servicosJson;
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

    public String getServicosFromConsumidor(Request req, Response res){
        try{
            String id = req.params(":id");
            String start = req.queryParams("start");
            String end = req.queryParams("end");

            String servicosJson = servicoDAO.toJson(servicoDAO.get(-1, Integer.parseInt(id), start, end));
            res.status(200);
            return servicosJson;
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return null;
        }
    }

    public String rate(Request req, Response res){
        try{
            String id = req.params(":id");
            String notaStr = req.queryParams("nota");
            String token = req.queryParams("token");
            String comentario = req.queryParams("comentario");

            if(id == null || notaStr == null || token == null){
                res.status(400);
                return "{\"status\":\"error\"}";
            }

            int nota = Integer.parseInt(notaStr);

            if(nota < -1 || nota > 5){
                res.status(400);
                return "{\"status\":\"error\"}";
            }

            Servico servico = servicoDAO.getElemento(Integer.parseInt(id));
            Consumidor consumidor = consumidorDAO.getElemento(servico.getIdConsumidor());

            if(!verifyHash(consumidor.getSenha(), token)){
                res.status(401);
                return "{\"status\":\"not_authenticated\"}";
            }

            if(nota != -1){
                int notaAtual = servico.getAvaliacao();

                if(notaAtual == -1){
                    prestadorDAO.rate(servico.getIdPrestador(), nota);
                }else{
                    prestadorDAO.changeRating(servico.getIdPrestador(), notaAtual, nota);
                }
            }


            servicoDAO.rate(Integer.parseInt(id), nota, comentario);

            res.status(200);
            return "{\"status\":\"ok\"}";

        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return "{\"status\":\"error\"}";
        }
    }

    public String getAvaliacoes(Request req, Response res){
        try{
            String idStr = req.params(":id");

            if(idStr == null){
                res.status(400);
                return "{\"status\":\"error\"}";
            }

            int id = Integer.parseInt(idStr);

            List<Servico> servicos = servicoDAO.get(id, -1, null, null);

            String json = "[";

            for(int i=0; i<servicos.size();i++){
                json += "{\"id\":" + servicos.get(i).getID() + ",\"nota\":" + servicos.get(i).getAvaliacao() + ",\"comentario\":\"" + servicos.get(i).getComentario() + "\", \"data\":\"" + servicos.get(i).getData() + "\", \"hora\":\"" + servicos.get(i).getHora() + "\"}";
                if(i != servicos.size() - 1){
                    json += ",";
                }
            }

            json += "]";

            res.status(200);
            return json;
            
        }catch(Exception e){
            e.printStackTrace();
            res.status(500);
            return "{\"status\":\"error\"}";
        }
    }
}

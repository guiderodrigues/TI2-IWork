package app;

import app.utils.*;

import app.utils.CloseableIterator;

import org.apache.spark.SparkConf;
import static spark.Spark.*;
import org.apache.spark.api.java.JavaSparkContext;
import spark.Request;
import spark.Response;



import model.*;
import dao.*;
import service.*;

public class Aplicacao {
	private static ConsumidorService consumidorService = new ConsumidorService();
	private static PrestadorService prestadorService = new PrestadorService();
	private static ServicoService servicoService = new ServicoService();
	private static HorariosDiaService horariosDiaService = new HorariosDiaService();

	public static String teste(Request req, Response res) {
		res.status(200);
		String value = req.queryParams("value");
		System.out.println("teste: " + value);
		return "teste";
	}

	public static void main(String[] args) throws Exception {
		port(80);
		staticFiles.location("/public");
		get("/cadastroConsumidor", (req, res) -> consumidorService.inserir(req, res));
		get("/cadastroPrestador", (req, res) -> prestadorService.inserir(req, res));
		get("/loginConsumidor", (req, res) -> consumidorService.login(req, res));
		get("/loginPrestador", (req, res) -> prestadorService.login(req, res));
		
		get("/consumidor/:id", (req, res) -> consumidorService.getSafe(req, res));
		get("/prestador/:id", (req, res) -> prestadorService.get(req, res));
		get("/horarios/:id", (req, res) -> horariosDiaService.get(req, res));
		get("/servicosPrestador/:id", (req, res) -> servicoService.getServicosFromPrestador(req, res));
		get("/servicosConsumidor/:id", (req, res) -> servicoService.getServicosFromConsumidor(req, res));

		get("/diasDaSemana/:id", (req, res) -> horariosDiaService.getHorariosPrestador(req, res));
		get("/diasDaSemana/:id/:dia", (req, res) -> horariosDiaService.getHorariosPrestadorDia(req, res));

		get("/search", (req, res) -> prestadorService.getPrestadores(req, res));

		get("/updatePrestador", (req, res) -> prestadorService.update(req, res));

		get("/updateHorarios", (req, res) -> horariosDiaService.update(req, res));

		get("/newServico", (req, res) -> servicoService.inserir(req, res));

		get("/rate/:id", (req, res) -> servicoService.rate(req, res));
		get("/avaliacoes/:id", (req, res) -> servicoService.getAvaliacoes(req, res));

		get("/teste", (req, res) -> teste(req, res));
	}
}
package br.gov.jfrj.siga.vraptor;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpUnidadeMedida;
import br.gov.jfrj.siga.ex.ExTemporalidade;
import br.gov.jfrj.siga.ex.ExVia;
import br.gov.jfrj.siga.ex.bl.Ex;
import br.gov.jfrj.siga.hibernate.ExDao;
import br.gov.jfrj.siga.model.dao.ModeloDao;

@Resource
public class ExTemporalidadeController extends ExController {

	public ExTemporalidadeController(HttpServletRequest request, HttpServletResponse response, ServletContext context, Result result, SigaObjects so,
			EntityManager em) {
		super(request, response, context, result, ExDao.getInstance(), so, em);

		result.on(AplicacaoException.class).forwardTo(this).appexception();
		result.on(Exception.class).forwardTo(this).exception();
	}

	@Get("app/expediente/temporalidade/listar")
	public void listarTemporalidade() {
		assertAcesso("FE:Ferramentas;TT:Tabela de Temporalidade");

		final List<ExTemporalidade> temporalidadeVigente = ExDao.getInstance().listarAtivos(ExTemporalidade.class, "descTemporalidade");

		result.include("temporalidadeVigente", temporalidadeVigente);
	}

	@Get("app/expediente/temporalidade/editar")
	public void editarTemporalidade(final Long idTemporalidade, final String acao) {
		assertAcesso("FE:Ferramentas;TT:Tabela de Temporalidade");

		final ExTemporalidade exTemporal = buscarExTemporalidade(idTemporalidade);

		if (exTemporal == null && !"nova_temporalidade".equals(acao)) {
			throw new AplicacaoException("A temporalidade n�o est� dispon�vel. ID = " + idTemporalidade);
		}

		final List<CpUnidadeMedida> listaCpUnidade = ExDao.getInstance().listarUnidadesMedida();

		result.include("exTemporal", exTemporal);
		result.include("acao", acao);
		result.include("listaCpUnidade", listaCpUnidade);
	}

	@Post("app/expediente/temporalidade/gravar")
	public void gravar(final Long idTemporalidade, final String acao, final String descTemporalidade, Integer valorTemporalidade, final Long idCpUnidade) {
		assertAcesso("FE:Ferramentas;TT:Tabela de Temporalidade");
		
		if (valorTemporalidade == -1){
			valorTemporalidade = null;
		}

		if (descTemporalidade == null || descTemporalidade.trim().length() == 0) {
			throw new AplicacaoException("Voc� deve especificar uma descri��o!");
		}
		if ((valorTemporalidade != null && valorTemporalidade >= 0) && idCpUnidade <= 0) {
			throw new AplicacaoException("Voc� deve especificar a unidade de medida do valor informado!");
		}
		if ((valorTemporalidade == null || valorTemporalidade <= 0) && idCpUnidade > 0) {
			throw new AplicacaoException("Voc� deve especificar um valor para a unidade de medida informada!");
		}

		ModeloDao.iniciarTransacao();

		ExTemporalidade exTemporal = buscarExTemporalidade(idTemporalidade);
		if ("nova_temporalidade".equals(acao)) {
			if (exTemporal != null) {
				throw new AplicacaoException("A temporalidade j� existe: " + exTemporal.getDescTemporalidade());
			} else {
				exTemporal = new ExTemporalidade();
				fillEntity(descTemporalidade, valorTemporalidade, idCpUnidade, exTemporal);
				Ex.getInstance().getBL().incluirExTemporalidade(exTemporal, getIdentidadeCadastrante());
			}
		} else {
			ExTemporalidade exTempNovo = Ex.getInstance().getBL().getCopia(exTemporal);
			fillEntity(descTemporalidade, valorTemporalidade, idCpUnidade, exTempNovo);

			Ex.getInstance().getBL().alterarExTemporalidade(exTempNovo, exTemporal, dao().consultarDataEHoraDoServidor(), getIdentidadeCadastrante());
		}

		ModeloDao.commitTransacao();

		result.redirectTo("/app/expediente/temporalidade/listar");
	}

	private void fillEntity(final String descTemporalidade, final Integer valorTemporalidade, final Long idCpUnidade, final ExTemporalidade exTemporal) {
		exTemporal.setDescTemporalidade(descTemporalidade);
		exTemporal.setValorTemporalidade(valorTemporalidade);

		CpUnidadeMedida cpUnidade = null;
		if (idCpUnidade > 0) {
			cpUnidade = ExDao.getInstance().consultar(idCpUnidade, CpUnidadeMedida.class, false);

		}
		exTemporal.setCpUnidadeMedida(cpUnidade);
	}

	@Get("app/expediente/temporalidade/excluir")
	public void excluir(final Long idTemporalidade) {
		assertAcesso("FE:Ferramentas;TT:Tabela de Temporalidade");
		ModeloDao.iniciarTransacao();
		final ExTemporalidade exTemporal = buscarExTemporalidade(idTemporalidade);
		Date dt = dao().consultarDataEHoraDoServidor();

		if (exTemporal.getExViaArqCorrenteSet().size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (ExVia v : exTemporal.getExViaArqCorrenteSet()) {
				sb.append("(id: ");
				sb.append(v.getId());
				sb.append(") (Arquivo Corrente)");
				sb.append("<br/> Classifica��o: ");
				sb.append(v.getExClassificacao().getDescricaoCompleta());
				sb.append(" (Via ");
				sb.append(v.getLetraVia());
				sb.append(")<br/><br/>");
			}
			exTemporal.getExViaArqCorrenteSet().iterator().next();

			throw new AplicacaoException(
					"N�o � poss�vel excluir a temporalidade documental, pois est� associada �s seguintes classifica��es documentais:<br/><br/>" + sb.toString());
		}

		if (exTemporal.getExViaArqIntermediarioSet().size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (ExVia v : exTemporal.getExViaArqIntermediarioSet()) {
				sb.append("(id: ");
				sb.append(v.getId());
				sb.append(") (Arquivo Intermediario)");
				sb.append("<br/> Classifica��o: ");
				sb.append(v.getExClassificacao().getDescricaoCompleta());
				sb.append(" (Via ");
				sb.append(v.getLetraVia());
				sb.append(")<br/><br/>");
			}
			exTemporal.getExViaArqCorrenteSet().iterator().next();

			throw new AplicacaoException(
					"N�o � poss�vel excluir a temporalidade documental, pois est� associada �s seguintes classifica��es documentais:<br/><br/>" + sb.toString());
		}

		dao().excluirComHistorico(exTemporal, dt, getIdentidadeCadastrante());
		ModeloDao.commitTransacao();

		result.redirectTo("/app/expediente/temporalidade/listar");
	}

	private ExTemporalidade buscarExTemporalidade(final Long id) {
		if (id != null) {			
			return ExDao.getInstance().consultar(id, ExTemporalidade.class, false);
		}
		return null;
	}

}

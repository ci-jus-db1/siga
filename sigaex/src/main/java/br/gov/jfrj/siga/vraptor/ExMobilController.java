/*******************************************************************************
 * Copyright (c) 2006 - 2011 SJRJ.
 * 
 *     This file is part of SIGA.
 * 
 *     SIGA is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     SIGA is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with SIGA.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Criado em  13/09/2005
 *
 */
package br.gov.jfrj.siga.vraptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.Texto;
import br.gov.jfrj.siga.cp.model.CpOrgaoSelecao;
import br.gov.jfrj.siga.cp.model.DpLotacaoSelecao;
import br.gov.jfrj.siga.cp.model.DpPessoaSelecao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.ex.ExDocumento;
import br.gov.jfrj.siga.ex.ExFormaDocumento;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.ExModelo;
import br.gov.jfrj.siga.ex.ExMovimentacao;
import br.gov.jfrj.siga.ex.ExTipoFormaDoc;
import br.gov.jfrj.siga.ex.bl.Ex;
import br.gov.jfrj.siga.ex.bl.ExBL;
import br.gov.jfrj.siga.model.GenericoSelecao;
import br.gov.jfrj.siga.model.Selecionavel;
import br.gov.jfrj.siga.persistencia.ExMobilDaoFiltro;
import br.gov.jfrj.siga.vraptor.builder.ExMobilBuilder;

@Resource
public class ExMobilController extends ExSelecionavelController<ExMobil, ExMobilDaoFiltro> {
	public ExMobilController(HttpServletRequest request, Result result, SigaObjects so, EntityManager em) {
		super(request, result, CpDao.getInstance(), so, em);
		setItemPagina(50);

		result.on(AplicacaoException.class).forwardTo(this).appexception();
		result.on(Exception.class).forwardTo(this).exception();
	}

	@Get("app/expediente/doc/marcar_tudo")
	public void aMarcarTudo() {
		Ex.getInstance().getBL().marcarTudo();
		result.redirectTo("/app/expediente/doc/finalizou_rotina");
	}

	@Get("app/expediente/doc/numerar_tudo")
	public void aNumerarTudo() {
		int aPartirDe = 0;
		if (param("apartir") != null) {
			aPartirDe = paramInteger("apartir");
		}
		Ex.getInstance().getBL().numerarTudo(aPartirDe);
		result.redirectTo("/app/expediente/doc/finalizou_rotina");
	}

	@Get("app/expediente/doc/marcar")
	public void aMarcar() {
		String sigla = param("sigla");
		String[] siglasSparadas = sigla.split(";");
		for (String s : siglasSparadas) {
			final ExMobilDaoFiltro filter = new ExMobilDaoFiltro();
			filter.setSigla(s);
			ExMobil mob = (ExMobil) dao().consultarPorSigla(filter);
			ExDocumento doque = mob.getExDocumento();
			Ex.getInstance().getBL().marcar(doque);
		}
		result.redirectTo("/app/expediente/doc/finalizou_rotina");
	}
	
	@Get("app/expediente/doc/finalizou_rotina")
	public void aFinalizouRotina() {
		System.out.println("Finalizou rotina");
	}

	@Get("app/expediente/buscar")
	public void aBuscar(final String sigla, final String popup, final String primeiraVez, final String propriedade, final Integer postback,
			final int apenasRefresh, final Long ultMovIdEstadoDoc, final int ordem, final int visualizacao, final Integer ultMovTipoResp,
			final DpPessoaSelecao ultMovRespSel, final DpLotacaoSelecao ultMovLotaRespSel, final Long orgaoUsu, final Long idTpDoc, final String dtDocString,
			final String dtDocFinalString, final Long idTipoFormaDoc, final Integer idFormaDoc, final Long idMod, final String anoEmissaoString,
			final String numExpediente, final String numExtDoc, final CpOrgaoSelecao cpOrgaoSel, final String numAntigoDoc,
			final DpPessoaSelecao subscritorSel, final Integer tipoCadastrante, final DpPessoaSelecao cadastranteSel,
			final DpLotacaoSelecao lotaCadastranteSel, final Integer tipoDestinatario, final DpPessoaSelecao destinatarioSel,
			final DpLotacaoSelecao lotacaoDestinatarioSel, final CpOrgaoSelecao orgaoExternoDestinatarioSel, final String nmDestinatario,
			final ExClassificacaoSelecao classificacaoSel, final String descrDocument, final String fullText, final Long ultMovEstadoDoc,
			final Integer offset) {
		getP().setOffset(offset);
		this.setSigla(sigla);
		this.setPostback(postback);

		final ExMobilBuilder builder = ExMobilBuilder.novaInstancia();

		builder.setPostback(postback).setUltMovTipoResp(ultMovTipoResp).setUltMovRespSel(ultMovRespSel).setUltMovLotaRespSel(ultMovLotaRespSel)
				.setOrgaoUsu(orgaoUsu).setIdTpDoc(idTpDoc).setCpOrgaoSel(cpOrgaoSel).setSubscritorSel(subscritorSel).setTipoCadastrante(tipoCadastrante)
				.setCadastranteSel(cadastranteSel).setLotaCadastranteSel(lotaCadastranteSel).setTipoDestinatario(tipoDestinatario)
				.setDestinatarioSel(destinatarioSel).setLotacaoDestinatarioSel(lotacaoDestinatarioSel)
				.setOrgaoExternoDestinatarioSel(orgaoExternoDestinatarioSel).setClassificacaoSel(classificacaoSel).setOffset(offset);

		builder.processar(getLotaTitular());

		if (primeiraVez == null || !primeiraVez.equals("sim")) {
			final ExMobilDaoFiltro flt = createDaoFiltro();
			final long tempoIni = System.currentTimeMillis();
			setTamanho(dao().consultarQuantidadePorFiltroOtimizado(flt, getTitular(), getLotaTitular()));

			System.out.println("Consulta dos por filtro: " + (System.currentTimeMillis() - tempoIni));

			setItens(dao().consultarPorFiltroOtimizado(flt, builder.getOffset(), getItemPagina(), getTitular(), getLotaTitular()));
		}

		result.include("primeiraVez", primeiraVez);
		result.include("popup", true);
		result.include("apenasRefresh", apenasRefresh);
		result.include("estados", this.getEstados());
		result.include("listaOrdem", this.getListaOrdem());
		result.include("ordem", ordem);
		result.include("listaVisualizacao", this.getListaVisualizacao());
		result.include("visualizacao", visualizacao);
		result.include("listaTipoResp", this.getListaTipoResp());
		result.include("ultMovTipoResp", builder.getUltMovTipoResp());
		result.include("ultMovRespSel", builder.getUltMovRespSel());
		result.include("orgaoUsu", builder.getOrgaoUsu());
		result.include("orgaosUsu", this.getOrgaosUsu());
		result.include("tiposDocumento", this.getTiposDocumento());
		result.include("idTpDoc", builder.getIdTpDoc());
		result.include("dtDocString", dtDocString);
		result.include("dtDocFinalString", dtDocFinalString);
		result.include("tiposFormaDoc", this.getTiposFormaDoc());
		result.include("idTipoFormaDoc", idFormaDoc);
		result.include("anoEmissaoString", anoEmissaoString);
		result.include("listaAnos", this.getListaAnos());
		result.include("numExpediente", numExpediente);
		result.include("numExtDoc", numExtDoc);
		result.include("cpOrgaoSel", builder.getCpOrgaoSel());
		result.include("numAntigoDoc", numAntigoDoc);
		result.include("nmSubscritor", null);
		result.include("subscritorSel", builder.getSubscritorSel());
		result.include("listaTipoResp", this.getListaTipoResp());
		result.include("tipoCadastrante", builder.getTipoCadastrante());
		result.include("cadastranteSel", builder.getCadastranteSel());
		result.include("lotaCadastranteSel", builder.getLotaCadastranteSel());
		result.include("orgaoExternoDestinatarioSel", builder.getOrgaoExternoDestinatarioSel());
		result.include("listaTipoDest", this.getListaTipoDest());
		result.include("tipoDestinatario", builder.getTipoDestinatario());
		result.include("destinatarioSel", builder.getDestinatarioSel());
		result.include("lotacaoDestinatarioSel", builder.getLotacaoDestinatarioSel());
		result.include("nmDestinatario", nmDestinatario);
		result.include("descrDocumento", descrDocument);
		result.include("visualizacao", visualizacao);
		result.include("itemPagina", this.getItemPagina());
		result.include("tamanho", this.getTamanho());
		result.include("itens", this.getItens());
		result.include("classificacaoSel", builder.getClassificacaoSel());
		result.include("ultMovLotaRespSel", builder.getUltMovLotaRespSel());
		result.include("ultMovEstadoDoc", ultMovIdEstadoDoc);
		result.include("paramoffset", builder.getOffset());
		result.include("sigla", this.getSigla());
		result.include("propriedade", propriedade);
		result.include("currentPageNumber", calculaPaginaAtual(offset));
	}

	@Get("app/expediente/doc/listar")
	public void aListar(final String popup, final String primeiraVez, final String propriedade, final Integer postback, final int apenasRefresh,
			final Long ultMovIdEstadoDoc, final int ordem, final int visualizacao, final Integer ultMovTipoResp, final DpPessoaSelecao ultMovRespSel,
			final DpLotacaoSelecao ultMovLotaRespSel, final Long orgaoUsu, final Long idTpDoc, final String dtDocString, final String dtDocFinalString,
			final Long idTipoFormaDoc, final Integer idFormaDoc, final Long idMod, final String anoEmissaoString, final String numExpediente,
			final String numExtDoc, final CpOrgaoSelecao cpOrgaoSel, final String numAntigoDoc, final DpPessoaSelecao subscritorSel,
			final Integer tipoCadastrante, final DpPessoaSelecao cadastranteSel, final DpLotacaoSelecao lotaCadastranteSel, final Integer tipoDestinatario,
			final DpPessoaSelecao destinatarioSel, final DpLotacaoSelecao lotacaoDestinatarioSel, final CpOrgaoSelecao orgaoExternoDestinatarioSel,
			final String nmDestinatario, final ExClassificacaoSelecao classificacaoSel, final String descrDocument, final String fullText,
			final Long ultMovEstadoDoc, final Integer paramoffset) {
		getP().setOffset(paramoffset);
		this.setPostback(postback);

		final ExMobilBuilder builder = ExMobilBuilder.novaInstancia();

		builder.setPostback(postback).setUltMovTipoResp(ultMovTipoResp).setUltMovRespSel(ultMovRespSel).setUltMovLotaRespSel(ultMovLotaRespSel)
				.setOrgaoUsu(orgaoUsu).setIdTpDoc(idTpDoc).setCpOrgaoSel(cpOrgaoSel).setSubscritorSel(subscritorSel).setTipoCadastrante(tipoCadastrante)
				.setCadastranteSel(cadastranteSel).setLotaCadastranteSel(lotaCadastranteSel).setTipoDestinatario(tipoDestinatario)
				.setDestinatarioSel(destinatarioSel).setLotacaoDestinatarioSel(lotacaoDestinatarioSel)
				.setOrgaoExternoDestinatarioSel(orgaoExternoDestinatarioSel).setClassificacaoSel(classificacaoSel).setOffset(paramoffset);

		builder.processar(getLotaTitular());

		if (primeiraVez == null || !primeiraVez.equals("sim")) {
			final ExMobilDaoFiltro flt = createDaoFiltro();
			long tempoIni = System.currentTimeMillis();
			setTamanho(dao().consultarQuantidadePorFiltroOtimizado(flt, getTitular(), getLotaTitular()));

			System.out.println("Consulta dos por filtro: " + (System.currentTimeMillis() - tempoIni));
			setItens(dao().consultarPorFiltroOtimizado(flt, builder.getOffset(), getItemPagina(), getTitular(), getLotaTitular()));
		}

		result.include("primeiraVez", primeiraVez);
		result.include("popup", popup);
		result.include("apenasRefresh", apenasRefresh);
		result.include("estados", this.getEstados());
		result.include("listaOrdem", this.getListaOrdem());
		result.include("ordem", ordem);
		result.include("listaVisualizacao", this.getListaVisualizacao());
		result.include("visualizacao", visualizacao);
		result.include("listaTipoResp", this.getListaTipoResp());
		result.include("ultMovTipoResp", builder.getUltMovTipoResp());
		result.include("ultMovRespSel", builder.getUltMovRespSel());
		result.include("orgaoUsu", builder.getOrgaoUsu());
		result.include("orgaosUsu", this.getOrgaosUsu());
		result.include("tiposDocumento", this.getTiposDocumento());
		result.include("idTpDoc", builder.getIdTpDoc());
		result.include("dtDocString", dtDocString);
		result.include("dtDocFinalString", dtDocFinalString);
		result.include("tiposFormaDoc", this.getTiposFormaDoc());
		result.include("idTipoFormaDoc", idFormaDoc);
		result.include("anoEmissaoString", anoEmissaoString);
		result.include("listaAnos", this.getListaAnos());
		result.include("numExpediente", numExpediente);
		result.include("numExtDoc", numExtDoc);
		result.include("cpOrgaoSel", builder.getCpOrgaoSel());
		result.include("numAntigoDoc", numAntigoDoc);
		result.include("nmSubscritor", null);
		result.include("subscritorSel", builder.getSubscritorSel());
		result.include("listaTipoResp", this.getListaTipoResp());
		result.include("tipoCadastrante", builder.getTipoCadastrante());
		result.include("cadastranteSel", builder.getCadastranteSel());
		result.include("lotaCadastranteSel", builder.getLotaCadastranteSel());
		result.include("orgaoExternoDestinatarioSel", builder.getOrgaoExternoDestinatarioSel());
		result.include("listaTipoDest", this.getListaTipoDest());
		result.include("tipoDestinatario", builder.getTipoDestinatario());
		result.include("destinatarioSel", builder.getDestinatarioSel());
		result.include("lotacaoDestinatarioSel", builder.getLotacaoDestinatarioSel());
		result.include("nmDestinatario", nmDestinatario);
		result.include("descrDocumento", descrDocument);
		result.include("visualizacao", visualizacao);
		result.include("itemPagina", this.getItemPagina());
		result.include("tamanho", this.getTamanho());
		result.include("itens", this.getItens());
		result.include("classificacaoSel", builder.getClassificacaoSel());
		result.include("ultMovLotaRespSel", builder.getUltMovLotaRespSel());
		result.include("ultMovIdEstadoDoc", ultMovIdEstadoDoc);
		result.include("fullText", fullText);
		result.include("currentPageNumber", calculaPaginaAtual(paramoffset));
	}

	@Override
	protected ExMobilDaoFiltro createDaoFiltro() {
		final ExMobilDaoFiltro flt = new ExMobilDaoFiltro();

		if (flt.getIdOrgaoUsu() == null || flt.getIdOrgaoUsu() == 0) {
			if (param("matricula") != null) {
				final DpPessoa pes = daoPes(param("matricula"));
				flt.setIdOrgaoUsu(pes.getOrgaoUsuario().getId());
			}
		}

		final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			String dt = param("dtDocString");
			if (dt != null && !dt.equals("")) {
				dt = dt + " 00:00:00";
			}

			flt.setDtDoc(df.parse(dt));
		} catch (final ParseException e) {
			flt.setDtDoc(null);
		} catch (final NullPointerException e) {
			flt.setDtDoc(null);
		}

		try {
			String dt = param("dtDocFinalString");
			if (dt != null && !dt.equals("")) {
				dt = dt + " 23:59:59";
			}

			flt.setDtDocFinal(df.parse(dt));
		} catch (final ParseException e) {
			flt.setDtDocFinal(null);
		} catch (final NullPointerException e) {
			flt.setDtDocFinal(null);
		}

		flt.setAnoEmissao(paramLong("anoEmissaoString"));
		flt.setClassificacaoSelId(paramLong("classificacaoSel.id"));
		flt.setDescrDocumento(Texto.removeAcentoMaiusculas(param("descrDocumento")));
		String paramFullText = param("fullText");
		if (paramFullText != null) {
			paramFullText = paramFullText.trim();
			paramFullText = paramFullText.replace("\"", "");
		}
		flt.setFullText(paramFullText);
		flt.setDestinatarioSelId(paramLong("destinatarioSel.id"));
		if (flt.getDestinatarioSelId() != null) {
			flt.setDestinatarioSelId((daoPes(flt.getDestinatarioSelId())).getIdInicial());
		}
		flt.setIdFormaDoc(paramInteger("idFormaDoc"));
		flt.setIdTipoFormaDoc(paramLong("idTipoFormaDoc"));
		flt.setIdTpDoc(paramInteger("idTpDoc"));
		flt.setLotacaoDestinatarioSelId(paramLong("lotacaoDestinatarioSel.id"));
		if (flt.getLotacaoDestinatarioSelId() != null) {
			flt.setLotacaoDestinatarioSelId((daoLot(flt.getLotacaoDestinatarioSelId())).getIdInicial());
		}
		if (param("nmDestinatario") != null) {
			flt.setNmDestinatario(param("nmDestinatario").toUpperCase());
		}
		flt.setNmSubscritorExt(param("nmSubscritorExt"));
		flt.setNumExpediente(paramLong("numExpediente"));
		flt.setNumExtDoc(param("numExtDoc"));
		flt.setNumAntigoDoc(param("numAntigoDoc"));
		flt.setOrgaoExternoSelId(paramLong("cpOrgaoSel.id"));
		flt.setOrgaoExternoDestinatarioSelId(paramLong("orgaoExternoDestinatarioSel.id"));
		flt.setSubscritorSelId(paramLong("subscritorSel.id"));
		if (flt.getSubscritorSelId() != null) {
			flt.setSubscritorSelId((daoPes(flt.getSubscritorSelId())).getIdInicial());
		}

		flt.setLotaCadastranteSelId(paramLong("lotaCadastranteSel.id"));
		if (flt.getLotaCadastranteSelId() != null) {
			flt.setLotaCadastranteSelId((daoLot(flt.getLotaCadastranteSelId())).getIdInicial());
		}
		flt.setCadastranteSelId(paramLong("cadastranteSel.id"));
		if (flt.getCadastranteSelId() != null) {
			flt.setCadastranteSelId((daoPes(flt.getCadastranteSelId())).getIdInicial());
		}

		flt.setUltMovIdEstadoDoc(paramLong("ultMovIdEstadoDoc"));
		flt.setUltMovLotaRespSelId(paramLong("ultMovLotaRespSel.id"));
		if (flt.getUltMovLotaRespSelId() != null)
			flt.setUltMovLotaRespSelId((daoLot(flt.getUltMovLotaRespSelId())).getIdInicial());
		flt.setUltMovRespSelId(paramLong("ultMovRespSel.id"));
		if (flt.getUltMovRespSelId() != null) {
			flt.setUltMovRespSelId((daoPes(flt.getUltMovRespSelId())).getIdInicial());
		}

		flt.setNumSequencia(paramInteger("numVia"));
		if (getCadastrante() != null) {
			flt.setLotacaoCadastranteAtualId(getCadastrante().getLotacao().getIdInicial());
		}

		if (paramLong("orgaoUsu") != null) {
			flt.setIdOrgaoUsu(paramLong("orgaoUsu"));
		}
		if (flt.getIdOrgaoUsu() == null && getLotaTitular() != null) {
			flt.setIdOrgaoUsu(getLotaTitular().getOrgaoUsuario().getIdOrgaoUsu());
		}
		flt.setIdMod(paramLong("idMod"));
		flt.setOrdem(paramInteger("ordem"));

		return flt;
	}
	
	@Get("app/expediente/doc/carregar_lista_formas")
	public void aCarregarListaFormas(Long tipoForma, Integer idFormaDoc) {
		result.include("todasFormasDocPorTipoForma", this.getTodasFormasDocPorTipoForma(tipoForma));
		result.include("idFormaDoc", idFormaDoc);
	}
	

	@Get("app/expediente/doc/carregar_lista_modelos")
	public void aCarregarListaModelos(final int forma, final Long idMod) {
		result.include("modelos", this.getModelos(forma));
		result.include("idMod", idMod);
	}

	private List<String> getListaAnos() {
		final ArrayList<String> lst = new ArrayList<String>();
		final Calendar cal = Calendar.getInstance();
		for (Integer ano = cal.get(Calendar.YEAR); ano >= 1990; ano--) {
			lst.add(ano.toString());
		}
		return lst;
	}

	private Map<Integer, String> getListaOrdem() {
		final Map<Integer, String> map = new TreeMap<Integer, String>();
		map.put(0, "Data do documento");
		map.put(1, "Data da situa��o");
		map.put(2, "Ano e n�mero");
		map.put(3, "Data de finaliza��o");
		map.put(4, "Data de cria��o do tempor�rio");
		return map;
	}

	private Map<Integer, String> getListaVisualizacao() {
		final Map<Integer, String> map = new TreeMap<Integer, String>();
		map.put(0, "Normal");
		map.put(1, "�ltima anota��o");
		return map;
	}

	private Map<Integer, String> getListaTipoDest() {
		final Map<Integer, String> map = new TreeMap<Integer, String>();
		map.put(1, "Matr�cula");
		map.put(2, "�rg�o Integrado");
		map.put(3, "�rg�o Externo");
		map.put(4, "Campo Livre");
		return map;
	}

	private List<ExFormaDocumento> getTodasFormasDocPorTipoForma(final Long idTipoFormaDoc) {
		final ExBL bl = Ex.getInstance().getBL();
		ExTipoFormaDoc tipoForma = null;
		if (idTipoFormaDoc != null && !idTipoFormaDoc.equals(0L)) {
			tipoForma = dao().consultar(idTipoFormaDoc, ExTipoFormaDoc.class, false);
		}

		final List<ExFormaDocumento> formasDoc = new ArrayList<ExFormaDocumento>();
		formasDoc.addAll(bl.obterFormasDocumento(bl.obterListaModelos(null, false, null, false, getTitular(), getLotaTitular(), false), null, tipoForma));
		return formasDoc;
	}

	private List<ExTipoFormaDoc> getTiposFormaDoc() {
		return dao().listarExTiposFormaDoc();
	}

	private List<ExModelo> getModelos(final Integer idFormaDoc) {
		ExFormaDocumento forma = null;
		if (idFormaDoc != null && idFormaDoc != 0) {
			forma = dao().consultar(idFormaDoc, ExFormaDocumento.class, false);
		}

		return Ex.getInstance().getBL().obterListaModelos(forma, false, "Todos", false, getTitular(), getLotaTitular(), false);
	}

	@Get("app/expediente/selecionar")
	public void selecionar(final String sigla, final String matricula) throws Exception {
		final String resultado = super.aSelecionar(sigla);
		if (getSel() != null && matricula != null) {
			GenericoSelecao sel = new GenericoSelecao();
			sel.setId(getSel().getId());
			sel.setSigla(getSel().getSigla());
			sel.setDescricao("/sigaex/app/expediente/doc/exibir?sigla=" + sel.getSigla());
			setSel(sel);
		}
		if (resultado == "ajax_retorno") {
			result.include("sel", getSel());
			result.use(Results.page()).forwardTo("/WEB-INF/jsp/ajax_retorno.jsp");
		} else {
			result.use(Results.page()).forwardTo("/WEB-INF/jsp/ajax_vazio.jsp");
		}
	}

	@Override
	public Selecionavel selecionarVerificar(Selecionavel sel) throws AplicacaoException {
		
		ExMobil mob = (ExMobil) sel;
		
		if (mob.doc() == null)
			return null;
		
		//Edson: Se a via, volume ou documento inteiro tiver sido eliminado(a), n�o retorna nada.
		if (mob.isEliminado())
			return null;
		
		// Se for uma via ou volume, retornar
		if (mob.isVia() || mob.isVolume())
			return mob;

		if (mob.isGeral() && mob.doc().isExpediente()) {
			// Se o numero da via nao foi especificado, tentar encontrar a via
			// de numero mais baixo que esteja com o titular.
			//
			for (ExMobil m : mob.doc().getExMobilSet()) {
				if (m.isGeral() || m.isEliminado())
					continue;
				ExMovimentacao mov = m.getUltimaMovimentacaoNaoCancelada();
				if (mov != null && mov.getResp() != null
						&& mov.getResp().equivale(super.getTitular())) {
					return m;
				}
			}

			// Se nao encontrar, tentar encontrar uma na lotacao do titular
			//
			for (ExMobil m : mob.doc().getExMobilSet()) {
				if (m.isGeral() || m.isEliminado())
					continue;
				ExMovimentacao mov = m.getUltimaMovimentacaoNaoCancelada();
				if (mov != null && mov.getLotaResp() != null
						&& mov.getLotaResp().equivale(super.getLotaTitular())) {
					return m;
				}
			}
		}

		if (mob.isGeral() && mob.doc().isProcesso()) {
			// Se o ultimo volume estiver na lota��o do titular ou com ele,
			// retornar o ultimo volume
			//
			ExMobil m = mob.doc().getUltimoVolume();
			if (m != null) {
				ExMovimentacao mov = m.getUltimaMovimentacaoNaoCancelada();
				if (mov == null) {
					return m;
				}
				if (mov.getLotaResp() != null
						&& mov.getLotaResp().equivale(super.getLotaTitular())) {
					return m;
				}
				if (mov.getResp() != null
						&& mov.getResp().equivale(super.getTitular())) {
					return m;
				}
			}
		}

		return sel;
	}
}
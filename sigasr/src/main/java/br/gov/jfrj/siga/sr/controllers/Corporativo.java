package br.gov.jfrj.siga.sr.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.sr.model.DadosRH;
import br.gov.jfrj.siga.sr.model.DadosRH.Cargo;
import br.gov.jfrj.siga.sr.model.DadosRH.Funcao;
import br.gov.jfrj.siga.sr.model.DadosRH.Lotacao;
import br.gov.jfrj.siga.sr.model.DadosRH.Orgao;
import br.gov.jfrj.siga.sr.model.DadosRH.Papel;
import br.gov.jfrj.siga.sr.model.DadosRH.Pessoa;
import br.gov.jfrj.siga.sr.validator.SrValidator;
import br.gov.jfrj.siga.sr.vraptor.SrController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

public class Corporativo extends SrController {
	public Corporativo(HttpServletRequest request, Result result, CpDao dao, SigaObjects so, EntityManager em, SrValidator srValidator) {
        super(request, result, dao, so, em, srValidator);
        // TODO Auto-generated constructor stub
    }

    public static void dadosrh() throws ParserConfigurationException {
		Map<Long, Cargo> mc = new TreeMap<Long, Cargo>();
		Map<Long, Lotacao> ml = new TreeMap<Long, Lotacao>();
		Map<Long, Funcao> mf = new TreeMap<Long, Funcao>();
		Map<Long, Pessoa> mp = new TreeMap<Long, Pessoa>();
		Map<Long, List<Papel>> mpp = new TreeMap<Long, List<Papel>>();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		List<DadosRH> l = DadosRH.AR.all().fetch();
		for (DadosRH d : l) {
			Pessoa p = d.getPessoa();
			if (p != null
					&& (!mp.containsKey(p.getPessoa_id()) || p.getLotacao_tipo_papel().equals("Principal"))
					&& (p.getPessoa_situacao().equals(1)
							|| p.getPessoa_situacao().equals(2) || p.getPessoa_situacao()
								.equals(31)))
				mp.put(p.getPessoa_id(), p);

			Lotacao x = d.getLotacao();
			if (x != null && !ml.containsKey(x.getLotacao_id()))
				ml.put(x.getLotacao_id(), x);

			Cargo c = d.getCargo();
			if (c != null && !mc.containsKey(c.getCargo_id()))
				mc.put(c.getCargo_id(), c);

			Funcao f = d.getFuncao();
			if (f != null && !mf.containsKey(f.getFuncao_id()))
				mf.put(f.getFuncao_id(), f);

			Papel pp = d.getPapel();
			if (pp != null && !mpp.containsKey(pp.getPapel_pessoa_id()))
				mpp.put(pp.getPapel_pessoa_id(), new ArrayList<Papel>());
			if (pp != null)
				mpp.get(pp.getPapel_pessoa_id()).add(pp);
			
			Orgao org = d.getOrgao();

		}

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("base");
		rootElement.setAttribute("orgaoUsuario", "RJ");
		rootElement.setAttribute("versao", "2"); // forcei a versao para testar
		doc.appendChild(rootElement);

		Element cargos = doc.createElement("cargos");
		rootElement.appendChild(cargos);
		for (Cargo c : mc.values()) {
			Element e = doc.createElement("cargo");
			setAttr(e, "id", c.getCargo_id().toString());
			setAttr(e, "nome", c.getCargo_nome());
			cargos.appendChild(e);
		}

		Element funcoes = doc.createElement("funcoes");
		rootElement.appendChild(funcoes);
		for (Funcao f : mf.values()) {
			Element e = doc.createElement("funcao");
			setAttr(e, "id", f.getFuncao_id().toString());
			setAttr(e, "nome", f.getFuncao_nome());
			setAttr(e, "sigla", f.getFuncao_sigla());
			funcoes.appendChild(e);
		}

		Element lotacoes = doc.createElement("lotacoes");
		rootElement.appendChild(lotacoes);
		for (Lotacao x : ml.values()) {
			Element e = doc.createElement("lotacao");
			setAttr(e, "id", x.getLotacao_id().toString());
			setAttr(e, "nome", x.getLotacao_nome());
			setAttr(e, "sigla", x.getLotacao_sigla());
			setAttr(e, "idPai", x.getLotacao_id_pai());
			setAttr(e, "tipo", x.getLotacao_tipo());
			setAttr(e, "papel", x.getLotacao_tipo_papel());
			lotacoes.appendChild(e);
		}

		Element pessoas = doc.createElement("pessoas");
		rootElement.appendChild(pessoas);
		for (Pessoa p : mp.values()) {
			Element e = doc.createElement("pessoa");
			setAttr(e, "id", p.getPessoa_id());
			setAttr(e, "cpf", p.getPessoa_cpf());
			setAttr(e, "nome", p.getPessoa_nome());
			setAttr(e, "sexo", p.getPessoa_sexo());
			setAttr(e, "dtNascimento", p.getPessoa_data_nascimento());
			setAttr(e, "rua", p.getPessoa_rua());
			setAttr(e, "bairro", p.getPessoa_bairro());
			setAttr(e, "cidade", p.getPessoa_cidade());
			setAttr(e, "uf", p.getPessoa_uf());
			setAttr(e, "cep", p.getPessoa_cep());
			setAttr(e, "atoNomeacao", p.getPessoa_ato_nomeacao());
			setAttr(e, "dtAtoPublicacao", p.getPessoa_dt_publ_nomeacao());
			setAttr(e, "dtInicioExercicio", p.getPessoa_data_inicio_exercicio());
			setAttr(e, "dtNomeacao", p.getPessoa_data_nomeacao());
			setAttr(e, "dtPosse", p.getPessoa_data_posse());
			setAttr(e, "email", p.getPessoa_email());
			setAttr(e, "estCivil", p.getPessoa_estado_civil());
			setAttr(e, "grauInstrucao", p.getPessoa_grau_de_instrucao());
			setAttr(e, "matricula", p.getPessoa_matricula());
			setAttr(e, "padraoReferencia", p.getPessoa_padrao_referencia());
			setAttr(e, "rg", p.getPessoa_rg());
			setAttr(e, "rgDtExp", p.getPessoa_data_expedicao_rg());
			setAttr(e, "rgOrgao", p.getPessoa_rg_orgao());
			setAttr(e, "rgUf", p.getPessoa_rg_uf());
			setAttr(e, "sigla", p.getPessoa_sigla());
			setAttr(e, "situacao", p.getPessoa_situacao());
			setAttr(e, "lotacao", p.getLotacao_id());
			setAttr(e, "cargo", p.getCargo_id());
			setAttr(e, "funcaoConfianca", p.getFuncao_id());
			setAttr(e, "tipo", p.getTipo_rh());
			setAttr(e, "tipoSanguineo", p.getPessoa_tp_sanguineo());
			setAttr(e, "naturalidade", p.getPessoa_naturalidade());
			setAttr(e, "nacionalidade", p.getPessoa_nacionalidade());
			pessoas.appendChild(e);
			if (mpp.containsKey(p.getPessoa_id())) {
				for (Papel papeis : mpp.get(p.getPessoa_id()))	{
					Element papel = doc.createElement("papel"); 
					setAttr(papel, "id", papeis.getPapel_id()); 
					setAttr(papel, "tipo",papeis.getPapel_lotacao_tipo());
					setAttr(papel, "cargo", papeis.getPapel_cargo_id()); 
					setAttr(papel,"funcaoConfianca", papeis.getPapel_funcao_id()); 
					setAttr(papel, "lotacao", papeis.getPapel_lotacao_id()); 
					e.appendChild(papel);	
				}
			}
		}

//		renderXml(doc);

	}

	private static void setAttr(Element e, String name, Object value) {
		if (value == null)
			return;
		if (value instanceof Date) {
			String s = value.toString();
			s = s.substring(8, 10) + s.substring(5, 7) + s.substring(0, 4);
			e.setAttribute(name, s);
			return;
		}
		e.setAttribute(name, value.toString());
	}
}

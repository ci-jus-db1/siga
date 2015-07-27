package br.gov.jfrj.siga.pp.vraptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.pp.dao.PpDao;
import br.gov.jfrj.siga.pp.models.Agendamentos;
import br.gov.jfrj.siga.pp.models.Locais;
import br.gov.jfrj.siga.pp.models.Peritos;
import br.gov.jfrj.siga.pp.models.UsuarioForum;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/agendamento")
public class AgendamentoController extends PpController {

    public AgendamentoController (HttpServletRequest request, Result result, CpDao dao, SigaObjects so, EntityManager em) {
        super(request, result, PpDao.getInstance(), so, em);
    }

    @Path("/hoje")
    public void hoje() {
		String matriculaSessao = getUsuarioMatricula();
		UsuarioForum objUsuario = UsuarioForum.findByMatricula(matriculaSessao);
		if (objUsuario != null) {
			// busca locais em fun��o da configura��o do usu�rio
			String criterioSalas="";
			List<Locais> listaDeSalas = Locais.AR.find("forumFk="+objUsuario.getForumFk().getCod_forum()).fetch();
			// monta string de criterio
			for(int j=0;j<listaDeSalas.size();j++){
				criterioSalas = criterioSalas + "'" +listaDeSalas.get(j).getCod_local().toString() + "'";
				if(j+1<listaDeSalas.size()){
					criterioSalas = criterioSalas + ",";
				}
			}
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			Date hoje = new Date();
			String dtt = df.format(hoje);
			// busca agendamentos de hoje
            if (criterioSalas.equals("")) {
                criterioSalas = "''";
            }
            List<Agendamentos> listAgendamentos = Agendamentos.AR.find(
                    "data_ag = to_date('" + dtt
                            + "','dd-mm-yy') and localFk in(" + criterioSalas
                            + ") order by hora_ag, cod_local").fetch();
            if (!listAgendamentos.isEmpty()) {
                // busca as salas daquele forum
                List<Locais> listLocais = Locais.AR.find("cod_forum='" + objUsuario.getForumFk().getCod_forum() + "'").fetch();
                // lista auxiliar
                List<Agendamentos> auxAgendamentos = new ArrayList<Agendamentos>();
                for (int i = 0; i < listAgendamentos.size(); i++) {
                    // varre listAgendamentos
                    for (int ii = 0; ii < listLocais.size(); ii++) {
                        // compara com cada local do forum do usuário
                        if (listAgendamentos.get(i).getLocalFk().getCod_local() == listLocais.get(ii).getCod_local()) {
                            auxAgendamentos.add((Agendamentos) listAgendamentos.get(i));
                        }
                    }
                }
                List<Peritos> listPeritos = new ArrayList<Peritos>();
                listPeritos = (ArrayList<Peritos>) Peritos.AR.findAll();
                result.include("listAgendamentos", listAgendamentos);
                result.include("listPeritos", listPeritos);
                result.include("dataHoje", dtt);
            }
		} else {
		    redirecionaPaginaErro("Usuario sem permissao" , null);
		}
    }

    @Path("/hojePrint")
    public void hojePrint(String frm_data_ag) {
		String matriculaSessao = getUsuarioMatricula();
		UsuarioForum objUsuario = UsuarioForum.AR.find("matricula_usu =" + matriculaSessao).first();
		if (objUsuario != null) {
			// busca locais em fun��o da configura��o do usu�rio
			String criterioSalas="";
			List<Locais> listaDeSalas = Locais.AR.find("forumFk="+objUsuario.getForumFk().getCod_forum()).fetch();
			// monta string de criterio
			for(int j=0; j<listaDeSalas.size();j++){
				criterioSalas = criterioSalas + "'" +listaDeSalas.get(j).getCod_local().toString() + "'";
				if(j+1<listaDeSalas.size()){
					criterioSalas = criterioSalas + ",";
				}
			}
			if ((null != frm_data_ag) && !(frm_data_ag.isEmpty())){
				List<Agendamentos> listAgendamentos = (List) Agendamentos.AR.find("data_ag=to_date('"+frm_data_ag.substring(0,10)+"','dd-mm-yy') and localFk in("+criterioSalas+") order by hora_ag , localFk" ).fetch();
				List<Peritos> listPeritos = (List) new ArrayList<Peritos>();
				listPeritos = Peritos.AR.findAll();
				result.include("listAgendamentos", listAgendamentos);
				result.include("listPeritos", listPeritos);
			}
		} else {
		    redirecionaPaginaErro("Usuario sem permissao" , null);
		}
    }

    @Path("/print")
    public void print(String frm_data_ag, String frm_sala_ag, String frm_processo_ag, String frm_periciado ){
		if(frm_periciado == "" || frm_periciado == null){
		    redirecionaPaginaErro("Relatorio depende de nome de periciado preenchido para ser impresso." , null);
		    return;
		}else if(frm_processo_ag  == "" || frm_periciado == null){
		    redirecionaPaginaErro("Relatorio depende de numero de processo preenchido para ser impresso." , null);
		    return;
		}else{
		    List listAgendamentos = (List) Agendamentos.AR.find(
		            "data_ag=to_date('"+frm_data_ag.substring(0,10)+"','yy-mm-dd') and localFk.cod_local='"+frm_sala_ag+"' and processo='"+frm_processo_ag+"' and periciado='"+frm_periciado+"'" ).fetch();
			List<Peritos> listPeritos = new ArrayList<Peritos>();
			listPeritos = Peritos.AR.findAll();
			result.include("frm_processo_ag", frm_processo_ag);
            result.include("listAgendamentos", listAgendamentos);
            result.include("listPeritos", listPeritos);
		}
    }

    @Path("/salaLista")
    public void salaLista(String frm_cod_local, String frm_data_ag){
		String local = "";
		String lotacaoSessao = getUsuarioLotacao();
		List<Locais> listSalas = new ArrayList();
		// pega usuario do sistema
		String matriculaSessao = getUsuarioMatricula();
		UsuarioForum objUsuario = UsuarioForum.AR.find(
			"matricula_usu =" + matriculaSessao).first();
		if (objUsuario != null) {
			// Pega o usu�rio do sistema, e, busca os locais(salas) daquele
			// forum onde ele est�.
			listSalas = ((List) Locais.AR.find("forumFk='" + objUsuario.getForumFk().getCod_forum() + "' order by ordem_apresentacao ").fetch()); // isso n�o d� erro no caso de retorno vazio.
			List<Agendamentos> listAgendamentosMeusSala = new ArrayList();
			if(!(frm_cod_local==null||frm_data_ag.isEmpty())){
				//lista os agendamentos do dia, e, da lota��o do cadastrante
				listAgendamentosMeusSala = ((List) Agendamentos.AR.find("localFk.cod_local='" + frm_cod_local + "' and data_ag = to_date('" + frm_data_ag + "','yy-mm-dd') order by hora_ag").fetch());
				for(int i=0;i<listSalas.size();i++){
					if(listSalas.get(i).getCod_local().equals(frm_cod_local)){
						local = listSalas.get(i).getLocal();
					}
				}

			}
			List<Peritos> listPeritos = new ArrayList<Peritos>();
			listPeritos = Peritos.AR.findAll();
            result.include("local", local);
            result.include("listSalas", listSalas);
            result.include("listAgendamentosMeusSala", listAgendamentosMeusSala);
            result.include("lotacaoSessao", lotacaoSessao);
            result.include("listPeritos", listPeritos);
		} else {
		    redirecionaPaginaErro("Usuario sem permissao" , null);
		}
    }

    public void insert(String frm_data_ag,
    			String frm_hora_ag, String frm_cod_local, String matricula,
    			String periciado, String perito_juizo, String perito_parte,
    			String orgao, String processo, Integer lote) {
		matricula = getUsuarioMatricula();
		String resposta = "";
		Locais auxLocal = Locais.AR.findById(frm_cod_local);
		String hr;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date parametro = df.parse(frm_data_ag);
			Agendamentos objAgendamento = new Agendamentos(parametro,
					frm_hora_ag, auxLocal, matricula, periciado, perito_juizo,
					perito_parte, processo, orgao);
			hr = frm_hora_ag;
			Agendamentos agendamentoEmConflito = null;
			// begin transaction, que, segundo o Da Rocha é automatico no inicio da action
			String hrAux = hr.substring(0, 2);
			String minAux = hr.substring(3, 5);
			if (hr != null && (!hr.isEmpty())) {
				//verifica se tem conflito
				String horaPretendida = null;
				for(int i = 0; i < lote; i++){
					horaPretendida=hrAux+minAux;
					agendamentoEmConflito = Agendamentos.AR.find("perito_juizo like '"+perito_juizo.trim()+"%' and perito_juizo <> '-' and hora_ag='" +horaPretendida+ "' and data_ag=to_date('"+ frm_data_ag +"' , 'yy-mm-dd')"  ).first();
					if (agendamentoEmConflito!=null){
					    redirecionaPaginaErro("Perito nao disponivel no horario de " + agendamentoEmConflito.getHora_ag().substring(0,2)+ "h" + agendamentoEmConflito.getHora_ag().substring(2,4) + "min" , null );
					    return;
					}
					minAux = String.valueOf(Integer.parseInt(minAux)
							+ auxLocal.getIntervalo_atendimento());
					if (Integer.parseInt(minAux) >= 60) {
						hrAux = String.valueOf(Integer.parseInt(hrAux) + 1);
						minAux = "00";
					}
				}
				// sloop
				hrAux = hr.substring(0, 2);
				minAux = hr.substring(3, 5);
				for (int i = 0; i < lote; i++) {
					objAgendamento.setHora_ag(hrAux + minAux);
					objAgendamento.save();
					ContextoPersistencia.em().flush();
					minAux = String.valueOf(Integer.parseInt(minAux)
							+ auxLocal.getIntervalo_atendimento());
					if (Integer.parseInt(minAux) >= 60) {
						hrAux = String.valueOf(Integer.parseInt(hrAux) + 1);
						minAux = "00";
					}
					resposta = "Ok.";
				}
				// floop
				// end transaction, que, segundo o Da Rocha é automático; no fim
				// da action
			} else {
				resposta = "N�o Ok.";
			}
		} catch (Exception e) {
			// rollback transaction, que segundo o Da Rocha é automático; ocorre
			// em qualquer erro
			e.printStackTrace();
			String erro = e.getMessage();
			if (erro.substring(24, 52).equals("ConstraintViolationException")) {
				resposta = "N�o Ok. O lote n�o foi agendado.";
			} else {
				resposta = "N�o Ok. Verifique se preencheu todos os campos do agendamento.";
			}
		} finally {
		    result.include("resposta", resposta);
		    result.include("perito_juizo", perito_juizo);
		}
    }

    @Path("/incluirAjax")
    public void incluirAjax(String fixo_perito_juizo) {
        String fixo_perito_juizo_nome = "";
        String lotacaoSessao = getUsuarioLotacao();
        List<Locais> listSalas = new ArrayList<Locais>();
        List<Peritos> listPeritos = new ArrayList<Peritos>();
        // pega usuario do sistema
        String matriculaSessao = getUsuarioMatricula();
        UsuarioForum objUsuario = UsuarioForum.AR.find(
                "matricula_usu =" + matriculaSessao).first();
        if (objUsuario != null) {
            // Pega o usu�rio do sistema, e, busca os locais(salas) daquele
            // forum onde ele est�.
            listSalas = (List) Locais.AR.find(
                    "cod_forum='" + objUsuario.getForumFk().getCod_forum()
                            + "' order by ordem_apresentacao ").fetch(); // isso n�o d� erro no caso de retorno vazio.
            listPeritos =  (List) Peritos.AR.find("1=1 order by nome_perito").fetch();
            //   buscar o nome do perito fixo na lista se existir
            if(fixo_perito_juizo!=null){
                for(int i=0;i<listPeritos.size();i++) { 
                    if(listPeritos.get(i).getCpf_perito().trim().equals( fixo_perito_juizo.trim() ) ){
                        fixo_perito_juizo_nome = listPeritos.get(i).getNome_perito();
                    }
                }
            }
            result.include("listSalas", listSalas);
            result.include("lotacaoSessao", lotacaoSessao);
            result.include("fixo_perito_juizo", fixo_perito_juizo);
            result.include("fixo_perito_juizo_nome", fixo_perito_juizo_nome);
            result.include("listPeritos", listPeritos);
        } else {
            redirecionaPaginaErro("Usuario sem permissao", null);
        }
    }

    private String verificaCampoEInicializaCasoNull(String campo) {
        if(campo == null)
            return "";
        
        return campo;
    }
    @Path("/update")
    public void update(String cod_sala, String data_ag, String hora_ag, String processo, String periciado, String perito_juizo, String perito_parte, String orgao_ag){
        processo = verificaCampoEInicializaCasoNull(processo);
        periciado = verificaCampoEInicializaCasoNull(periciado);
        perito_parte = verificaCampoEInicializaCasoNull(perito_parte);
        
        String resultado = "";
		String dtt = data_ag.replaceAll("/", "-");
		Agendamentos agendamentoEmConflito = null;
		try{
			// Devo verificar agendamento conflitante, antes de fazer o UPDATE.
			System.out.println(perito_juizo.trim()+""+dtt+" "+hora_ag.substring(0,2)+hora_ag.substring(3,5));
			agendamentoEmConflito = Agendamentos.AR.find("perito_juizo like '"+perito_juizo.trim()+"%' and perito_juizo <> '-' and hora_ag='" +hora_ag.substring(0,2)+hora_ag.substring(3,5)+ "' and data_ag=to_date('"+ dtt +"', 'dd-mm-yy' ) and localFk<>'"+cod_sala+"'").first();
			if (agendamentoEmConflito!=null) {
				redirecionaPaginaErro("Perito nao disponivel no horario de " + agendamentoEmConflito.getHora_ag().substring(0,2) +"h"+agendamentoEmConflito.getHora_ag().substring(2,4)+"min" , " excluir?data="+dtt);
				return;
			}
		        
			ContextoPersistencia.em().createQuery("update Agendamentos set processo = '"+ processo +"', "+ "periciado='"+ periciado +"', perito_juizo='"+ perito_juizo.trim() +"', perito_parte='"+perito_parte+"', orgao='"+orgao_ag+"' where cod_local='"+cod_sala+"' and  hora_ag='"+hora_ag.substring(0,2)+hora_ag.substring(3,5)+"' and data_ag=to_date('"+dtt+"','dd-mm-yy')").executeUpdate();
			ContextoPersistencia.em().flush();
			Agendamentos objAgendamento = (Agendamentos) Agendamentos.AR.find("cod_local='"+cod_sala+"' and  hora_ag='"+hora_ag.substring(0,2)+hora_ag.substring(3,5)+"' and data_ag=to_date('"+dtt+"','dd-mm-yy')" ).first();
			if(objAgendamento==null){
				resultado = "Nao Ok.";
			}else{
				resultado = "Ok.";
			}
		}catch(PersistenceException e){
			e.printStackTrace();
			resultado = "N�o Ok.";
		}catch(Exception ee){
			ee.printStackTrace();
			resultado = "N�o Ok.";
		}finally{
		    result.include("resultado", resultado);
		    result.include("data_ag", dtt);
		}
    }

    @Path("/imprime")
    public void imprime(String frm_data_ag){
		String matriculaSessao = getUsuarioMatricula();
		String lotacaoSessao = getUsuarioLotacao();
		List<Agendamentos> listAgendamentos = new ArrayList<Agendamentos>();
		UsuarioForum objUsuario = UsuarioForum.AR.find(
				"matricula_usu =" + matriculaSessao).first();
    	if (objUsuario != null) {
    		if(frm_data_ag==null)
    			frm_data_ag = "";
    		else{
    			listAgendamentos = Agendamentos.AR.find( "data_ag=to_date('" + frm_data_ag + "','dd-mm-yy') order by hora_ag, cod_local" ).fetch();
    			DpPessoa p = null;
    			// deleta os agendamentos de outros org�os
    			for(int i=0;i<listAgendamentos.size();i++){
    				 p = (DpPessoa) DpPessoa.AR.find("orgaoUsuario.idOrgaoUsu = " + getCadastrante().getOrgaoUsuario().getIdOrgaoUsu()
    								+ " and dataFimPessoa is null and matricula='"+ listAgendamentos.get(i).getMatricula() + "'").first();
    				if(lotacaoSessao.trim().equals(p.getLotacao().getSiglaLotacao().toString().trim()))
    					System.out.println("");
    				else{
    					listAgendamentos.remove(i);
    					i--;
    				}
    			}
    		}
    		List<Peritos> listPeritos = new ArrayList<Peritos>();
    		listPeritos = Peritos.AR.findAll();
            result.include("listAgendamentos", listAgendamentos);
            result.include("listPeritos", listPeritos);
    	} else
    	    redirecionaPaginaErro("Usuario sem permissao" , null);
    	
   	}

    public void delete(String data_ag, String hora_ag, String cod_local) {
    	String resultado = "";
    	
    	try {
    		Agendamentos ag = Agendamentos.AR.find(
    				"hora_ag = '" + hora_ag
    						+ "' and localFk.cod_local='" + cod_local
    						+ "' and data_ag = to_date('" + data_ag
    						+ "','dd/mm/yy')").first();
    		//--------------------------
    		String lotacaoSessao = getCadastrante().getLotacao().getIdLotacao().toString();
    		String matricula_ag = ag.getMatricula();
    		DpPessoa p = (DpPessoa) DpPessoa.AR.find(
    				"orgaoUsuario.idOrgaoUsu = "
    						+ getUsuarioIdOrgaoUsu()
    						+ " and dataFimPessoa is null and matricula='"
    						+ matricula_ag + "'").first();
    		String lotacao_ag = p.getLotacao().getIdLotacao().toString();
    		//System.out.println(p.getNomePessoa().toString()+ "Lotado em:" + lotacao_ag);
    		if(lotacaoSessao.trim().equals(lotacao_ag.trim())){
    		//--------------------------
    		ag.delete();
    		ContextoPersistencia.em().flush();
    		resultado = "Ok.";
    		}else{
    		    redirecionaPaginaErro("Esse agendamento nao pode ser deletado; pertence a outra vara." , null);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		resultado = "N�o Ok.";
    	} finally {
    	    String dtt = data_ag.replace("/", "-");
    	    result.include("resultado", resultado);
    	    result.include("dtt", dtt);
    	}
    }

    @Path("/excluir")
    public void excluir(String data) {
        // pega matricula do usuario do sistema
        String matriculaSessao = getUsuarioMatricula();
        // pega a permissão do usuario
        UsuarioForum objUsuario = UsuarioForum.findByMatricula(matriculaSessao);
        // verifica se tem permissao
        if (objUsuario != null) {
            List<Agendamentos> listAgendamentos = new ArrayList<Agendamentos>();
            // verifica se o formulário submeteu alguma data
            if (data != null) {
                // Busca os agendamentos da data do formulário
                listAgendamentos = Agendamentos.AR.find("data_ag = to_date('" + data + "','dd-mm-yy') order by hora_ag , cod_local")
                        .fetch();
                // busca os locais do forum do usuario
                List<Locais> listLocais = Locais.AR.find(
                        "cod_forum='" + objUsuario.getForumFk().getCod_forum() + "'")
                        .fetch();
                // Verifica se existe local naquele forum do usuário
                if (!listAgendamentos.isEmpty()) {
                    // para cada agendamento, inlcui na lista a sala que é do
                    // forum daquele usu�rio
                    List<Agendamentos> auxAgendamentos = new ArrayList<Agendamentos>();
                    for (Integer i = 0; i < listAgendamentos.size(); i++) {
                        // pega o agendamento
                        for (Integer ii = 0; ii < listLocais.size(); ii++) {
                            // varre os locais do forum
                            if (listAgendamentos.get(i).getLocalFk().getCod_local() == listLocais
                                    .get(ii).getCod_local()) {
                                // pertence à lista de agendamentos do forum do
                                // usuario
                                auxAgendamentos
                                        .add((Agendamentos) listAgendamentos
                                                .get(i));
                            }
                        }
                    }
                    listLocais.clear();
                    listAgendamentos.clear();
                    listAgendamentos.addAll(auxAgendamentos);
                    auxAgendamentos.clear();
                }

            }
            if (!listAgendamentos.isEmpty()) {
                List <Peritos> listPeritos = new ArrayList<Peritos>();
                listPeritos = Peritos.AR.findAll();
                // excluir do arraylist, os peritos que n�o possuem agendamentos nesta data.
                result.include("listAgendamentos", listAgendamentos);
                result.include("listPeritos", listPeritos);
            } else {

            }

        } else {
            exception();
        }
    }

    @Path("/atualiza")
    public void atualiza(String cod_sala, String data_ag, String hora_ag) {
    	// pega usuario do sistema
    	String matriculaSessao = getUsuarioMatricula();
    	UsuarioForum objUsuario = UsuarioForum.AR.find(
    			"matricula_usu =" + matriculaSessao).first();
    	if (objUsuario != null) {
    		// Pega o usu�rio do sistema, e, busca os locais(salas) daquele
    		// forum onde ele est�.
    		Locais objSala = Locais.AR.find("cod_forum='" + objUsuario.getForumFk().getCod_forum() + "' and cod_local='" + cod_sala + "'").first(); // isso n�o d� erro no caso de retorno vazio?
    		String sala_ag = objSala.getLocal();
    		String lotacaoSessao = getCadastrante().getLotacao().getIdLotacao().toString();;
    		//System.out.println(lotacaoSessao);
    		Agendamentos objAgendamento = Agendamentos.AR.find("cod_local='" + cod_sala + "' and data_ag = to_date('" + data_ag + "','yy-mm-dd') and hora_ag='" + hora_ag + "'").first();
    		String matricula_ag = objAgendamento.getMatricula();
    		DpPessoa p = (DpPessoa) DpPessoa.AR.find("orgaoUsuario.idOrgaoUsu = " + getUsuarioIdOrgaoUsu() + " and dataFimPessoa is null and matricula='"	+ matricula_ag + "'").first();
    		String lotacao_ag = p.getLotacao().getIdLotacao().toString();
    		//System.out.println(p.getNomePessoa().toString()+ "Lotado em:" + lotacao_ag);
    		if(lotacaoSessao.trim().equals(lotacao_ag.trim())){
    			String nome_perito_juizo="";
    			String processo = objAgendamento.getProcesso();
    			String periciado = objAgendamento.getPericiado();
    			String perito_juizo = objAgendamento.getPerito_juizo();
    			String perito_parte = objAgendamento.getPerito_parte();
    			String orgao_julgador = objAgendamento.getOrgao();
    			ContextoPersistencia.em().flush();
    			List<Peritos> listPeritos = new ArrayList<Peritos>();
    			listPeritos = Peritos.AR.find("1=1 order by nome_perito").fetch();
    			if(perito_juizo==null){perito_juizo="-";}
    			if(!perito_juizo.trim().equals("-")){
    				for(int i=0;i<listPeritos.size();i++){
    					if(listPeritos.get(i).getCpf_perito().trim().equals(perito_juizo.trim())){
    						nome_perito_juizo = listPeritos.get(i).getNome_perito();
    					}
    				}
    			}
    			result.include("sala_ag", sala_ag);
    			result.include("cod_sala", cod_sala);
    			result.include("data_ag", data_ag);
                result.include("hora_ag", hora_ag);
                result.include("processo", processo);
                result.include("periciado", periciado);
                result.include("perito_juizo", perito_juizo);
                result.include("nome_perito_juizo", nome_perito_juizo);
                result.include("perito_parte", perito_parte);
                result.include("orgao_julgador", orgao_julgador);
                result.include("listPeritos", listPeritos);
    		}else{
    		    redirecionaPaginaErro("Esse agendamento nao pode ser modificado; pertence a outra vara." , null);
    		}
    	} else {
    	    redirecionaPaginaErro("Usuario sem permissao" , null);
    	}
    }
    

    @Path("/calendarioVetor")
    public void calendarioVetor(String frm_cod_local) {
        List listDatasLotadas = new ArrayList();
        List listDatasDoMes = new ArrayList();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date parametro = new Date();
        Date dt = new Date();
        String dtt = df.format(dt);
        Agendamentos objAgendamento = new Agendamentos(parametro, null, null,
                null, null, null, null, null, null);
        try {
            List<Agendamentos> results = Agendamentos.AR.find(
                    "data_ag >= to_date('" + dtt.trim()
                            + "','dd/MM/yyyy') and cod_local='"
                            + frm_cod_local.trim() + "'  order by data_ag")
                    .fetch();
            // verifica se veio algum agendamento
            if (results.size() != 0) {
                // preenche as datas do local no 'MÊS' na agenda
                // CORRENTE
                for (Iterator it = results.iterator(); it.hasNext();) {
                    objAgendamento = (Agendamentos) it.next();
                    listDatasDoMes.add(objAgendamento.getData_ag().toString());
                }
                String dia_ag_ant = "";
                String dia_ag_prox;
                int i = 0;
                // conta os agendamentos de cada dia, do local que
                // veio do form
                for (Iterator it = listDatasDoMes.iterator(); it.hasNext();) {
                    dia_ag_prox = (String) it.next(); // pegou o pr�ximo
                    if (i == 0) {
                        dia_ag_ant = dia_ag_prox;
                    }
                    if (dia_ag_prox.equals(dia_ag_ant)) {
                        i++; // contou a repeti��o
                    } else {
                        i = 1;
                        dia_ag_ant = dia_ag_prox;
                    }
                    // se a data estiver lotada, marca
                    if (i >= 49) {
                        listDatasLotadas.add(dia_ag_ant);
                    } // guardou a data lotada
                }
                // veio algum agendamento
                // System.out.println(results.size() + " agendamentos...");
            } else {
                // n�o veio nenhum agendamento
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.include("listDatasLotadas", listDatasLotadas);
    }
    
    @Path("/horarioVetor")
    public void horarioVetor(String frm_cod_local, String frm_data_ag) {
        List<String> listHorasLivres = new ArrayList<String>();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = new Date();
        String dtt = df.format(dt);
        Agendamentos objAgendamento = new Agendamentos(null, null, null, null,
                null, null, null, null, null);
        List<Agendamentos> results = new ArrayList<Agendamentos>();
        if (frm_data_ag != null && !frm_data_ag.isEmpty()) {
            listHorasLivres.add("09:00");
            listHorasLivres.add("09:10");
            listHorasLivres.add("09:20");
            listHorasLivres.add("09:30");
            listHorasLivres.add("09:40");
            listHorasLivres.add("09:50");
            listHorasLivres.add("10:00");
            listHorasLivres.add("10:10");
            listHorasLivres.add("10:20");
            listHorasLivres.add("10:30");
            listHorasLivres.add("10:40");
            listHorasLivres.add("10:50");
            listHorasLivres.add("11:00");
            listHorasLivres.add("11:10");
            listHorasLivres.add("11:20");
            listHorasLivres.add("11:30");
            listHorasLivres.add("11:40");
            listHorasLivres.add("11:50");
            listHorasLivres.add("12:00");
            listHorasLivres.add("12:10");
            listHorasLivres.add("12:20");
            listHorasLivres.add("12:30");
            listHorasLivres.add("12:40");
            listHorasLivres.add("12:50");
            listHorasLivres.add("13:00");
            listHorasLivres.add("13:10");
            listHorasLivres.add("13:20");
            listHorasLivres.add("13:30");
            listHorasLivres.add("13:40");
            listHorasLivres.add("13:50");
            listHorasLivres.add("14:00");
            listHorasLivres.add("14:10");
            listHorasLivres.add("14:20");
            listHorasLivres.add("14:30");
            listHorasLivres.add("14:40");
            listHorasLivres.add("14:50");
            listHorasLivres.add("15:00");
            listHorasLivres.add("15:10");
            listHorasLivres.add("15:20");
            listHorasLivres.add("15:30");
            listHorasLivres.add("15:40");
            listHorasLivres.add("15:50");
            listHorasLivres.add("16:00");
            listHorasLivres.add("16:10");
            listHorasLivres.add("16:20");
            listHorasLivres.add("16:30");
            listHorasLivres.add("16:40");
            listHorasLivres.add("16:50");
            listHorasLivres.add("17:00");
            df.applyPattern("dd-MM-yyyy");
            try {
                dtt = frm_data_ag;
                results = Agendamentos.AR.find(
                        "data_ag = to_date('" + dtt.trim()
                                + "','dd-mm-yy') and cod_local='"
                                + frm_cod_local.trim() + "'").fetch();
                // zera os horários ocupados na frm_data_ag
                // selecionada, no local frm_cod_local
                String hrr = "";
                for (Iterator it = results.iterator(); it.hasNext();) {
                    objAgendamento = (Agendamentos) it.next();
                    hrr = objAgendamento.getHora_ag();
                    hrr = hrr.substring(0, 2) + ":" + hrr.substring(2, 4);
                    listHorasLivres.set(listHorasLivres.indexOf(hrr), "");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage().equals("-1")) {
                    listHorasLivres.clear();
                    listHorasLivres.add("Erro de hor�rio inv�lido na base.");
                }
            } finally {
                result.include("listHorasLivres", listHorasLivres);
            }
        }

    }
}

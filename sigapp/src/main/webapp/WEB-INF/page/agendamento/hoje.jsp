<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<siga:pagina titulo="Agendadas Hoje">
	<link rel="stylesheet" href="/sigapp/stylesheets/jquery-ui.css" type="text/css" media="screen, projection" />
	<center class="ui-tabs">
		Per&iacute;cias marcadas para hoje:
		<c:if test="${listAgendamentos!=null}">
	 		${listAgendamentos.data_ag.toString().substring(9,11)}-${listAgendamentos.data_ag.toString().substring(6,8)}-${listAgendamentos.data_ag.toString().substring(1,5)}
	 	</c:if>
	</center>
	<form action="${linkTo[AgendamentoController].hojePrint}" method="get"
		style="position: relative; left: 10%;">
		<input type="hidden" name="frm_data_ag"
			<c:if test="${listAgendamentos==null}">
	    		value=""
	    	</c:if>
			<c:if test="${listAgendamentos!=null}">
	    		value="${listAgendamentos.data_ag.toString().substring(9,11)}-${listAgendamentos.data_ag.toString().substring(6,8)}-${listAgendamentos.data_ag.toString().substring(1,5)}"
	    	</c:if> />
		<input type="submit" value="imprime" />
	</form>
	<table class="ui-tabs" align="center" style="font-size: 100%;">
		<tr bgcolor="Silver">
			<th>&nbsp; Hora &nbsp;</th>
			<th>&nbsp; Periciado &nbsp;</th>
			<th>&nbsp; Juizado &nbsp;</th>
			<th>&nbsp; Processo &nbsp;</th>
			<th>&nbsp; Perito Ju&iacute;zo &nbsp;</th>
			<th>&nbsp; Sala &nbsp;</th>
		</tr>
		<c:forEach items="${listAgendamentos}" var="ag">
			<tr class="ui-button-icon-only"
				<c:if test="${!b}">
		    		bgcolor="#dddddd"
			    </c:if>>
				<c:set var="b" value="${!b}" />
				<td>&nbsp;
					${ag.hora_ag.substring(0,2)}:${ag.hora_ag.substring(2,4)}</td>
				<td>&nbsp; ${ag.periciado}</td>
				<td>&nbsp; ${ag.orgao}</td>
				<td>&nbsp; ${ag.processo}</td>
				<td>&nbsp; <c:if test="${null == ag.perito_juizo}">
					Sem perito do ju&iacute;zo
				</c:if> <c:if test="${null != ag.perito_juizo}">
						<c:if test="${'' == ag.perito_juizo.trim()}">
							<c:forEach items="${listPeritos}" var="prt">
								<c:if test="${ag.perito_juizo.trim() == prt.cpf_perito.trim()}">
								${prt.nome_perito}	
							</c:if>
							</c:forEach>
						</c:if>
					</c:if>
				</td>
				<td>&nbsp; ${ag.localFk.local}</td>
			</tr>
		</c:forEach>
	</table>
</siga:pagina>
<a style="position: absolute; left: 5%;" class="ui-state-hover" href="/sigapp/">Voltar</a>
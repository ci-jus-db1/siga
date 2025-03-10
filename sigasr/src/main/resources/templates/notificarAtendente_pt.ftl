<html>
<body>
	<p>
		Informamos que a solicita&ccedil;&atilde;o <b>${(sol.codigo)!}</b>
		recebeu a seguinte movimenta&ccedil;&atilde;o em ${(movimentacao.dtIniMovDDMMYYYYHHMM)!}:
	</p>
	<blockquote>
		<p>Tipo de movimenta&ccedil;&atilde;o: ${(movimentacao.tipoMov.nome)!}</p>
		<p>${(movimentacao.descrMovimentacao)?default("")}</p>
		<p>
			Por ${(movimentacao.cadastrante.descricaoIniciaisMaiusculas)!}
				(${(movimentacao.lotaCadastrante.siglaLotacao)!})
		</p>
	</blockquote>
	<p>
		<#if (sol.solicitacaoPai.atendente)??>
			<#assign descricao = (sol.solicitacaoPai.atendente.descricaoIniciaisMaiusculas)!>
		<#else>
			<#assign descricao = (sol.solicitacaoPai.lotaAtendente.descricao)!>
		</#if>
		Este email foi enviado porque <b>${(descricao)!}</b> &eacute; atendente atual da solicita&ccedil;&atilde;o <b>${(sol.solicitacaoPai.codigo)!}</b>, 
		que gerou a solicita&ccedil;&atilde;o acima atrav&eacute;s da a&ccedil;&atilde;o <b>Escalonar</b>.
	</p>
	<p>
		Para acessar a solicita&ccedil;&atilde;o, clique <a href="${link}">aqui</a>.
	</p>
</body>
</html>
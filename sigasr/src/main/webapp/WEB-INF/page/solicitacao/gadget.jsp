<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="sigasr"></div>
<div class="gt-content-box gt-for-table">
    <table border="0" class="gt-table">
        <thead>
            <tr>
                <th width="25%">Situa&ccedil;&atilde;o</th>
                <th width="25%" style="text-align: right">Atendente</th>
                <th width="25%" style="text-align: right">Lota&ccedil;&atilde;o</th>
                <th width="25%" style="text-align: right">N&atilde;o designadas</th>
            </tr>
        </thead>
        <tbody>
            <c:set var="p" value="filtro.pesquisar=true"/>
            <c:forEach items="${contagens}" var="contagem">
            <tr>
                <td>
                    <a href="${linkTo[SolicitacaoController].buscar}?${p}&filtro.situacao.idMarcador=${contagem[0]}&filtro.lotaAtendenteSel.id=${lotaTitular.idLotacao}">${contagem[1]}</a>
                </td>
                <td align="right">
                    <a href="${linkTo[SolicitacaoController].buscar}?${p}&filtro.situacao.idMarcador=${contagem[0]}&filtro.atendenteSel.id=${titular.idPessoa}">${contagem[2]}</a>
                </td>
                <td align="right">
                    <a href="${linkTo[SolicitacaoController].buscar}?${p}&filtro.situacao.idMarcador=${contagem[0]}&filtro.lotaAtendenteSel.id=${lotaTitular.idLotacao}">${contagem[3]}</a>
                </td>
                <td align="right">
                    <a href="${linkTo[SolicitacaoController].buscar}?${p}&filtro.situacao.idMarcador=${contagem[0]}&filtro.lotaAtendenteSel.id=${lotaTitular.idLotacao}&filtro.naoDesignados=true">${contagem[4]}</a>
                </td>
            </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
<form action="${linkTo[SolicitacaoController].editar}" enctype="multipart/form-data">
<div id='rightbottom'></div>
<br>
<h2><input type="submit" value="Nova" class="gt-btn-small gt-btn-right" /></h2> 
</form>


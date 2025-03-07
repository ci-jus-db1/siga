package br.gov.jfrj.siga.sr.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.gov.jfrj.siga.model.Assemelhavel;
import br.gov.jfrj.siga.sr.util.SrProcessadorFreemarker;
import br.gov.jfrj.siga.vraptor.entity.HistoricoSuporteVraptor;

public class SrModeloRelatorio extends HistoricoSuporteVraptor {
    private static final long serialVersionUID = 2948543585700308996L;

    private String nomeModelo;

    private Set<SrIndicador> indicadores;

    public Set<SrIndicador> getIndicadores() {
        SrIndicador i = new SrIndicador();
        indicadores = new TreeSet<SrIndicador>();
        indicadores.add(i);
        return indicadores;
    }

    public void setIndicadores(Set<SrIndicador> indicadores) {
        this.indicadores = indicadores;
    }

    public String gerar() throws Exception {
        // geraria massa de dados

        Map<String, Object> attrs = new HashMap<String, Object>();

        // varre os indicadores
        for (SrIndicador i : getIndicadores())
            attrs.put(i.getNomeIndicador(), i.calcular());

        // passa resultados pro Freemarker
        SrProcessadorFreemarker f = new SrProcessadorFreemarker();
        attrs.put("template", getConteudoBlobString());
        attrs.put("nmMod", getNomeModelo());
        return f.processarModelo(attrs);
    }

    public String getConteudoBlobString() {
        return "<div>${indicadorDeTeste}</div>";
    }

    public String getNomeModelo() {
        return "meuModelo";
    }

    public static void main(String args[]) throws Exception {
        SrModeloRelatorio mod = new SrModeloRelatorio();
        System.out.println(mod.gerar());
    }

    @Override
    public Long getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setId(Long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean semelhante(Assemelhavel obj, int profundidade) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setNomeModelo(String nomeModelo) {
        this.nomeModelo = nomeModelo;
    }

}

package br.gov.jfrj.siga.pp.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.model.Objeto;
import br.gov.jfrj.siga.pp.dao.PpDao;

@Entity
@Table(name = "UsuarioForum", schema = "SIGAPMP")
public class UsuarioForum extends Objeto {

    private static final long serialVersionUID = -698697354242184472L;
    public static final ActiveRecord<UsuarioForum> AR = new ActiveRecord<>(UsuarioForum.class);

    @Id
    @Column(name = "matricula_usu", length = 6, nullable = false, unique = true)
    private String matricula_usu;

    @Id()
    @ManyToOne
    @JoinColumn(name = "cod_forum", nullable = false)
    // fk, e tem que atribuir via objeto forumfK
    private Foruns forumFk; // isso é coluna, mas tem que atribuir como objeto

    @Column(name = "nome_usu", length = 50, nullable = true, unique = true)
    private String nome_usu;

    public UsuarioForum(String matricula_usu_construt, String nome_usu_construt, Foruns cod_forum_construt) {
        this.matricula_usu = matricula_usu_construt;
        this.nome_usu = nome_usu_construt;
        this.forumFk = cod_forum_construt;
    }
    
    public UsuarioForum() {
    }
    
    public String getMatricula_usu() {
        return matricula_usu;
    }

    public void setMatricula_usu(String matricula_usu) {
        this.matricula_usu = matricula_usu;
    }

    public Foruns getForumFk() {
        return forumFk;
    }

    public void setForumFk(Foruns forumFk) {
        this.forumFk = forumFk;
    }

    public String getNome_usu() {
        return nome_usu;
    }

    public void setNome_usu(String nome_usu) {
        this.nome_usu = nome_usu;
    }
    
    public static UsuarioForum findByMatricula(String matriculaSessao) {
        return AR.find("matricula_usu =" + matriculaSessao).first();
    }
    
    @Override
    public void delete() {
        ContextoPersistencia.em().remove(this);
    }
    
    @Override
    public void save() {
        PpDao.getInstance().gravar(this);
    }
    
}

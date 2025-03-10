package br.gov.jfrj.siga.gc.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.model.Objeto;

@Entity
@Table(name = "GC_ARQUIVO", schema = "SIGAGC")
public class GcArquivo extends Objeto implements Serializable {
	private static final long serialVersionUID = -1924833226821913592L;
	public static ActiveRecord<GcArquivo> AR = new ActiveRecord<>(
			GcArquivo.class);
	@Id
	@SequenceGenerator(sequenceName = "SIGAGC.hibernate_sequence", name = "gcArquivoSeq")
	@GeneratedValue(generator = "gcArquivoSeq")
	@Column(name = "ID_CONTEUDO")
	public long id;

	@Column(name = "TITULO")
	public String titulo;

	@Column(name = "CLASSIFICACAO")
	public String classificacao;

	@Lob
	@Column(name = "CONTEUDO")
	public byte[] conteudo;

	@Column(name = "CONTEUDO_TIPO")
	public String mimeType;

	public void setConteudoTXT(String html) {
		if (html != null && html.startsWith("<")) {
			mimeType = "text/html";
		} else {
			mimeType = "text/plain";
		}
		conteudo = html.getBytes(Charset.forName("utf-8"));
	}

	public String getConteudoTXT() throws IOException {
		return new String(conteudo, Charset.forName("utf-8"));
	}

	public void setConteudoBinario(byte[] conteudo, String mimeType) {
		this.conteudo = conteudo;
		this.mimeType = mimeType;
	}

	public boolean isImage() {
		return mimeType != null && mimeType.startsWith("image/");
	}

	public boolean isPDF() {
		return mimeType != null && mimeType.endsWith("/pdf");
	}

	public boolean isWord() {
		return mimeType != null
				&& (mimeType.endsWith("/msword") || mimeType
						.endsWith(".wordprocessingml.document"));
	}

	public boolean isExcel() {
		return mimeType != null
				&& (mimeType.endsWith("/vnd.ms-excel") || mimeType
						.endsWith(".spreadsheetml.sheet"));
	}

	public boolean isPresentation() {
		return mimeType != null
				&& (mimeType.endsWith("/vnd.ms-powerpoint") || mimeType
						.endsWith(".presentationml.presentation"));
	}

	public String getIcon() {
		if (isImage())
			return "image";
		if (isPDF())
			return "page_white_acrobat";
		if (isWord())
			return "page_white_word";
		if (isExcel())
			return "page_white_excel";
		if (isPresentation())
			return "page_white_powerpoint";

		return "page_white";
	}

	/**
	 * Duplica o conte�do de um conhecimento atrav�s de serializa��o. Uma das
	 * formas de se fazer deep copying do conte�do, assim quando alterar a c�pia
	 * n�o modifica o original
	 */
	public GcArquivo duplicarConteudoInfo() {
		try {
			ByteArrayOutputStream saida = new ByteArrayOutputStream();
			ObjectOutputStream objSaida = new ObjectOutputStream(saida);
			objSaida.writeObject(this);
			objSaida.close();

			ByteArrayInputStream entrada = new ByteArrayInputStream(
					saida.toByteArray());
			ObjectInputStream objEntrada = new ObjectInputStream(entrada);

			GcArquivo cloneConteudoInfo = (GcArquivo) objEntrada.readObject();
			return cloneConteudoInfo;
		} catch (Exception e) {
			throw new AplicacaoException(
					"N�o foi poss�vel duplicar esse conhecimento.");
		}
	}

	/**
	 * M�todo criado pois a ferramenta plupload retornar um mime type padr�o
	 * "octet stream". Ent�o, � necess�rio que a aplica��o identifique pela
	 * extens�o do arquivo qual o mime type do anexo.
	 * 
	 * @return mimeType
	 */
	public String obterMimeType() {
		String extensao = titulo.split("\\.")[1];

		if (extensao != null) {
			if (extensao.contentEquals("gif") || extensao.contentEquals("jpg")
					|| extensao.contentEquals("png")
					|| extensao.contentEquals("tiff"))
				return "image/" + extensao;
			if (extensao.contains("pdf"))
				return "application/pdf";
			if (extensao.contains("doc"))
				return "application/msword";
			if (extensao.contains("xls"))
				return "application/vnd.ms-excel";
			if (extensao.contains("ppt"))
				return "application/vnd.ms-powerpoint";
		}
		return null;
	}

	public long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public byte[] getConteudo() {
		return conteudo;
	}

	public String getMimeType() {
		return mimeType;
	}
}


CREATE TABLE "SIGASR"."SR_TIPO_MOVIMENTACAO"
(
	"ID_TIPO_MOVIMENTACAO"   NUMBER(19,0) NOT NULL,
    "NOME_TIPO_MOVIMENTACAO" VARCHAR2(255 CHAR) NOT NULL,
    PRIMARY KEY ("ID_TIPO_MOVIMENTACAO") 
);
  
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (1, 'Início do Atendimento');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (2, 'Andamento');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (3, 'Inclusão em Lista');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (4, 'Início do Pré-Atendimento');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (5, 'Início do Pós-Atendimento');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (6, 'Retirada de Lista');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (7, 'Fechamento');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (8, 'Cancelamento de Solicitação');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (9, 'Início de Pendência');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (10, 'Reabertura');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (11, 'Término de Pendência');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (12, 'Anexação de Arquivo');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (13, 'Alteração de Prioridade em Lista');
insert into sigasr.sr_tipo_movimentacao (id_tipo_movimentacao, nome_tipo_movimentacao) values (14, 'Cancelamento de Movimentação');

CREATE TABLE "SIGASR"."SR_LISTA"
  (
    "ID_LISTA" NUMBER(19,0) NOT NULL,
    "DT_REG" TIMESTAMP (6),
    "NOME_LISTA"          VARCHAR2(255 CHAR),
    "ID_LOTA_CADASTRANTE" NUMBER(19,0) NOT NULL,
    "HIS_ID_INI"          NUMBER(19,0),
    "HIS_DT_INI" TIMESTAMP (6),
    "HIS_DT_FIM" TIMESTAMP (6),
    PRIMARY KEY ("ID_LISTA")
  );
 create sequence sigasr.sr_lista_seq;

alter TABLE SIGASR.SR_ANDAMENTO rename TO SR_MOVIMENTACAO;
alter table sigasr.sr_movimentacao rename column DT_REG to DT_INI_MOV;
alter table sigasr.sr_movimentacao rename column ID_ANDAMENTO to ID_MOVIMENTACAO;
alter table sigasr.sr_movimentacao rename column DESCR_ANDAMENTO to DESCR_MOVIMENTACAO;
rename sr_andamento_seq TO sr_movimentacao_seq; 

alter table sigasr.sr_movimentacao add 
(
	ID_PRIORIDADE	NUMBER(19,0),
	ID_TIPO_MOVIMENTACAO	NUMBER(19,0),
	ID_MOV_CANCELADORA		NUMBER(19,0),
	ID_LISTA NUMBER(19,0),
	CONSTRAINT SR_MOV_TIPO_MOV_FK FOREIGN KEY (ID_TIPO_MOVIMENTACAO) REFERENCES SR_TIPO_MOVIMENTACAO(ID_TIPO_MOVIMENTACAO),
	CONSTRAINT SR_MOV_CANCELADORA_FK FOREIGN KEY (ID_MOV_CANCELADORA) REFERENCES SR_MOVIMENTACAO(ID_MOVIMENTACAO),
	CONSTRAINT SR_MOV_LISTA_FK FOREIGN KEY (ID_LISTA) REFERENCES SR_LISTA(ID_LISTA)
);
update sigasr.sr_movimentacao set id_tipo_movimentacao = 2
commit;
ALTER TABLE sigasr.SR_MOVIMENTACAO MODIFY (ID_TIPO_MOVIMENTACAO NUMBER(19,0) NOT NULL); 

--Início atendimento
update sr_movimentacao set id_tp_movimentacao = 1 where id_movimentacao in (
  select id_movimentacao from sigasr.sr_movimentacao m where dt_ini_mov = (
    select min(dt_ini_mov) from sigasr.sr_movimentacao 
    where id_solicitacao = m.id_solicitacao 
    and estado = 0
  )
)

--Início Pré-atendimento
update sr_movimentacao set id_tp_movimentacao = 4 where id_movimentacao in (
  select id_movimentacao from sigasr.sr_movimentacao m where dt_ini_mov = (
    select min(dt_ini_mov) from sigasr.sr_movimentacao 
    where id_solicitacao = m.id_solicitacao 
    and estado = 4
  )
)

--Início pós-atendimento
update sr_movimentacao set id_tp_movimentacao = 5 where id_movimentacao in (
  select id_movimentacao from sigasr.sr_movimentacao m where dt_ini_mov = (
    select min(dt_ini_mov) from sigasr.sr_movimentacao 
    where id_solicitacao = m.id_solicitacao 
    and estado = 5
  )
)

--Fechamento
update sr_movimentacao set id_tp_movimentacao = 7 where id_movimentacao in (
  select id_movimentacao from sigasr.sr_movimentacao m where dt_ini_mov = (
    select min(dt_ini_mov) from sigasr.sr_movimentacao 
    where id_solicitacao = m.id_solicitacao 
    and estado = 2
  )
)

--Cancelamento
update sr_movimentacao set id_tp_movimentacao = 8 where id_movimentacao in (
  select * from sigasr.sr_andamento m where dt_reg = (
    select min(dt_reg) from sigasr.sr_andamento 
    where id_solicitacao = m.id_solicitacao 
    and estado = 3
  )
)

--Início de pendência
update sr_movimentacao set id_tp_movimentacao = 9 where id_movimentacao in (
  select * from sigasr.sr_movimentacao m where dt_ini_mov = (
    select min(dt_ini_mov) from sigasr.sr_movimentacao 
    where id_solicitacao = m.id_solicitacao 
    and estado = 1
  )
)

--Anexação de Arquivo
update sigasr.sr_movimentacao set id_tp_movimentacao = 12
where id_arquivo is not null
and id_tipo_movimentacao = 2;

--Apagando movimentações canceladas
delete from sr_movimentacao where id_cancelador is not null

--Resolver manualmente: fim de pendência e reabertura

alter table sigasr.sr_movimentacao drop column ESTADO;
alter table sigasr.sr_movimentacao drop column ID_CANCELADOR;
alter table sigasr.sr_movimentacao drop column ID_LOTA_CANCELADOR;
alter table sigasr.sr_movimentacao drop column DT_CANCELAMENTO;

commit;

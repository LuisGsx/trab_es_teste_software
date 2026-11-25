# Relatório Geral do Projeto — Fase 1 (Caixa-Preta)

Este documento resume tudo o que foi realizado no projeto **Engenharia de Software — Teste de Software da Biblioteca** até a presente data (24/11/2026), cobrindo a entrega completa da **Fase 1 (Teste Caixa-Preta)**.

---

## 1. Visão Geral e Contexto da Entrega

O objetivo da Fase 1 foi projetar, documentar, implementar e executar uma suíte de testes de caixa-preta baseada **exclusivamente na especificação funcional (Anexo A)** contra o sistema fornecido compilado em `lib/biblioteca-1.0.jar` (sem inspecionar nem descompilar o `.jar`).

### Regras Inegociáveis Seguidas:
1. **Caixa-Preta Estrita**: Nenhuma inspeção do código compilado (via CFR, IntelliJ, javap, etc.).
2. **Especificação como Verdade Absoluta**: Valores esperados foram derivados estritamente do Anexo A. Nenhum resultado esperado foi adaptado para casar com o retorno do sistema. Teste que falha = defeito revelado com sucesso.
3. **Isolamento de Causa de Falha**: Cada teste exercita apenas uma classe ou valor-limite, sem combinar entradas inválidas em um mesmo caso de teste.
4. **Rastreabilidade Bidirecional**: 100% dos testes implementados possuem um identificador rastreável no [PLANO-DE-TESTES.md](file:///W:/projetos/trab_es_teste_software/PLANO-DE-TESTES.md).
5. **Estrutura e Convenção**: Pacote `br.edu.biblioteca` em `src/test/java`, utilizando JUnit 5 (`@Test`, `@ParameterizedTest`, `@CsvSource`, `@DisplayName`).

---

## 2. Métodos Mapeados e Testados

Todos os 7 métodos de negócio da classe `RegrasBiblioteca` foram cobertos com tabelas de **Classes de Equivalência (PE)** e **Análise de Valores-Limite (AVL)**:

1. **`prazoDias(TipoUsuario tipo)`** — [PrazoDiasTest.java](file:///W:/projetos/trab_es_teste_software/src/test/java/br/edu/biblioteca/PrazoDiasTest.java):
   - Mapeamento das classes do enum (`ALUNO -> 14`, `PROFESSOR -> 30`, `VISITANTE -> 7`) e exceção para `null`.
2. **`limiteEmprestimos(TipoUsuario tipo)`** — [LimiteEmprestimosTest.java](file:///W:/projetos/trab_es_teste_software/src/test/java/br/edu/biblioteca/LimiteEmprestimosTest.java):
   - Mapeamento dos limites simultâneos (`ALUNO -> 3`, `PROFESSOR -> 8`, `VISITANTE -> 1`) e exceção para `null`.
3. **`dataDevolucao(LocalDate retirada, TipoUsuario tipo)`** — [DataDevolucaoTest.java](file:///W:/projetos/trab_es_teste_software/src/test/java/br/edu/biblioteca/DataDevolucaoTest.java):
   - Dias comuns (sem ajuste), sábado como dia normal, domingo transferido para segunda-feira (+1 dia), transições críticas de calendário (fim de mês, fim de ano e 29/fevereiro bissexto), além de validações de data nula e tipo nulo.
4. **`multaCentavos(TipoUsuario tipo, int diasAtraso)`** — [MultaCentavosTest.java](file:///W:/projetos/trab_es_teste_software/src/test/java/br/edu/biblioteca/MultaCentavosTest.java):
   - Carência de 3 dias para Professor (0, 1, 3 dias -> 0 centavos);
   - Início da cobrança (4 dias -> 100 centavos) e Faixa 1 (1 a 10 dias cobrados a R$ 1,00/dia);
   - Faixa 2 (11º dia cobrado em diante a R$ 2,00/dia, ex.: 11 dias cobrados = 1200 centavos, 12 dias = 1400 centavos);
   - Teto de R$ 50,00 (5000 centavos) no ponto exato (30 dias cobrados), logo antes (29 dias = 4800) e na saturação (31 e 1000 dias sem overflow);
   - Validação de atraso negativo (-1) e tipo nulo lançando `IllegalArgumentException`.
5. **`podeEmprestar(TipoUsuario tipo, int emprestimosAtivos, int multaPendenteCentavos, boolean possuiAtraso)`** — [PodeEmprestarTest.java](file:///W:/projetos/trab_es_teste_software/src/test/java/br/edu/biblioteca/PodeEmprestarTest.java):
   - Limites de ativos para cada categoria (`limite - 1`, `limite` e `limite + 1`);
   - Fronteiras de multa pendente (0, 1000 centavos permitidos; 1001 centavos bloqueado);
   - Bloqueio isolado por `possuiAtraso = true`;
   - Combinações com múltiplas condições falhando simultaneamente;
   - Validações de entradas negativas (`ativos = -1`, `multa = -1`) e tipo nulo.
6. **`podeRenovar(TipoUsuario tipo, int renovacoesFeitas, boolean possuiReserva, boolean emAtraso)`** — [PodeRenovarTest.java](file:///W:/projetos/trab_es_teste_software/src/test/java/br/edu/biblioteca/PodeRenovarTest.java):
   - Regra estrita de Visitante: nunca renova (mesmo com 0 renovações, sem atraso e sem reserva);
   - Aluno renova "até 2 vezes" (0 e 1 feitas -> `true`; 2 e 3 feitas -> `false`);
   - Professor renova "até 3 vezes" (0 e 2 feitas -> `true`; 3 e 4 feitas -> `false`);
   - Bloqueio isolado por reserva da obra (`possuiReserva = true`);
   - Bloqueio isolado por atraso (`emAtraso = true`);
   - Bloqueio conjunto por reserva e atraso;
   - Validação de renovações negativas (-1) lançando `IllegalArgumentException` para **qualquer tipo**.
7. **`multaTotalCentavos(TipoUsuario tipo, int[] diasAtrasoPorEmprestimo)`** — [MultaTotalCentavosTest.java](file:///W:/projetos/trab_es_teste_software/src/test/java/br/edu/biblioteca/MultaTotalCentavosTest.java):
   - Lista vazia (total 0);
   - Elemento zero (total 0);
   - Soma simples de múltiplos empréstimos (`[1, 2, 12]` = 1700 centavos);
   - Teto individual de 5000 centavos aplicado antes da soma (`[100, 100]` = 10000);
   - Fronteira do teto global de R$ 100,00 (10000 centavos) no ponto exato (`[30, 30]`), logo abaixo (`[29, 30]` = 9800) e na saturação (`[30, 30, 1]` = 10000);
   - Carência de professor aplicada item a item do array (`[3, 4]` = 100 centavos);
   - Validação de lista nula e elemento negativo no início (`[-1, 2, 3]`), no meio (`[5, -2, 3]`), no fim (`[2, 4, -5]`) e isolado (`[-1]`).

---

## 3. Artefatos Produzidos e Entregas Documentadas

| Arquivo | Descrição |
|---------|-----------|
| [PLANO-DE-TESTES.md](file:///W:/projetos/trab_es_teste_software/PLANO-DE-TESTES.md) | Documento formal contendo tabelas separadas de **Classes de Equivalência** e **Valores-Limite** para os 7 métodos, com IDs rastreáveis, entradas, resultados esperados e o teste correspondente. |
| [DEFEITOS.md](file:///W:/projetos/trab_es_teste_software/DEFEITOS.md) | Catálogo completo com 19 registros de defeitos reais revelados pela execução do `mvn test`, detalhando método, entrada, resultado esperado, resultado obtido, classe/limite e testes relacionados. |
| [USO-DE-IA.md](file:///W:/projetos/trab_es_teste_software/USO-DE-IA.md) | Declaração de transparência sobre o uso de IA conforme exigido pelo enunciado, dividida em *"O que pedimos"*, *"O que aproveitamos"* e *"O que tivemos de corrigir"*. |
| [ROTEIRO-ARGUICAO.md](file:///W:/projetos/trab_es_teste_software/ROTEIRO-ARGUICAO.md) | Guia prático de estudo para a equipe com 7 perguntas prováveis do professor e suas respostas técnicas fundamentadas em PE e AVL. |
| `src/test/java/br/edu/biblioteca/*.java` | 8 classes de teste JUnit 5 (incluindo `ExemploTest`), totalizando 114 casos de teste automatizados. |

---

## 4. Métricas e Resultados da Execução (`mvn test`)

- **Total de testes executados**: 114
- **Testes aprovados (passaram)**: 69
- **Testes reprovados (falharam/revelaram defeitos)**: 45
- **Erros de compilação ou execução**: 0 (compilação limpa no JDK 17)

### Distribuição por Classe de Teste:
| Classe de Teste | Testes Totais | Passaram | Falharam | Defeito Principal |
|-----------------|---------------|----------|----------|-------------------|
| `PrazoDiasTest` | 4 | 3 | 1 | Prazo de Visitante retorna 10 em vez de 7 |
| `LimiteEmprestimosTest` | 4 | 4 | 0 | Nenhum defeito detectado |
| `DataDevolucaoTest` | 18 | 13 | 5 | Propagação de erro de Visitante e falha no ajuste de domingo para Professor |
| `MultaCentavosTest` | 31 | 15 | 16 | Não valida atraso negativo; erro na virada da faixa 2; ausência de teto; erro na carência de Professor |
| `PodeEmprestarTest` | 25 | 15 | 10 | Não valida multa negativa; permite empréstimo com limite atingido; não bloqueia multa > 1000; ignora atraso |
| `PodeRenovarTest` | 16 | 14 | 2 | Permite renovação com 3 renovações para Professor; permite renovação para Visitante |
| `MultaTotalCentavosTest` | 15 | 4 | 11 | Não valida elementos negativos; aplica teto de R$ 50 em vez de R$ 100 |
| `ExemploTest` | 1 | 1 | 0 | Teste de fumaça inicial |

---

## 5. Histórico de Versões e Commits Git

Todo o trabalho foi versionado em commits claros e estruturados, devidamente enviados para o GitHub (tanto na branch `main` quanto na `feat/novos-testes`):

- `d3a9738`: `docs: adiciona ROTEIRO-ARGUICAO.md para preparação da equipe`
- `ee113e7`: `fase 1: testes de multa, emprestar, renovar e multa total; defeitos e uso de IA`
- `f668d09`: `testes caixa-preta: prazoDias, limiteEmprestimos, dataDevolucao`
- `1f2d442`: `kit da fase 1` (commit inicial fornecido)

---

## 6. Situação Atual e Próximos Passos

A **Fase 1 (Caixa-Preta) está 100% pronta e finalizada** antes do prazo de 24/11/2026, 23h59:
- O repositório está pronto para avaliação do professor.
- A equipe possui o roteiro completo para a **Arguição 1** presencial (25/11/2026).
- Após a liberação do código-fonte pelo professor (Fase 2), o projeto avançará para a análise de cobertura JaCoCo, complexidade ciclomática V(G) e implementação da fila de reservas por TDD (Fase 3).

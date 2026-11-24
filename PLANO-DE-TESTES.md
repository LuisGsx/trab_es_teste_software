# Plano de testes — Caixa-preta (fase 1)

Técnicas: partição de equivalência (PE) e análise de valor-limite (AVL). Resultados esperados derivados estritamente do Anexo A.
Regra geral do Anexo A: em todos os métodos, `tipo` nulo lança `IllegalArgumentException`.

---

## 1. `prazoDias(TipoUsuario tipo)`

Entrada única: `tipo` (enumeração, sem fronteiras numéricas → só PE, cada valor do enum é uma classe).

### Classes de Equivalência
| ID | Classe de equivalência | Válida? | Entrada | Esperado | Teste que cobre |
|----|------------------------|---------|---------|----------|-----------------|
| PD-V1 | tipo = ALUNO | sim | ALUNO | 14 | `PrazoDiasTest.prazoPorTipo` |
| PD-V2 | tipo = PROFESSOR | sim | PROFESSOR | 30 | `PrazoDiasTest.prazoPorTipo` |
| PD-V3 | tipo = VISITANTE | sim | VISITANTE | 7 | `PrazoDiasTest.prazoPorTipo` |
| PD-I1 | tipo nulo | não | null | `IllegalArgumentException` | `PrazoDiasTest.tipoNuloLancaExcecao` |

---

## 2. `limiteEmprestimos(TipoUsuario tipo)`

Mesma estrutura: só PE sobre o enum.

### Classes de Equivalência
| ID | Classe de equivalência | Válida? | Entrada | Esperado | Teste que cobre |
|----|------------------------|---------|---------|----------|-----------------|
| LE-V1 | tipo = ALUNO | sim | ALUNO | 3 | `LimiteEmprestimosTest.limitePorTipo` |
| LE-V2 | tipo = PROFESSOR | sim | PROFESSOR | 8 | `LimiteEmprestimosTest.limitePorTipo` |
| LE-V3 | tipo = VISITANTE | sim | VISITANTE | 1 | `LimiteEmprestimosTest.limitePorTipo` |
| LE-I1 | tipo nulo | não | null | `IllegalArgumentException` | `LimiteEmprestimosTest.tipoNuloLancaExcecao` |

---

## 3. `dataDevolucao(LocalDate retirada, TipoUsuario tipo)`

Regra: retirada + prazo do tipo; se cair num domingo, passa para a segunda seguinte; sábado é dia normal.

### Classes de Equivalência
| ID | Classe de equivalência | Válida? | Retirada | Tipo | Esperado | Teste que cobre |
|----|------------------------|---------|----------|------|----------|-----------------|
| DD-V1 | resultado em dia comum (ALUNO) | sim | 2026-11-03 (ter) | ALUNO | 2026-11-17 | `DataDevolucaoTest.semAjusteDiaComum` |
| DD-V2 | resultado em dia comum (PROFESSOR) | sim | 2026-11-03 (ter) | PROFESSOR | 2026-12-03 | `DataDevolucaoTest.semAjusteDiaComum` |
| DD-V3 | resultado em dia comum (VISITANTE) | sim | 2026-11-03 (ter) | VISITANTE | 2026-11-10 | `DataDevolucaoTest.semAjusteDiaComum` |
| DD-I1 | tipo nulo | não | 2026-11-03 | null | `IllegalArgumentException` | `DataDevolucaoTest.tipoNuloLancaExcecao` |
| DD-I2 | retirada nula | não | null | ALUNO | `IllegalArgumentException` | `DataDevolucaoTest.retiradaNulaLancaExcecao` |
| DD-I3 | retirada e tipo nulos | não | null | null | `IllegalArgumentException` | `DataDevolucaoTest.ambosNulosLancaExcecao` |

### Valores-Limite
| ID | Fronteira / Limite | Retirada | Tipo | Esperado | Teste que cobre |
|----|--------------------|----------|------|----------|-----------------|
| DD-L1 | Véspera de domingo (sábado, ALUNO) | 2026-11-07 | ALUNO | 2026-11-21 | `DataDevolucaoTest.sabadoNaoAjusta` |
| DD-L2 | Véspera de domingo (sábado, VISITANTE) | 2026-11-07 | VISITANTE | 2026-11-14 | `DataDevolucaoTest.sabadoNaoAjusta` |
| DD-L3 | Véspera de domingo (sábado, PROFESSOR) | 2026-10-29 | PROFESSOR | 2026-11-28 | `DataDevolucaoTest.sabadoNaoAjusta` |
| DD-L4 | No domingo (ajusta +1 p/ segunda, ALUNO) | 2026-11-01 | ALUNO | 2026-11-16 | `DataDevolucaoTest.domingoVaiParaSegunda` |
| DD-L5 | No domingo (ajusta +1 p/ segunda, VISITANTE) | 2026-11-01 | VISITANTE | 2026-11-09 | `DataDevolucaoTest.domingoVaiParaSegunda` |
| DD-L6 | No domingo (ajusta +1 p/ segunda, PROFESSOR) | 2026-10-30 | PROFESSOR | 2026-11-30 | `DataDevolucaoTest.domingoVaiParaSegunda` |
| DD-L7 | Dia seguinte ao domingo (segunda, ALUNO) | 2026-11-02 | ALUNO | 2026-11-16 | `DataDevolucaoTest.segundaNaoAjusta` |
| DD-L8 | Dia seguinte ao domingo (segunda, VISITANTE) | 2026-11-02 | VISITANTE | 2026-11-09 | `DataDevolucaoTest.segundaNaoAjusta` |
| DD-L9 | Dia seguinte ao domingo (segunda, PROFESSOR) | 2026-10-31 | PROFESSOR | 2026-11-30 | `DataDevolucaoTest.segundaNaoAjusta` |
| DD-C1 | Calendário: domingo no fim do mês | 2027-01-17 | ALUNO | 2027-02-01 | `DataDevolucaoTest.ajusteAtravessaCalendario` |
| DD-C2 | Calendário: domingo no fim do ano | 2028-12-17 | ALUNO | 2029-01-01 | `DataDevolucaoTest.ajusteAtravessaCalendario` |
| DD-C3 | Calendário: domingo em 29/fev (bissexto) | 2032-02-15 | ALUNO | 2032-03-01 | `DataDevolucaoTest.ajusteAtravessaCalendario` |

---

## 4. `multaCentavos(TipoUsuario tipo, int diasAtraso)`

Regras da especificação:
- Professores têm carência: os 3 primeiros dias de atraso não são cobrados. Para os demais tipos, todos os dias são cobrados.
- Do 1º ao 10º dia cobrado: R$ 1,00 por dia (100 centavos). Do 11º dia cobrado em diante: R$ 2,00 por dia (200 centavos).
- Teto: a multa de um empréstimo nunca passa de R$ 50,00 (5000 centavos).
  - Cálculo do teto: 10 dias × 100 = 1000 centavos. Restam 4000 centavos a 200 centavos/dia = 20 dias na faixa 2. Total de dias cobrados para teto = 10 + 20 = 30 dias cobrados.
  - Aluno/Visitante: 30 dias de atraso = 5000 centavos; 31 dias de atraso = 5000 centavos (teto saturado).
  - Professor: carência de 3 dias → 33 dias de atraso = 30 dias cobrados = 5000 centavos; 34 dias de atraso = 31 dias cobrados = 5000 centavos (teto).
- Dias de atraso negativos lançam `IllegalArgumentException`.
- Tipo nulo lança `IllegalArgumentException`.

### Classes de Equivalência
| ID | Entrada / Classe | Válida? | Resultado Esperado | Teste que cobre |
|----|------------------|---------|---------------------|-----------------|
| CE-MC-V1 | Aluno sem atraso (0 dias) | sim | 0 centavos | `MultaCentavosTest.multaCentavos_aluno_zeroDias_semMulta` |
| CE-MC-V2 | Aluno faixa 1 (1 a 10 dias cobrados a 100 centavos/dia) | sim | dias * 100 centavos | `MultaCentavosTest.multaCentavos_aluno_faixa1_fronteiras` |
| CE-MC-V3 | Aluno faixa 2 (11 a 29 dias: 1000 + (dias-10)*200) | sim | 1000 + (dias-10)*200 | `MultaCentavosTest.multaCentavos_aluno_faixa2_fronteiraEExemploSpec` |
| CE-MC-V4 | Aluno teto (dias >= 30 saturado em 5000 centavos) | sim | 5000 centavos | `MultaCentavosTest.multaCentavos_aluno_tetoFronteiraESaturacao` |
| CE-MC-V5 | Visitante (todos os dias cobrados, sem carência) | sim | Conforme faixas 1, 2 e teto | `MultaCentavosTest.multaCentavos_visitante_valoresELimites` |
| CE-MC-V6 | Professor carência (0 a 3 dias de atraso não cobrados) | sim | 0 centavos | `MultaCentavosTest.multaCentavos_professor_carenciaTresDias` |
| CE-MC-V7 | Professor faixa 1 (4 a 13 dias atraso = 1 a 10 cobrados) | sim | (dias-3) * 100 centavos | `MultaCentavosTest.multaCentavos_professor_faixa1_fronteiras` |
| CE-MC-V8 | Professor faixa 2 (14 a 32 dias atraso = 11 a 29 cobrados) | sim | 1000 + (dias-13)*200 | `MultaCentavosTest.multaCentavos_professor_14Dias_inicioFaixa2` |
| CE-MC-V9 | Professor teto (dias >= 33 = cobrados >= 30) | sim | 5000 centavos | `MultaCentavosTest.multaCentavos_professor_tetoFronteiraESaturacao` |
| CE-MC-I1 | Tipo nulo | não | `IllegalArgumentException` | `MultaCentavosTest.multaCentavos_tipoNulo_lancaExcecao` |
| CE-MC-I2 | Atraso negativo ALUNO | não | `IllegalArgumentException` | `MultaCentavosTest.multaCentavos_diasAtrasoNegativo_lancaExcecao` |
| CE-MC-I3 | Atraso negativo PROFESSOR | não | `IllegalArgumentException` | `MultaCentavosTest.multaCentavos_diasAtrasoNegativo_lancaExcecao` |
| CE-MC-I4 | Atraso negativo VISITANTE | não | `IllegalArgumentException` | `MultaCentavosTest.multaCentavos_diasAtrasoNegativo_lancaExcecao` |

### Valores-Limite
| ID | Fronteira / Regra | Tipo | Entrada | Esperado | Teste que cobre |
|----|-------------------|------|---------|----------|-----------------|
| VL-MC-01 | Fronteira inferior inválida (dias < 0) | ALUNO/PROF/VIS | -1 | `IllegalArgumentException` | `multaCentavos_diasAtrasoNegativo_lancaExcecao` |
| VL-MC-02 | Fronteira inferior válida (dias = 0) | ALUNO | 0 | 0 | `multaCentavos_aluno_zeroDias_semMulta` |
| VL-MC-03 | Início da faixa 1 (1 dia cobrado) | ALUNO | 1 | 100 | `multaCentavos_aluno_faixa1_fronteiras` |
| VL-MC-04 | Fim da faixa 1 (10 dias cobrados) | ALUNO | 10 | 1000 | `multaCentavos_aluno_faixa1_fronteiras` |
| VL-MC-05 | Início da faixa 2 (11º dia cobrado) | ALUNO | 11 | 1200 | `multaCentavos_aluno_faixa2_fronteiraEExemploSpec` |
| VL-MC-06 | Exemplo da especificação (12 dias) | ALUNO | 12 | 1400 | `multaCentavos_aluno_faixa2_fronteiraEExemploSpec` |
| VL-MC-07 | Imediatamente antes do teto de R$ 50 | ALUNO | 29 | 4800 | `multaCentavos_aluno_tetoFronteiraESaturacao` |
| VL-MC-08 | Teto atingido exatamente (30 dias) | ALUNO | 30 | 5000 | `multaCentavos_aluno_tetoFronteiraESaturacao` |
| VL-MC-09 | Imediatamente acima do teto (31 dias) | ALUNO | 31 | 5000 | `multaCentavos_aluno_tetoFronteiraESaturacao` |
| VL-MC-10 | Valor grande sem overflow (1000 dias) | ALUNO | 1000 | 5000 | `multaCentavos_aluno_tetoFronteiraESaturacao` |
| VL-MC-11 | Visitante dias = 0 | VISITANTE | 0 | 0 | `multaCentavos_visitante_valoresELimites` |
| VL-MC-12 | Visitante início faixa 1 | VISITANTE | 1 | 100 | `multaCentavos_visitante_valoresELimites` |
| VL-MC-13 | Visitante fim faixa 1 | VISITANTE | 10 | 1000 | `multaCentavos_visitante_valoresELimites` |
| VL-MC-14 | Visitante início faixa 2 | VISITANTE | 11 | 1200 | `multaCentavos_visitante_valoresELimites` |
| VL-MC-15 | Visitante meio faixa 2 | VISITANTE | 12 | 1400 | `multaCentavos_visitante_valoresELimites` |
| VL-MC-16 | Visitante antes do teto | VISITANTE | 29 | 4800 | `multaCentavos_visitante_valoresELimites` |
| VL-MC-17 | Visitante teto exato | VISITANTE | 30 | 5000 | `multaCentavos_visitante_valoresELimites` |
| VL-MC-18 | Visitante acima do teto | VISITANTE | 31 | 5000 | `multaCentavos_visitante_valoresELimites` |
| VL-MC-19 | Professor sem atraso | PROFESSOR | 0 | 0 | `multaCentavos_professor_carenciaTresDias` |
| VL-MC-20 | Professor 1º dia de carência | PROFESSOR | 1 | 0 | `multaCentavos_professor_carenciaTresDias` |
| VL-MC-21 | Professor limite da carência (3º dia) | PROFESSOR | 3 | 0 | `multaCentavos_professor_carenciaTresDias` |
| VL-MC-22 | Professor 1º dia cobrado (4 dias) | PROFESSOR | 4 | 100 | `multaCentavos_professor_faixa1_fronteiras` |
| VL-MC-23 | Professor 10º dia cobrado (13 dias) | PROFESSOR | 13 | 1000 | `multaCentavos_professor_faixa1_fronteiras` |
| VL-MC-24 | Professor 11º dia cobrado (14 dias) | PROFESSOR | 14 | 1200 | `multaCentavos_professor_14Dias_inicioFaixa2` |
| VL-MC-25 | Professor antes do teto (32 dias) | PROFESSOR | 32 | 4800 | `multaCentavos_professor_tetoFronteiraESaturacao` |
| VL-MC-26 | Professor teto exato (33 dias) | PROFESSOR | 33 | 5000 | `multaCentavos_professor_tetoFronteiraESaturacao` |
| VL-MC-27 | Professor acima do teto (34 dias) | PROFESSOR | 34 | 5000 | `multaCentavos_professor_tetoFronteiraESaturacao` |
| VL-MC-28 | Professor valor grande (1000 dias) | PROFESSOR | 1000 | 5000 | `multaCentavos_professor_tetoFronteiraESaturacao` |

---

## 5. `podeEmprestar(TipoUsuario tipo, int emprestimosAtivos, int multaPendenteCentavos, boolean possuiAtraso)`

Regra: devolve `true` somente se as três condições valerem juntas:
1. `emprestimosAtivos < limiteEmprestimos(tipo)` (aluno: 3, professor: 8, visitante: 1).
2. `multaPendenteCentavos <= 1000` (máximo R$ 10,00).
3. `!possuiAtraso` (não tem empréstimo em atraso).

Exceções: `emprestimosAtivos < 0`, `multaPendenteCentavos < 0` ou `tipo == null` lançam `IllegalArgumentException`.

### Classes de Equivalência
| ID | Entrada / Condição | Válida? | Esperado | Teste que cobre |
|----|--------------------|---------|----------|-----------------|
| CE-PE-V1 | Aluno com condições válidas | sim | true / false conforme limite | `PodeEmprestarTest.podeEmprestar_aluno_limitesAtivos` |
| CE-PE-V2 | Professor com condições válidas | sim | true / false conforme limite | `PodeEmprestarTest.podeEmprestar_professor_limitesAtivos` |
| CE-PE-V3 | Visitante com condições válidas | sim | true / false conforme limite | `PodeEmprestarTest.podeEmprestar_visitante_limitesAtivos` |
| CE-PE-V4 | Multa pendente <= 1000 (válida) vs > 1000 (impede) | sim | true se <= 1000, false se > 1000 | `PodeEmprestarTest.podeEmprestar_multaPendente_fronteiras` |
| CE-PE-V5 | possuiAtraso false (permite) vs true (impede) | sim | true se false, false se true | `PodeEmprestarTest.podeEmprestar_semAtraso_valido`, `podeEmprestar_atrasoIsolado_impede` |
| CE-PE-V6 | Combinações com 2 ou 3 condições falhando | sim | false | `PodeEmprestarTest.podeEmprestar_multiplasCondicoesInvalidas` |
| CE-PE-I1 | Tipo nulo | não | `IllegalArgumentException` | `PodeEmprestarTest.podeEmprestar_tipoNulo_lancaExcecao` |
| CE-PE-I2 | Empréstimos ativos negativo (-1) | não | `IllegalArgumentException` | `PodeEmprestarTest.podeEmprestar_ativosNegativo_lancaExcecao` |
| CE-PE-I3 | Multa pendente negativa (-1) | não | `IllegalArgumentException` | `PodeEmprestarTest.podeEmprestar_multaNegativa_lancaExcecao` |

### Valores-Limite
| ID | Fronteira / Variável | Tipo | Entrada (ativos, multa, atraso) | Esperado | Teste que cobre |
|----|----------------------|------|---------------------------------|----------|-----------------|
| VL-PE-01 | Fronteira inferior ativos (< 0) | ALUNO | (-1, 0, false) | `IllegalArgumentException` | `podeEmprestar_ativosNegativo_lancaExcecao` |
| VL-PE-02 | Fronteira inferior multa (< 0) | ALUNO | (0, -1, false) | `IllegalArgumentException` | `podeEmprestar_multaNegativa_lancaExcecao` |
| VL-PE-03 | Aluno 0 ativos | ALUNO | (0, 0, false) | true | `podeEmprestar_aluno_limitesAtivos` |
| VL-PE-04 | Aluno limite - 1 (2 ativos) | ALUNO | (2, 0, false) | true | `podeEmprestar_aluno_limitesAtivos` |
| VL-PE-05 | Aluno limite exato (3 ativos) | ALUNO | (3, 0, false) | false | `podeEmprestar_aluno_limitesAtivos` |
| VL-PE-06 | Aluno limite + 1 (4 ativos) | ALUNO | (4, 0, false) | false | `podeEmprestar_aluno_limitesAtivos` |
| VL-PE-07 | Professor 0 ativos | PROFESSOR | (0, 0, false) | true | `podeEmprestar_professor_limitesAtivos` |
| VL-PE-08 | Professor limite - 1 (7 ativos) | PROFESSOR | (7, 0, false) | true | `podeEmprestar_professor_limitesAtivos` |
| VL-PE-09 | Professor limite exato (8 ativos) | PROFESSOR | (8, 0, false) | false | `podeEmprestar_professor_limitesAtivos` |
| VL-PE-10 | Professor limite + 1 (9 ativos) | PROFESSOR | (9, 0, false) | false | `podeEmprestar_professor_limitesAtivos` |
| VL-PE-11 | Visitante limite - 1 (0 ativos) | VISITANTE | (0, 0, false) | true | `podeEmprestar_visitante_limitesAtivos` |
| VL-PE-12 | Visitante limite exato (1 ativo) | VISITANTE | (1, 0, false) | false | `podeEmprestar_visitante_limitesAtivos` |
| VL-PE-13 | Visitante limite + 1 (2 ativos) | VISITANTE | (2, 0, false) | false | `podeEmprestar_visitante_limitesAtivos` |
| VL-PE-14 | Multa pendente = 0 centavos | ALUNO | (0, 0, false) | true | `podeEmprestar_multaPendente_fronteiras` |
| VL-PE-15 | Multa limite exato (1000 centavos) | ALUNO | (0, 1000, false) | true | `podeEmprestar_multaPendente_fronteiras` |
| VL-PE-16 | Multa logo acima do limite (1001) | ALUNO | (0, 1001, false) | false | `podeEmprestar_multaPendente_fronteiras` |
| VL-PE-17 | Sem atraso isolado | ALUNO | (0, 0, false) | true | `podeEmprestar_semAtraso_valido` |
| VL-PE-18 | Atraso isolado | ALUNO/PROF/VIS | (0, 0, true) | false | `podeEmprestar_atrasoIsolado_impede` |
| VL-PE-19 | Duas falhas: ativos limite E multa > 1000 | ALUNO | (3, 1001, false) | false | `podeEmprestar_multiplasCondicoesInvalidas` |
| VL-PE-20 | Duas falhas: ativos limite E com atraso | ALUNO | (3, 0, true) | false | `podeEmprestar_multiplasCondicoesInvalidas` |
| VL-PE-21 | Duas falhas: multa > 1000 E com atraso | ALUNO | (0, 1001, true) | false | `podeEmprestar_multiplasCondicoesInvalidas` |
| VL-PE-22 | Três falhas conjuntas (ativos, multa E atraso) | ALUNO | (3, 1001, true) | false | `podeEmprestar_multiplasCondicoesInvalidas` |

---

## 6. `podeRenovar(TipoUsuario tipo, int renovacoesFeitas, boolean possuiReserva, boolean emAtraso)`

Regra da especificação:
- Visitantes nunca renovam (`false`).
- Alunos renovam o mesmo empréstimo **até 2 vezes**; professores **até 3 vezes**.
  - *Interpretação de `renovacoesFeitas`:* representa a quantidade de renovações que o usuário já efetuou para aquele empréstimo. Se o aluno pode renovar "até 2 vezes", significa que com 0 renovações feitas ele tem direito à 1ª renovação (`true`); com 1 renovação feita ele tem direito à 2ª renovação (`true`); tendo atingido 2 renovações feitas, esgotou o limite permitido e não pode renovar mais (`false`). Para professor (até 3 vezes): 0, 1 e 2 feitas retornam `true`; com 3 feitas esgotou o limite e retorna `false`.
- Só é possível renovar se `!possuiReserva` e `!emAtraso`.
- `renovacoesFeitas < 0` lança `IllegalArgumentException` para **qualquer tipo**.
- `tipo == null` lança `IllegalArgumentException`.

### Classes de Equivalência
| ID | Entrada / Condição | Válida? | Esperado | Teste que cobre |
|----|--------------------|---------|----------|-----------------|
| CE-PR-V1 | Visitante em qualquer situação | sim | false | `PodeRenovarTest.podeRenovar_visitante_zeroRenovacoesCondicoesFavoraveis_retornaFalse` |
| CE-PR-V2 | Aluno com renovações feitas dentro/fora do limite | sim | true se < 2, false se >= 2 | `PodeRenovarTest.podeRenovar_aluno_limitesRenovacoes` |
| CE-PR-V3 | Professor com renovações feitas dentro/fora do limite | sim | true se < 3, false se >= 3 | `PodeRenovarTest.podeRenovar_professor_limitesRenovacoes` |
| CE-PR-V4 | Obra com reserva isolada | sim | false | `PodeRenovarTest.podeRenovar_reservaIsolada_impede` |
| CE-PR-V5 | Empréstimo em atraso isolado | sim | false | `PodeRenovarTest.podeRenovar_atrasoIsolado_impede` |
| CE-PR-V6 | Obra com reserva E em atraso | sim | false | `PodeRenovarTest.podeRenovar_reservaEAtrasoJuntos_impede` |
| CE-PR-I1 | Tipo nulo | não | `IllegalArgumentException` | `PodeRenovarTest.podeRenovar_tipoNulo_lancaExcecao` |
| CE-PR-I2 | Renovações negativas ALUNO | não | `IllegalArgumentException` | `PodeRenovarTest.podeRenovar_renovacoesNegativo_lancaExcecao` |
| CE-PR-I3 | Renovações negativas PROFESSOR | não | `IllegalArgumentException` | `PodeRenovarTest.podeRenovar_renovacoesNegativo_lancaExcecao` |
| CE-PR-I4 | Renovações negativas VISITANTE | não | `IllegalArgumentException` | `PodeRenovarTest.podeRenovar_renovacoesNegativo_lancaExcecao` |

### Valores-Limite
| ID | Fronteira / Variável | Tipo | Entrada (renovações, reserva, atraso) | Esperado | Teste que cobre |
|----|----------------------|------|---------------------------------------|----------|-----------------|
| VL-PR-01 | Fronteira inferior renovações (< 0) | ALUNO/PROF/VIS | (-1, false, false) | `IllegalArgumentException` | `podeRenovar_renovacoesNegativo_lancaExcecao` |
| VL-PR-02 | Visitante com 0 renovações e condições favoráveis | VISITANTE | (0, false, false) | false | `podeRenovar_visitante_zeroRenovacoesCondicoesFavoraveis_retornaFalse` |
| VL-PR-03 | Aluno 0 renovações feitas (1ª renovação) | ALUNO | (0, false, false) | true | `podeRenovar_aluno_limitesRenovacoes` |
| VL-PR-04 | Aluno 1 renovação feita (2ª renovação) | ALUNO | (1, false, false) | true | `podeRenovar_aluno_limitesRenovacoes` |
| VL-PR-05 | Aluno 2 renovações feitas (limite atingido) | ALUNO | (2, false, false) | false | `podeRenovar_aluno_limitesRenovacoes` |
| VL-PR-06 | Aluno 3 renovações feitas (acima do limite) | ALUNO | (3, false, false) | false | `podeRenovar_aluno_limitesRenovacoes` |
| VL-PR-07 | Professor 0 renovações feitas | PROFESSOR | (0, false, false) | true | `podeRenovar_professor_limitesRenovacoes` |
| VL-PR-08 | Professor 2 renovações feitas (3ª permitida) | PROFESSOR | (2, false, false) | true | `podeRenovar_professor_limitesRenovacoes` |
| VL-PR-09 | Professor 3 renovações feitas (limite atingido) | PROFESSOR | (3, false, false) | false | `podeRenovar_professor_limitesRenovacoes` |
| VL-PR-10 | Professor 4 renovações feitas (acima do limite) | PROFESSOR | (4, false, false) | false | `podeRenovar_professor_limitesRenovacoes` |
| VL-PR-12 | Reserva isolada (possuiReserva = true) | ALUNO | (0, true, false) | false | `podeRenovar_reservaIsolada_impede` |
| VL-PR-13 | Atraso isolado (emAtraso = true) | ALUNO | (0, false, true) | false | `podeRenovar_atrasoIsolado_impede` |
| VL-PR-14 | Reserva e atraso juntos | ALUNO | (0, true, true) | false | `podeRenovar_reservaEAtrasoJuntos_impede` |

---

## 7. `multaTotalCentavos(TipoUsuario tipo, int[] diasAtrasoPorEmprestimo)`

Regra da especificação:
- Soma a multa de cada empréstimo da lista (cada uma com as regras de `multaCentavos`, inclusive teto individual de R$ 50,00 e carência por item para professor).
- Total nunca passa de R$ 100,00 (10000 centavos).
- Lista vazia dá total 0.
- Lista nula ou elemento negativo lançam `IllegalArgumentException`.
- Tipo nulo lança `IllegalArgumentException`.

### Classes de Equivalência
| ID | Entrada / Condição | Válida? | Esperado | Teste que cobre |
|----|--------------------|---------|----------|-----------------|
| CE-MT-V1 | Lista vazia (`new int[]{}`) | sim | 0 centavos | `MultaTotalCentavosTest.multaTotalCentavos_listaVazia_retornaZero` |
| CE-MT-V2 | Lista com 1 elemento com valor 0 | sim | 0 centavos | `MultaTotalCentavosTest.multaTotalCentavos_umElementoZero_retornaZero` |
| CE-MT-V3 | Lista com múltiplos elementos (soma simples) | sim | Soma dos valores individuais | `MultaTotalCentavosTest.multaTotalCentavos_multiplosElementos_somaSimples` |
| CE-MT-V4 | Multa individual limitada a 5000 antes de somar | sim | 10000 centavos | `MultaTotalCentavosTest.multaTotalCentavos_multaIndividualLimitadaAntesDeSomar` |
| CE-MT-V5 | Teto global exato de 10000 centavos | sim | 10000 centavos | `MultaTotalCentavosTest.multaTotalCentavos_somaExatamenteTetoGlobal` |
| CE-MT-V6 | Soma logo abaixo do teto global | sim | 9800 centavos | `MultaTotalCentavosTest.multaTotalCentavos_somaLogoAbaixoTetoGlobal` |
| CE-MT-V7 | Soma acima do teto global (satura em 10000) | sim | 10000 centavos | `MultaTotalCentavosTest.multaTotalCentavos_somaAcimaTetoGlobal_saturaEm10000` |
| CE-MT-V8 | Muitos elementos saturando o teto global | sim | 10000 centavos | `MultaTotalCentavosTest.multaTotalCentavos_muitosElementos_saturaNoTetoGlobal` |
| CE-MT-V9 | Professor com carência de 3 dias aplicada por elemento | sim | 100 centavos | `MultaTotalCentavosTest.multaTotalCentavos_professor_carenciaAplicadaPorElemento` |
| CE-MT-I1 | Tipo nulo | não | `IllegalArgumentException` | `MultaTotalCentavosTest.multaTotalCentavos_tipoNulo_lancaExcecao` |
| CE-MT-I2 | Lista nula | não | `IllegalArgumentException` | `MultaTotalCentavosTest.multaTotalCentavos_listaNula_lancaExcecao` |
| CE-MT-I3 | Lista só com elemento negativo | não | `IllegalArgumentException` | `MultaTotalCentavosTest.multaTotalCentavos_elementoNegativoUnico_lancaExcecao` |
| CE-MT-I4 | Elemento negativo em diferentes posições | não | `IllegalArgumentException` | `multaTotalCentavos_elementoNegativoNoInicio_lancaExcecao`, `NoMeio`, `NoFim` |

### Valores-Limite
| ID | Fronteira / Variável | Tipo | Entrada (lista) | Esperado | Teste que cobre |
|----|----------------------|------|-----------------|----------|-----------------|
| VL-MT-01 | Fronteira de elemento inválido (único negativo) | ALUNO | `[-1]` | `IllegalArgumentException` | `multaTotalCentavos_elementoNegativoUnico_lancaExcecao` |
| VL-MT-02 | Elemento negativo no início | ALUNO | `[-1, 2, 3]` | `IllegalArgumentException` | `multaTotalCentavos_elementoNegativoNoInicio_lancaExcecao` |
| VL-MT-03 | Elemento negativo no meio | PROFESSOR | `[5, -2, 3]` | `IllegalArgumentException` | `multaTotalCentavos_elementoNegativoNoMeio_lancaExcecao` |
| VL-MT-04 | Elemento negativo no fim | VISITANTE | `[2, 4, -5]` | `IllegalArgumentException` | `multaTotalCentavos_elementoNegativoNoFim_lancaExcecao` |
| VL-MT-05 | Tamanho da lista = 0 | ALUNO | `[]` | 0 | `multaTotalCentavos_listaVazia_retornaZero` |
| VL-MT-06 | Elemento com 0 dias | ALUNO | `[0]` | 0 | `multaTotalCentavos_umElementoZero_retornaZero` |
| VL-MT-07 | Soma simples [1, 2, 12] (100+200+1400) | ALUNO | `[1, 2, 12]` | 1700 | `multaTotalCentavos_multiplosElementos_somaSimples` |
| VL-MT-08 | Multa individual limitada a 5000 [100, 100] | ALUNO | `[100, 100]` | 10000 | `multaTotalCentavos_multaIndividualLimitadaAntesDeSomar` |
| VL-MT-09 | Fronteira exata teto global [30, 30] | ALUNO | `[30, 30]` | 10000 | `multaTotalCentavos_somaExatamenteTetoGlobal` |
| VL-MT-10 | Logo abaixo do teto global [29, 30] (4800+5000) | ALUNO | `[29, 30]` | 9800 | `multaTotalCentavos_somaLogoAbaixoTetoGlobal` |
| VL-MT-11 | Acima do teto global [30, 30, 1] (10100 -> 10000) | ALUNO | `[30, 30, 1]` | 10000 | `multaTotalCentavos_somaAcimaTetoGlobal_saturaEm10000` |
| VL-MT-12 | Muitos elementos saturando teto [10*5, 30*2] | ALUNO | `[10,10,10,10,10,30,30]` | 10000 | `multaTotalCentavos_muitosElementos_saturaNoTetoGlobal` |
| VL-MT-13 | Carência de professor por elemento [3, 4] | PROFESSOR | `[3, 4]` | 100 | `multaTotalCentavos_professor_carenciaAplicadaPorElemento` |

# Registro de Defeitos Encontrados — Caixa-Preta (Fase 1)

Os resultados esperados foram derivados estritamente da especificação (Anexo A).
Cada falha apresentada abaixo decorre da execução real do comando `mvn test` contra `lib/biblioteca-1.0.jar`.

A coluna **Situação** separa o que é defeito do que é apenas consequência de outro defeito já registrado (o mesmo erro aparecendo em outro método ou em outra entrada).

## Tabela de Defeitos

| # | Método | Entrada | Resultado esperado (spec) | Resultado obtido | Classe/limite que levou ao teste | Teste | Situação |
|---|--------|---------|---------------------------|------------------|----------------------------------|-------|----------|
| 1 | `prazoDias` | `tipo = VISITANTE` | `7` | `10` | CE: PD-V3 (tipo = VISITANTE) | `PrazoDiasTest.prazoPorTipo` | Defeito |
| 2 | `dataDevolucao` | `retirada = 2026-11-03 (ter), tipo = VISITANTE` (e demais entradas com VISITANTE) | `2026-11-10` (+7 dias) | `2026-11-13` (+10 dias) | DD-V3, DD-L2, DD-L5, DD-L8 | `DataDevolucaoTest.semAjusteDiaComum`, `sabadoNaoAjusta`, `domingoVaiParaSegunda`, `segundaNaoAjusta` | Consequência de #1 e #20 (ver nota sobre #2) |
| 3 | `dataDevolucao` | `retirada = 2026-10-30 (sex), tipo = PROFESSOR` | `2026-11-30` (segunda, ajuste de domingo) | `2026-11-29` (domingo) | AVL: DD-L6 (fronteira do domingo para PROFESSOR) | `DataDevolucaoTest.domingoVaiParaSegunda` | Defeito |
| 4 | `multaCentavos` | `tipo = ALUNO/PROFESSOR/VISITANTE, diasAtraso = -1` | `IllegalArgumentException` | Nenhuma exceção lançada (retorna valor calculado) | CE/AVL: CE-MC-I2, CE-MC-I3, CE-MC-I4, VL-MC-01 | `MultaCentavosTest.multaCentavos_diasAtrasoNegativo_lancaExcecao` | Defeito |
| 5 | `multaCentavos` | `tipo = ALUNO, diasAtraso = 11` (e VISITANTE 11) | `1200` centavos (10x100 + 1x200) | `1100` centavos | CE/AVL: CE-MC-V3, VL-MC-05, VL-MC-14 (início faixa 2) | `MultaCentavosTest.multaCentavos_aluno_faixa2_fronteiraEExemploSpec`, `multaCentavos_visitante_valoresELimites` | Defeito |
| 6 | `multaCentavos` | `tipo = ALUNO, diasAtraso = 31` e `1000`; `tipo = VISITANTE, diasAtraso = 31` | `5000` centavos (teto de R$ 50,00) | `5200` e `199000` centavos (sem limitação ao teto) | CE/AVL: CE-MC-V4, VL-MC-09, VL-MC-10, VL-MC-18 (saturação do teto) | `MultaCentavosTest.multaCentavos_aluno_tetoFronteiraESaturacao`, `multaCentavos_visitante_valoresELimites` | Defeito |
| 7 | `multaCentavos` | `tipo = PROFESSOR, diasAtraso = 3` | `0` centavos (carência dos 3 primeiros dias) | `100` centavos | CE/AVL: CE-MC-V6, VL-MC-21 (limite da carência) | `MultaCentavosTest.multaCentavos_professor_carenciaTresDias` | Defeito |
| 8 | `multaCentavos` | `tipo = PROFESSOR, diasAtraso = 4`, `13`, `14`, `32`, `33`, `34`, `1000` | Ex.: 4 dias = `100`, 13 = `1000`, 14 = `1200`, 33 = `5000` | 4 dias = `200`, 13 = `1100`, 14 = `1400`, 33 = `5200`, 1000 = `198600` | CE/AVL: CE-MC-V7, CE-MC-V8, CE-MC-V9, VL-MC-22 a VL-MC-28 | `MultaCentavosTest.multaCentavos_professor_faixa1_fronteiras`, `multaCentavos_professor_14Dias_inicioFaixa2`, `multaCentavos_professor_tetoFronteiraESaturacao` | Consequência de #5, #6 e #7 (carência menor que 3 dias, faixa 2 deslocada e ausência do teto) |
| 9 | `podeEmprestar` | `tipo = ALUNO, multaPendenteCentavos = -1` | `IllegalArgumentException` | Nenhuma exceção lançada | CE/AVL: CE-PE-I3, VL-PE-02 (multa negativa) | `PodeEmprestarTest.podeEmprestar_multaNegativa_lancaExcecao` | Defeito |
| 10 | `podeEmprestar` | `tipo = ALUNO, ativos = 3`, `PROFESSOR ativos = 8`, `VISITANTE ativos = 1` | `false` (limite atingido) | `true` | CE/AVL: CE-PE-V1, CE-PE-V2, CE-PE-V3, VL-PE-05, VL-PE-09, VL-PE-12 | `PodeEmprestarTest.podeEmprestar_aluno_limitesAtivos`, `professor_limitesAtivos`, `visitante_limitesAtivos` | Defeito |
| 11 | `podeEmprestar` | `multaPendenteCentavos = 1001` (com 0 ativos e sem atraso) | `false` (acima do teto de R$ 10,00) | `true` | CE/AVL: CE-PE-V4, VL-PE-16 (multa acima de 1000) | `PodeEmprestarTest.podeEmprestar_multaPendente_fronteiras` | Defeito |
| 12 | `podeEmprestar` | `possuiAtraso = true` (para ALUNO, PROFESSOR e VISITANTE) | `false` (bloqueado por atraso) | `true` | CE/AVL: CE-PE-V5, VL-PE-18 (atraso isolado) | `PodeEmprestarTest.podeEmprestar_atrasoIsolado_impede` | Defeito |
| 13 | `podeEmprestar` | Múltiplas condições inválidas conjuntas (`ativos=3, multa=1001`, etc.) | `false` | `true` | CE/AVL: CE-PE-V6, VL-PE-19, VL-PE-20 | `PodeEmprestarTest.podeEmprestar_multiplasCondicoesInvalidas` | Consequência de #10, #11 e #12 (cada condição já é ignorada isoladamente) |
| 14 | `podeRenovar` | `tipo = PROFESSOR, renovacoesFeitas = 3, reserva = false, atraso = false` | `false` (atingiu o limite de até 3 renovações) | `true` | CE/AVL: CE-PR-V3, VL-PR-09 (limite de 3 renovações) | `PodeRenovarTest.podeRenovar_professor_limitesRenovacoes` | Defeito |
| 15 | `podeRenovar` | `tipo = VISITANTE, renovacoesFeitas = 0, reserva = false, atraso = false` | `false` (visitante nunca renova) | `true` | CE/AVL: CE-PR-V1, VL-PR-02 (visitante nunca renova) | `PodeRenovarTest.podeRenovar_visitante_zeroRenovacoesCondicoesFavoraveis_retornaFalse` | Defeito |
| 16 | `multaTotalCentavos` | Lista com elemento negativo (`[-1]`, `[-1, 2, 3]`, `[5, -2, 3]`, `[2, 4, -5]`) | `IllegalArgumentException` | Nenhuma exceção lançada (retorna valor calculado) | CE/AVL: CE-MT-I3, CE-MT-I4, VL-MT-01 a VL-MT-04 | `MultaTotalCentavosTest.multaTotalCentavos_elementoNegativoUnico_lancaExcecao`, `NoInicio`, `NoMeio`, `NoFim` | Defeito |
| 17 | `multaTotalCentavos` | `tipo = ALUNO, lista = [1, 2, 12]` | `1700` centavos (100 + 200 + 1400) | `1600` centavos | CE/AVL: CE-MT-V3, VL-MT-07 (soma simples) | `MultaTotalCentavosTest.multaTotalCentavos_multiplosElementos_somaSimples` | Defeito (ver nota sobre #17 e #18) |
| 18 | `multaTotalCentavos` | `tipo = PROFESSOR, lista = [3, 4]` | `100` centavos (item 1 carência = 0; item 2 = 100) | `200` centavos | CE/AVL: CE-MT-V9, VL-MT-13 | `MultaTotalCentavosTest.multaTotalCentavos_professor_carenciaAplicadaPorElemento` | Defeito (ver nota sobre #17 e #18) |
| 19 | `multaTotalCentavos` | `tipo = ALUNO, lista = [100, 100]`, `[30, 30]`, `[29, 30]`, `[30, 30, 1]`, `muitos elementos` | Ex.: `[30, 30] = 10000`, `[29, 30] = 9800`, `[30, 30, 1] = 10000` | `5000` na maioria dos casos (⚠ conferir no relatório o valor exato de cada teste; em algum caso foi `5100`), indicando que o método parece aplicar um teto de R$ 50,00 em vez do teto de R$ 100,00 da especificação | CE/AVL: CE-MT-V4 a CE-MT-V8, VL-MT-08 a VL-MT-12 (fronteiras do teto global de R$ 100,00) | `MultaTotalCentavosTest.multaTotalCentavos_multaIndividualLimitadaAntesDeSomar`, `somaExatamenteTetoGlobal`, `somaLogoAbaixoTetoGlobal`, `somaAcimaTetoGlobal_saturaEm10000`, `muitosElementos_saturaNoTetoGlobal` | Defeito |
| 20 | `dataDevolucao` | `retirada = 2026-11-01 (dom), tipo = PROFESSOR` | `2026-12-01` (terça; 01/11 + 30 dias) | `2026-12-02` (um dia a mais) | AVL: DD-L10 (retirada em domingo, resultado em dia útil) | `DataDevolucaoTest.retiradaEmDomingoNaoAjusta` | Defeito |

## Notas

**Nota sobre #2 e #20.** A maioria das falhas de `dataDevolucao` com VISITANTE segue do prazo errado de #1 (10 dias em vez de 7). O caso com retirada em `2026-11-01` (domingo) devolveu `2026-11-12`, um dia a mais que 01/11 + 10 dias (`2026-11-11`). O teste DD-L10 confirmou a causa: com retirada em domingo e PROFESSOR (30 dias), o sistema devolveu `2026-12-02` em vez de `2026-12-01`, ou seja, soma um dia a mais quando a retirada é em domingo. Esse defeito ficava escondido para ALUNO e VISITANTE, porque 14 e 7 dias são múltiplos de 7: a data calculada cai em domingo e o ajuste para segunda dá o mesmo resultado esperado. Só o prazo de 30 dias do professor o expõe.

**Nota sobre #17 e #18.** Os valores obtidos não são a soma do que `multaCentavos` devolve para os mesmos itens (por exemplo, `multaCentavos(ALUNO, 1) + (2) + (12)` = 100 + 200 + 1400 = 1700, e não 1600). Por isso tratamos os dois como defeitos próprios de `multaTotalCentavos`, e não como consequência dos defeitos de `multaCentavos`. A caixa-preta não permite saber se os dois têm a mesma causa.

**Resumo.** Foram registrados 20 itens: 17 defeitos (#1, #3, #4, #5, #6, #7, #9, #10, #11, #12, #14, #15, #16, #17, #18, #19, #20) e 3 registros que são consequência de outros (#2, #8, #13).

---

## Métodos sem defeitos detectados na suíte

- `limiteEmprestimos(TipoUsuario tipo)`: Todos os testes passaram (`ALUNO -> 3`, `PROFESSOR -> 8`, `VISITANTE -> 1`, `null -> IllegalArgumentException`). Nenhum defeito foi detectado pela suíte; isso não prova que o método está correto, apenas que os casos testados passaram.

---

## Dúvidas de Interpretação da Especificação

1. **`podeRenovar` — Contagem de renovações:** A especificação afirma que *"Alunos renovam o mesmo empréstimo até 2 vezes, e professores até 3"*. Adotamos a semântica padrão em bibliotecas onde o parâmetro `renovacoesFeitas` indica as renovações já consumidas. Portanto, com `renovacoesFeitas = 2`, o aluno já utilizou as 2 renovações permitidas e não pode mais renovar (`false`). Para professor, com 3 já feitas, não pode mais renovar (`false`). O sistema falhou ao permitir a renovação de professor com 3 renovações feitas.
2. **`multaTotalCentavos` — Teto global versus teto individual:** A especificação determina expressamente que *"O total nunca passa de R$ 100,00"*. Os testes indicam que o sistema limita o total a um valor próximo de R$ 50,00 (5000 centavos), o que sugere um teto errado para o total acumulado. Como `multaCentavos` também tem defeitos, essa é uma hipótese e não uma confirmação. Mantivemos o resultado esperado estritamente em R$ 100,00 (10000 centavos) conforme o Anexo A.

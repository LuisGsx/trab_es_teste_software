# Registro de Defeitos Encontrados — Caixa-Preta (Fase 1)

Os resultados esperados foram derivados estritamente da especificação (Anexo A).
Cada falha apresentada abaixo decorre da execução real do comando `mvn test` contra `lib/biblioteca-1.0.jar`.

## Tabela de Defeitos

| # | Método | Entrada | Resultado esperado (spec) | Resultado obtido | Classe/limite que levou ao teste | Teste |
|---|--------|---------|---------------------------|------------------|----------------------------------|-------|
| 1 | `prazoDias` | `tipo = VISITANTE` | `7` | `10` | CE: PD-V3 (tipo = VISITANTE) | `PrazoDiasTest.prazoPorTipo` |
| 2 | `dataDevolucao` | `retirada = 2026-11-03 (ter), tipo = VISITANTE` (e demais entradas com VISITANTE) | `2026-11-10` (+7 dias) | `2026-11-13` (+10 dias) | DD-V3, DD-L2, DD-L5, DD-L8 | `DataDevolucaoTest.semAjusteDiaComum`, `sabadoNaoAjusta`, `domingoVaiParaSegunda`, `segundaNaoAjusta` |
| 3 | `dataDevolucao` | `retirada = 2026-10-30 (sex), tipo = PROFESSOR` | `2026-11-30` (segunda, ajuste de domingo) | `2026-11-29` (domingo) | AVL: DD-L6 (fronteira do domingo para PROFESSOR) | `DataDevolucaoTest.domingoVaiParaSegunda` |
| 4 | `multaCentavos` | `tipo = ALUNO/PROFESSOR/VISITANTE, diasAtraso = -1` | `IllegalArgumentException` | Nenhuma exceção lançada (retorna valor calculado) | CE/AVL: CE-MC-I2, CE-MC-I3, CE-MC-I4, VL-MC-01 | `MultaCentavosTest.multaCentavos_diasAtrasoNegativo_lancaExcecao` |
| 5 | `multaCentavos` | `tipo = ALUNO, diasAtraso = 11` (e VISITANTE 11) | `1200` centavos (10x100 + 1x200) | `1100` centavos | CE/AVL: CE-MC-V3, VL-MC-05, VL-MC-14 (início faixa 2) | `MultaCentavosTest.multaCentavos_aluno_faixa2_fronteiraEExemploSpec`, `multaCentavos_visitante_valoresELimites` |
| 6 | `multaCentavos` | `tipo = ALUNO, diasAtraso = 31` e `1000`; `tipo = VISITANTE, diasAtraso = 31` | `5000` centavos (teto de R$ 50,00) | `5200` e `199000` centavos (sem limitação ao teto) | CE/AVL: CE-MC-V4, VL-MC-09, VL-MC-10, VL-MC-18 (saturação do teto) | `MultaCentavosTest.multaCentavos_aluno_tetoFronteiraESaturacao`, `multaCentavos_visitante_valoresELimites` |
| 7 | `multaCentavos` | `tipo = PROFESSOR, diasAtraso = 3` | `0` centavos (carência dos 3 primeiros dias) | `100` centavos | CE/AVL: CE-MC-V6, VL-MC-21 (limite da carência) | `MultaCentavosTest.multaCentavos_professor_carenciaTresDias` |
| 8 | `multaCentavos` | `tipo = PROFESSOR, diasAtraso = 4`, `13`, `14`, `32`, `33`, `34`, `1000` | Ex.: 4 dias = `100`, 13 = `1000`, 33 = `5000` | 4 dias = `200`, 13 = `1100`, 14 = `1400`, 33 = `5200`, 1000 = `198600` | CE/AVL: CE-MC-V7, CE-MC-V8, CE-MC-V9, VL-MC-22 a VL-MC-28 | `MultaCentavosTest.multaCentavos_professor_faixa1_fronteiras`, `multaCentavos_professor_14Dias_inicioFaixa2`, `multaCentavos_professor_tetoFronteiraESaturacao` |
| 9 | `podeEmprestar` | `tipo = ALUNO, multaPendenteCentavos = -1` | `IllegalArgumentException` | Nenhuma exceção lançada | CE/AVL: CE-PE-I3, VL-PE-02 (multa negativa) | `PodeEmprestarTest.podeEmprestar_multaNegativa_lancaExcecao` |
| 10 | `podeEmprestar` | `tipo = ALUNO, ativos = 3`, `PROFESSOR ativos = 8`, `VISITANTE ativos = 1` | `false` (limite atingido) | `true` | CE/AVL: CE-PE-V1, CE-PE-V2, CE-PE-V3, VL-PE-05, VL-PE-09, VL-PE-12 | `PodeEmprestarTest.podeEmprestar_aluno_limitesAtivos`, `professor_limitesAtivos`, `visitante_limitesAtivos` |
| 11 | `podeEmprestar` | `multaPendenteCentavos = 1001` (com 0 ativos e sem atraso) | `false` (acima do teto de R$ 10,00) | `true` | CE/AVL: CE-PE-V4, VL-PE-16 (multa acima de 1000) | `PodeEmprestarTest.podeEmprestar_multaPendente_fronteiras` |
| 12 | `podeEmprestar` | `possuiAtraso = true` (para ALUNO, PROFESSOR e VISITANTE) | `false` (bloqueado por atraso) | `true` | CE/AVL: CE-PE-V5, VL-PE-18 (atraso isolado) | `PodeEmprestarTest.podeEmprestar_atrasoIsolado_impede` |
| 13 | `podeEmprestar` | Múltiplas condições inválidas conjuntas (`ativos=3, multa=1001`, etc.) | `false` | `true` | CE/AVL: CE-PE-V6, VL-PE-19, VL-PE-20 | `PodeEmprestarTest.podeEmprestar_multiplasCondicoesInvalidas` |
| 14 | `podeRenovar` | `tipo = PROFESSOR, renovacoesFeitas = 3, reserva = false, atraso = false` | `false` (atingiu o limite de até 3 renovações) | `true` | CE/AVL: CE-PR-V3, VL-PR-09 (limite de 3 renovações) | `PodeRenovarTest.podeRenovar_professor_limitesRenovacoes` |
| 15 | `podeRenovar` | `tipo = VISITANTE, renovacoesFeitas = 0, reserva = false, atraso = false` | `false` (visitante nunca renova) | `true` | CE/AVL: CE-PR-V1, VL-PR-02 (visitante nunca renova) | `PodeRenovarTest.podeRenovar_visitante_zeroRenovacoesCondicoesFavoraveis_retornaFalse` |
| 16 | `multaTotalCentavos` | Lista com elemento negativo (`[-1]`, `[-1, 2, 3]`, `[5, -2, 3]`, `[2, 4, -5]`) | `IllegalArgumentException` | Nenhuma exceção lançada (retorna valor calculado) | CE/AVL: CE-MT-I3, CE-MT-I4, VL-MT-01 a VL-MT-04 | `MultaTotalCentavosTest.multaTotalCentavos_elementoNegativoUnico_lancaExcecao`, `NoInicio`, `NoMeio`, `NoFim` |
| 17 | `multaTotalCentavos` | `tipo = ALUNO, lista = [1, 2, 12]` | `1700` centavos (100 + 200 + 1400) | `1600` centavos (consequência do defeito na faixa 2 de multaCentavos) | CE/AVL: CE-MT-V3, VL-MT-07 (soma simples) | `MultaTotalCentavosTest.multaTotalCentavos_multiplosElementos_somaSimples` |
| 18 | `multaTotalCentavos` | `tipo = PROFESSOR, lista = [3, 4]` | `100` centavos (item 1 carência = 0; item 2 = 100) | `200` centavos (consequência do defeito na carência do professor) | CE/AVL: CE-MT-V9, VL-MT-13 | `MultaTotalCentavosTest.multaTotalCentavos_professor_carenciaAplicadaPorElemento` |
| 19 | `multaTotalCentavos` | `tipo = ALUNO, lista = [100, 100]`, `[30, 30]`, `[29, 30]`, `[30, 30, 1]`, `muitos elementos` | Ex.: `[30, 30] = 10000`, `[29, 30] = 9800`, `[30, 30, 1] = 10000` | Todas retornam `5000` (ou `5100`), indicando que o método aplica teto global de R$ 50,00 em vez do teto de R$ 100,00 da especificação | CE/AVL: CE-MT-V4 a CE-MT-V8, VL-MT-08 a VL-MT-12 (fronteiras do teto global de R$ 100,00) | `MultaTotalCentavosTest.multaTotalCentavos_multaIndividualLimitadaAntesDeSomar`, `somaExatamenteTetoGlobal`, `somaLogoAbaixoTetoGlobal`, `somaAcimaTetoGlobal_saturaEm10000`, `muitosElementos_saturaNoTetoGlobal` |

---

## Métodos sem defeitos detectados na suíte

- `limiteEmprestimos(TipoUsuario tipo)`: Todos os testes passaram (`ALUNO -> 3`, `PROFESSOR -> 8`, `VISITANTE -> 1`, `null -> IllegalArgumentException`). Nenhum defeito aparente encontrado neste método isolado.

---

## Dúvidas de Interpretação da Especificação

1. **`podeRenovar` — Contagem de renovações:** A especificação afirma que *"Alunos renovam o mesmo empréstimo até 2 vezes, e professores até 3"*. Adotamos a semântica padrão em bibliotecas onde o parâmetro `renovacoesFeitas` indica as renovações já consumidas. Portanto, com `renovacoesFeitas = 2`, o aluno já utilizou as 2 renovações permitidas e não pode mais renovar (`false`). Para professor, com 3 já feitas, não pode mais renovar (`false`). O sistema falhou ao permitir a renovação de professor com 3 renovações feitas.
2. **`multaTotalCentavos` — Teto global versus teto individual:** A especificação determina expressamente que *"O total nunca passa de R$ 100,00"*. No entanto, os testes revelaram que o sistema limita o total da soma a R$ 50,00 (5000 centavos), sugerindo que reutilizou erroneamente a constante de teto de `multaCentavos` para o total acumulado. Mantivemos o resultado esperado estritamente em R$ 100,00 (10000 centavos) conforme o Anexo A.

# Plano de testes — Caixa-preta (fase 1)

Técnicas: partição de equivalência (PE) e análise de valor-limite (AVL). Resultados esperados derivados só do Anexo A.
Regra geral do Anexo A: em todos os métodos, `tipo` nulo lança `IllegalArgumentException`.

---

## 1. `prazoDias(TipoUsuario tipo)`

Entrada única: `tipo` (enumeração, sem fronteiras numéricas → só PE, cada valor do enum é uma classe).

| ID | Classe de equivalência | Válida? | Entrada | Esperado | Teste |
|----|------------------------|---------|---------|----------|-------|
| PD-V1 | tipo = ALUNO | sim | ALUNO | 14 | `PrazoDiasTest.prazoPorTipo` |
| PD-V2 | tipo = PROFESSOR | sim | PROFESSOR | 30 | `PrazoDiasTest.prazoPorTipo` |
| PD-V3 | tipo = VISITANTE | sim | VISITANTE | 7 | `PrazoDiasTest.prazoPorTipo` |
| PD-I1 | tipo nulo | não | null | `IllegalArgumentException` | `PrazoDiasTest.tipoNuloLancaExcecao` |

---

## 2. `limiteEmprestimos(TipoUsuario tipo)`

Mesma estrutura: só PE sobre o enum.

| ID | Classe de equivalência | Válida? | Entrada | Esperado | Teste |
|----|------------------------|---------|---------|----------|-------|
| LE-V1 | tipo = ALUNO | sim | ALUNO | 3 | `LimiteEmprestimosTest.limitePorTipo` |
| LE-V2 | tipo = PROFESSOR | sim | PROFESSOR | 8 | `LimiteEmprestimosTest.limitePorTipo` |
| LE-V3 | tipo = VISITANTE | sim | VISITANTE | 1 | `LimiteEmprestimosTest.limitePorTipo` |
| LE-I1 | tipo nulo | não | null | `IllegalArgumentException` | `LimiteEmprestimosTest.tipoNuloLancaExcecao` |

---

## 3. `dataDevolucao(LocalDate retirada, TipoUsuario tipo)`

Regra: retirada + prazo do tipo; se cair num domingo, passa para a segunda seguinte; sábado é dia normal.

**Entradas e classes**
- `tipo`: ALUNO, PROFESSOR, VISITANTE (válidas); nulo (inválida).
- `retirada`: data válida (válida); nula (inválida).
- Saída (particiona o domínio pelo dia da semana da data calculada): cai em domingo (ajusta +1 dia) / cai em outro dia (sem ajuste).

**Valores-limite:** a fronteira da regra é o domingo. Testamos o dia imediatamente antes (sábado, não ajusta), o próprio domingo (ajusta) e o dia depois (segunda, não ajusta). Fronteiras de calendário: o ajuste atravessa fim de mês, fim de ano e 29 de fevereiro.

Referência de datas: 2026-11-01 é domingo.

| ID | Classe / limite | Retirada | Tipo | Cálculo | Esperado | Teste |
|----|-----------------|----------|------|---------|----------|-------|
| DD-V1 | resultado em dia comum (sem ajuste) | 2026-11-03 (ter) | ALUNO | +14 = 2026-11-17 (ter) | 2026-11-17 | `semAjusteDiaComum` |
| DD-V2 | idem, PROFESSOR | 2026-11-03 (ter) | PROFESSOR | +30 = 2026-12-03 (qui) | 2026-12-03 | `semAjusteDiaComum` |
| DD-V3 | idem, VISITANTE | 2026-11-03 (ter) | VISITANTE | +7 = 2026-11-10 (ter) | 2026-11-10 | `semAjusteDiaComum` |
| DD-L1 | limite: sábado (véspera do domingo), ALUNO | 2026-11-07 (sáb) | ALUNO | +14 = 2026-11-21 (sáb) | 2026-11-21 | `sabadoNaoAjusta` |
| DD-L2 | limite: sábado, VISITANTE | 2026-11-07 (sáb) | VISITANTE | +7 = 2026-11-14 (sáb) | 2026-11-14 | `sabadoNaoAjusta` |
| DD-L3 | limite: sábado, PROFESSOR | 2026-10-29 (qui) | PROFESSOR | +30 = 2026-11-28 (sáb) | 2026-11-28 | `sabadoNaoAjusta` |
| DD-L4 | limite: domingo, ALUNO | 2026-11-01 (dom) | ALUNO | +14 = 2026-11-15 (dom) | 2026-11-16 (seg) | `domingoVaiParaSegunda` |
| DD-L5 | limite: domingo, VISITANTE | 2026-11-01 (dom) | VISITANTE | +7 = 2026-11-08 (dom) | 2026-11-09 (seg) | `domingoVaiParaSegunda` |
| DD-L6 | limite: domingo, PROFESSOR | 2026-10-30 (sex) | PROFESSOR | +30 = 2026-11-29 (dom) | 2026-11-30 (seg) | `domingoVaiParaSegunda` |
| DD-L7 | limite: segunda (dia após o domingo), ALUNO | 2026-11-02 (seg) | ALUNO | +14 = 2026-11-16 (seg) | 2026-11-16 | `segundaNaoAjusta` |
| DD-L8 | limite: segunda, VISITANTE | 2026-11-02 (seg) | VISITANTE | +7 = 2026-11-09 (seg) | 2026-11-09 | `segundaNaoAjusta` |
| DD-L9 | limite: segunda, PROFESSOR | 2026-10-31 (sáb) | PROFESSOR | +30 = 2026-11-30 (seg) | 2026-11-30 | `segundaNaoAjusta` |
| DD-C1 | calendário: domingo no fim do mês | 2027-01-17 | ALUNO | +14 = 2027-01-31 (dom) | 2027-02-01 | `ajusteAtravessaCalendario` |
| DD-C2 | calendário: domingo no fim do ano | 2028-12-17 | ALUNO | +14 = 2028-12-31 (dom) | 2029-01-01 | `ajusteAtravessaCalendario` |
| DD-C3 | calendário: domingo em 29/fev (bissexto) | 2032-02-15 | ALUNO | +14 = 2032-02-29 (dom) | 2032-03-01 | `ajusteAtravessaCalendario` |
| DD-I1 | tipo nulo | 2026-11-03 | null | — | `IllegalArgumentException` | `tipoNuloLancaExcecao` |
| DD-I2 | retirada nula | null | ALUNO | — | `IllegalArgumentException` | `retiradaNulaLancaExcecao` |
| DD-I3 | retirada e tipo nulos | null | null | — | `IllegalArgumentException` | `ambosNulosLancaExcecao` |

Observação: DD-V1..V3 cobrem os três prazos; DD-L* repetem a fronteira do domingo para cada tipo, porque um defeito pode estar na regra do domingo de um tipo só.

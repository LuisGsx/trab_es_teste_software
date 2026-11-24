# Guia de Preparação para a Arguição Oral — Fase 1 (Caixa-Preta)

Este documento foi elaborado para preparar os integrantes da equipe para a **Arguição 1 (Presencial e Individual)** sobre teste de software caixa-preta do sistema de biblioteca.

A nota individual depende da capacidade de cada integrante de justificar tecnicamente as escolhas da suíte com base nos conceitos de **Partição em Classes de Equivalência (PE)** e **Análise de Valores-Limite (AVL)** derivados estritamente do Anexo A da especificação.

---

## 1. Princípios Fundamentais para a Defesa

1. **A especificação é a única fonte da verdade**: Nunca justifique um teste pelo comportamento do sistema compilado (`.jar`). Um teste que falha é um **sucesso** da suíte, pois revelou um defeito.
2. **Uma causa de falha por teste**: Nunca combinamos entradas inválidas no mesmo teste porque a ordem de validação interna não é contratada. Se combinássemos duas entradas inválidas, uma validação poderia mascarar a ausência da outra.
3. **Rastreabilidade**: Todo teste implementado corresponde a um ID do [PLANO-DE-TESTES.md](file:///W:/projetos/trab_es_teste_software/PLANO-DE-TESTES.md). Se o professor perguntar de onde saiu um teste, aponte a classe de equivalência ou a fronteira limite na tabela correspondente.

---

## 2. Perguntas Prováveis e Respostas Técnicas

### Pergunta 1: *Por que no método `podeEmprestar` foram testados os valores 1000 e 1001 para a multa pendente?*
- **Técnica aplicada**: Análise de Valores-Limite (AVL).
- **Resposta**:
  > *"A especificação estabelece como regra que a multa pendente deve ser de 'no máximo R$ 10,00' (1000 centavos). Pela técnica de Análise de Valor-Limite, o maior risco de defeito por troca de operador relacional (`<` em vez de `<=`) ou erro off-by-one ocorre exatamente na fronteira. Testamos o valor limite superior da partição válida (1000 centavos, que deve retornar `true`) e o valor imediatamente vizinho na partição inválida (1001 centavos, que deve retornar `false`)."*

---

### Pergunta 2: *O que mudaria no plano e nos testes se o teto de `multaCentavos` passasse de R$ 50,00 para R$ 60,00?*
- **Técnica aplicada**: Recálculo de fronteiras da AVL.
- **Resposta**:
  > *"Recalcularíamos a fronteira de saturação do teto:
  > - Os primeiros 10 dias cobrados custam R$ 1,00/dia = 1000 centavos.
  > - Para atingir o novo teto de R$ 60,00 (6000 centavos), restam 5000 centavos a R$ 2,00/dia, o que equivale a mais 25 dias cobrados na faixa 2.
  > - Logo, o número total de dias cobrados para atingir o teto passa a ser 10 + 25 = 35 dias cobrados.
  > - Para **Aluno** e **Visitante**: testaríamos 34 dias (5800 centavos, logo antes do teto), 35 dias (6000 centavos, teto exato) e 36 dias (6000 centavos, saturação).
  > - Para **Professor**: somando a carência de 3 dias, a fronteira do teto iria para 38 dias de atraso (35 cobrados = 6000), testando 37, 38 e 39 dias."*

---

### Pergunta 3: *Por que não combinamos `emprestimosAtivos = -1` com `multaPendenteCentavos = -1` no mesmo teste em `podeEmprestar`?*
- **Técnica aplicada**: Princípio de Isolamento de Causa de Falha (Regra inegociável de Caixa-Preta).
- **Resposta**:
  > *"Porque a especificação não define qual validação deve ocorrer primeiro no código. Se combinássemos duas entradas inválidas em um mesmo teste e o sistema lançasse exceção para a primeira, o teste passaria sem jamais verificar se a validação da segunda entrada foi realmente implementada. Além disso, se o teste falhasse de forma inesperada, seria impossível determinar qual das entradas causou o erro."*

---

### Pergunta 4: *Como você justificou os testes de `renovacoesFeitas` para Aluno serem 0, 1, 2 e 3?*
- **Técnica aplicada**: Semântica de domínio e AVL.
- **Resposta**:
  > *"A regra do Anexo A diz que o aluno renova o mesmo empréstimo 'até 2 vezes'. O parâmetro `renovacoesFeitas` indica a contagem de renovações já realizadas anteriormente para aquele exemplar:
  > - `renovacoesFeitas = 0`: o aluno está solicitando a 1ª renovação (permitido -> `true`).
  > - `renovacoesFeitas = 1`: o aluno está solicitando a 2ª renovação, atingindo o teto permitido (permitido -> `true`).
  > - `renovacoesFeitas = 2`: o aluno já efetuou as 2 renovações às quais tinha direito; qualquer nova renovação violaria o limite 'até 2 vezes' (fronteira esgotada -> `false`).
  > - `renovacoesFeitas = 3`: acima do limite (inválido -> `false`)."*

---

### Pergunta 5: *Por que testamos elementos negativos no início, no meio e no fim do array em `multaTotalCentavos`?*
- **Técnica aplicada**: Partição de Coleções e Estruturas Indexadas.
- **Resposta**:
  > *"Porque falhas de iteração em coleções dependem comumente do índice do laço. Por exemplo:
  > - Um loop que começa em `i = 1` deixaria de validar o elemento do início `[-1, 2, 3]`.
  > - Um loop que usa `i < length - 1` deixaria de validar o último elemento `[2, 4, -5]`.
  > - Uma lógica com interrupção prematura (`break` ou atribuição incorreta) poderia não examinar o meio `[5, -2, 3]`.
  > Testar essas posições garante que o contrato de validação (`qualquer elemento negativo lança IllegalArgumentException`) seja respeitado independentemente da posição no array."*

---

### Pergunta 6: *Por que foram testadas as fronteiras de calendário em `dataDevolucao` (fim de mês, fim de ano e ano bissexto)?*
- **Técnica aplicada**: Valores-Limite de Domínio Temporal.
- **Resposta**:
  > *"O método precisa somar o prazo e, caso o dia final caia em um domingo, transferir para a segunda-feira seguinte (+1 dia). Esse ajuste de domingo para segunda pode coincidir com transições críticas de calendário:
  > - Domingo em 31/01 -> Segunda em 01/02 (mudança de mês).
  > - Domingo em 31/12 -> Segunda em 01/01 (mudança de ano).
  > - Domingo em 29/02 de ano bissexto -> Segunda em 01/03 (limite de ano bissexto).
  > Testar essas datas garante que a regra de ajuste de domingo não cause exceções ou erros de formatação ao cruzar os limites de mês e ano."*

---

### Pergunta 7: *Qual a diferença entre a carência do Professor e os demais tipos em `multaCentavos` e em `multaTotalCentavos`?*
- **Técnica aplicada**: Rastreabilidade de Regra de Negócio Composta.
- **Resposta**:
  > *"Em `multaCentavos`, o professor não paga multa nos 3 primeiros dias de atraso (dias 0 a 3 resultam em 0 centavos; a cobrança só se inicia no 4º dia). Em `multaTotalCentavos`, a especificação determina que cada empréstimo da lista é calculado individualmente como em `multaCentavos`. Portanto, a carência de 3 dias se aplica a **cada item do array separadamente**, e não à soma total de dias."*

---

## 3. Relação Rápida de Defeitos Encontrados para Citar na Arguição

Se o professor perguntar: *"Quais foram os principais defeitos encontrados na versão compilada?"*, mencione com segurança:

1. **`prazoDias`**: Visitante retorna 10 dias em vez dos 7 previstos na especificação.
2. **`dataDevolucao`**: Não ajusta o domingo para a segunda-feira no caso de Professor (ex: retirada 2026-10-30 cai em domingo 29/11 e o sistema devolve domingo em vez de segunda 30/11).
3. **`multaCentavos`**:
   - Não valida dias de atraso negativos (não lança `IllegalArgumentException` para `-1`).
   - Calcula incorretamente a virada da faixa 2 (cobra 1100 em vez de 1200 para 11 dias).
   - Não aplica o teto de R$ 50,00 (continua cobrando além de 5000 centavos, ex.: 31 dias deu 5200; 1000 dias deu 199000).
   - Cobra professor no 3º dia de atraso (violando a carência dos 3 primeiros dias).
4. **`podeEmprestar`**:
   - Não valida multa negativa (`multaPendenteCentavos = -1`).
   - Permite empréstimo quando o limite é atingido (usou `<=` em vez de `<`).
   - Permite empréstimo com multa superior a R$ 10,00 (1001 centavos).
   - Ignora completamente a flag `possuiAtraso = true`.
5. **`podeRenovar`**:
   - Permite renovação para Professor com 3 renovações feitas (ultrapassando 'até 3').
   - Permite renovação para Visitante com 0 renovações (violando a regra 'visitante nunca renova').
6. **`multaTotalCentavos`**:
   - Não valida elementos negativos na lista.
   - Aplica incorretamente o teto de R$ 50,00 em vez do teto global de R$ 100,00 especificado no Anexo A.

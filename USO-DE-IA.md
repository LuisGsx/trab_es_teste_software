# Uso de Inteligência Artificial — Fase 1 (Caixa-Preta)

## O que pedimos à IA
- Solicitamos à IA que analisasse os arquivos existentes do projeto (`src/test/java/**`, `PLANO-DE-TESTES.md`, `DEFEITOS.md`, `pom.xml`) e o Anexo A da especificação para diagnosticar o que já havia sido testado e o que ainda faltava cobrir.
- Pedimos que a IA estruturasse a partição em classes de equivalência (PE) e a análise de valores-limite (AVL) para os 4 métodos faltantes (`multaCentavos`, `podeEmprestar`, `podeRenovar`, `multaTotalCentavos`), gerando a documentação completa em `PLANO-DE-TESTES.md`.
- Solicitamos a geração das quatro classes de teste correspondentes em JUnit 5 (`MultaCentavosTest`, `PodeEmprestarTest`, `PodeRenovarTest`, `MultaTotalCentavosTest`), garantindo rastreabilidade com comentários de ID de classe/limite, testes parametrizados com `@ParameterizedTest` / `@CsvSource` e `@DisplayName`.
- Pedimos auxílio na configuração do Apache Maven no ambiente de execução (PowerShell / Windows) para permitir a compilação e execução automatizada via `mvn test`.
- Pedimos a compilação do relatório de defeitos em `DEFEITOS.md` com base estritamente nas falhas reais observadas no `surefire-reports`, além de perguntas prováveis para a arguição oral com suas justificativas técnicas.

## O que aproveitamos
- A estrutura e as tabelas formais de Classes de Equivalência e Valores-Limite adicionadas ao `PLANO-DE-TESTES.md`, que cobriram exaustivamente os requisitos de cada método.
- Os testes em JUnit 5 gerados para os 4 métodos faltantes, que seguiram à risca a especificação sem violar a regra de caixa-preta (nenhuma inspeção interna do `.jar`).
- A compilação e execução automatizada via `mvn test`, que gerou 114 testes no total e revelou 45 falhas reais sem erros de compilação.
- A catalogação dos defeitos no `DEFEITOS.md`, agrupando falhas com a mesma causa aparente e registrando a rastreabilidade direta com o caso de teste e a classe/limite do plano.
- As respostas formuladas para a preparação da equipe para a arguição oral presencial.

## O que tivemos de corrigir
- Inicialmente, em uma rodada intermediária, alguns testes de `multaTotalCentavos` foram gerados com valores arbitrários que não cobriam as fronteiras críticas especificadas no checklist do professor (como o teste de `[1, 2, 12]`, elemento negativo no início, meio e fim, e a distinção clara entre teto individual de 5000 e teto global de 10000). A equipe solicitou a refatoração para garantir a exata aderência ao checklist.
- Corrigimos o plano de testes para separar explicitamente as tabelas de Classes de Equivalência e as tabelas de Valores-Limite para cada um dos quatro métodos, alinhando ao formato padrão exigido.
- Reforçamos os nomes dos métodos de teste para que fossem autodescritivos e incluíssem anotações explícitas e comentários com o ID exato da tabela do plano de testes.

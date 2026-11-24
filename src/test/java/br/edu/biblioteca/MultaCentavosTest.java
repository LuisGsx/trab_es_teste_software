package br.edu.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Caixa-preta: multaCentavos. Ver PLANO-DE-TESTES.md, seção 4.
 */
class MultaCentavosTest {

    private final RegrasBiblioteca regras = new RegrasBiblioteca();

    // CE-MC-I1: Tipo nulo lança IllegalArgumentException
    @Test
    @DisplayName("Tipo nulo lança IllegalArgumentException")
    void multaCentavos_tipoNulo_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> regras.multaCentavos(null, 5));
    }

    // CE-MC-I2, CE-MC-I3, CE-MC-I4, VL-MC-01: Dias de atraso negativo (-1) lança IllegalArgumentException
    @ParameterizedTest(name = "{0} com diasAtraso={1} -> IllegalArgumentException")
    @CsvSource({
        "ALUNO, -1",
        "PROFESSOR, -1",
        "VISITANTE, -1"
    })
    @DisplayName("Dias de atraso negativo lança IllegalArgumentException")
    void multaCentavos_diasAtrasoNegativo_lancaExcecao(TipoUsuario tipo, int diasAtraso) {
        // VL-MC-01: Fronteira inferior inválida (-1)
        assertThrows(IllegalArgumentException.class, () -> regras.multaCentavos(tipo, diasAtraso));
    }

    // CE-MC-V1, VL-MC-02: Aluno sem atraso (0 dias) -> 0 centavos
    @Test
    @DisplayName("Aluno sem atraso -> 0 centavos")
    void multaCentavos_aluno_zeroDias_semMulta() {
        // VL-MC-02: Fronteira inferior válida (0 dias)
        assertEquals(0, regras.multaCentavos(TipoUsuario.ALUNO, 0));
    }

    // CE-MC-V2, VL-MC-03, VL-MC-04: Aluno na faixa 1 (1 a 10 dias a R$ 1,00/dia = 100 centavos)
    @ParameterizedTest(name = "Aluno {0} dias atraso -> {1} centavos")
    @CsvSource({
        "1, 100",   // VL-MC-03: Início faixa 1 (1 dia cobrado)
        "10, 1000"  // VL-MC-04: Fim faixa 1 (10 dias cobrados)
    })
    @DisplayName("Aluno faixa 1 (1 a 10 dias) cobrados a 100 centavos/dia")
    void multaCentavos_aluno_faixa1_fronteiras(int diasAtraso, int esperado) {
        assertEquals(esperado, regras.multaCentavos(TipoUsuario.ALUNO, diasAtraso));
    }

    // CE-MC-V3, VL-MC-05, VL-MC-06: Aluno faixa 2 (11º dia em diante a R$ 2,00/dia = 200 centavos)
    @ParameterizedTest(name = "Aluno {0} dias atraso -> {1} centavos")
    @CsvSource({
        "11, 1200", // VL-MC-05: Início faixa 2 (10*100 + 1*200)
        "12, 1400"  // VL-MC-06: Exemplo da especificação (10*100 + 2*200)
    })
    @DisplayName("Aluno faixa 2 vira cobrança dobrada")
    void multaCentavos_aluno_faixa2_fronteiraEExemploSpec(int diasAtraso, int esperado) {
        assertEquals(esperado, regras.multaCentavos(TipoUsuario.ALUNO, diasAtraso));
    }

    // CE-MC-V4, VL-MC-07, VL-MC-08, VL-MC-09, VL-MC-10: Aluno fronteira do teto de R$ 50,00 (5000 centavos)
    @ParameterizedTest(name = "Aluno {0} dias atraso -> {1} centavos")
    @CsvSource({
        "29, 4800",   // VL-MC-07: Imediatamente antes do teto (29 dias cobrados)
        "30, 5000",   // VL-MC-08: Teto atingido exatamente (30 dias cobrados = 1000 + 4000)
        "31, 5000",   // VL-MC-09: Imediatamente acima do teto (saturação)
        "1000, 5000"  // VL-MC-10: Valor grande sem overflow
    })
    @DisplayName("Aluno fronteira do teto de 5000 centavos e saturação")
    void multaCentavos_aluno_tetoFronteiraESaturacao(int diasAtraso, int esperado) {
        assertEquals(esperado, regras.multaCentavos(TipoUsuario.ALUNO, diasAtraso));
    }

    // CE-MC-V5, VL-MC-11 a VL-MC-18: Visitante (mesmas regras de aluno, todos os dias cobrados)
    @ParameterizedTest(name = "Visitante {0} dias atraso -> {1} centavos")
    @CsvSource({
        "0, 0",       // VL-MC-11: 0 dias
        "1, 100",     // VL-MC-12: Início faixa 1
        "10, 1000",   // VL-MC-13: Fim faixa 1
        "11, 1200",   // VL-MC-14: Início faixa 2
        "12, 1400",   // VL-MC-15: Meio faixa 2
        "29, 4800",   // VL-MC-16: Antes do teto
        "30, 5000",   // VL-MC-17: Teto exato
        "31, 5000"    // VL-MC-18: Acima do teto
    })
    @DisplayName("Visitante segue regras sem carência e respeita teto")
    void multaCentavos_visitante_valoresELimites(int diasAtraso, int esperado) {
        assertEquals(esperado, regras.multaCentavos(TipoUsuario.VISITANTE, diasAtraso));
    }

    // CE-MC-V6, VL-MC-19 a VL-MC-21: Professor carência de 3 dias (0, 1, 3 dias -> 0 centavos)
    @ParameterizedTest(name = "Professor carência {0} dias atraso -> 0 centavos")
    @CsvSource({
        "0", // VL-MC-19: Sem atraso
        "1", // VL-MC-20: Primeiro dia de carência
        "3"  // VL-MC-21: Limite da carência (3º dia não cobrado)
    })
    @DisplayName("Professor carência de 3 dias não cobra nada")
    void multaCentavos_professor_carenciaTresDias(int diasAtraso) {
        assertEquals(0, regras.multaCentavos(TipoUsuario.PROFESSOR, diasAtraso));
    }

    // CE-MC-V7, VL-MC-22, VL-MC-23: Professor faixa 1 (4 a 13 dias de atraso = 1 a 10 dias cobrados)
    @ParameterizedTest(name = "Professor {0} dias atraso -> {1} centavos")
    @CsvSource({
        "4, 100",   // VL-MC-22: 4 dias atraso = 1º dia cobrado (100 centavos)
        "13, 1000"  // VL-MC-23: 13 dias atraso = 10º dia cobrado (1000 centavos)
    })
    @DisplayName("Professor faixa 1 após carência")
    void multaCentavos_professor_faixa1_fronteiras(int diasAtraso, int esperado) {
        assertEquals(esperado, regras.multaCentavos(TipoUsuario.PROFESSOR, diasAtraso));
    }

    // CE-MC-V8, VL-MC-24: Professor faixa 2 (14 dias atraso = 11º dia cobrado = 1200 centavos)
    @Test
    @DisplayName("Professor 14 dias atraso -> 11º dia cobrado vira faixa dobrada (1200 centavos)")
    void multaCentavos_professor_14Dias_inicioFaixa2() {
        // VL-MC-24: 11º dia cobrado de professor
        assertEquals(1200, regras.multaCentavos(TipoUsuario.PROFESSOR, 14));
    }

    // CE-MC-V9, VL-MC-25 a VL-MC-28: Professor teto de 5000 centavos (33 dias = 30 cobrados = 5000)
    @ParameterizedTest(name = "Professor {0} dias atraso -> {1} centavos")
    @CsvSource({
        "32, 4800",   // VL-MC-25: 32 dias = 29 dias cobrados (antes do teto)
        "33, 5000",   // VL-MC-26: 33 dias = 30 dias cobrados (teto exato)
        "34, 5000",   // VL-MC-27: 34 dias = 31 dias cobrados (acima do teto)
        "1000, 5000"  // VL-MC-28: Valor grande de professor sem overflow
    })
    @DisplayName("Professor fronteira do teto de 5000 centavos e saturação")
    void multaCentavos_professor_tetoFronteiraESaturacao(int diasAtraso, int esperado) {
        assertEquals(esperado, regras.multaCentavos(TipoUsuario.PROFESSOR, diasAtraso));
    }
}

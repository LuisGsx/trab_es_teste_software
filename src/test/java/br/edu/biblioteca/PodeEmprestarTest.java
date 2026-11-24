package br.edu.biblioteca;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Caixa-preta: podeEmprestar. Ver PLANO-DE-TESTES.md, seção 5.
 */
class PodeEmprestarTest {

    private final RegrasBiblioteca regras = new RegrasBiblioteca();

    // CE-PE-I1: Tipo nulo lança IllegalArgumentException
    @Test
    @DisplayName("Tipo nulo lança IllegalArgumentException")
    void podeEmprestar_tipoNulo_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> regras.podeEmprestar(null, 0, 0, false));
    }

    // CE-PE-I2, VL-PE-01: Empréstimos ativos negativo (-1) lança IllegalArgumentException
    @Test
    @DisplayName("Empréstimos ativos negativo (-1) lança IllegalArgumentException")
    void podeEmprestar_ativosNegativo_lancaExcecao() {
        // VL-PE-01: Fronteira inferior inválida de ativos
        assertThrows(IllegalArgumentException.class,
                () -> regras.podeEmprestar(TipoUsuario.ALUNO, -1, 0, false));
    }

    // CE-PE-I3, VL-PE-02: Multa pendente negativa (-1) lança IllegalArgumentException
    @Test
    @DisplayName("Multa pendente negativa (-1) lança IllegalArgumentException")
    void podeEmprestar_multaNegativa_lancaExcecao() {
        // VL-PE-02: Fronteira inferior inválida de multa
        assertThrows(IllegalArgumentException.class,
                () -> regras.podeEmprestar(TipoUsuario.ALUNO, 0, -1, false));
    }

    // CE-PE-V1, VL-PE-03, VL-PE-04, VL-PE-05, VL-PE-06: Aluno limites de ativos (limite = 3)
    @ParameterizedTest(name = "Aluno ativos={0} -> pode={1}")
    @CsvSource({
        "0, true",  // VL-PE-03: 0 ativos (caso base válido)
        "2, true",  // VL-PE-04: limite - 1 (2 ativos -> true)
        "3, false", // VL-PE-05: limite exato (3 ativos -> false)
        "4, false"  // VL-PE-06: limite + 1 (4 ativos -> false)
    })
    @DisplayName("Aluno: limites de empréstimos ativos (limite 3)")
    void podeEmprestar_aluno_limitesAtivos(int ativos, boolean esperado) {
        assertEqualsBool(esperado, regras.podeEmprestar(TipoUsuario.ALUNO, ativos, 0, false));
    }

    // CE-PE-V2, VL-PE-07, VL-PE-08, VL-PE-09, VL-PE-10: Professor limites de ativos (limite = 8)
    @ParameterizedTest(name = "Professor ativos={0} -> pode={1}")
    @CsvSource({
        "0, true",  // VL-PE-07: 0 ativos
        "7, true",  // VL-PE-08: limite - 1 (7 ativos -> true)
        "8, false", // VL-PE-09: limite exato (8 ativos -> false)
        "9, false"  // VL-PE-10: limite + 1 (9 ativos -> false)
    })
    @DisplayName("Professor: limites de empréstimos ativos (limite 8)")
    void podeEmprestar_professor_limitesAtivos(int ativos, boolean esperado) {
        assertEqualsBool(esperado, regras.podeEmprestar(TipoUsuario.PROFESSOR, ativos, 0, false));
    }

    // CE-PE-V3, VL-PE-11, VL-PE-12, VL-PE-13: Visitante limites de ativos (limite = 1)
    @ParameterizedTest(name = "Visitante ativos={0} -> pode={1}")
    @CsvSource({
        "0, true",  // VL-PE-11: limite - 1 (0 ativos -> true)
        "1, false", // VL-PE-12: limite exato (1 ativo -> false)
        "2, false"  // VL-PE-13: limite + 1 (2 ativos -> false)
    })
    @DisplayName("Visitante: limites de empréstimos ativos (limite 1)")
    void podeEmprestar_visitante_limitesAtivos(int ativos, boolean esperado) {
        assertEqualsBool(esperado, regras.podeEmprestar(TipoUsuario.VISITANTE, ativos, 0, false));
    }

    // CE-PE-V4, VL-PE-14, VL-PE-15, VL-PE-16: Multa pendente (fronteira R$ 10,00 = 1000 centavos)
    @ParameterizedTest(name = "Multa {0} centavos -> pode={1}")
    @CsvSource({
        "0, true",     // VL-PE-14: Multa zero (válido)
        "1000, true",  // VL-PE-15: Multa no limite exato de R$ 10,00 (válido)
        "1001, false"  // VL-PE-16: Multa logo acima do limite (R$ 10,01 -> false)
    })
    @DisplayName("Multa pendente: fronteira de R$ 10,00 (1000 centavos)")
    void podeEmprestar_multaPendente_fronteiras(int multaPendente, boolean esperado) {
        assertEqualsBool(esperado, regras.podeEmprestar(TipoUsuario.ALUNO, 0, multaPendente, false));
    }

    // CE-PE-V5, VL-PE-17, VL-PE-18: Possui atraso isolado
    @Test
    @DisplayName("possuiAtraso=false com outras válidas -> true")
    void podeEmprestar_semAtraso_valido() {
        // VL-PE-17: Sem atraso
        assertTrue(regras.podeEmprestar(TipoUsuario.ALUNO, 0, 0, false));
    }

    @ParameterizedTest(name = "{0} com atraso isolado -> false")
    @CsvSource({
        "ALUNO",
        "PROFESSOR",
        "VISITANTE"
    })
    @DisplayName("possuiAtraso=true isolado (outras válidas) impede empréstimo")
    void podeEmprestar_atrasoIsolado_impede(TipoUsuario tipo) {
        // VL-PE-18: Com atraso
        assertFalse(regras.podeEmprestar(tipo, 0, 0, true));
    }

    // CE-PE-V6, VL-PE-19 a VL-PE-22: Combinações com duas e três condições falhando
    @ParameterizedTest(name = "Aluno ativos={0}, multa={1}, atraso={2} -> false")
    @CsvSource({
        "3, 1001, false", // VL-PE-19: Duas falhas (ativos no limite E multa excedida)
        "3, 0, true",     // VL-PE-20: Duas falhas (ativos no limite E com atraso)
        "0, 1001, true",  // VL-PE-21: Duas falhas (multa excedida E com atraso)
        "3, 1001, true"   // VL-PE-22: Três falhas conjuntas (ativos, multa E atraso)
    })
    @DisplayName("Combinações onde 2 ou 3 condições falham simultaneamente")
    void podeEmprestar_multiplasCondicoesInvalidas(int ativos, int multa, boolean atraso) {
        assertFalse(regras.podeEmprestar(TipoUsuario.ALUNO, ativos, multa, atraso));
    }

    private void assertEqualsBool(boolean esperado, boolean obtido) {
        if (esperado) {
            assertTrue(obtido);
        } else {
            assertFalse(obtido);
        }
    }
}

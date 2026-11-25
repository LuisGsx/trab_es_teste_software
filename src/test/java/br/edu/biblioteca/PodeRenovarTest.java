package br.edu.biblioteca;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Caixa-preta: podeRenovar. Ver PLANO-DE-TESTES.md, seção 6.
 */
class PodeRenovarTest {

    private final RegrasBiblioteca regras = new RegrasBiblioteca();

    // CE-PR-I1: Tipo nulo lança IllegalArgumentException
    @Test
    @DisplayName("Tipo nulo lança IllegalArgumentException")
    void podeRenovar_tipoNulo_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> regras.podeRenovar(null, 0, false, false));
    }

    // CE-PR-I2, CE-PR-I3, CE-PR-I4, VL-PR-01: renovacoesFeitas negativo lança IllegalArgumentException para qualquer tipo
    @ParameterizedTest(name = "{0} com renovacoesFeitas={1} -> IllegalArgumentException")
    @CsvSource({
        "ALUNO, -1",
        "PROFESSOR, -1",
        "VISITANTE, -1"
    })
    @DisplayName("renovacoesFeitas negativo (-1) lança IllegalArgumentException para QUALQUER tipo")
    void podeRenovar_renovacoesNegativo_lancaExcecao(TipoUsuario tipo, int renovacoes) {
        // VL-PR-01: Fronteira inferior inválida (-1)
        assertThrows(IllegalArgumentException.class,
                () -> regras.podeRenovar(tipo, renovacoes, false, false));
    }

    // CE-PR-V1, VL-PR-02: Visitante nunca renova (mesmo com 0 renovações, sem reserva e sem atraso)
    @Test
    @DisplayName("Visitante com 0 renovações, sem reserva e sem atraso -> false")
    void podeRenovar_visitante_zeroRenovacoesCondicoesFavoraveis_retornaFalse() {
        // VL-PR-02: Visitante com 0 renovações feitas
        assertFalse(regras.podeRenovar(TipoUsuario.VISITANTE, 0, false, false));
    }

    // CE-PR-V2, VL-PR-03, VL-PR-04, VL-PR-05, VL-PR-06: Aluno renova até 2 vezes (0, 1 -> true; 2, 3 -> false)
    @ParameterizedTest(name = "Aluno renovacoesFeitas={0} -> pode={1}")
    @CsvSource({
        "0, true",  // VL-PR-03: 0 renovações (1ª renovação permitida)
        "1, true",  // VL-PR-04: 1 renovação (2ª renovação permitida - limite atingível)
        "2, false", // VL-PR-05: 2 renovações já feitas (atingiu limite de 2 -> false)
        "3, false"  // VL-PR-06: 3 renovações (acima do limite -> false)
    })
    @DisplayName("Aluno: limites de renovações feitas (até 2 vezes)")
    void podeRenovar_aluno_limitesRenovacoes(int renovacoes, boolean esperado) {
        assertEqualsBool(esperado, regras.podeRenovar(TipoUsuario.ALUNO, renovacoes, false, false));
    }

    // CE-PR-V3, VL-PR-07, VL-PR-08, VL-PR-09, VL-PR-10, VL-PR-11: Professor renova até 3 vezes (0, 2 -> true; 3, 4 -> false)
    @ParameterizedTest(name = "Professor renovacoesFeitas={0} -> pode={1}")
    @CsvSource({
        "0, true",  // VL-PR-07: 0 renovações feitas -> true
        "2, true",  // VL-PR-08: 2 renovações feitas (3ª permitida) -> true
        "3, false", // VL-PR-09: 3 renovações já feitas (atingiu limite de 3) -> false
        "4, false"  // VL-PR-10: 4 renovações feitas (acima do limite) -> false
    })
    @DisplayName("Professor: limites de renovações feitas (até 3 vezes)")
    void podeRenovar_professor_limitesRenovacoes(int renovacoes, boolean esperado) {
        assertEqualsBool(esperado, regras.podeRenovar(TipoUsuario.PROFESSOR, renovacoes, false, false));
    }

    // CE-PR-V4, VL-PR-12: Reserva isolada impede renovação (possuiReserva = true)
    @Test
    @DisplayName("Reserva isolada (true) impede renovação de Aluno")
    void podeRenovar_reservaIsolada_impede() {
        // VL-PR-12: Reserva true isolada
        assertFalse(regras.podeRenovar(TipoUsuario.ALUNO, 0, true, false));
    }

    // CE-PR-V5, VL-PR-13: Em atraso isolado impede renovação (emAtraso = true)
    @Test
    @DisplayName("Atraso isolado (true) impede renovação de Aluno")
    void podeRenovar_atrasoIsolado_impede() {
        // VL-PR-13: Atraso true isolado
        assertFalse(regras.podeRenovar(TipoUsuario.ALUNO, 0, false, true));
    }

    // CE-PR-V6, VL-PR-14: Reserva E atraso simultaneamente impedem renovação
    @Test
    @DisplayName("Reserva e atraso juntos impedem renovação")
    void podeRenovar_reservaEAtrasoJuntos_impede() {
        // VL-PR-14: Ambas condições de bloqueio ativas
        assertFalse(regras.podeRenovar(TipoUsuario.ALUNO, 0, true, true));
    }
    
    // VL-PR-15: Reserva isolada impede renovação de Professor
    @Test
    @DisplayName("Reserva isolada (true) impede renovação de Professor")
    void podeRenovar_professor_reservaIsolada_impede() {
        assertFalse(regras.podeRenovar(TipoUsuario.PROFESSOR, 0, true, false));
    }
 
    // VL-PR-16: Atraso isolado impede renovação de Professor
    @Test
    @DisplayName("Atraso isolado (true) impede renovação de Professor")
    void podeRenovar_professor_atrasoIsolado_impede() {
        assertFalse(regras.podeRenovar(TipoUsuario.PROFESSOR, 0, false, true));
    }

    private void assertEqualsBool(boolean esperado, boolean obtido) {
        if (esperado) {
            assertTrue(obtido);
        } else {
            assertFalse(obtido);
        }
    }
}

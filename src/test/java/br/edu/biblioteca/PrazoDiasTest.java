package br.edu.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Caixa-preta: prazoDias. Ver PLANO-DE-TESTES.md, seção 1. */
class PrazoDiasTest {

    private final RegrasBiblioteca regras = new RegrasBiblioteca();

    // PD-V1, PD-V2, PD-V3
    @ParameterizedTest(name = "{0} -> {1} dias")
    @CsvSource({"ALUNO,14", "PROFESSOR,30", "VISITANTE,7"})
    void prazoPorTipo(TipoUsuario tipo, int esperado) {
        assertEquals(esperado, regras.prazoDias(tipo));
    }

    // PD-I1
    @Test
    void tipoNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> regras.prazoDias(null));
    }
}

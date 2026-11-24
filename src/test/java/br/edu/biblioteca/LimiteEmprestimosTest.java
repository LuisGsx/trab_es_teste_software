package br.edu.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Caixa-preta: limiteEmprestimos. Ver PLANO-DE-TESTES.md, seção 2. */
class LimiteEmprestimosTest {

    private final RegrasBiblioteca regras = new RegrasBiblioteca();

    // LE-V1, LE-V2, LE-V3
    @ParameterizedTest(name = "{0} -> limite {1}")
    @CsvSource({"ALUNO,3", "PROFESSOR,8", "VISITANTE,1"})
    void limitePorTipo(TipoUsuario tipo, int esperado) {
        assertEquals(esperado, regras.limiteEmprestimos(tipo));
    }

    // LE-I1
    @Test
    void tipoNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> regras.limiteEmprestimos(null));
    }
}

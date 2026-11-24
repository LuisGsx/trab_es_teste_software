package br.edu.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Caixa-preta: dataDevolucao. Ver PLANO-DE-TESTES.md, seção 3. */
class DataDevolucaoTest {

    private final RegrasBiblioteca regras = new RegrasBiblioteca();

    private void verifica(String retirada, TipoUsuario tipo, String esperada) {
        assertEquals(LocalDate.parse(esperada),
                regras.dataDevolucao(LocalDate.parse(retirada), tipo));
    }

    // DD-V1, DD-V2, DD-V3: resultado em dia comum, sem ajuste
    @ParameterizedTest(name = "{1}: {0} -> {2}")
    @CsvSource({
        "2026-11-03,ALUNO,2026-11-17",
        "2026-11-03,PROFESSOR,2026-12-03",
        "2026-11-03,VISITANTE,2026-11-10"
    })
    void semAjusteDiaComum(String retirada, TipoUsuario tipo, String esperada) {
        verifica(retirada, tipo, esperada);
    }

    // DD-L1, DD-L2, DD-L3: sábado é dia normal
    @ParameterizedTest(name = "{1}: {0} -> {2}")
    @CsvSource({
        "2026-11-07,ALUNO,2026-11-21",
        "2026-11-07,VISITANTE,2026-11-14",
        "2026-10-29,PROFESSOR,2026-11-28"
    })
    void sabadoNaoAjusta(String retirada, TipoUsuario tipo, String esperada) {
        verifica(retirada, tipo, esperada);
    }

    // DD-L4, DD-L5, DD-L6: domingo passa para segunda
    @ParameterizedTest(name = "{1}: {0} -> {2}")
    @CsvSource({
        "2026-11-01,ALUNO,2026-11-16",
        "2026-11-01,VISITANTE,2026-11-09",
        "2026-10-30,PROFESSOR,2026-11-30"
    })
    void domingoVaiParaSegunda(String retirada, TipoUsuario tipo, String esperada) {
        verifica(retirada, tipo, esperada);
    }

    // DD-L7, DD-L8, DD-L9: segunda não é ajustada
    @ParameterizedTest(name = "{1}: {0} -> {2}")
    @CsvSource({
        "2026-11-02,ALUNO,2026-11-16",
        "2026-11-02,VISITANTE,2026-11-09",
        "2026-10-31,PROFESSOR,2026-11-30"
    })
    void segundaNaoAjusta(String retirada, TipoUsuario tipo, String esperada) {
        verifica(retirada, tipo, esperada);
    }

    // DD-C1, DD-C2, DD-C3: o ajuste atravessa fim de mês, de ano e 29/fev
    @ParameterizedTest(name = "{0} -> {2}")
    @CsvSource({
        "2027-01-17,ALUNO,2027-02-01",
        "2028-12-17,ALUNO,2029-01-01",
        "2032-02-15,ALUNO,2032-03-01"
    })
    void ajusteAtravessaCalendario(String retirada, TipoUsuario tipo, String esperada) {
        verifica(retirada, tipo, esperada);
    }

    // DD-I1
    @Test
    void tipoNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> regras.dataDevolucao(LocalDate.of(2026, 11, 3), null));
    }

    // DD-I2
    @Test
    void retiradaNulaLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> regras.dataDevolucao(null, TipoUsuario.ALUNO));
    }

    // DD-I3
    @Test
    void ambosNulosLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> regras.dataDevolucao(null, null));
    }
}

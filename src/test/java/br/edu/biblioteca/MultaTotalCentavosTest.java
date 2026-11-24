package br.edu.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Caixa-preta: multaTotalCentavos. Ver PLANO-DE-TESTES.md, seção 7.
 */
class MultaTotalCentavosTest {

    private final RegrasBiblioteca regras = new RegrasBiblioteca();

    // CE-MT-I1: Tipo nulo lança IllegalArgumentException
    @Test
    @DisplayName("Tipo nulo lança IllegalArgumentException")
    void multaTotalCentavos_tipoNulo_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> regras.multaTotalCentavos(null, new int[]{1, 2}));
    }

    // CE-MT-I2: Lista nula lança IllegalArgumentException
    @Test
    @DisplayName("Lista nula lança IllegalArgumentException")
    void multaTotalCentavos_listaNula_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> regras.multaTotalCentavos(TipoUsuario.ALUNO, null));
    }

    // CE-MT-I3, VL-MT-01: Elemento negativo em lista só com o negativo [-1]
    @Test
    @DisplayName("Lista com único elemento negativo [-1] lança IllegalArgumentException")
    void multaTotalCentavos_elementoNegativoUnico_lancaExcecao() {
        // VL-MT-01: Fronteira inferior inválida de elemento
        assertThrows(IllegalArgumentException.class,
                () -> regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{-1}));
    }

    // CE-MT-I4, VL-MT-02 a VL-MT-04: Elemento negativo no início, meio e fim
    @Test
    @DisplayName("Elemento negativo no início da lista [-1, 2, 3] lança IllegalArgumentException")
    void multaTotalCentavos_elementoNegativoNoInicio_lancaExcecao() {
        // VL-MT-02: Negativo no início
        assertThrows(IllegalArgumentException.class,
                () -> regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{-1, 2, 3}));
    }

    @Test
    @DisplayName("Elemento negativo no meio da lista [5, -2, 3] lança IllegalArgumentException")
    void multaTotalCentavos_elementoNegativoNoMeio_lancaExcecao() {
        // VL-MT-03: Negativo no meio
        assertThrows(IllegalArgumentException.class,
                () -> regras.multaTotalCentavos(TipoUsuario.PROFESSOR, new int[]{5, -2, 3}));
    }

    @Test
    @DisplayName("Elemento negativo no fim da lista [2, 4, -5] lança IllegalArgumentException")
    void multaTotalCentavos_elementoNegativoNoFim_lancaExcecao() {
        // VL-MT-04: Negativo no fim
        assertThrows(IllegalArgumentException.class,
                () -> regras.multaTotalCentavos(TipoUsuario.VISITANTE, new int[]{2, 4, -5}));
    }

    // CE-MT-V1, VL-MT-05: Lista vazia dá total 0
    @Test
    @DisplayName("Lista vazia retorna total 0")
    void multaTotalCentavos_listaVazia_retornaZero() {
        // VL-MT-05: Tamanho da lista = 0
        assertEquals(0, regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{}));
    }

    // CE-MT-V2, VL-MT-06: Um elemento sem atraso (elemento 0)
    @Test
    @DisplayName("Um elemento sem atraso [0] retorna total 0")
    void multaTotalCentavos_umElementoZero_retornaZero() {
        // VL-MT-06: Elemento com 0 dias
        assertEquals(0, regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{0}));
    }

    // CE-MT-V3, VL-MT-07: Vários elementos com soma simples [1, 2, 12] = 100 + 200 + 1400 = 1700 centavos
    @Test
    @DisplayName("Vários elementos soma simples [1, 2, 12] = 1700 centavos")
    void multaTotalCentavos_multiplosElementos_somaSimples() {
        // VL-MT-07: Aluno [1, 2, 12] -> 100 + 200 + 1400 = 1700 centavos
        assertEquals(1700, regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{1, 2, 12}));
    }

    // CE-MT-V4, VL-MT-08: Cada multa individual já limitada a 5000 antes de somar [100, 100] = 5000 + 5000 = 10000
    @Test
    @DisplayName("Cada multa individual limitada a 5000 antes de somar [100, 100] = 10000")
    void multaTotalCentavos_multaIndividualLimitadaAntesDeSomar() {
        // VL-MT-08: [100, 100] -> cada um dá teto individual de 5000 -> 5000 + 5000 = 10000
        assertEquals(10000, regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{100, 100}));
    }

    // CE-MT-V5, VL-MT-09: Teto de 10000 com soma exatamente 10000 [30, 30] = 5000 + 5000 = 10000
    @Test
    @DisplayName("Soma exatamente no teto global de 10000 centavos [30, 30]")
    void multaTotalCentavos_somaExatamenteTetoGlobal() {
        // VL-MT-09: Fronteira exata do teto global (30 dias = 5000; 5000 + 5000 = 10000)
        assertEquals(10000, regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{30, 30}));
    }

    // CE-MT-V6, VL-MT-10: Soma abaixo do teto [29, 30] = 4800 + 5000 = 9800 centavos
    @Test
    @DisplayName("Soma logo abaixo do teto global [29, 30] = 9800 centavos")
    void multaTotalCentavos_somaLogoAbaixoTetoGlobal() {
        // VL-MT-10: 29 dias (4800) + 30 dias (5000) = 9800
        assertEquals(9800, regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{29, 30}));
    }

    // CE-MT-V7, VL-MT-11: Soma acima do teto [30, 30, 1] = 5000 + 5000 + 100 = 10100 -> satura em 10000
    @Test
    @DisplayName("Soma acima do teto global satura em 10000 centavos [30, 30, 1]")
    void multaTotalCentavos_somaAcimaTetoGlobal_saturaEm10000() {
        // VL-MT-11: 5000 + 5000 + 100 = 10100 -> satura no teto global de 10000
        assertEquals(10000, regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{30, 30, 1}));
    }

    // CE-MT-V8, VL-MT-12: Muitos elementos ultrapassando o teto global
    @Test
    @DisplayName("Muitos elementos ultrapassando o teto global saturam em 10000 centavos")
    void multaTotalCentavos_muitosElementos_saturaNoTetoGlobal() {
        // VL-MT-12: 5 empréstimos de 10 dias (5 * 1000 = 5000) + 2 de 30 dias (2 * 5000 = 10000) -> teto 10000
        assertEquals(10000, regras.multaTotalCentavos(TipoUsuario.ALUNO, new int[]{10, 10, 10, 10, 10, 30, 30}));
    }

    // CE-MT-V9, VL-MT-13: Professor com carência de 3 dias aplicada por elemento [3, 4] = 0 + 100 = 100
    @Test
    @DisplayName("Professor com carência de 3 dias aplicada por elemento [3, 4] = 100 centavos")
    void multaTotalCentavos_professor_carenciaAplicadaPorElemento() {
        // VL-MT-13: Elemento 1 (3 dias = carência = 0); Elemento 2 (4 dias = 1 dia cobrado = 100) -> total 100
        assertEquals(100, regras.multaTotalCentavos(TipoUsuario.PROFESSOR, new int[]{3, 4}));
    }
}

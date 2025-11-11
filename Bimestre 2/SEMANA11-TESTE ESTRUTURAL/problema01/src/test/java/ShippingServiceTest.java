import org.example.checkout.ShippingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShippingServiceTest {

        private final ShippingService service = new ShippingService();

        // Validação
        @Test
        void deveLancarExcecaoSePesoNegativo() {
            assertThrows(IllegalArgumentException.class, () -> {
                service.calculate("SUL", -1.0, 100.0, false);
            });
        }

        // Frete Grátis (já coberto no CheckoutService, mas bom isolar)
        @Test
        void deveRetornarFreteGratisSeFlagAtiva() {
            assertEquals(0.0, service.calculate("NORTE", 10.0, 10.0, true));
        }

        // Região SUL/SUDESTE (Apenas 20.00 e a fronteira 2.0/5.0 faltam explicitamente)
        @Test
        void deveCalcularFreteSUL_PesoMax2() {
            assertEquals(20.0, service.calculate("SUL", 2.0, 10.0, false));
        }
        @Test
        void deveCalcularFreteSUDESTE_PesoMin2_01() {
            assertEquals(35.0, service.calculate("SUDESTE", 2.01, 10.0, false));
        }

        // Região NORTE (Todos os 3 limites faltam)
        @Test
        void deveCalcularFreteNORTE_PesoMax2() {
            assertEquals(30.0, service.calculate("NORTE", 2.0, 10.0, false));
        }
        @Test
        void deveCalcularFreteNORTE_PesoMax5() {
            assertEquals(55.0, service.calculate("NORTE", 5.0, 10.0, false));
        }
        @Test
        void deveCalcularFreteNORTE_PesoMaiorQue5() {
            assertEquals(80.0, service.calculate("NORTE", 5.01, 10.0, false));
        }

        // Outras Regiões / Região Nula
        @Test
        void deveCalcularFreteFixoParaOutrasRegioes() {
            assertEquals(40.0, service.calculate("CENTRO-OESTE", 10.0, 10.0, false));
        }
        @Test
        void deveCalcularFreteFixoParaRegiaoNulaOuVazia() {
            assertEquals(40.0, service.calculate(null, 1.0, 10.0, false));
            assertEquals(40.0, service.calculate("", 1.0, 10.0, false));
        }
}


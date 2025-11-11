import org.example.checkout.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CouponServiceTest {

    @Test
    public void deveAplicarDESC20SeValido() {
        var service = new CheckoutService(new CouponService(), new ShippingService());

        // Subtotal 100.00 (mínimo atendido), não expirado
        var itens = List.of(new Item("ROUPA", 100.00, 1));

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "SUL", 1.0, "DESC20", LocalDate.now(), LocalDate.now().plusDays(1)
        );

        // 20% de 100.00 = 20.00
        assertEquals(20.00, res.discountValue);
    }

    @Test
    public void deveIgnorarDESC20SeExpirado() {
        var service = new CheckoutService(new CouponService(), new ShippingService());

        var itens = List.of(new Item("ROUPA", 100.00, 1));

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "SUL", 1.0, "DESC20", LocalDate.now(), LocalDate.now().minusDays(1) // EXPIRADO!
        );

        // Desconto deve ser 0.00
        assertEquals(0.00, res.discountValue);
    }

    @Test
    public void deveConcederFreteGratisSeSubtotalMaiorOuIgualA300() {
        var service = new CheckoutService(new CouponService(), new ShippingService());

        // Subtotal 300.00 (limite)
        var itens = List.of(new Item("ROUPA", 300.00, 1));

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "NORTE", // Região cara, mas deve ser grátis
                10.0, // Peso alto, mas deve ser grátis
                null,
                LocalDate.now(), null
        );

        // Frete deve ser 0.0
        assertEquals(0.00, res.shipping);
    }

    @Test
    public void deveArredondarMeioParaCimaCorretamente() {
        var couponSvc = new CouponService();
        var shipSvc = new ShippingService() {
            // Mock do ShippingService para garantir que não interfere no cálculo
            @Override
            public double calculate(String region, double weight, double subtotal, boolean freeShipping) {
                return 0.0;
            }
        };
        var service = new CheckoutService(couponSvc, shipSvc);

        // Item: 1 item * 100.415. O Subtotal será 100.42 (HALF_UP)
        // O desconto (ex: 10%) e o imposto (ex: 12%) também testarão o Money.round2
        var itens = List.of(new Item("ROUPA", 100.415, 1));

        var res = service.checkout(
                itens,
                CustomerTier.SILVER, // 5% de desconto
                false,
                "SUL", 1.0, null, LocalDate.now(), null
        );

        // Subtotal arredondado (100.415 -> 100.42)
        assertEquals(100.42, res.subtotal);

        // Base Desconto: 100.42 * 0.05 = 5.021 -> 5.02 (arredonda para baixo)
        assertEquals(5.02, res.discountValue);

        // Base p/ Imposto: 100.42 - 5.02 = 95.40
        // Imposto: 95.40 * 0.12 = 11.448 -> 11.45 (arredonda para cima)
        assertEquals(11.45, res.tax);
    }

    @Test
    public void deveAplicarDESC10() {
        var service = new CheckoutService(new CouponService(), new ShippingService());

        // Subtotal 50.00
        var itens = List.of(new Item("ROUPA", 50.00, 1));

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "SUL", 1.0, "DESC10", LocalDate.now(), null
        );
        // 10% de 50.00 = 5.00
        assertEquals(5.00, res.discountValue);
    }

    @Test
    public void deveIgnorarDESC20SeSubtotalAbaixoDoMinimo() {
        var service = new CheckoutService(new CouponService(), new ShippingService());

        // Subtotal 99.99 (abaixo de 100.00)
        var itens = List.of(new Item("ROUPA", 99.99, 1));

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "SUL", 1.0, "DESC20", LocalDate.now(), LocalDate.now().plusDays(1)
        );

        // Desconto deve ser 0.00
        assertEquals(0.00, res.discountValue);
    }

    @Test
    public void deveAplicarCupomFRETEGRATISSePesoAte5() {
        var service = new CheckoutService(new CouponService(), new ShippingService());

        // Peso 5.0 (limite)
        var itens = List.of(new Item("ROUPA", 10.00, 1));

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "NORTE", // Região cara, frete padrão seria 55.00
                5.0,
                "FRETEGRATIS",
                LocalDate.now(), null
        );
        // Frete deve ser 0.0, discountValue 0.00
        assertEquals(0.00, res.shipping);
        assertEquals(0.00, res.discountValue);
    }

    // Adicionar ao CouponServiceTest.java

    @Test
    public void deveCalcularFreteNormalSeCupomFreteGratisMasPesoMaiorQue5() {
        var service = new CheckoutService(new CouponService(), new ShippingService());

        var itens = List.of(new Item("ROUPA", 10.00, 1));

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "NORTE", // Região NORTE: peso > 5 => 80.00
                5.1, // Peso acima de 5.0
                "FRETEGRATIS", // Cupom ativo, mas peso maior que o limite
                LocalDate.now(), null
        );

        // Frete não deve ser grátis, deve ser o valor normal (NORTE > 5.0)
        assertEquals(80.00, res.shipping);
    }

    @Test
    public void deveIgnorarCupomNuloVazioOuInvalido() {
        var service = new CheckoutService(new CouponService(), new ShippingService());
        // Subtotal alto para garantir que o desconto não seria 0 por outras regras
        var itens = List.of(new Item("ROUPA", 100.00, 1));

        // 1. Cupom nulo
        var resNull = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1.0, null, LocalDate.now(), null);
        // 2. Cupom vazio
        var resBlank = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1.0, "  ", LocalDate.now(), null);
        // 3. Cupom desconhecido
        var resInvalid = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1.0, "SUPERDESCONTO", LocalDate.now(), null);

        // Desconto deve ser 0.00 em todos os casos (caminho default / null/blank)
        assertEquals(0.00, resNull.discountValue);
        assertEquals(0.00, resBlank.discountValue);
        assertEquals(0.00, resInvalid.discountValue);

        // Frete deve ser calculado normalmente (SUL, peso 1.0 -> 20.00)
        assertEquals(20.00, resNull.shipping);
    }
}

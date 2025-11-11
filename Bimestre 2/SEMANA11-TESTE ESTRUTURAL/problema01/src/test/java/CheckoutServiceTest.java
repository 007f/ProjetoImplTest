import org.example.checkout.*;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CheckoutServiceTest {

//    @Rule // POR ENQUANTO NÃO IMPORTAR
//    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void deveCalcularBasicoSemDescontosEImpostoApenasNaoBook() {
        var couponSvc = new CouponService();
        var shipSvc = new ShippingService();
        var service = new CheckoutService(couponSvc, shipSvc);

        var itens = List.of(
                new Item("BOOK", 100.00, 1),
                new Item("ELETRONICO", 50.00, 2) // tributável
        );

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "SUL",
                3.0,
                null,
                LocalDate.now(),
                null
        );

        assertEquals(200.00, res.subtotal);          // 100 + (50*2)
        assertEquals(0.00, res.discountValue);
        // imposto 12% sobre parte tributável: 100 (eletrônicos)
        assertEquals(12.00, res.tax);
        // frete SUL com peso 3 → 35
        assertEquals(35.00, res.shipping);
        assertEquals(247.00, res.total);
    }

    @Test
    public void deveAplicarDescontoGOLDMaisPrimeiraCompra_E_Teto() {
        // Cenário: GOLD (10%) + Primeira Compra (5%) = 15%. Subtotal alto para testar teto.
        var couponSvc = new CouponService();
        var shipSvc = new ShippingService();
        var service = new CheckoutService(couponSvc, shipSvc);

        // Item: 100 * 3 = 300.00
        var itens = List.of(new Item("ELETRONICO", 100.00, 3));

        var res = service.checkout(
                itens,
                CustomerTier.GOLD,
                true, // PRIMEIRA COMPRA
                "SUDESTE",
                10.0,
                null,
                LocalDate.now(),
                null
        );

        // Desconto Total: 10% (GOLD) + 5% (Primeira Compra) = 15%
        // 15% de 300.00 = 45.00
        assertEquals(45.00, res.discountValue);
        // Base após desconto: 300.00 - 45.00 = 255.00
        // Imposto (12% de 255.00) = 30.60
        // Frete SUDESTE com peso 10 > 5 => 50.00
        assertEquals(255.00, res.subtotal - res.discountValue);

        // Teste de Teto (exemplo) - Cenário onde o total é > 30%
        var resTeto = service.checkout(
                // subtotal 500
                List.of(new Item("ROUPA", 100.0, 5)),
                CustomerTier.GOLD, // 10%
                true, // 5%
                "SUDESTE",
                1.0,
                "DESC20", // 20%
                LocalDate.now(), // Cupom DESC20 (10+5+20 = 35% -> TETO 30%)
                LocalDate.now().plusDays(1)
        );
        // Desconto Máximo: 30% de 500.00 = 150.00
        assertEquals(150.00, resTeto.discountValue);
    }

    // Para cobrir a fronteira da Primeira Compra (subtotal >= 50)
    @Test
    public void deveIgnorarPrimeiraCompraSeSubtotalForMenorQue50() {
        var service = new CheckoutService(new CouponService(), new ShippingService());

        // Subtotal: 1 item * 49.99 = 49.99 (abaixo de 50)
        var itens = List.of(new Item("ROUPA", 49.99, 1));

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                true, // Primeira Compra
                "SUL", 1.0, null, LocalDate.now(), null
        );

        // Desconto deve ser 0.00, pois 49.99 < 50.00
        assertEquals(0.00, res.discountValue);
    }

    @Test
    public void falhaDeItemPorQt() {

        try {
            Item itens = new Item("BOOK", 100.00, 0);
            fail("Uma exceção IllegalArgumentException era esperada!");
        } catch (IllegalArgumentException e) {
            // Se chegarmos aqui, a exceção foi lançada, e podemos verificá-la.
            assertEquals("quantidade <= 0", e.getMessage());
        }

    }

    @Test
    public void falhaDeItemPorPreco() {
        try {
            Item itens = new Item("BOOK", -100.00, 2);
            fail("Uma exceção IllegalArgumentException era esperada!");
        } catch (IllegalArgumentException e) {
            // Se chegarmos aqui, a exceção foi lançada, e podemos verificá-la.
            assertEquals("precoUnitario < 0", e.getMessage());
        }
    }

    //PROXIMO TESTE
//    @Test
//    public void deveCalcularBasicoSemDescontosEImpostoEBook() {
//        var couponSvc = new CouponService();
//        var shipSvc = new ShippingService();
//        var service = new CheckoutService(couponSvc, shipSvc);
//
//        var itens = List.of(
//                new Item("BOOK", 100.00, 1),
//                new Item("ELETRONICO", 50.00, 2) // tributável
//        );
//
//        var res = service.checkout(
//                itens,
//                CustomerTier.BASIC,
//                false,
//                "SUL",
//                3.0,
//                null,
//                LocalDate.now(),
//                null
//        );
//
//        assertEquals(200.00, res.subtotal);          // 100 + (50*2)
//        assertEquals(0.00, res.discountValue);
//        // imposto 12% sobre parte tributável: 100 (eletrônicos)
//        assertEquals(12.00, res.tax);
//        // frete SUL com peso 3 → 35
//        assertEquals(35.00, res.shipping);
//        assertEquals(247.00, res.total);
//    }
}

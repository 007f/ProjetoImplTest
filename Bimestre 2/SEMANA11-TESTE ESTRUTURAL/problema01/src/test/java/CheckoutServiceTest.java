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

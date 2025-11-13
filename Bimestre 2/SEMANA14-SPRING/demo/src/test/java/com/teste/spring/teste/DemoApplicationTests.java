package com.teste.spring.teste;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	ApplicationContext ctx;

	@Test
	void contexto_carrega() {
		assertThat(ctx).isNotNull();
	}

	@Test
	void contexto_tem_servico() {
		assertThat(ctx.getBean(com.teste.spring.teste.service.ClienteService.class)).isNotNull();
	}

	@Test
	void contexto_tem_controller() {
		assertThat(ctx.containsBean("clienteController")).isTrue();
	}

}

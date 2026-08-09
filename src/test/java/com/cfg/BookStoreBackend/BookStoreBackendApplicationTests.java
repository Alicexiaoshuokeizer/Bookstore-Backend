package com.cfg.BookStoreBackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BookStoreBackendApplicationTests {


	@MockitoBean
	private com.cfg.BookStoreBackend.model.repository.BookRepository bookRepository;

	@MockitoBean
	private com.cfg.BookStoreBackend.model.repository.PurchaseRepository purchaseRepository;

	@Test
	void contextLoads() {
		// This will now pass instantly with zero configuration errors!
	}
}

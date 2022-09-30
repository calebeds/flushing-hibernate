package com.calebe.flushing;

import com.calebe.flushing.domain.Product;
import com.calebe.flushing.support.TransactionRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

@SpringBootTest
class FlushingApplicationTests {
	private final static Logger LOGGER = Logger.getLogger(FlushingApplicationTests.class.getName());

	@Autowired
	private TransactionRunner txRunner;

	@Test
	void testSimpleFlushing() {
		txRunner.executeInTransaction(entityManager -> {
			Product p1 = new Product(1, "p1");
			Product p2 = new Product(2, "p2");
			entityManager.persist(p1);
			LOGGER.info("Before flush");
			entityManager.flush();
			LOGGER.info("After flush");
			entityManager.persist(p2);
			LOGGER.info("Before Tx commit");
		});
	}

}

package com.calebe.flushing;

import com.calebe.flushing.domain.DifferentProduct;
import com.calebe.flushing.domain.Product;
import com.calebe.flushing.support.TransactionRunner;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.FlushModeType;
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

	@Test
	void testAutoFlushingWithJPQLQuerySameEntity() {
		txRunner.executeInTransaction( entityManager -> {
			Product p1 = new Product(1, "p1");
			Product p2 = new Product(2, "p2");
			entityManager.persist(p1);
			LOGGER.info("Before JPQL query");
			entityManager.createQuery("FROM Product", Product.class).getResultList();
			LOGGER.info("After JPQL query");
			entityManager.persist(p2);
			LOGGER.info("Before Tx commit");
		});
	}

	@Test
	void testAutoFlushingWithJPQLQueryDifferentEntity() {
		txRunner.executeInTransaction( entityManager -> {
			Product p1 = new Product(1, "p1");
			Product p2 = new Product(2, "p2");
			entityManager.persist(p1);
			LOGGER.info("Before JPQL query");
			entityManager.createQuery("FROM DifferentProduct", DifferentProduct.class).getResultList();
			LOGGER.info("After JPQL query");
			entityManager.persist(p2);
			LOGGER.info("Before Tx commit");
		});
	}

	@Test
	void testAutoFlushingWithNativeQuery() {
		txRunner.executeInTransaction( entityManager -> {
			Product p1 = new Product(1, "p1");
			Product p2 = new Product(2, "p2");
			entityManager.persist(p1);
			LOGGER.info("Before native query");
			entityManager.createNativeQuery("SELECT * FROM different_products").getResultList();
			LOGGER.info("After native query");
			entityManager.persist(p2);
			LOGGER.info("Before Tx commit");
		});
	}

	@Test
	void testCommitFlushing() {
		txRunner.executeInTransaction( entityManager -> {
			entityManager.setFlushMode(FlushModeType.COMMIT);
			Product p1 = new Product(1, "p1");
			Product p2 = new Product(2, "p2");
			entityManager.persist(p1);
			LOGGER.info("Before native query");
			entityManager.createNativeQuery("SELECT * FROM different_products").getResultList();
			LOGGER.info("After native query");
			entityManager.persist(p2);
			LOGGER.info("Before Tx commit");
		});
	}

	@Test
	void testAlwaysFlushingJPQLQueryDifferentEntity() {
		txRunner.executeInTransaction( entityManager -> {
			entityManager.unwrap(Session.class).setFlushMode(FlushMode.ALWAYS);
			Product p1 = new Product(1, "p1");
			Product p2 = new Product(2, "p2");
			entityManager.persist(p1);
			LOGGER.info("Before JPQL query");
			entityManager.createQuery("FROM DifferentProduct", DifferentProduct.class).getResultList();
			LOGGER.info("After JPQL query");
			entityManager.persist(p2);
			LOGGER.info("Before Tx commit");
		});
	}

	@Test
	void testManualFlushing() {
		txRunner.executeInTransaction( entityManager -> {
			entityManager.unwrap(Session.class).setFlushMode(FlushMode.MANUAL);
			Product p1 = new Product(1, "p1");
			Product p2 = new Product(2, "p2");
			entityManager.persist(p1);
			LOGGER.info("Before JPQL query");
			entityManager.createNativeQuery("SELECT * FROM products").getResultList();
			LOGGER.info("After JPQL query");
			entityManager.persist(p2);
			LOGGER.info("Before Tx commit");
			entityManager.flush();
		});
	}

}

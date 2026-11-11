package com.financetracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.financetracker.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    @Test
    void findsTransactionsWithinDateRange() {
        repository.save(new Transaction(LocalDate.of(2026, 1, 5), "Coffee", new BigDecimal("-4.50"), "Food"));
        repository.save(new Transaction(LocalDate.of(2026, 3, 1), "Salary", new BigDecimal("2500.00"), "Income"));

        List<Transaction> result = repository.findByDateBetweenOrderByDateDesc(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertEquals(1, result.size());
        assertEquals("Coffee", result.get(0).getDescription());
    }
}

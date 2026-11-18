package com.financetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.financetracker.model.Transaction;
import com.financetracker.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class TransactionServiceTest {

    @Test
    void summarizeSplitsIncomeExpensesAndCategories() {
        TransactionRepository repo = mock(TransactionRepository.class);
        LocalDate d = LocalDate.of(2026, 11, 1);
        when(repo.findAll()).thenReturn(List.of(
                new Transaction(d, "Salary", new BigDecimal("1000.00"), "Income"),
                new Transaction(d, "Coffee", new BigDecimal("-4.50"), "Food"),
                new Transaction(d, "Lunch", new BigDecimal("-10.50"), "Food")));

        TransactionService.Summary s = new TransactionService(repo).summarize();

        assertEquals(0, new BigDecimal("1000.00").compareTo(s.income()));
        assertEquals(0, new BigDecimal("15.00").compareTo(s.expenses()));
        assertEquals(0, new BigDecimal("985.00").compareTo(s.balance()));
        assertEquals(0, new BigDecimal("15.00").compareTo(s.expensesByCategory().get("Food")));
    }
}

package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public record Summary(BigDecimal income, BigDecimal expenses, BigDecimal balance,
                          Map<String, BigDecimal> expensesByCategory) {}

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "date", "id"));
    }

    public Transaction save(Transaction transaction) {
        if (transaction.getCategory() == null || transaction.getCategory().isBlank()) {
            transaction.setCategory("Uncategorized");
        }
        return repository.save(transaction);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    public Summary summarize() {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        Map<String, BigDecimal> byCategory = new LinkedHashMap<>();
        for (Transaction t : repository.findAll()) {
            if (t.getAmount().signum() >= 0) {
                income = income.add(t.getAmount());
            } else {
                BigDecimal spent = t.getAmount().abs();
                expenses = expenses.add(spent);
                byCategory.merge(t.getCategory() == null ? "Uncategorized" : t.getCategory(),
                        spent, BigDecimal::add);
            }
        }
        return new Summary(income, expenses, income.subtract(expenses), byCategory);
    }
}

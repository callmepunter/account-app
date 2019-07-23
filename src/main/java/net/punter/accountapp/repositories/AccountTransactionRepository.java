package net.punter.accountapp.repositories;

import net.punter.accountapp.domains.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, String> {

    Collection<AccountTransaction> findByAccountId(String accountId);
}

package net.punter.accountapp.repositories;

import net.punter.accountapp.domains.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long>{
}

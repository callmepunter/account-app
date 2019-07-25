package net.punter.accounting.repositories;

import net.punter.accounting.domains.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>{
}

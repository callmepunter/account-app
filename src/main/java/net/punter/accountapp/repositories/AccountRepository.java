package net.punter.accountapp.repositories;

import net.punter.accountapp.domains.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>{
}

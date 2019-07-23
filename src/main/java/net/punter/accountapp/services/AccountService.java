package net.punter.accountapp.services;

import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;
import net.punter.accountapp.domains.Balance;

import java.util.Collection;
import java.util.Set;

public interface AccountService {

    Account createNew(Account account);

    Collection<Account> getAllAccounts();

    Account get(Long id);

    void delete(Long id);

    String deposit(Long accountId, AccountTransaction accountTransaction);

    String withDraw(Long accountId, AccountTransaction accountTransaction);
}

package net.punter.accountapp.services;

import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;

import java.util.Collection;
import java.util.List;

public interface AccountService {

    Account createAccount(Account account);

    Collection<Account> getAllAccounts();

    Account getAccount(Long accountId);

    void deleteAccount(Long accountId);

    /**
     * @param accountId - identifier linked to account.
     */
    String deposit(Long accountId, AccountTransaction accountTransaction);

    String withDraw(Long accountId, AccountTransaction accountTransaction);

    List<AccountTransaction> getAllTransactions(Long accountId);
}

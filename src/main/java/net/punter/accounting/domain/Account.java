package net.punter.accounting.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.punter.accounting.InsufficientBalanceException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;


@Entity
@Table(name = "tb_account")
@Getter
@Setter
@NoArgsConstructor

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.EAGER)
    @Setter(AccessLevel.NONE)
    private Set<Balance> balances;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private Collection<AccountTransaction> accountTransactions;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private ACCOUNT_TYPE type = ACCOUNT_TYPE.SAVINGS;

    /**
     * Account is opened as type for a currency. They can not be changed ever...
     */
    public Account(ACCOUNT_TYPE type) {

        this.type = type;
    }

    public Balance findBalance(@NotNull Currency currency) {
        for (Balance balance : getBalances()) {
            if (balance.getCurrency().equals(currency)) {
                return balance;
            }
        }
        return this.createNewBalance(currency);
    }

    protected Balance createNewBalance(@NotNull Currency currency) {
        Balance newBalance = new Balance(currency);
        newBalance.setAccount(this);
        this.balances.add(newBalance);
        return newBalance;
    }

    /***
     * If type of balance exists then add to it.
     * Other wise link the new balance to account.
     * @param balance
     */
    public Balance credit(@NotNull Balance balance) {
        Balance existing = findBalance(balance.getCurrency());
        existing.setAmount(existing.getAmount().add(balance.getAmount()));
        return existing;
    }

    public Balance debit(@NotNull Balance balance) {
        Balance existing = findBalance(balance.getCurrency());
        if (existing.isGreaterThanOrEquals(balance.getAmount())) {
            existing.setAmount(existing.getAmount().subtract(balance.getAmount()));
            return existing;
        }
        throw new InsufficientBalanceException();
    }

    public Set<Balance> getBalances() {
        if (balances == null) {
            balances = new HashSet<>();
        }
        return balances;
    }

    public Collection<AccountTransaction> getAccountTransactions() {
        if (this.accountTransactions == null) {
            this.accountTransactions = new ArrayList<>();
        }
        return accountTransactions;
    }

    public enum ACCOUNT_TYPE {
        SAVINGS, CURRENT;
    }

    /**
     * This is for the account query interface. Return the lightweight payload first.
     */
    public void trimTransactions(){
        getAccountTransactions().clear();
    }
}

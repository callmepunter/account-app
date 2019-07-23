package net.punter.accountapp.domains;


import lombok.*;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "tb_account")
@Data
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.EAGER)
    @Setter(AccessLevel.NONE)
    private Set<Balance> balances;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Collection<AccountTransaction> transactions;

    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private ACCOUNT_TYPE type = ACCOUNT_TYPE.SAVINGS;

    /**
     * Account is opened as type for a currency. They can not be changed ever...
     */
    public Account(ACCOUNT_TYPE type) {

        this.type = type;
    }

    private Account() {

    }


    public enum ACCOUNT_TYPE {
        SAVINGS, CURRENT;
    }

    public Balance findBalance(Currency currency) {
        Balance holder = null;
        for (Balance balance : getBalances()) {
            if (balance.getCurrency().equals(currency)) {
                return balance;
            }
        }
        /*if holder is till null it is a new balance add and then return*/
        if (holder == null) {
            holder = new Balance(currency);
            holder.setAccount(this);
            balances.add(holder);
        }
        return holder;
    }

    public void addBalance(Balance balance) {
        getBalances().add(balance);
    }

    public Set<Balance> getBalances() {
        if (balances == null) {
            balances = new HashSet<>();
        }
        return balances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(getId(), account.getId()) &&
                Objects.equals(getName(), account.getName()) &&
                getType() == account.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getType());
    }
}

package net.punter.accountapp.domains;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Collection<AccountTransaction> transactions;

    @Column(updatable = false)
    private ACCOUNT_TYPE type = ACCOUNT_TYPE.SAVINGS;

    /**
     * Account is opened as type for a currency. They can not be changed ever...
     */
    public Account(ACCOUNT_TYPE type) {

        this.type = type;
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
        balance.setAccount(this);
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

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}

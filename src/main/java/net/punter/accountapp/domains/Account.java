package net.punter.accountapp.domains;



import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;


@Entity
@Table(name = "tb_account")
@Data
@ToString
public class Account {

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Balance> balances;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Collection<AccountTransaction> orders ;

    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private ACCOUNT_TYPE type = ACCOUNT_TYPE.SAVINGS;

    /**
     * Account is opened as type for a currency. They can not be changed ever...
     */
    public Account (ACCOUNT_TYPE type){

        this.type = type;
    }

    private Account(){

    }





    public enum ACCOUNT_TYPE{
        SAVINGS, CURRENT;
    }
}

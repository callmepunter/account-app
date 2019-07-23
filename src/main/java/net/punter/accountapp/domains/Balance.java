package net.punter.accountapp.domains;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "tb_account_balance")
@Data
@ToString
public class Balance {


    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;


    public Balance(Currency currency){
        this.currency = currency;
    }

    @Column
    BigDecimal amount = BigDecimal.ZERO;

    /**
     * Balance currency can not be modified ever...
     */
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    Currency currency;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tb_account_id")
    private Account account;

}

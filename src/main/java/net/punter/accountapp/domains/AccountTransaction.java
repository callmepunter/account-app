package net.punter.accountapp.domains;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@Entity
@Table(name = "tb_account_transaction")
@Data
public class AccountTransaction {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tb_account_id")
    @JsonIgnore
    private Account account;

    @Column
    private BigDecimal amount;

    @Column
    private Currency currency;

    @Column
    private TYPE type = TYPE.INVALID;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/MM/dd")
    private Date requestedOn;

    public enum TYPE {
        DEBIT, CREDIT, INVALID;
    }


}

package net.punter.accountapp.domains;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.Instant;
import java.util.Currency;
import java.util.Date;
import java.util.Objects;
@Immutable
@Entity
@Table(name = "tb_account_transaction")
@Data
public class AccountTransaction {


    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    String id;

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

    @Column
    String remarks;

    @PrePersist
    void createdOn(){
        this.requestedOn = new Date();
    }

    @Override
    public String toString() {
        return "AccountTransaction{" +
                "id='" + id + '\'' +
                ", account=" + account.getId() +
                ", amount=" + amount +
                ", currency=" + currency +
                ", type=" + type +
                ", requestedOn=" + requestedOn +
                '}';
    }
}

package iisc.ac.in.Wallet;

import jakarta.persistence.*;

@Entity
public class Wallet {
    @Id
    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer balance;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Wallet() {
    }

    public Wallet(Integer userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }
}
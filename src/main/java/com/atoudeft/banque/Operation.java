package com.atoudeft.banque;

import java.util.Date;

public abstract class Operation {
    private Date date;
    private TypeOperation typeOperation;
    private double montant;

    public Operation(TypeOperation typeOperation, double montant){
        this.typeOperation = typeOperation;
        this.montant = montant;
        this.date = new Date(System.currentTimeMillis());
    }

    public Date getDate() {
        return this.date;
    }

    public double getMontant() {
        return montant;
    }

}

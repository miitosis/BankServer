package com.atoudeft.banque;

import java.util.Date;

public abstract class Operation {
    private long temps;
    private Date date;
    private TypeOperation typeOperation;
    private double montant;

    public Operation(TypeOperation typeOperation, double montant){
        this.typeOperation = typeOperation;
        this.montant = montant;
    }
}

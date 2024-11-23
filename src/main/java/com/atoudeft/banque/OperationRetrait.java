package com.atoudeft.banque;

public class OperationRetrait extends Operation{

    public OperationRetrait(TypeOperation typeOperation, double montant) {
        super(typeOperation, montant);
    }

    @Override
    public String toString() {
        return (getDate() + " " + "RETRAIT" + " " + getMontant());
    }

}

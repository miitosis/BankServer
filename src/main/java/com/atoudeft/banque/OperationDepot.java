package com.atoudeft.banque;

public class OperationDepot extends Operation{
    public OperationDepot(TypeOperation typeOperation, double montant) {
        super(typeOperation, montant);
    }

    @Override
    public String toString() {
        return (getDate() + " " + "DEPOT" + " " + getMontant());
    }

}

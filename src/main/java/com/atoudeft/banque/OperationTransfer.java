package com.atoudeft.banque;

public class OperationTransfer extends Operation{
    private String numCompteConcerne;

    public OperationTransfer(TypeOperation typeOperation, double montant, String numCompteConcerne) {
        super(typeOperation, montant);
        this.numCompteConcerne = numCompteConcerne;
    }

    @Override
    public String toString() {
        return (getDate() + " " + "TRANSFER" + " " + getMontant() + " " + this.numCompteConcerne);
    }

}

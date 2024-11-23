package com.atoudeft.banque;

public class OperationFacture extends Operation{
    private String numFacture;
    private String desc;

    public OperationFacture(TypeOperation typeOperation, double montant, String numFacture, String desc) {
        super(typeOperation, montant);
        this.numFacture = numFacture;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return (getDate() + " " + "FACTURE" + " " + getMontant() + " " + numFacture + " " + this.desc);
    }

}

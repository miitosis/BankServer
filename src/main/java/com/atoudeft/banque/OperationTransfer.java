package com.atoudeft.banque;

public class OperationTransfer extends Operation{
    private String numCompteClient;

    public OperationTransfer(TypeOperation typeOperation, double montant, String numCompteClient) {
        super(typeOperation, montant);
        this.numCompteClient = numCompteClient;
    }
}

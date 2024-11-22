package com.atoudeft.banque;

public class OperationTransfer extends Operation{
    private String numCompteClient;

    public OperationTransfer(TypeOperation typeOperation, int montant, String numCompteClient) {
        super(typeOperation, montant);
        this.numCompteClient = numCompteClient;
    }
}

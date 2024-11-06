package com.atoudeft.banque;

public class CompteCheque extends CompteBancaire{


    /**
     * Crée un compte bancaire.
     *
     * @param numero numéro du compte
     * @param type   type du compte
     */
    public CompteCheque(String numero, TypeCompte type) {
        super(numero, type);
    }

    @Override
    public boolean crediter(double montant) {
        /*
        if (montant > 0) {
            solde += montant;
            return true;
        }
         */

        return false;
    }

    @Override
    public boolean debiter(double montant) {
        /*
        if (montant > 0 && solde >= montant) {
            solde -= montant;
            return true;
        }
         */

        return false;
    }

    @Override
    public boolean payerFacture(String numeroFacture, double montant, String description) {
        return false;
    }

    @Override
    public boolean transferer(double montant, String numeroCompteDestinataire) {
        return false;
    }
}

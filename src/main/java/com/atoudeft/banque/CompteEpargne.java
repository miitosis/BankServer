package com.atoudeft.banque;
//AUTEUR: NAJIB TAHIRI
public class CompteEpargne extends CompteBancaire {

    private final double Limite = 1000;
    private final double frais = 2;

    private double tauxInteret;

    public CompteEpargne(String numero, TypeCompte type, double tauxInteret) {
        super(numero, type);
        this.tauxInteret = tauxInteret;
    }

    @Override
    public boolean crediter(double montant) {
        double solde = getSolde();
        if (montant > 0) {
            solde+= montant;
            setSolde(solde);
            return true;
        }
        return false;
    }

    @Override
    public boolean debiter(double montant) {
        double soldeInitial = getSolde();
        if(montant > 0) {
            if(montant < soldeInitial) {
                setSolde(soldeInitial-montant);
                if(soldeInitial < Limite) {
                    setSolde(getSolde() - frais);
                    return true;
                }
                return true;
            }
        }
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

    public void ajouterInterets() {
        double interets = getSolde() * tauxInteret / 12;
        setSolde(getSolde() - interets);
    }

}

package com.atoudeft.banque;

import com.atoudeft.banque.CompteEpargne;
import jdk.nashorn.internal.ir.WhileNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Banque implements Serializable {
    private String nom;
    private List<CompteClient> comptes;

    public Banque(String nom) {
        this.nom = nom;
        this.comptes = new ArrayList<>();
    }

    public CompteClient getCompteClient(String numeroCompteClient) {
        CompteClient cpt = new CompteClient(numeroCompteClient,"");
        int index = this.comptes.indexOf(cpt);
        if (index != -1)
            return this.comptes.get(index);
        else
            return null;
    }

    public boolean appartientA(String numeroCompteBancaire, String numeroCompteClient) {
        throw new NotImplementedException();
    }

    public boolean deposer(double montant, String numeroCompte) {
        throw new NotImplementedException();
    }

    public boolean retirer(double montant, String numeroCompte) {
        throw new NotImplementedException();
    }
    public boolean transferer(double montant, String numeroCompteInitial, String numeroCompteFinal) {
        throw new NotImplementedException();
    }

    public boolean payerFacture(double montant, String numeroCompte, String numeroFacture, String description) {
        throw new NotImplementedException();
    }

    public boolean ajouter(String numCompteClient, String nip) {
        if (numCompteClient.length() < 6 || numCompteClient.length() > 8) {
            return false;
        }
        for (char c : numCompteClient.toCharArray()) {
            if (!Character.isUpperCase(c) && !Character.isDigit(c)) {
                return false;
            }
        }
        if (nip.length() < 4 || nip.length() > 5) {
            return false;
        }
        for (char c : nip.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        for(CompteClient client : comptes) {
            if(client.getNumero().equals(numCompteClient)){
                return false;
            }
        }
        CompteClient nouveauClient = new CompteClient(numCompteClient, nip);
        String nouveauNumeroCompte;
        do{
            nouveauNumeroCompte = CompteBancaire.genereNouveauNumero();
        }
        while(compteDejaExistant(nouveauNumeroCompte));
        CompteCheque compteCheque = new CompteCheque(nouveauNumeroCompte, TypeCompte.CHEQUE);
        nouveauClient.ajouter(compteCheque);
        return comptes.add(nouveauClient);
    }
    private boolean compteDejaExistant(String numeroCompte) {
        for (CompteClient client : comptes) {
            for (CompteBancaire compte : client.getComptes()) {
                if (compte.getNumero().equals(numeroCompte)) {
                    return true;
                }
            }
        }
        return false;

    }

    public String getNumeroCompteParDefaut(String numCompteClient) {
        for(CompteClient client : comptes) {
            if(client.getNumero().equals(numCompteClient)){
                if(!client.getComptes().isEmpty()) {
                    return client.getComptes().get(0).getNumero();
                } else{
                    return null;
                }
            }
        }
        return null;
    }
    public String getNumeroCompteEpargne(String numCompteClient) {
        CompteClient client = getCompteClient(numCompteClient);
        if (client != null) {
            for (CompteBancaire compte : client.getComptes()) {
                if (compte instanceof CompteEpargne) {
                    return compte.getNumero();
                }
            }
        }
        return null;
    }
}
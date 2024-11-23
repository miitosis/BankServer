package com.atoudeft.banque.serveur;

import com.atoudeft.banque.CompteBancaire;
import com.atoudeft.commun.net.Connexion;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.Socket;

public class ConnexionBanque extends Connexion {
    private String numeroCompteClient;
    private long tempsDerniereOperation;
    private String numeroCompteActuel;

    public ConnexionBanque(Socket s) {
        super(s);
        tempsDerniereOperation = System.currentTimeMillis();
    }


    public boolean estInactifDepuis(long delai) {
        long tempsActuel = System.currentTimeMillis();
        return tempsActuel - tempsDerniereOperation >= delai;

    }


    public long getTempsDerniereOperation() {
        return tempsDerniereOperation;
    }


    public void setTempsDerniereOperation(long tempsDerniereOperation) {
        this.tempsDerniereOperation = tempsDerniereOperation;
    }

    public String getNumeroCompteClient() {
        return numeroCompteClient;
    }


    public void setNumeroCompteClient(String numeroCompteClient) {
        this.numeroCompteClient = numeroCompteClient;
    }


    public String getNumeroCompteActuel() {
        return numeroCompteActuel;
    }


    public void setNumeroCompteActuel(String numeroCompteActuel) {
        this.numeroCompteActuel = numeroCompteActuel;
    }
}
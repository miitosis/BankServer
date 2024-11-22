package com.atoudeft.banque;

import java.io.Serializable;

// Classe Noeud représentant un élément de la pile
public class Noeud<type> implements Serializable {
    // Valeur contenue dans le noeud
    private type valeur;
    // Pointeur vers le noeud suivant dans la pile
    private Noeud<type> suivant;

    // Constructeur pour initialiser un noeud avec une valeur
    public Noeud(type valeur) {
        this.valeur = valeur;
        this.suivant = null;
    }

    // Getter pour obtenir la valeur du noeud
    public type getValeur() {
        return valeur;
    }

    // Setter pour modifier la valeur du noeud
    public void setValeur(type valeur) {
        this.valeur = valeur;
    }

    // Getter pour obtenir le noeud suivant
    public Noeud<type> getSuivant() {
        return suivant;
    }

    // Setter pour définir le noeud suivant
    public void setSuivant(Noeud<type> suivant) {
        this.suivant = suivant;
    }
}

package com.atoudeft.banque;

import java.io.Serializable;

// Classe Pile implémentant une pile chaînée générique
public class PileChainee<type> implements Serializable {

    // Noeud sommet de la pile (le dernier élément empilé)
    private Noeud<type> sommet;

    // Constructeur par défaut initialisant une pile vide
    public PileChainee() {
        this.sommet = null;
    }

    // Méthode pour vérifier si la pile est vide
    public boolean estVide() {
        return sommet == null;
    }

    // Méthode pour ajouter un élément au sommet de la pile
    public void empiler(type x) {
        // Créer un nouveau noeud avec la valeur à empiler
        Noeud<type> newNoeud = new Noeud<>(x);
        // Le noeud suivant devient l'ancien sommet
        newNoeud.setSuivant(sommet);
        // Le nouveau noeud devient le sommet
        sommet = newNoeud;
    }

    // Méthode pour afficher tous les éléments de la pile
    public String afficherElementsPile() {
        if (estVide()) {
            return "L'historique est vide";
        }

        // Utiliser un StringBuilder pour construire la représentation des éléments
        StringBuilder stringBuilder1 = new StringBuilder();
        Noeud<type> current = sommet;
        while (current != null) {
            // Ajouter la valeur de chaque noeud à la chaîne
            stringBuilder1.append(current.getValeur().toString()).append("\n");
            current = current.getSuivant();
        }
        return stringBuilder1.toString();
    }

}

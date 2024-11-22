package com.atoudeft.serveur;

import com.atoudeft.banque.*;
import com.atoudeft.banque.serveur.ConnexionBanque;
import com.atoudeft.banque.serveur.ServeurBanque;
import com.atoudeft.commun.evenement.Evenement;
import com.atoudeft.commun.evenement.GestionnaireEvenement;
import com.atoudeft.commun.net.Connexion;

/**
 * Cette classe représente un gestionnaire d'événement d'un serveur. Lorsqu'un serveur reçoit un texte d'un client,
 * il crée un événement à partir du texte reçu et alerte ce gestionnaire qui réagit en gérant l'événement.
 *
 * @author Abdelmoumène Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 *
 * AUTEUR: NAJIB TAHIRI
 */



public class GestionnaireEvenementServeur implements GestionnaireEvenement {
    private Serveur serveur;

    /**
     * Construit un gestionnaire d'événements pour un serveur.
     *
     * @param serveur Serveur Le serveur pour lequel ce gestionnaire gère des événements
     */
    public GestionnaireEvenementServeur(Serveur serveur) {
        this.serveur = serveur;
    }

    /**
     * Méthode de gestion d'événements. Cette méthode contiendra le code qui gère les réponses obtenues d'un client.
     *
     * @param evenement L'événement à gérer.
     */
    @Override
    public void traiter(Evenement evenement) {
        Object source = evenement.getSource();
        ServeurBanque serveurBanque = (ServeurBanque)serveur;
        Banque banque;
        ConnexionBanque cnx;
        String msg, typeEvenement, argument, numCompteClient, nip;
        String[] t;

        if (source instanceof Connexion) {
            cnx = (ConnexionBanque) source;
            System.out.println("SERVEUR: Recu : " + evenement.getType() + " " + evenement.getArgument());
            typeEvenement = evenement.getType();
            cnx.setTempsDerniereOperation(System.currentTimeMillis());
            switch (typeEvenement) {
                /******************* COMMANDES GÉNÉRALES *******************/
                case "EXIT": //Ferme la connexion avec le client qui a envoyé "EXIT":
                    cnx.envoyer("END");
                    serveurBanque.enlever(cnx);
                    cnx.close();
                    break;
                case "LIST": //Envoie la liste des numéros de comptes-clients connectés :
                    cnx.envoyer("LIST " + serveurBanque.list());
                    break;
                /******************* COMMANDES DE GESTION DE COMPTES *******************/
                case "NOUVEAU": //Crée un nouveau compte-client :
                    if (cnx.getNumeroCompteClient()!=null) {
                        cnx.envoyer("NOUVEAU NO deja connecte");
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    if (t.length<2) {
                        cnx.envoyer("NOUVEAU NO");
                    }
                    else {
                        numCompteClient = t[0];
                        nip = t[1];
                        banque = serveurBanque.getBanque();
                        if (banque.ajouter(numCompteClient,nip)) {
                            cnx.setNumeroCompteClient(numCompteClient);
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                            cnx.envoyer("NOUVEAU OK " + t[0] + " cree");
                        }
                        else
                            cnx.envoyer("NOUVEAU NO "+t[0]+" existe");
                    }
                    break;

                case "CONNECT":
                    if (cnx.getNumeroCompteClient()!=null) {
                        cnx.envoyer("CONNECT NO deja connecte");
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    if (t.length<2) {
                        cnx.envoyer("CONNECT NO");
                    }
                    else{
                        numCompteClient = t[0];
                        nip = t[1];
                        banque = serveurBanque.getBanque();
                        CompteClient compte = banque.getCompteClient(numCompteClient);
                        if (compte != null && compte.getNip().equals(nip) ) {
                            cnx.setNumeroCompteClient(numCompteClient);
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                            cnx.envoyer("CONNECT OK");
                        } else {
                            cnx.envoyer("CONNECT NO");
                        }
                    }
                    break;
                case "EPARGNE":
                    if (cnx.getNumeroCompteClient() == null) {
                        cnx.envoyer("EPARGNE NO");

                    }else{
                        banque = serveurBanque.getBanque();
                        CompteClient compteClient = banque.getCompteClient(cnx.getNumeroCompteClient());
                        boolean IfExist = false;
                        for (CompteBancaire compte : compteClient.getComptes()) {
                            if (compte instanceof CompteEpargne) {
                                IfExist = true;
                                break;
                            }
                        }
                        if (IfExist) {
                            cnx.envoyer("EPARGNE NO");
                        } else {
                            String numeroCompte = CompteBancaire.genereNouveauNumero();
                            CompteEpargne compteEpargne = new CompteEpargne(numeroCompte, TypeCompte.EPARGNE, 0.05);
                            compteClient.ajouter(compteEpargne);
                            cnx.envoyer("EPARGNE OK");
                        }
                    }
                    break;
                case "SELECT":
                    String accountType = evenement.getArgument().toLowerCase();
                    if (cnx.getNumeroCompteClient() == null) {
                        cnx.envoyer("SELECT NO");
                    } else {
                        String numeroDeCompte = null;
                        banque = serveurBanque.getBanque();
                        if (accountType.equals("cheque")) {
                            numeroDeCompte = banque.getNumeroCompteParDefaut(cnx.getNumeroCompteClient());
                        } else if (accountType.equals("epargne")) {
                            numeroDeCompte = banque.getNumeroCompteEpargne(cnx.getNumeroCompteClient());
                        } else {
                            cnx.envoyer("SELECT NO Invalid");
                            return;
                        }
                        if (numeroDeCompte != null) {
                            cnx.setNumeroCompteActuel(numeroDeCompte);
                            cnx.envoyer("SELECT OK");
                        } else {
                            cnx.envoyer("SELECT NO");
                        }
                    }
                    break;
                case "DEPOT":
                    if (cnx.getNumeroCompteClient() == null || cnx.getNumeroCompteActuel() == null) {
                        cnx.envoyer("DEPOT NO 1");
                    } else{

                    }

                    break;
                /******************* TRAITEMENT PAR DÉFAUT *******************/
                default: //Renvoyer le texte recu convertit en majuscules :
                    msg = (evenement.getType() + " " + evenement.getArgument()).toUpperCase();
                    cnx.envoyer(msg);
            }
        }
    }
}
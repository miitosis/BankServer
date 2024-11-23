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
                case "NOUVEAU": // Crée un nouveau compte-client
                    // Vérifie si un client est déjà connecté
                    if (cnx.getNumeroCompteClient() != null) {
                        cnx.envoyer("NOUVEAU NO deja connecté"); // Envoie un message indiquant que le client est déjà connecté
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":"); // Sépare l'argument par le caractère ':'
                    if (t.length < 2) {
                        cnx.envoyer("NOUVEAU NO"); // Envoie un message d'échec si le format de l'argument est incorrect
                    } else {
                        numCompteClient = t[0]; // Récupère le numéro de compte client
                        nip = t[1]; // Récupère le NIP
                        banque = serveurBanque.getBanque();
                        if (banque.ajouter(numCompteClient, nip)) { // Ajoute le compte client à la banque
                            cnx.setNumeroCompteClient(numCompteClient); // Enregistre le numéro de compte client dans la connexion
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient)); // Sélectionne le compte par défaut
                            cnx.envoyer("NOUVEAU OK " + t[0] + " cree"); // Envoie un message de succès
                        } else {
                            cnx.envoyer("NOUVEAU NO " + t[0] + " existe"); // Envoie un message indiquant que le compte existe déjà
                        }
                    }
                    break;

                case "CONNECT": // Connecte un client existant
                    // Vérifie si un client est déjà connecté
                    if (cnx.getNumeroCompteClient() != null) {
                        cnx.envoyer("CONNECT NO deja connecté"); // Envoie un message indiquant que le client est déjà connecté
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":"); // Sépare l'argument par le caractère ':'
                    if (t.length < 2) {
                        cnx.envoyer("CONNECT NO"); // Envoie un message d'échec si le format de l'argument est incorrect
                    } else {
                        numCompteClient = t[0]; // Récupère le numéro de compte client
                        nip = t[1]; // Récupère le NIP
                        banque = serveurBanque.getBanque();
                        CompteClient compte = banque.getCompteClient(numCompteClient);
                        if (compte != null && compte.getNip().equals(nip)) { // Vérifie si le compte existe et si le NIP correspond
                            cnx.setNumeroCompteClient(numCompteClient); // Enregistre le numéro de compte client dans la connexion
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient)); // Sélectionne le compte par défaut
                            cnx.envoyer("CONNECT OK"); // Envoie un message de succès
                        } else {
                            cnx.envoyer("CONNECT NO"); // Envoie un message d'échec si le compte n'existe pas ou si le NIP est incorrect
                        }
                    }
                    break;

                case "EPARGNE": // Crée un compte épargne pour un client connecté
                    // Vérifie si un client est connecté
                    if (cnx.getNumeroCompteClient() == null) {
                        cnx.envoyer("EPARGNE NO"); // Envoie un message d'échec si aucun client n'est connecté
                    } else {
                        banque = serveurBanque.getBanque();
                        CompteClient compteClient = banque.getCompteClient(cnx.getNumeroCompteClient());
                        boolean IfExist = false;
                        // Vérifie si un compte épargne existe déjà
                        for (CompteBancaire compte : compteClient.getComptes()) {
                            if (compte instanceof CompteEpargne) {
                                IfExist = true;
                                break;
                            }
                        }
                        if (IfExist) {
                            cnx.envoyer("EPARGNE NO"); // Envoie un message d'échec si un compte épargne existe déjà
                        } else {
                            String numeroCompte = CompteBancaire.genereNouveauNumero(); // Génère un nouveau numéro de compte
                            CompteEpargne compteEpargne = new CompteEpargne(numeroCompte, TypeCompte.EPARGNE, 0.05); // Crée un compte épargne avec un taux d'intérêt de 5%
                            compteClient.ajouter(compteEpargne); // Ajoute le compte épargne au client
                            cnx.envoyer("EPARGNE OK"); // Envoie un message de succès
                        }
                    }
                    break;

                case "SELECT": // Sélectionne un compte (chèque ou épargne) comme compte actif
                    String accountType = evenement.getArgument().toLowerCase(); // Récupère le type de compte demandé
                    // Vérifie si un client est connecté
                    if (cnx.getNumeroCompteClient() == null) {
                        cnx.envoyer("SELECT NO"); // Envoie un message d'échec si aucun client n'est connecté
                    } else {
                        String numeroDeCompte = null;
                        banque = serveurBanque.getBanque();
                        // Vérifie le type de compte demandé et récupère le numéro de compte correspondant
                        if (accountType.equals("cheque")) {
                            numeroDeCompte = banque.getNumeroCompteParDefaut(cnx.getNumeroCompteClient());
                        } else if (accountType.equals("epargne")) {
                            numeroDeCompte = banque.getNumeroCompteEpargne(cnx.getNumeroCompteClient());
                        } else {
                            cnx.envoyer("SELECT NO"); // Envoie un message d'échec si le type de compte est invalide
                            return;
                        }
                        if (numeroDeCompte != null) {
                            cnx.setNumeroCompteActuel(numeroDeCompte); // Définit le compte actuel
                            cnx.envoyer("SELECT OK"); // Envoie un message de succès
                        } else {
                            cnx.envoyer("SELECT NO"); // Envoie un message d'échec si le compte n'existe pas
                        }
                    }
                    break;

                case "DEPOT": // Effectue un dépôt sur le compte actif du client
                    // Vérifie si un client est connecté et si un compte est sélectionné
                    if (cnx.getNumeroCompteClient() == null || cnx.getNumeroCompteActuel() == null) {
                        cnx.envoyer("DEPOT NO"); // Envoie un message d'échec si aucun client n'est connecté ou si aucun compte n'est sélectionné
                    } else {
                        banque = serveurBanque.getBanque();
                        CompteClient compteClient = banque.getCompteClient(cnx.getNumeroCompteClient());
                        boolean accFound = false;
                        CompteBancaire cpt = null;
                        // Recherche le compte sélectionné parmi les comptes du client
                        for (CompteBancaire account : compteClient.getComptes()) {
                            if (account.getNumero().equals(cnx.getNumeroCompteActuel())) {
                                accFound = true;
                                cpt = account;
                                break;
                            }
                        }
                        if (!accFound) {
                            cnx.envoyer("DEPOT NO"); // Envoie un message d'échec si le compte n'est pas trouvé
                        } else {
                            argument = evenement.getArgument();
                            t = argument.split(" "); // Sépare l'argument par les espaces
                            double montant;
                            try {
                                montant = Double.parseDouble(t[0]); // Convertit le montant en nombre
                            } catch (NumberFormatException a) {
                                cnx.envoyer("DEPOT NO"); // Envoie un message d'échec si le montant n'est pas un nombre valide
                                break;
                            }
                            if (!cpt.crediter(montant)) { // Crédite le compte du montant indiqué
                                cnx.envoyer("DEPOT NO"); // Envoie un message d'échec si le crédit échoue
                            } else {
                                OperationDepot operationDepot = new OperationDepot(TypeOperation.DEPOT, montant); // Crée une nouvelle opération de dépôt
                                cpt.getHistorique().empiler(operationDepot); // Ajoute l'opération à l'historique du compte
                                cnx.envoyer("DEPOT OK " + cpt.getSolde()); // Envoie un message de succès avec le nouveau solde
                            }
                        }
                    }
                    break;
                case "RETRAIT" :
                    // Vérification que les numéros de compte client et compte actuel ne sont pas nuls
                    if (cnx.getNumeroCompteClient() == null || cnx.getNumeroCompteActuel() == null) {
                        // Si l'un des numéros de compte est manquant, envoyer une réponse négative
                        cnx.envoyer("RETRAIT NO");
                    } else {
                        // Récupération de la banque à partir du serveur
                        banque = serveurBanque.getBanque();
                        // Récupération du compte client associé au numéro de compte client
                        CompteClient compteClient = banque.getCompteClient(cnx.getNumeroCompteClient());
                        boolean accFound = false;
                        CompteBancaire cpt = null;

                        // Recherche du compte bancaire correspondant au numéro de compte actuel
                        for (CompteBancaire account : compteClient.getComptes()) {
                            if (account.getNumero().equals(cnx.getNumeroCompteActuel())) {
                                accFound = true;
                                cpt = account;
                                break;
                            }
                        }

                        // Si le compte n'a pas été trouvé, envoyer une réponse négative
                        if (!accFound) {
                            cnx.envoyer("RETRAIT NO");
                        } else {
                            // Extraction de l'argument de l'événement et séparation en tokens
                            argument = evenement.getArgument();
                            t = argument.split(" ");
                            double montantRetrait;

                            // Tentative de conversion du montant de retrait en double
                            try {
                                montantRetrait = Double.parseDouble(t[0]);
                            } catch (NumberFormatException a) {
                                // Si la conversion échoue, envoyer une réponse négative
                                cnx.envoyer("RETRAIT NO");
                                break;
                            }

                            // Tentative de débit du montant du compte
                            if (!cpt.debiter(montantRetrait)) {
                                // Si le débit échoue (fonds insuffisants), envoyer une réponse négative
                                cnx.envoyer("RETRAIT NO");
                            } else {
                                // Si le débit réussit, créer une nouvelle opération de retrait
                                OperationRetrait operationRetrait = new OperationRetrait(TypeOperation.RETRAIT, montantRetrait);
                                // Empiler l'opération de retrait dans l'historique des opérations du compte
                                cpt.getHistorique().empiler(operationRetrait);
                                // Envoyer une réponse positive indiquant que le retrait a réussi
                                cnx.envoyer("RETRAIT OK");
                            }
                        }
                    }
                    break;

                case "FACTURE":
                    // Vérification si les informations de compte client sont nulles
                    if (cnx.getNumeroCompteClient() == null || cnx.getNumeroCompteActuel() == null) {
                        // Si l'une des informations de compte est nulle, envoyer une erreur "FACTURE NO"
                        cnx.envoyer("FACTURE NO");
                    } else {
                        // Récupération de l'objet banque
                        banque = serveurBanque.getBanque();
                        // Obtention du compte client correspondant
                        CompteClient compteClient = banque.getCompteClient(cnx.getNumeroCompteClient());

                        boolean accFound = false;
                        CompteBancaire cpt = null;

                        // Recherche du compte bancaire correspondant dans les comptes du client
                        for (CompteBancaire account : compteClient.getComptes()) {
                            if (account.getNumero().equals(cnx.getNumeroCompteActuel())) {
                                accFound = true;
                                cpt = account;
                                break;  // Quitter la boucle une fois le compte trouvé
                            }
                        }

                        // Si aucun compte n'a été trouvé, envoyer une erreur "FACTURE NO"
                        if (!accFound) {
                            cnx.envoyer("FACTURE NO");
                        } else {
                            // Récupération des arguments (montant et description de la facture)
                            argument = evenement.getArgument();
                            t = argument.split(" ");
                            Double montantFacture;
                            try {
                                // Conversion du premier argument en montant de type Double
                                montantFacture = Double.parseDouble(t[0]);
                            } catch (NumberFormatException a) {
                                // En cas d'erreur de format, envoyer une erreur "FACTURE NO"
                                cnx.envoyer("FACTURE NO");
                                break;
                            }

                            // Débiter le montant de la facture du compte
                            if (!cpt.debiter(montantFacture)) {
                                // Si le débit échoue, envoyer une erreur "FACTURE NO"
                                cnx.envoyer("FACTURE NO");
                            } else {
                                // Créer une nouvelle opération de facture
                                OperationFacture operationFacture = new OperationFacture(TypeOperation.FACTURE, montantFacture, t[1], t[2]);
                                // Ajouter l'opération au historique du compte
                                cpt.getHistorique().empiler(operationFacture);
                                // Envoyer une confirmation "FACTURE OK"
                                cnx.envoyer("FACTURE OK ");
                            }
                        }
                    }
                    break;

                case "TRANSFER":
                    // Vérification si les informations de compte client sont nulles
                    if (cnx.getNumeroCompteClient() == null || cnx.getNumeroCompteActuel() == null) {
                        // Si l'une des informations de compte est nulle, envoyer une erreur "TRANSFER NO"
                        cnx.envoyer("TRANSFER NO");
                    } else {
                        // Récupération de l'objet banque
                        banque = serveurBanque.getBanque();
                        // Obtention du compte client correspondant
                        CompteClient compteClient = banque.getCompteClient(cnx.getNumeroCompteClient());

                        // Récupération des arguments (montant et numéro de compte concerné)
                        argument = evenement.getArgument();
                        t = argument.split(" ");

                        // Vérification de la validité des arguments
                        if (t.length < 2) {
                            // Si moins de deux arguments, envoyer une erreur "TRANSFER NO"
                            cnx.envoyer("TRANSFER NO");
                            return;
                        }

                        String numCptConcerne = t[1];
                        Double montantTransfer;
                        try {
                            // Conversion du premier argument en montant de type Double
                            montantTransfer = Double.parseDouble(t[0]);
                        } catch (NumberFormatException e) {
                            // En cas d'erreur de format, envoyer une erreur "TRANSFER NO"
                            cnx.envoyer("TRANSFER NO");
                            return;
                        }

                        // Recherche du compte bancaire à transférer dans les comptes du client
                        CompteBancaire cpt = null;
                        for (CompteBancaire account : compteClient.getComptes()) {
                            if (account.getNumero().equals(cnx.getNumeroCompteActuel())) {
                                cpt = account;
                                break;  // Quitter la boucle une fois le compte trouvé
                            }
                        }

                        // Si le compte à débiter n'a pas été trouvé, envoyer une erreur "TRANSFER NO"
                        if (cpt == null) {
                            cnx.envoyer("TRANSFER NO");
                            return;
                        }

                        // Obtention du compte client concerné par le transfert
                        CompteClient cptConcerne = banque.getCompteClient(numCptConcerne);

                        // Si le compte concerné est introuvable, envoyer une erreur "TRANSFER NO"
                        if (cptConcerne == null) {
                            cnx.envoyer("TRANSFER NO");
                            return;
                        }

                        // Obtention du premier compte de chèque du client concerné (hypothèse que c'est le compte visé)
                        CompteCheque cptChequeConcerne = (CompteCheque) cptConcerne.getComptes().get(0);

                        // Débiter le compte actuel du montant à transférer
                        if (!cpt.debiter(montantTransfer)) {
                            // Si le débit échoue, envoyer une erreur "TRANSFER NO"
                            cnx.envoyer("TRANSFER NO");
                            // Créditer le compte concerné du montant transféré
                        } else if (!cptChequeConcerne.crediter(montantTransfer)) {
                            // Si le crédit échoue, envoyer une erreur "TRANSFER NO"
                            cnx.envoyer("TRANSFER NO");
                        } else {
                            // Créer une nouvelle opération de transfert
                            OperationTransfer operationTransfer = new OperationTransfer(TypeOperation.TRANSFER, montantTransfer, t[1]);
                            // Ajouter l'opération au historique du compte
                            cpt.getHistorique().empiler(operationTransfer);
                            // Envoyer une confirmation "TRANSFER OK"
                            cnx.envoyer("TRANSFER OK");
                        }
                    }
                    break;
                case "HIST":
                    // Vérifie si le client est connecté en vérifiant la présence de numéros de comptes
                    if (cnx.getNumeroCompteClient() == null || cnx.getNumeroCompteActuel() == null) {
                        // Si un des numéros de comptes est manquant, on envoie "HIST NO" pour indiquer un problème
                        cnx.envoyer("HIST NO");
                    } else {
                        // Récupère la banque depuis le serveur
                        banque = serveurBanque.getBanque();

                        // Récupère le compte client correspondant au numéro de compte du client
                        CompteClient compteClient = banque.getCompteClient(cnx.getNumeroCompteClient());

                        // Déclare une variable pour stocker le compte bancaire actuel (initialisé à null)
                        CompteBancaire cpt = null;

                        // Parcourt la liste des comptes du client pour trouver le compte correspondant
                        for (CompteBancaire account : compteClient.getComptes()) {
                            // Vérifie si le numéro du compte correspond au numéro de compte actuel
                            if (account.getNumero().equals(cnx.getNumeroCompteActuel())) {
                                // Si le compte est trouvé, l'assigne à 'cpt' et sort de la boucle
                                cpt = account;
                                break;
                            }
                        }

                        // Vérifie si le compte n'a pas été trouvé (cpt est toujours null)
                        if (cpt == null) {
                            // Envoie "HIST NO" pour indiquer qu'aucun compte n'a été trouvé correspondant au numéro actuel
                            cnx.envoyer("HIST NO");
                        } else {
                            // Si le compte a été trouvé, envoie l'historique du compte au client
                            cnx.envoyer("HIST\n" + cpt.getHistToString());
                        }
                    }
                    // Fin du traitement du cas "HIST"
                    break;



                /******************* TRAITEMENT PAR DÉFAUT *******************/
                default: //Renvoyer le texte recu convertit en majuscules :
                    msg = (evenement.getType() + " " + evenement.getArgument()).toUpperCase();
                    cnx.envoyer(msg);
            }
        }
    }
}
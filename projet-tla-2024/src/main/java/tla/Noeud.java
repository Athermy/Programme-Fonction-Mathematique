/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

import java.util.ArrayList;
import java.util.List;

public class Noeud {
    private List<Noeud> enfants = new ArrayList<>();
    private TypeDeNoeud typeDeNoeud;
    private String valeur;

    // Constructeur pour un noeud avec un type et une valeur 
    public Noeud(TypeDeNoeud typeDeNoeud, String valeur) {
        this.typeDeNoeud = typeDeNoeud;
        this.valeur = valeur;
    }

    // Constructeur pour un noeud avec seulement un type 
    public Noeud(TypeDeNoeud cl) {
        this.typeDeNoeud = cl;
    }

    // Ajoute un enfant à ce noeud 
    public void ajout(Noeud n) {
        enfants.add(n);
    }

    // Retourne l'enfant à l'index spécifié de ce noeud
    public Noeud enfant(int i) {
        return this.enfants.get(i);
    }

    // Retourne le nombre d'enfants de ce noeud 
    public int nombreEnfants() {
        return this.enfants.size();
    }

    // Retourne le type de ce noeud 
    public TypeDeNoeud getTypeDeNoeud() {
        return typeDeNoeud;
    }

    // Retourne la valeur de ce noeud 
    public String getValeur() {
        return valeur;
    }

    // Retourne une représentation en chaîne de ce noeud 
    public String toString() {
        String s = "<";
        if (typeDeNoeud != null) s = s + typeDeNoeud;
        if (valeur != null) s = s + ", " + valeur;
        return s + ">";
    }

    /*
    Affiche le noeud n d'un arbre syntaxique avec un niveau d'indentation depth,
    et appels récursifs sur les noeuds enfants de n à un niveau d'indendation depth+1 
    */
    static void afficheNoeud(Noeud n, int profondeur) {
        String s = "";
        // Indentation pour afficher le noeud à la bonne profondeur
        for(int i = 0; i < profondeur; i++) s = s + "  ";
        // Informations sur le noeud actuel
        int nbNoeudsEnfants = n.nombreEnfants();
        s = s + n.toString() + (" (" + nbNoeudsEnfants + " noeuds enfants)");
        System.out.println(s);
        // Appels récursifs sur les noeuds enfants
        for(int i = 0; i < nbNoeudsEnfants; i++) {
            afficheNoeud(n.enfant(i), profondeur + 1);
        }
    }
}

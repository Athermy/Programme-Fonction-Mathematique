/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

public class Token {

    private TypeDeToken typeDeToken;
    private String valeur;

    // Constructeur pour un token avec un type et une valeur 
    public Token(TypeDeToken typeDeToken, String valeur) {
        this.typeDeToken = typeDeToken;
        this.valeur = valeur;
    }

    // Constructeur pour un token avec seulement un type 
    public Token(TypeDeToken typeDeToken) {
        this.typeDeToken = typeDeToken;
    }

    // Retourne le type de ce token 
    public TypeDeToken getTypeDeToken() {
        return typeDeToken;
    }

    // Retourne la valeur de ce token 
    public String getValeur() {
        return valeur;
    }

    // Retourne une représentation en chaîne de ce token 
    @Override
    public String toString() {
        String res = typeDeToken.toString();
        if (valeur != null) res = res + "(" + valeur + ")";
        return res;
    }
}

/*
MAKLOUFI Mayassa
CHEN Christophe
CASTEL Arthur
*/

package tla;

// Énumération des types de noeuds dans l'arbre syntaxique 
public enum TypeDeNoeud {
    statement,  // Instruction
    add,        // Addition
    subtract,   // Soustraction
    multiply,   // Multiplication
    divide,     // Division
    kPow,       // Puissance
    abs,        // Valeur absolue
    sin,        // Sinus
    cos,        // Cosinus
    tan,        // Tangente
    exp,        // Exponentielle
    intv,       // Valeur entière
    ident       // Identifiant
}
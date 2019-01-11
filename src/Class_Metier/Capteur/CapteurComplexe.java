package Class_Metier.Capteur;

import java.util.*;

public class CapteurComplexe extends CapteurAbstrait implements Observer {

    private Map<CapteurAbstrait, Integer> listeCapteur;
    public Map<CapteurAbstrait,Integer> getListeCapteur() { return listeCapteur; }

    public CapteurComplexe(Map<CapteurAbstrait, Integer> m, String nom) {
        super(0f, nom);
        this.listeCapteur = m;
        this.setValeur(calculValeur());
    }

    private float calculValeur() {
        float sommeCoeff = 0f;
        float valeur = 0f;
        if(listeCapteur.size() == 0) {
            return valeur;
        }
        else {
            Set<Map.Entry<CapteurAbstrait, Integer>> setListeCapteur = this.listeCapteur.entrySet();
            Iterator<Map.Entry<CapteurAbstrait, Integer>> it = setListeCapteur.iterator();
            while (it.hasNext()) {
                Map.Entry<CapteurAbstrait, Integer> e = it.next();
                sommeCoeff = sommeCoeff + e.getValue();
                valeur = valeur + e.getKey().getValeur() * e.getValue();
            }
            return valeur / sommeCoeff;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.setValeur(calculValeur());
    }

    public void ajoutCapteur(CapteurAbstrait c, Integer coeff) {
        this.listeCapteur.put(c, coeff);
        c.addObserver(this);
        this.setValeur(calculValeur());
    }
}

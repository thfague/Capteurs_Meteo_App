package vue.configCapteur;

import class_Metier.capteur.CapteurAbstrait;
import class_Metier.capteur.CapteurComplexe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import vue.DigitalFormatCell;

import java.io.IOException;
import java.util.*;

public class AffichageConfigCaptComp {
    @FXML
    private GridPane gridConfig;

    public CapteurComplexe capteur;
    private List<CapteurAbstrait> listeTotalCapteur;
    private VBox vb = new VBox();
    private List<CapteurAbstrait> listeCapteurLie;
    private List<Integer> listeCapteurLieCoeff;
    private ObservableList<CapteurAbstrait> observablelListeCapteur;
    private ObservableList<CapteurAbstrait> oCapteur;
    private Text coeffTxt;
    private Spinner<Integer> coeffSpinner;
    private Text nomCapteur = new Text();
    private TextField nomCapteurTF = new TextField();
    private Button validation;
    private Map<CapteurAbstrait,Integer> m = new HashMap<>();
    private CapteurAbstrait captComp = new CapteurComplexe(m,"");

    public AffichageConfigCaptComp(CapteurComplexe  c, List<CapteurAbstrait> l){
        capteur=c;
        listeTotalCapteur = l;
    }

    @FXML
    private void initialize() {
        Text nomCapteur = new Text();
        nomCapteur.setText(capteur.getNom());
        Font font = new Font("Arial", 18);
        nomCapteur.setFont(font);
        coeffTxt = new Text("Coefficient : ");
        coeffSpinner = new Spinner<>();
        coeffSpinner.valueFactoryProperty().setValue(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));

        boutonAjouterC();
        chargementCapteurLie();

        validation = new Button("Valider");
        validation.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                validationCapteurComplexe();
            }
        });

        affichageCapteurComplexe();
        //gridAjoutCapteur.add(vb, 0, 0);






        gridConfig.add(nomCapteur,0, 0);
        gridConfig.add(vb, 1, 1);
    }

    //Méthode qui charge tous les capteurs liés au capteur complexe
    private void chargementCapteurLie() {
        listeCapteurLieCoeff = new ArrayList<>();
        listeCapteurLie = new ArrayList<>();
        Set<Map.Entry<CapteurAbstrait, Integer>> setListeCapteur = capteur.getListeCapteur().entrySet();
        Iterator<Map.Entry<CapteurAbstrait, Integer>> it = setListeCapteur.iterator();
        while (it.hasNext()) {
            Map.Entry<CapteurAbstrait, Integer> e = it.next();
            listeCapteurLie.add(e.getKey());
            listeCapteurLieCoeff.add(e.getValue());
        }

        observablelListeCapteur = FXCollections.observableList(listeCapteurLie);
        ListView<CapteurAbstrait> listeVCapteur = new ListView<>(observablelListeCapteur);

        listeVCapteur.setCellFactory(new Callback<ListView<CapteurAbstrait>, ListCell<CapteurAbstrait>>() {
            @Override
            public ListCell<CapteurAbstrait> call(ListView<CapteurAbstrait> param) {
                return new DigitalFormatCell(listeCapteurLie);
            }
        });

        listeVCapteur.getSelectionModel().selectedItemProperty().addListener((listeCapteur, oV, nV) -> {
            vb.getChildren().clear();
            boutonAjouterC();
            changementCoeff(nV, observablelListeCapteur);
            boutonSupprimerC(nV, observablelListeCapteur);
        });

        gridConfig.add(listeVCapteur, 0, 1);
    }

    //Méthode qui affiche le bouton supprimer
    private void boutonSupprimerC(CapteurAbstrait c, ObservableList<CapteurAbstrait> olCapteur) {
        Button buttonSupp = new Button("Supprimer " + c.getNom());
        vb.getChildren().add(buttonSupp);
        buttonSupp.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                olCapteur.remove(c);
                capteur.getListeCapteur().remove(c);
            }
        });
    }

    //Méthode qui affiche le bouton ajouter qui affiche la liste des capteurs non liés
    private void boutonAjouterC() {
        Button buttonAjout = new Button("Ajouter un capteur");
        vb.getChildren().add(buttonAjout);
        buttonAjout.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CapteurAbstrait> listeCapteurNonLie = new ArrayList<>();

                for(int i=0; i < listeTotalCapteur.size(); i++) {
                    if(!observablelListeCapteur.contains(listeTotalCapteur.get(i))){
                        if(!listeTotalCapteur.get(i).getNom().equals(capteur.getNom())) {
                            if(listeTotalCapteur.get(i) instanceof CapteurComplexe){
                                if(!((CapteurComplexe) listeTotalCapteur.get(i)).getListeCapteur().containsKey(capteur)) {
                                    listeCapteurNonLie.add(listeTotalCapteur.get(i));
                                }
                            }
                            else {
                                listeCapteurNonLie.add(listeTotalCapteur.get(i));
                            }
                        }
                    }
                }

                oCapteur = FXCollections.observableList(listeCapteurNonLie);
                ListView<CapteurAbstrait> listeViewCapteur = new ListView<>(oCapteur);

                listeViewCapteur.setCellFactory(new Callback<ListView<CapteurAbstrait>, ListCell<CapteurAbstrait>>() {
                    @Override
                    public ListCell<CapteurAbstrait> call(ListView<CapteurAbstrait> param) {
                        return new DigitalFormatCell(listeCapteurNonLie);
                    }
                });

                listeViewCapteur.getSelectionModel().selectedItemProperty().addListener((listeCNonApproprie, oV, nV) -> {
                    vb.getChildren().clear();
                    ajouterCapteurInexistant();
                    vb.getChildren().add(listeViewCapteur);
                    vb.getChildren().add(coeffTxt);
                    vb.getChildren().add(coeffSpinner);
                    ajoutCapteur(nV, oCapteur);
                });

                vb.getChildren().clear();
                ajouterCapteurInexistant();
                vb.getChildren().add(listeViewCapteur);
            }
        });
    }

    //Affiche et ajoute un capteur existant non lié au capteur complexe
    private void ajoutCapteur(CapteurAbstrait c, ObservableList<CapteurAbstrait> oCapteur) {
        Button buttonAjout = new Button("Ajouter " + c.getNom());
        vb.getChildren().add(buttonAjout);
        buttonAjout.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                capteur.ajoutCapteur(c, coeffSpinner.getValue());
                chargementCapteurLie();
                vb.getChildren().clear();
                boutonAjouterC();
            }
        });
    }

    private void changementCoeff(CapteurAbstrait c, ObservableList<CapteurAbstrait> oCapteur) {
        coeffTxt.setText("Coefficent : ");
        Spinner<Integer> chgmtCoeffSpinner = new Spinner<>();
        chgmtCoeffSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,20,listeCapteurLieCoeff.get(listeCapteurLie.indexOf(c))));
        vb.getChildren().add(coeffTxt);
        vb.getChildren().add(chgmtCoeffSpinner);
        Button buttonModif = new Button("Modifier coeff " + c.getNom());
        vb.getChildren().add(buttonModif);
        buttonModif.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                capteur.getListeCapteur().remove(c);
                capteur.ajoutCapteur(c, chgmtCoeffSpinner.getValue());
                chargementCapteurLie();
                vb.getChildren().clear();
                boutonAjouterC();
            }
        });
    }

    private void ajouterCapteurInexistant() {
        Button ajoutCInex = new Button("Ajouter un nouveau capteur");
        vb.getChildren().add(ajoutCInex);
        ajoutCInex.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent actionEvent){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ajoutCapteur.fxml"));
                    //loader.setController(new AjoutCapteur(oCapteur));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.initOwner(gridConfig.getScene().getWindow());
                    stage.setTitle(ajoutCInex.getText());
                    stage.setScene(new Scene(root, 500, 400));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();

                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void affichageCapteurComplexe()
    {
        vb.getChildren().clear();

        nomCapteur.setText("Nom du capteur complexe : ");
        vb.getChildren().add(nomCapteur);
        vb.getChildren().add(nomCapteurTF);

        Text titre = new Text("Configurer la liste de capteur");
        vb.getChildren().add(titre);

        vb.getChildren().add(validation);
    }

    private void validationCapteurComplexe() {
        for(int i = 0; i < listeTotalCapteur.size(); i++) {
            if(nomCapteurTF.getText().equals(listeTotalCapteur.get(i).getNom())) {
                affichageCapteurComplexe();
                nomCapteurTF.setText("");
                vb.getChildren().add(new Text("Nom de capteur déjà pris"));
                return;
            }
        }
        captComp.setNom(nomCapteurTF.getText());
        listeTotalCapteur.add(captComp);
        Stage stage = (Stage) validation.getScene().getWindow();
        stage.close();
    }
}
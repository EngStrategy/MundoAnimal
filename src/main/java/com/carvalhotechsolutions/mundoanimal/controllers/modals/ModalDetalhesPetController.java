package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.model.Animal;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ModalDetalhesPetController {
    @FXML
    private Text pet_name_label;

    @FXML
    private Text pet_specie_label;

    @FXML
    private Text pet_race_label;

    @FXML
    private Text pet_age_label;

    @FXML
    private Text pet_notes_label;

    private Animal animal;

    public void configurarParaExibicao(Animal animal) {
        this.animal = animal;

        pet_name_label.setText(animal.getNome());
        pet_specie_label.setText(animal.getEspecie().toString());
        pet_race_label.setText(animal.getRaca() == null ? "Sem informações" : animal.getRaca());
        pet_age_label.setText(animal.getIdade() == null ? "Sem informações" : animal.getIdade().toString() + " anos");
        pet_notes_label.setText(animal.getObservacoes() == null ? "Sem informações" : animal.getObservacoes());
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) pet_name_label.getScene().getWindow();
        stage.close();
    }
}

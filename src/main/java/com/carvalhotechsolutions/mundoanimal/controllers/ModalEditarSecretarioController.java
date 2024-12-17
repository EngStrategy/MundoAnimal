package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import com.carvalhotechsolutions.mundoanimal.repositories.SecretarioRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModalEditarSecretarioController {
    @FXML
    private TextField secretary_id_field;

    @FXML
    private TextField create_secretary_name_field;

    @FXML
    private TextField create_secretary_phone_field;

    private SecretarioRepository secretarioRepository = new SecretarioRepository();

    // Referência para o controlador principal
    private SecretarioController secretarioController;

    public void setSecretarioController(SecretarioController secretarioController) {
        this.secretarioController = secretarioController;
    }

    @FXML
    public void editarSecretario() {
        Long id = Long.parseLong(secretary_id_field.getText());
        Secretario secretario = secretarioRepository.findById(id);

        String nome = create_secretary_name_field.getText();
        String telefone = create_secretary_phone_field.getText();

        if (!validarInputs(nome, telefone)) {
            return;
        }

        secretario.setNomeUsuario(nome);
        secretario.setTelefone(telefone);

        secretarioRepository.save(secretario);

        if (secretarioController != null) {
            secretarioController.atualizarTableView();
        }

        fecharModal();
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) create_secretary_name_field.getScene().getWindow();
        stage.close();
    }

    private boolean validarInputs(String nome, String telefone) {
        if (nome.isEmpty() || telefone.isEmpty()) {
            mostrarAlerta("Erro", "Campo(s) obrigatório(s) vazio(s)!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    public void configurarParaEdicao(Secretario secretario) {
        secretary_id_field.setText(secretario.getId().toString()); // Preencher o ID invisível
        create_secretary_name_field.setText(secretario.getNomeUsuario());
        create_secretary_phone_field.setText(secretario.getTelefone());
    }
}

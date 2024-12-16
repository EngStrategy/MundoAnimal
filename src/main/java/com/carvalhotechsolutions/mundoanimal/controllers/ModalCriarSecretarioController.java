package com.carvalhotechsolutions.mundoanimal.controllers;

import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import com.carvalhotechsolutions.mundoanimal.model.Servico;
import com.carvalhotechsolutions.mundoanimal.model.enums.TipoUsuario;
import com.carvalhotechsolutions.mundoanimal.repositories.SecretarioRepository;
import com.carvalhotechsolutions.mundoanimal.security.PasswordUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ModalCriarSecretarioController {
    @FXML
    private Label titleLabel;

    @FXML
    private Button actionButton;

    @FXML
    private HBox secretary_password_container;

    @FXML
    private VBox modal_secretary_container;

    @FXML
    private HBox secretary_editable_fields_container;

    @FXML
    private TextField secretary_id_field;

    @FXML
    private TextField create_secretary_name_field;

    @FXML
    private TextField create_secretary_phone_field;

    @FXML
    private PasswordField create_secretary_password_field;

    @FXML
    private PasswordField create_secretary_password_confirmation_field;

    private SecretarioRepository secretarioRepository = new SecretarioRepository();

    private Secretario secretarioAtual;

    // Referência para o controlador principal
    private SecretarioController secretarioController;

    public void setSecretarioController(SecretarioController secretarioController) {
        this.secretarioController = secretarioController;
    }

    @FXML
    public void cadastrarSecretario() {

        String nome = create_secretary_name_field.getText();
        String telefone = create_secretary_phone_field.getText();
        String password = create_secretary_password_field.getText();
        String passwordConfirmation = create_secretary_password_confirmation_field.getText();

        if (!validarInputs(nome, telefone, password, passwordConfirmation)) {
            return;
        }

        try {

            Secretario secretario;

            if (secretary_id_field.getText() != null && !secretary_id_field.getText().isEmpty()) {
                Long id = Long.parseLong(secretary_id_field.getText());
                secretario = secretarioRepository.findById(id);
            } else {
                secretario = new Secretario();
            }

            secretario.setNomeUsuario(nome);
            secretario.setTelefone(telefone);
            secretario.setSenha(PasswordUtils.hashPassword(create_secretary_password_field.getText()));
            secretario.setTipoUsuario(TipoUsuario.SECRETARIO);

            secretarioRepository.save(secretario);

            if (secretarioController != null) {
                secretarioController.atualizarTableView();
            }

            fecharModal();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao cadastrar secretário. Tente novamente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) create_secretary_name_field.getScene().getWindow();
        stage.close();
    }

    private boolean validarInputs(String nome, String telefone, String password, String passwordConfirmation) {
        if (nome.isEmpty() || telefone.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            mostrarAlerta("Erro", "Campo(s) obrigatório(s) vazio(s)!", Alert.AlertType.ERROR);
            return false;
        }
        if (!password.equals(passwordConfirmation)) {
            mostrarAlerta("Erro", "Senhas não estão iguais.", Alert.AlertType.ERROR);
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
        this.secretarioAtual = secretario;

        // Remover campos de senha
        secretary_password_container.setVisible(false);
        secretary_password_container.setManaged(false);

        // Reajustando altura do modal
        modal_secretary_container.setPrefHeight(255.0);

        // Ajustando css do container de campos editáveis
        secretary_editable_fields_container.setStyle("-fx-border-color: #cccccc transparent #cccccc transparent;");
        secretary_editable_fields_container.setPrefHeight(115.0);

        // Atualizar campos
        secretary_id_field.setText(secretario.getId().toString()); // Preencher o ID invisível
        create_secretary_name_field.setText(secretario.getNomeUsuario());
        create_secretary_phone_field.setText(secretario.getTelefone());
        create_secretary_password_field.setText(secretario.getSenha());
        create_secretary_password_confirmation_field.setText(secretario.getSenha());

        // Alterar título e botão
        titleLabel.setText("Editar Secretário(a)");
        actionButton.setText("Salvar");
    }
}

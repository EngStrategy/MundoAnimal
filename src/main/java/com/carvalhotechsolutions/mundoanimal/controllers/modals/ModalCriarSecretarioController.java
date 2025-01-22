package com.carvalhotechsolutions.mundoanimal.controllers.modals;

import com.carvalhotechsolutions.mundoanimal.controllers.gerenciamento.SecretarioController;
import com.carvalhotechsolutions.mundoanimal.model.Secretario;
import com.carvalhotechsolutions.mundoanimal.enums.TipoUsuario;
import com.carvalhotechsolutions.mundoanimal.repositories.SecretarioRepository;
import com.carvalhotechsolutions.mundoanimal.utils.MaskedTextField;
import com.carvalhotechsolutions.mundoanimal.utils.PasswordManager;
import com.carvalhotechsolutions.mundoanimal.utils.TextFormatterManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalCriarSecretarioController {
    @FXML
    private TextField create_secretary_name_field;

    @FXML
    private MaskedTextField create_secretary_phone_field;

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

            Secretario secretario = new Secretario();

            secretario.setNomeUsuario(nome);
            secretario.setTelefone(telefone);
            secretario.setSenha(PasswordManager.hashPassword(create_secretary_password_field.getText()));
            secretario.setTipoUsuario(TipoUsuario.SECRETARIO);

            secretarioRepository.save(secretario);

            if (secretarioController != null) {
                secretarioController.atualizarTableView();
                secretarioController.handleSuccessfulOperation("Secretário(a) cadastrado(a) com sucesso!");
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
        nome = nome.trim();
        telefone = telefone.trim();

        if (nome.isEmpty() || telefone.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            mostrarAlerta("Erro", "Campo(s) obrigatório(s) vazio(s)!", Alert.AlertType.ERROR);
            return false;
        }

        if (!password.equals(passwordConfirmation)) {
            mostrarAlerta("Erro", "Senhas não estão iguais.", Alert.AlertType.ERROR);
            return false;
        }

        String finalTelefone = telefone;

        boolean telefoneJaCadastrado = secretarioRepository.findAll().stream()
                .anyMatch(secretario -> secretario.getTelefone().equals(finalTelefone) &&
                        (secretarioAtual == null || !secretario.getId().equals(secretarioAtual.getId())));
        if (telefoneJaCadastrado) {
            mostrarAlerta("Erro", "O telefone informado já está cadastrado no sistema.", Alert.AlertType.ERROR);
            return false;
        }

        String finalNome = nome;
        boolean nomeJaCadastrado = secretarioRepository.findAll().stream()
                .anyMatch(secretario -> secretario.getNomeUsuario().equalsIgnoreCase(finalNome) &&
                        (secretarioAtual == null || !secretario.getId().equals(secretarioAtual.getId())));
        if (nomeJaCadastrado) {
            mostrarAlerta("Erro", "Já existe um secretário cadastrado com esse nome.", Alert.AlertType.ERROR);
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
}

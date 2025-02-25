package com.carvalhotechsolutions.mundoanimal;

import animatefx.animation.FadeIn;
import com.carvalhotechsolutions.mundoanimal.database.DatabaseChecker;
import com.carvalhotechsolutions.mundoanimal.enums.ScreenEnum;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManagerHolder;
import com.carvalhotechsolutions.mundoanimal.utils.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;
import com.jcraft.jsch.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) {

        // Verifica conexão com o banco de dados
        DatabaseChecker.testConnectionAndInitializeAdmin();
        // Inicializa o ScreenManager
        ScreenManager sceneManager = new ScreenManager(stage);
        // Inicializa o ScreenManagerHolder
        ScreenManagerHolder.initialize(sceneManager);
        // Seleciona a primeira tela da aplicação
        sceneManager.switchTo(ScreenEnum.LOGIN);
        // Exibe a tela
        stage.show();
        // Aplicando FadeIn (AnimateFX)
        new FadeIn(sceneManager.getScreen(ScreenEnum.LOGIN)).play();
    }

    public static void main(String[] args) {
//        String sshHost = "ec2-**-***-***-***.sa-east-1.compute.amazonaws.com"; // Host do EC2
//        int sshPort = **; // Porta SSH
//        String sshUser = "***********"; // Usuário SSH
//        String identityFilePath = "*:\\**\\**\\**\\**.pem"; // Caminho para a chave privada
//
//        String remoteHost = "XXXXXXXXXXXXXXX.***********.sa-east-1.rds.amazonaws.com"; // Host do banco de dados
//        int remotePort = ****; // Porta do banco de dados (PostgreSQL)
//        int localPort = ****; // Porta local para o túnel
//
//        // Cria a sessão SSH em um thread separado para não bloquear a interface
//        new Thread(() -> {
//            try {
//                // Cria a sessão SSH
//                JSch jsch = new JSch();
//                jsch.addIdentity(identityFilePath); // Adiciona a chave privada para autenticação
//
//                Session session = jsch.getSession(sshUser, sshHost, sshPort);
//
//                session.setConfig("StrictHostKeyChecking", "no");
//                session.setConfig("UserKnownHostsFile", "/dev/null");
//
//                // Conecta à sessão SSH
//                session.connect();
//
//                // Cria o túnel SSH
//                session.setPortForwardingL(localPort, remoteHost, remotePort);
//
//                System.out.println("Túnel SSH criado com sucesso. Agora, a aplicação pode se conectar ao banco de dados.");
//
//                launch();
//            } catch (JSchException e) {
//                e.printStackTrace();
//            }
//        }).start();
        launch();
    }
}
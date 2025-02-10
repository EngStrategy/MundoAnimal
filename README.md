# 🐾 Mundo Animal - Sistema de Gestão para Petshop

Mundo Animal é um sistema desktop desenvolvido em **JavaFX** com **PostgreSQL** e **Hibernate (JPA)**, projetado para a gestão eficiente de um petshop. O sistema permite o gerenciamento de clientes, animais, agendamentos, serviços e secretários.

## 🚀 Tecnologias Utilizadas
- **Java 23**
- **JavaFX** (para interface gráfica)
- **PostgreSQL** (banco de dados relacional)
- **Hibernate/JPA** (persistência de dados)
- **Maven** (gerenciador de dependências)
- **BCrypt** (para criptografar senhas)

## 📌Funcionalidades Principais
- **Autenticação:** Login seguro para administradores e secretários.
- **Gerenciamento de Agendamentos:** Exibição dinâmica dos próximos atendimentos na tela inicial.
- **Cadastro de Clientes e Animais:** Relacionamento entre clientes e seus respectivos pets.
- **Controle de Serviços:** Registros de atendimento e relatório de operações.
- **Interface intuitiva:** Com animações suaves e pop-ups de confirmação.

## 🛠️ Instalação e Execução
### Pré-requisitos
- Java 21+ instalado
- Docker instalado e rodando
- Maven instalado

### Passos para executar o projeto
1. Clone o repositório
2. Execute o comando `docker-compose up -d` na raiz do projeto (executará o banco de dados PostgreSQL)
3. Execute o comando `mvn clean install javafx:run` na raiz do projeto

## 🛠️ Acesso ao banco de dados
### Passos para criar o banco de dados
- Acesse http://localhost:5050/ no terminal
- Email: admin@admin.com
- Password: admin
- Botão direito em Servers -> Register -> Server
- General -> Name: EngStrategy
- Connection :
  - Hostname: postgres
  - Port: 5432
  - Maintenance Database: db_mundo_animal
  - Username: engstrategy
  - Password: engstrategy1234@
  - Save the password

## 📂 Estrutura do Projeto
```
MundoAnimal/
│── src/
│   ├── main/
│   │   ├── java/com/carvalhotechsolutions/mundoanimal/
│   │   │   ├── controllers/
│   │   │   ├── models/
│   │   │   ├── repository/
│   │   │   ├── services/
│   │   ├── resources/
│   │   │   ├── fxml/
│   │   │   ├── images/
│   │   │   ├── styles/
│── pom.xml
│── README.md
```

## 🤝 Contribuição
Se deseja contribuir, siga os passos:
1. Fork este repositório
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas alterações (`git commit -m 'Adiciona nova funcionalidade'`)
4. Envie um push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📜 Licença
Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

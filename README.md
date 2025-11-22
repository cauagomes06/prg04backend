# FitHub API - Backend (Spring Boot)

API RESTful para o sistema de gestão de ginásios FitHub. Esta aplicação é responsável por toda a lógica de negócio, persistência de dados e autenticação, utilizando o framework Spring Boot.

## 🚀 Tecnologias

* **Linguagem:** Java 17+
* **Framework:** Spring Boot 3.x
* **Build Tool:** Apache Maven
* **Base de Dados:** PostgreSQL (Configurável)
* **Segurança:** Spring Security + JWT (JSON Web Tokens)
* **ORM:** Spring Data JPA / Hibernate
* **Mapeamento:** ModelMapper
* **Documentação:** OpenAPI 3 (Swagger UI)

## ✨ Principais Funcionalidades

A API oferece endpoints protegidos por JWT para gerir as seguintes áreas:

### 👤 Usuários e Perfis
* Registo de novos usuários (`ROLE_CLIENTE` por padrão).
* Sistema de autenticação (Login).
* Gerenciamento de perfis (`ADMIN`, `PERSONAL`, `CLIENTE`).
* Atualização de dados pessoais e senha do perfil logado.
* Ranking geral de usuários com base no `scoreTotal`.

### 🏋️ Treinos e Exercícios
* CRUD de exercícios (catálogo) disponível para `ADMIN` e `PERSONAL`.
* Criação e gerenciamento de fichas de treino por usuários.
* Funcionalidade de publicar treinos para a biblioteca.
* Funcionalidade de clonar treinos públicos da biblioteca.

### 🗓️ Aulas de Grupo
* CRUD de aulas agendadas (disponível para `ADMIN` e `PERSONAL`).
* Sistema de reservas de vaga (`Reserva`) para clientes.
* Contagem de vagas disponíveis em tempo real.
* Endpoint para listar instrutores (`ROLE_PERSONAL`).

### 🏆 Competições
* CRUD de competições (disponível para `ADMIN` e `PERSONAL`).
* Inscrição em competições ativas.
* Submissão de resultados pelos participantes.
* Ranking dinâmico por competição (ordenado por Maior/Menor melhor).
* Scheduler para processar e premiar os vencedores ao final da competição.

### 📢 Notificações
* Sistema de notificações personalizadas (ex: ganho de pontos em competição).
* Funcionalidade de "broadcast" para `ADMIN` enviar mensagens para todos os usuários.

## ⚙️ Configuração Local

### 1. Pré-requisitos
* Java Development Kit (JDK) 17+
* Apache Maven
* PostgreSQL Database

### 2. Configuração do Banco de Dados

Crie um banco de dados PostgreSQL e ajuste as credenciais no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/fithub}
spring.datasource.username=${DB_USERNAME:fithubuser}
spring.datasource.password=${DB_PASSWORD:fithubpassword}

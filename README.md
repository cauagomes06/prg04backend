# Fit Hub API — Backend

## 📌 Overview

O **Fit Hub API** é uma aplicação backend desenvolvida para suportar uma plataforma completa de gestão fitness.  
O sistema permite gerenciamento de treinos, exercícios, aulas, planos de subscrição e um sistema de gamificação com progressão por XP e conquistas.

O projeto foi estruturado com foco em:

- Arquitetura limpa e organizada por domínios  
- Segurança com autenticação JWT  
- Escalabilidade  
- Boas práticas REST  
- Separação clara de responsabilidades  

---

## 🚀 Funcionalidades

- Autenticação e autorização com JWT  
- Gestão de utilizadores e perfis  
- Criação e acompanhamento de treinos  
- Biblioteca de exercícios  
- Sistema de reservas de aulas  
- Planos de subscrição  
- Sistema de gamificação (XP, níveis e conquistas)  
- Dashboard administrativo  
- Sistema interno de notificações  
- Upload de ficheiros para Amazon S3  

---

## 🛠 Stack Tecnológica

- Java 17  
- Spring Boot (Web, Data JPA, Security)  
- Hibernate (ORM)  
- JWT (JSON Web Token)  
- Maven  
- Amazon S3  
- OpenAPI / Swagger  

---

## 🏗 Arquitetura

A aplicação segue arquitetura em camadas:

Controller → Service → Repository


### Organização por domínios

| Módulo | Responsabilidade |
|--------|------------------|
| `auth` | Autenticação e geração de tokens |
| `usuario / perfil / pessoa` | Gestão de utilizadores |
| `treino / exercicio / execucao` | Gestão de treinos |
| `aula / reserva` | Agendamento de aulas |
| `gamificacao / conquista` | Sistema de progressão |
| `dashboard` | Dados analíticos |
| `notificacao` | Notificações internas |

---

## 🔐 Segurança

- Autenticação baseada em JWT  
- Controle de acesso por roles  
- Proteção de endpoints sensíveis via Spring Security  
- Validação de dados de entrada  

---

## 📄 Documentação da API

Swagger UI  
http://localhost:8080/swagger-ui.html  

OpenAPI Docs  
http://localhost:8080/v3/api-docs  

---

## ⚙️ Execução Local

### Pré-requisitos

- Java 17+  
- Maven  
- Base de dados configurada  

### Clonar o projeto

```bash
git clone https://github.com/cauagomes06/prg04backend.git
cd fithub-api


### Configurar variáveis de ambiente

Definir no `application.properties` ou via variáveis de ambiente:

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
AWS_ACCESS_KEY=
AWS_SECRET_KEY=
```

📦 Status

Projeto em desenvolvimento contínuo.

👨‍💻 Autor

Cauã Gomes
Desenvolvedor Backend Java

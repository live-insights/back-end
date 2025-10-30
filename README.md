# Live Insights 📊 

Bem-vindo ao Live Insights WebApi!
Aqui, você vai avaliar a performance da sua live no YouTube de forma prática.
Vamos começar?

---

## 📦 **Dependências**

Antes de rodar o projeto, verifique se as seguintes dependências estão instaladas:

- **PostgreSQL**: É necessário ter o **PostgreSQL** instalado e rodando localmente para que o banco de dados seja configurado corretamente.

---

## ▶️ Criação da Chave da API do YouTube

Para utilizar a **YouTube Data API v3**, é necessário gerar uma chave de API no **Google Cloud Console**.

### Passo a passo

1. **Acesse o Google Cloud Console**  
   - [console.cloud.google.com](https://console.cloud.google.com/)  
   - Crie uma conta caso ainda não possua

2. **Crie um novo projeto**  
   - No painel inicial clique em **Criar Projeto**  
   - Defina um nome e confirme a criação

3. **Ative a API do YouTube**  
   - Vá em **APIs e serviços > Biblioteca**  
   - Pesquise por **YouTube Data API v3**  
   - Clique em **Ativar**

4. **Crie as credenciais da API**  
   - Acesse **APIs e serviços > Credenciais**  
   - Clique em **Criar credenciais > Chave de API**  
   - Copie a chave gerada e guarde em local seguro

🔗 Guia completo: [Tutorial](https://suporte.presence.com.br/portal/pt/kb/articles/criando-uma-chave-para-a-api-de-dados-do-youtube)

--- 

## 🤖 Criação da Chave da API do Groq

Para utilizar um client do Groq, é necessário gerar uma chave de API no **Console Groq**.

### Passo a passo

1. **Acesse o [Console Groq](https://console.groq.com/keys)**  
   - Faça login com sua conta Google (crie uma se ainda não possuir).

2. **Crie a chave da API**  
   - Clique em **Create API Key**  
   - Copie a chave gerada e guarde em local seguro.

---

## 📁 Configuração de ambiente

É necessário um arquivo `.env` na raiz do projeto com a seguinte estrutura: 

```declarative
YOUTUBE_API_KEY="SuaChaveApiYoutube"
LLM_PROVIDER=GROQ
LLM_API_KEY="SuaChaveApiGroq"
LLM_BATCH_SIZE=30
LLM_PROMPT="Você é um analista de comentários de lives. Sua tarefa é classificar cada comentário com dois números:

                  - O primeiro número representa o sentimento:
                    0 = Negativo
                    1 = Neutro
                    2 = Positivo

                  - O segundo número representa o tipo de interação:
                    3 = Pergunta
                    4 = Elogio
                    5 = Crítica
                    6 = Sugestão
                    7 = Meme / Piada
                    8 = Reclamação
                    9 = Reação emocional
                    10 = Ofensivo / Preconceituoso

                  Siga estritamente o padrão abaixo, não adicione nem remova nenhuma informação:
                  [ID] '<comentário> →' <sentimento> <tipo>

                  Exemplo:
                  [0] 'Muito bom!' → 2 4

                  Não modifique o ID nem o comentário. Apenas classifique.
                  Caso não se encaixe exatamente em nenhuma categoria classifique com a categoria mais próxima e sempre siga o padrão fornecido."
```

---

## 🗄️ **Configuração do Banco de Dados - PostgreSQL**

1. **Crie um banco de dados com o seguinte nome:**

```bash
live-insights
```

2. **Configurações do banco de dados:**

- **Usuário (superusuário):** `postgres`
- **Senha:** `postgres`
- **Porta:** 5432

Obs.: essas são as configurações definidas no arquivo `src/main/resources/application.yml`

Certifique-se de que o PostgreSQL esteja rodando corretamente em sua máquina local para que o backend se conecte ao banco de dados.

---

## 🚀 **Rodando o Projeto no Terminal**

Com as dependências configuradas, vamos rodar o projeto. Para isso, execute o seguinte comando:

```bash
mvn clean spring-boot:run
```

Obs.: O `clean` é opcional, mas recomendado caso precise limpar o build e começar de novo.

---

## 🔧 Acessando a API - Swagger UI
Agora que o BackEnd está rodando, você pode visualizar e testar a API diretamente no Swagger UI!

Acesse em:
http://localhost:8090/swagger-ui.html

## 🧩 Documentação da Arquitetura 

## 🧭 Visão Geral

O **Live Insights** é uma aplicação backend desenvolvida com **Spring Boot** que permite **análise em tempo real de lives (transmissões ao vivo)**, coletando e processando comentários através de integrações com **Large Language Models (LLMs)** para análise de sentimentos e insights.

---

## ⚙️ Tecnologias Principais

- **Java 17**  
- **Spring Boot 3.4.5**  
- **Spring Security** (autenticação baseada em sessão)  
- **Spring Data JPA**  
- **PostgreSQL**  
- **Maven**  
- **OpenAPI / Swagger** (documentação de API)  
- **OkHttp** (cliente HTTP)  
- **Jackson** (processamento JSON)

---

## 🏗️ Estrutura do Projeto

### 🔧 Camada de Configuração (`config`)

#### **SecurityConfig**
- Autenticação baseada em sessão HTTP  
- Proteção de endpoints (exceto `/auth/register`, `/auth/login` e documentação Swagger)  
- Encoder de senha com **BCrypt**  
- Configuração do **AuthenticationManager** com `DaoAuthenticationProvider`

#### **CorsConfig**
- Configura CORS para permitir requisições dos frontends:
  - `http://localhost:5500`
  - `http://localhost:5501`
  - `http://localhost:3000`
  - `https://localhost:3000`

---

### 🎮 Camada de Controle (`controller`)

#### **AuthController**
- **POST /auth/register:** Cadastro de novos usuários  
- **POST /auth/login:** Login com criação de sessão HTTP  

#### **LiveController**
- **GET /lives:** Lista todas as lives do usuário autenticado  
- **POST /lives:** Cria nova live com validação de duplicidade  
- **PUT /lives/{liveId}:** Atualiza título da live  
- **PUT /lives/{liveId}/tag:** Atualiza tag da live  
- **DELETE /lives/{liveId}:** Remove live e limpa tags órfãs  

> Inclui um `@RestController` interno **TagController** que expõe:  
> - **GET /tags:** Lista todas as tags disponíveis

#### **CommentsController**
- **GET /comments/{liveId}:** Retorna comentários de uma live específica do usuário autenticado  

#### **LiveAnalysisController**
- **POST /live/start/{liveId}:** Inicia análise de uma live  
- **POST /live/stop:** Interrompe análise em execução  
- **GET /live/status:** Verifica status da análise  

---

### 🧱 Camada de Modelo (`model`)

#### **Entidades Principais**

**User**
- Representa usuários do sistema  
- Relacionamento **one-to-many** com `Live`

**Live**
- Representa uma transmissão ao vivo  
- Campos: `liveId`, `title`, referência para `User` e `Tag`  
- Relacionamento **many-to-one** com `User` e `Tag`

**Tag**
- Categorização de lives  
- Relacionamento **one-to-many** com `Live`

**CommentsInfo**
- Armazena informações agregadas dos comentários coletados  
- Relacionamento com `Live` e `User`

---

#### **DTOs de API Externa**

- `GeneralInfoData`: Informações gerais sobre a live  
- `CommentsDetails`, `CommentsDetailsData`: Detalhes dos comentários individuais  
- `AuthorDetails`, `AuthorDetailsData`: Informações sobre autores dos comentários  

---

#### **Enums**
- `LiveStatus`: Estados possíveis de uma live  
- `Sentiment`: Classificação de sentimento dos comentários  
- `Interaction`: Tipos de interação do usuário  

---

### 🗃️ Camada de Repositório (`repository`)

Interfaces **Spring Data JPA**:
- `UserRepository`: Busca usuários por username  
- `LiveRepository`: Queries customizadas para lives (por usuário, por liveId, contagem por tag)  
- `TagRepository`: Busca tags por nome (case-insensitive)  
- `CommentsRepository`: Busca comentários por live e usuário  

---

### 🧠 Camada de Serviço (`service`)

#### **Serviços de LLM**
Interfaces:
- `ILLMService`: Interface base para serviços de LLM  
- `IOpenAiService`, `IGeminiService`, `IGroqService`: Interfaces específicas  

Implementações:
- `LLMService`: Classe abstrata base  
- `OpenAiService`: Integração com OpenAI  
- `GeminiService`: Integração com Google Gemini  
- `GroqService`: Integração com Groq  

---

#### **Serviços de Análise**
- **LiveAnalysisService (`ILiveAnalysisService`)**: Orquestra o processo completo de análise de lives  
  - Gerencia configuração, início e parada de análises  
  - Coordena coleta de comentários e processamento via LLM  

- **ScheduledAnalysisTask**: Executa tarefas agendadas de análise, coletando dados periodicamente durante a transmissão  

- **FetchLiveComments**: Responsável pela coleta de comentários da API externa e estruturação dos dados  

- **CheckLiveActivity**: Verifica se uma live está ativa/online  

---

#### **Serviços Auxiliares**
- **ConsumeApi (IConvertData):** Cliente HTTP genérico para consumo de APIs externas usando **OkHttp**  
- **ConvertData (IConvertData):** Conversão entre JSON e objetos Java usando **Jackson**  
- **UserDetailsServiceImpl:** Implementação customizada de `UserDetailsService` do **Spring Security**  

---

### 🧾 DTOs (`dto`)

- `AuthRequest`: Payload para requisições de autenticação (login/registro)  
- `ErrorResponse`: Estrutura padronizada de resposta de erro  

---

### 🚨 Tratamento Global de Exceções

**GlobalExceptionHandler (`@ControllerAdvice`)**
- Captura exceções não tratadas  
- Retorna respostas HTTP apropriadas com `ErrorResponse`  
- Trata especificamente `EntityNotFoundException` (404)  

---

## ⚙️ Configuração da Aplicação

**application.yml**
- Porta do servidor: `8090`  
- Configuração do PostgreSQL (`localhost:5432`)  
- Hibernate DDL: `update`  
- Logs SQL ativados  

**OpenApiConfig**
- Configura documentação **Swagger / OpenAPI**  
- Acessível via `/swagger-ui.html`  

---

## 🔁 Fluxo de Dados Principal

1. **Autenticação:** Usuário se registra ou faz login via `AuthController`  
2. **Criação de Live:** Usuário cria uma live através do `LiveController`  
3. **Início da Análise:** `LiveAnalysisController` inicia análise da live  
4. **Coleta de Dados:** `FetchLiveComments` busca comentários periodicamente  
5. **Processamento:** `LiveAnalysisService` envia dados para o LLM escolhido  
6. **Armazenamento:** Resultados são salvos via `CommentsRepository`  
7. **Consulta:** Usuário recupera análises via `CommentsController`  

---

## 🧱 Padrões de Arquitetura

- **MVC / REST:** Separação clara entre Controller, Service e Repository  
- **Dependency Injection:** Uso extensivo de `@Autowired`  
- **Strategy Pattern:** Implementações múltiplas de LLM services  
- **Repository Pattern:** Abstração de acesso a dados  
- **DTO Pattern:** Separação entre entidades e objetos de transferência  

---

## 🔐 Segurança

- Autenticação baseada em sessão HTTP (não JWT)  
- Senhas criptografadas com **BCrypt**  
- **CSRF** desabilitado (comum em APIs REST)  
- Validação de **ownership** (usuário só acessa suas próprias lives)  
- CORS configurado para origens específicas  

## Stack Tecnológica
```
Backend Framework: Spring Boot 3.4.5
Linguagem: Java 17
Build Tool: Maven
Banco de Dados: PostgreSQL
ORM: Spring Data JPA (Hibernate)
Segurança: Spring Security
Cliente HTTP: OkHttp 4.12.0
Serialização JSON: Jackson 2.15.2
Documentação: SpringDoc OpenAPI 2.8.8
Gerenciamento de Variáveis: dotenv-java 3.2.0
```

### Visão Geral da Arquitetura

<p align="center">
  <img width="700" alt="image" src="https://github.com/user-attachments/assets/7dad90eb-7f06-4d50-bc61-b1c4ff11442a" />
</p>

---

## Estrutura de Pacotes

```
br.com.wtd.liveinsights
├── config
│   ├── CorsConfig              # Configuração CORS
│   └── SecurityConfig          # Segurança e autenticação
├── controller
│   ├── AuthController          # Registro e login
│   ├── LiveController          # CRUD de lives e tags
│   ├── CommentsController      # Consulta de comentários
│   └── LiveAnalysisController  # Controle de análise
├── dto
│   ├── AuthRequest
│   └── ErrorResponse
├── model
│   ├── User                    # Usuário do sistema
│   ├── Live                    # Transmissão ao vivo
│   ├── Tag                     # Categorização
│   ├── CommentsInfo            # Análises armazenadas
│   ├── Sentiment               # Enum de sentimentos
│   ├── Interaction             # Enum de interações
│   └── [DTOs de APIs externas]
├── repository
│   ├── UserRepository
│   ├── LiveRepository
│   ├── TagRepository
│   └── CommentsRepository
├── service
│   ├── interfaces
│   │   └── [Interfaces de serviços]
│   ├── UserDetailsServiceImpl  # Autenticação
│   ├── LiveAnalysisService     # Orquestração de análise
│   ├── ScheduledAnalysisTask   # Coleta periódica
│   ├── FetchLiveComments       # Busca comentários
│   ├── CheckLiveActivity       # Verifica status da live
│   ├── LLMService              # Classe base para LLMs
│   ├── OpenAiService           # Integração OpenAI
│   ├── GeminiService           # Integração Gemini
│   ├── GroqService             # Integração Groq
│   ├── ConsumeApi              # Cliente HTTP genérico
│   └── ConvertData             # Serialização JSON
├── GlobalExceptionHandler      # Tratamento de erros
└── OpenApiConfig               # Configuração Swagger

---

## Controllers

**AuthController** (`/auth`)
- `POST /register` - Cadastro de usuários
- `POST /login` - Login com criação de sessão

**LiveController** (`/lives`)
- `GET /lives` - Lista lives do usuário
- `POST /lives` - Cria nova live (com tag)
- `PUT /lives/{liveId}` - Atualiza título
- `PUT /lives/{liveId}/tag` - Atualiza tag
- `DELETE /lives/{liveId}` - Remove live
- `GET /tags` - Lista todas as tags (nested controller)

**CommentsController** (`/comments`)
- `GET /comments/{liveId}` - Retorna análises da live

**LiveAnalysisController** (`/live`)
- `POST /live/start/{liveId}` - Inicia análise
- `POST /live/stop` - Para análise
- `GET /live/status` - Status atual

---

## Services

**Autenticação:**
- **UserDetailsServiceImpl**: Carrega dados do usuário para Spring Security

**Análise de Lives:**
- **LiveAnalysisService**: Orquestra todo processo de análise
- **ScheduledAnalysisTask**: Executa coleta periódica (a cada 5 min)
- **FetchLiveComments**: Busca comentários da API externa
- **CheckLiveActivity**: Verifica se live está ativa

**Integração com LLMs:**
- **LLMService** (abstrata): Classe base com lógica comum
- **OpenAiService**: Integração com GPT-4
- **GeminiService**: Integração com Gemini Pro
- **GroqService**: Integração com Llama2/Mixtral

**Utilitários:**
- **ConsumeApi**: Cliente HTTP com OkHttp
- **ConvertData**: Serialização JSON com Jackson

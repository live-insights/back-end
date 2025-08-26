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

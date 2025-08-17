<div align="center">
 <h1>Simulação de Loja Virtual com Visões de Cliente e Administrador</h1>
<p>
   <img src="https://img.shields.io/badge/Java-21%2B-blue?style=for-the-badge&logo=java" alt="Java 21+">
   <img src="https://img.shields.io/badge/JavaFX-Framework-orange?style=for-the-badge&logo=openjfx" alt="JavaFX">
   <img src="https://img.shields.io/badge/Status-Concluído-green?style=for-the-badge" alt="Status">
 </p>
</div>

<p align="center">
 Um projeto em Java com foco em Orientação a Objetos para simular um e-commerce funcional, separando as responsabilidades e visões entre usuários finais (clientes) e administradores.
</p>

---

### **Equipe**

<ul>
 <li>Alison Andrade Alves</li>
 <li>José Arthur de Araújo Almeida</li>
 <li>Ruan Gouveia dos Santos</li>
</ul>

---

### **Índice**

1. [Objetivos Principais](#objetivos-principais)
2. [Funcionalidades Planejadas](#funcionalidades-planejadas)
3. [Estrutura do Projeto](#estrutura-do-projeto)
4. [Tecnologias Utilizadas](#tecnologias-utilizadas)
5. [Como Executar o Projeto](#como-executar-o-projeto)
6. [Como criar o excutável JAR](#como-criar-o-executável-jar)

---

### **Objetivos Principais**

<ul>
 <li>
   <strong>Modelagem Orientada a Objetos:</strong> Implementar as entidades de uma loja virtual (Produtos, Clientes, Carrinho de Compras, Pedidos), definindo seus atributos e comportamentos, e estabelecendo os relacionamentos entre elas.
 </li>
 <li>
   <strong>Interface Gráfica Interativa:</strong> Desenvolver interfaces de usuário distintas para clientes e administradores utilizando uma biblioteca gráfica Java, proporcionando uma experiência intuitiva para cada tipo de usuário.
 </li>
 <li>
   <strong>Visão do Cliente:</strong> Permitir que os clientes naveguem por um catálogo de produtos, visualizem detalhes dos produtos, adicionem itens a um carrinho de compras, visualizem e modifiquem o carrinho, e simulem a finalização de um pedido.
 </li>
 <li>
   <strong>Visão do Administrador:</strong> Fornecer funcionalidades para o gerenciamento do catálogo de produtos, incluindo o cadastro, edição e exclusão de produtos.
 </li>
 <li>
   <strong>Separação de Acesso:</strong> Implementar mecanismos para garantir que os clientes não tenham acesso às funcionalidades administrativas, utilizando interfaces separadas e um sistema básico de login para administradores.
 </li>
</ul>

---

### **Funcionalidades Planejadas**
<br>

#### **Visão do Cliente**

<ul>
 <li>Exibição de um catálogo de produtos (com nome, preço, imagem e breve descrição).</li>
 <li>Adição de produtos ao carrinho de compras com especificação de quantidade.</li>
 <li>Visualização do carrinho de compras com a possibilidade de alterar quantidades e remover itens.</li>
 <li>Cálculo do valor total do carrinho.</li>
 <li>Simulação do processo de finalização de pedido (coleta de informações básicas).</li>
</ul>
<br>

#### **Visão do Administrador**

<ul>
 <li>Tela de login para acesso à área administrativa.</li>
 <li>Listagem de produtos cadastrados.</li>
 <li>Formulário para cadastro de novos produtos.</li>
 <li>Formulário para edição de produtos existentes.</li>
 <li>Funcionalidade para exclusão de produtos.</li>
</ul>

---

### **Estrutura do Projeto**
O projeto segue uma estrutura modular organizada da seguinte forma:


```text
simulador-loja-poo/
│
├── src/main/
│   ├── java/br/edu/ifpb/lojavirtual/
│   │   ├── controller/   # Controladores que gerenciam a interação View-Model
│   │   ├── dao/          # Camada de Acesso a Dados (Data Access Objects)
│   │   ├── model/        # Classes de modelo/entidades.
│   │   ├── pagamento/    # Lógica de processamento de pagamentos
│   │   ├── service/      # Camada de serviços e lógica de negócios
│   │   ├── util/         # Classes utilitárias
│   │   └── MainApp.java  # Ponto de entrada da aplicação
│   │
│   └── resources/br/edu/ifpb/lojavirtual/
│       ├── imagens/      # Imagens utilizadas na interface gráfica
│       └── view/         # Arquivos da interface (telas, componentes visuais)
│
├── .gitignore
├── build.gradle.kts      # Arquivo de configuração e dependências do Gradle
├── loja_virtual.db       # Banco de dados SQLite da aplicação
└── README.md
```
### **Tecnologias Utilizadas**

<ul>
 <li><strong>Linguagem:</strong> Java</li>
 <li><strong>Interface Gráfica:</strong> JavaFX</li>
 <li><strong>IDE:</strong>  IntelliJ IDEA</li>
 <li><strong>Versionamento de Código:</strong> GitLab</li>
 <li><strong>Criação de Telas:</strong> Scene Builder</li>

</ul>

---

### **Como Executar o Projeto**

#### **Pré-requisitos:**
*   Java Development Kit (JDK) 21 ou superior.
*   Uma IDE Java como <a href="https://www.eclipse.org/">Eclipse</a> ou <a href="https://www.jetbrains.com/idea/">IntelliJ IDEA</a>.


#### **Passo a passo - Executando pela interface da IDE:**

1. **Clone o repositório:**
   ```sh
   git clone https://gitlab.com/projeto-poo1/simulador-loja-poo.git
   ```
2.  **Abra o projeto** na sua IDE de preferência.
3.  **Localize a classe principal** (`MainApp`).
4.  **Execute o projeto** para iniciar a aplicação.

#### **Passo a passo - Executando pelo terminal:**
1. **Clone o repositório:**
   ```sh
   git clone https://gitlab.com/projeto-poo1/simulador-loja-poo.git
   ```
2.  **Abra o terminal** na raiz do projeto.
3. **Execute** o seguinte comando:
   ```sh
    ./gradlew run
   ```
### **Como criar o executável JAR**
1. **Mude** para a branch `criando-jar`:
   1. Pelo terminal:
      ```sh
      git checkout criando-jar
      ```
   2. Ou pela interface da IDE, selecionando a branch `criando-jar`.
2. **Abra o terminal** na raiz do projeto.
3. **Execute** o seguinte comando:
   ```sh
    ./gradlew shadowJar
   ```
4. **Localize o arquivo JAR** gerado em `build/libs/simulador-loja-poo-1.0.0.jar`.
5. **Execute o JAR** com o comando:
   ```sh
   java -jar build/libs/simulador-loja-poo-1.0.0.jar
   ```


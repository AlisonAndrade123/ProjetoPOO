<div align="center">
 <h1>Simulação de Loja Virtual com Visões de Cliente e Administrador</h1>
</div>


<p align="center">
 Um projeto em Java com foco em Orientação a Objetos para simular um e-commerce funcional, separando as responsabilidades e visões entre usuários finais (clientes) e administradores.
</p>


---


### 👥 **Equipe**


<ul>
 <li>Alison Andrade Alves</li>
 <li>José Arthur de A. Almeida</li>
 <li>Ruan Gouveia dos Santos</li>
</ul>


---


### 📋 **Índice**


<ol>
 <li><a href="#-objetivos-principais">Objetivos Principais</a></li>
 <li><a href="#-funcionalidades-planejadas">Funcionalidades Planejadas</a></li>
 <li><a href="#-estrutura-do-projeto">Estrutura do Projeto</a></li>
 <li><a href="#️-tecnologias-utilizadas">Tecnologias Utilizadas</a></li>
 <li><a href="#-como-executar-o-projeto">Como Executar o Projeto</a></li>
</ol>


---


### 🎯 **Objetivos Principais**


<ul>
 <li>
   <strong>Modelagem Orientada a Objetos:</strong> Implementar as entidades chave de uma loja virtual (Produtos, Clientes, Carrinho de Compras, Pedidos), definindo seus atributos e comportamentos, e estabelecendo os relacionamentos entre elas.
 </li>
 <li>
   <strong>Interface Gráfica Interativa:</strong> Desenvolver interfaces de usuário distintas para clientes e administradores utilizando uma biblioteca gráfica Java, proporcionando uma experiência intuitiva para cada tipo de usuário.
 </li>
 <li>
   <strong>Visão do Cliente:</strong> Permitir que os clientes naveguem por um catálogo de produtos, visualizem detalhes dos produtos, adicionem itens a um carrinho de compras, visualizem e modifiquem o carrinho, e simulem a finalização de um pedido.
 </li>
 <li>
   <strong>Visão do Administrador:</strong> Fornecer funcionalidades para o gerenciamento do catálogo de produtos, incluindo o cadastro, edição e exclusão de produtos. Adicionalmente, poderá incluir o gerenciamento de categorias e a visualização (simulada) de pedidos realizados pelos clientes.
 </li>
 <li>
   <strong>Separação de Acesso:</strong> Implementar mecanismos para garantir que os clientes não tenham acesso às funcionalidades administrativas, utilizando interfaces separadas e um sistema básico de login para administradores.
 </li>
</ul>


---


### ✨ **Funcionalidades Planejadas**


<br>


#### 🛍️ **Visão do Cliente**


<ul>
 <li>Exibição de um catálogo de produtos (com nome, preço, imagem e breve descrição).</li>
 <li>Visualização detalhada de produtos (descrição completa e atributos).</li>
 <li>Adição de produtos ao carrinho de compras com especificação de quantidade.</li>
 <li>Visualização do carrinho de compras com a possibilidade de alterar quantidades e remover itens.</li>
 <li>Cálculo do valor total do carrinho.</li>
 <li>Simulação do processo de finalização de pedido (coleta de informações básicas).</li>
</ul>


<br>


#### ⚙️ **Visão do Administrador**


<ul>
 <li>Tela de login para acesso à área administrativa.</li>
 <li>Listagem de produtos cadastrados (com informações básicas).</li>
 <li>Formulário para cadastro de novos produtos.</li>
 <li>Formulário para edição de produtos existentes.</li>
 <li>Funcionalidade para exclusão de produtos.</li>
</ul>


---


### 📂 **Estrutura do Projeto**
O projeto segue uma estrutura modular organizada da seguinte forma:


```text
simulador-loja-poo/
│
├── src/main/
│   ├── java/org/example/pooprojeto/
│   │   ├── controller/   # Controladores que gerenciam a interação View-Model
│   │   ├── dao/          # Camada de Acesso a Dados (Data Access Objects)
│   │   ├── model/        # Classes de modelo/entidades (Produto, Usuário.)
│   │   ├── pagamento/    # Lógica de processamento de pagamentos
│   │   ├── service/      # Camada de serviços e lógica de negócios
│   │   ├── util/         # Classes utilitárias
│   │   └── MainApp.java  # Ponto de entrada da aplicação
│   │
│   └── resources/org/example/pooprojeto/
│       ├── imagens/      # Imagens utilizadas na interface gráfica
│       └── view/         # Arquivos da interface (telas, componentes visuais)
│
├── .gitignore
├── build.gradle.kts      # Arquivo de configuração e dependências do Gradle
├── loja_virtual.db       # Banco de dados SQLite da aplicação
└── README.md
```
### 🛠️ **Tecnologias Utilizadas**


<ul>
 <li><strong>Linguagem:</strong> Java</li>
 <li><strong>Interface Gráfica:</strong> JavaFX</li>
 <li><strong>IDE:</strong>  IntelliJ IDEA</li>
</ul>


---


### 🚀 **Como Executar o Projeto**


#### **Pré-requisitos:**
*   Java Development Kit (JDK) 11 ou superior.
*   Uma IDE Java como <a href="https://www.eclipse.org/">Eclipse</a> ou <a href="https://www.jetbrains.com/idea/">IntelliJ IDEA</a>.


#### **Passo a passo:**


1.  **Clone o repositório:**
   ```sh
   git clone https://gitlab.com/projeto-poo1/simulador-loja-poo.git
   ```


2.  **Abra o projeto** na sua IDE de preferência.


3.  **Localize a classe principal** (`MainApp`).


4.  **Execute o projeto** para iniciar a aplicação.


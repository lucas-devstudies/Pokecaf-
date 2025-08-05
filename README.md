# ☕ PokéCafé – Sistema de Vendas

Este projeto foi desenvolvido como parte da disciplina de **Programação Orientada a Objetos** no curso de **Engenharia de Software** do **IFAM – Instituto Federal do Amazonas**.  
A proposta foi criar um sistema de vendas simples para uma **cafeteria temática de Pokémon**, utilizando **Java com Swing**, **NetBeans** e **MySQL**.

---

## 📌 Descrição Geral

O PokéCafé é uma aplicação de desktop que permite:

- Cadastro de produtos com nome, ingredientes e valor.
- Registro de vendas com nome do cliente, forma de pagamento e tipo de venda.
- Inserção de itens em cada venda.
- Consulta de vendas por período e por status.
- Gerenciamento de usuários do sistema.

A interface gráfica foi construída com componentes Swing utilizando a interface de arrastar e soltar do NetBeans.

---

## 🗃️ Estrutura do Banco de Dados

O sistema utiliza um banco de dados MySQL com as seguintes tabelas principais:

- `Produto`
- `Usuarios`
- `Venda`
- `ItemVenda`

As tabelas estão relacionadas por meio de chaves estrangeiras, garantindo integridade entre produtos, vendas e itens vendidos.

---

## 💻 Tecnologias Utilizadas

- **Java** (Swing)
- **MySQL**
- **NetBeans IDE**
- **JDBC**

---

## ⚠️ Observações Importantes

> Por conta do tempo limitado durante as aulas, **não foi possível implementar um tratamento completo de erros** e validações mais avançadas.  
> Ainda assim, o sistema está funcional e cumpre todos os requisitos propostos inicialmente.

---

## 🧠 Possibilidades de Melhorias Futuras

- Melhor tratamento de exceções.
- Hash de senhas e autenticação segura.
- Geração de relatórios (PDF, Excel).
- Layout mais moderno e responsivo.
- Integração com banco de dados online.

---

## 🧑‍🎓 Autor

Desenvolvido por Lucas Ferreira - estudante de Engenharia de Software do IFAM, como forma de aplicar os conhecimentos adquiridos em Programação Orientada a Objetos.

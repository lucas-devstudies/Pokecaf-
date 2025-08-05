/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokecafe.model.dao;

import com.pokecafe.connection.ConnectionFactory;
import com.pokecafe.model.bean.ProdutoCarrinho;
import com.pokecafe.model.bean.Venda;
import com.pokecafe.view.AlertCompraFinalizada;
import com.pokecafe.view.AlertErro;
import com.pokecafe.view.AlertSucesso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author lucas
 */
public class VendaDAO {
    public void create(Venda v){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItem = null;
        ResultSet rs = null;
        try {
            con.setAutoCommit(false);
            
            //Inserindo na tabela venda
            String sqlVenda = "INSERT INTO Venda (nome_cliente, forma_pagamento, tipo_venda, statusVenda) VALUES (?, ?, ?, ?)";
            stmtVenda = con.prepareStatement(sqlVenda, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtVenda.setString(1, v.getNomeCliente());
            stmtVenda.setString(2, v.getFormaPagamento());
            stmtVenda.setString(3, v.getTipoVenda());
            stmtVenda.setString(4, v.getStatus());

            stmtVenda.executeUpdate();

            // Pegar o ID gerado
            rs = stmtVenda.getGeneratedKeys();
            int vendaId = 0;
            if (rs.next()) {
                vendaId = rs.getInt(1);
            }

            // 2. Inserir os itens da venda
            String sqlItem = "INSERT INTO ItemVenda (venda_id, produto_id, quantidade, total,observacao) VALUES (?, ?, ?, ?, ?)";
            stmtItem = con.prepareStatement(sqlItem);

            for (ProdutoCarrinho pc : v.getListaProdutos()) {
                stmtItem.setInt(1, vendaId);
                stmtItem.setInt(2, pc.getId()); // produto_id
                stmtItem.setInt(3, pc.getQuantidade());
                stmtItem.setDouble(4, pc.getPreco() * pc.getQuantidade());
                stmtItem.setString(5, pc.getObservacao());
                stmtItem.addBatch(); // melhora performance
            }

            stmtItem.executeBatch(); // executa todos os inserts de uma vez
            con.commit(); // finaliza transação

            new AlertCompraFinalizada(null,String.format("Código: %d", vendaId)).setVisible(true);


        } catch (SQLException ex) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException e) {
                new AlertErro(null,"Erro ao fazer rollback: " + e).setVisible(true);
            }
            new AlertErro(null,"Erro ao salvar venda: " + ex).setVisible(true);

        } finally {
            ConnectionFactory.closeConnection(con, stmtVenda, rs);
            ConnectionFactory.closeConnection(null, stmtItem);
        }
    }
    public ArrayList<Venda> readIncompletos() {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItens = null;
        ResultSet rsVenda = null;
        ResultSet rsItens = null;
        ArrayList<Venda> vendas = new ArrayList<>();

        try {
            // 1. Buscar todas as vendas que não estejam prontas
            String sqlVenda = "SELECT * FROM Venda ORDER BY data DESC";
            stmtVenda = con.prepareStatement(sqlVenda);
            rsVenda = stmtVenda.executeQuery();

            while (rsVenda.next()) {
                Venda venda = new Venda();

                venda.setId(rsVenda.getInt("id"));
                venda.setNomeCliente(rsVenda.getString("nome_cliente"));
                venda.setFormaPagamento(rsVenda.getString("forma_pagamento"));
                venda.setTipoVenda(rsVenda.getString("tipo_venda"));
                venda.setStatus(rsVenda.getString("statusVenda"));
                venda.setData(rsVenda.getTimestamp("data")); // Date ou Timestamp, dependendo

                // 2. Buscar os itens dessa venda
                String sqlItens = "SELECT p.id, p.nome, p.ingredientes, iv.quantidade, iv.total,iv.observacao " +
                                  "FROM ItemVenda iv " +
                                  "JOIN Produto p ON iv.produto_id = p.id " +
                                  "WHERE iv.venda_id = ?";

                stmtItens = con.prepareStatement(sqlItens);
                stmtItens.setInt(1, venda.getId());
                rsItens = stmtItens.executeQuery();

                ArrayList<ProdutoCarrinho> listaProdutos = new ArrayList<>();
                while (rsItens.next()) {
                    ProdutoCarrinho pc = new ProdutoCarrinho();
                    pc.setId(rsItens.getInt("id"));
                    pc.setNome(rsItens.getString("nome"));
                    pc.setIngredientes(rsItens.getString("ingredientes"));
                    pc.setQuantidade(rsItens.getInt("quantidade"));
                    pc.setPreco(rsItens.getDouble("total") / pc.getQuantidade()); // calcula preço unitário
                    pc.setObservacao(rsItens.getString("observacao"));
                    listaProdutos.add(pc);
                }

                venda.setListaProdutos(listaProdutos);

                // Fechar o ResultSet de itens antes do próximo loop
                rsItens.close();
                stmtItens.close();

                vendas.add(venda);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmtVenda, rsVenda);
            // stmtItens e rsItens já fechados dentro do loop
        }

        return vendas;
    }
    //usar pras vendas finalizadas:
    public ArrayList<Venda> readCompletos() {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItens = null;
        ResultSet rsVenda = null;
        ResultSet rsItens = null;
        ArrayList<Venda> vendas = new ArrayList<>();

        try {
            // 1. Buscar todas as vendas que não estejam prontas
            String sqlVenda = "SELECT * FROM Venda WHERE statusVenda = 'Pronto'";
            stmtVenda = con.prepareStatement(sqlVenda);
            rsVenda = stmtVenda.executeQuery();

            while (rsVenda.next()) {
                Venda venda = new Venda();

                venda.setId(rsVenda.getInt("id"));
                venda.setNomeCliente(rsVenda.getString("nome_cliente"));
                venda.setFormaPagamento(rsVenda.getString("forma_pagamento"));
                venda.setTipoVenda(rsVenda.getString("tipo_venda"));
                venda.setStatus(rsVenda.getString("statusVenda"));
                venda.setData(rsVenda.getTimestamp("data")); // Date ou Timestamp, dependendo
                // 2. Buscar os itens dessa venda
                String sqlItens = "SELECT p.id, p.nome, p.ingredientes, iv.quantidade, iv.total,iv.observacao " +
                                  "FROM ItemVenda iv " +
                                  "JOIN Produto p ON iv.produto_id = p.id " +
                                  "WHERE iv.venda_id = ?";
                
                stmtItens = con.prepareStatement(sqlItens);
                stmtItens.setInt(1, venda.getId());
                rsItens = stmtItens.executeQuery();

                ArrayList<ProdutoCarrinho> listaProdutos = new ArrayList<>();
                double total = 0;
                while (rsItens.next()) {
                    ProdutoCarrinho pc = new ProdutoCarrinho();
                    pc.setId(rsItens.getInt("id"));
                    pc.setNome(rsItens.getString("nome"));
                    pc.setIngredientes(rsItens.getString("ingredientes"));
                    pc.setQuantidade(rsItens.getInt("quantidade"));
                    pc.setPreco(rsItens.getDouble("total") / pc.getQuantidade()); // calcula preço unitário
                    pc.setObservacao(rsItens.getString("observacao"));
                    total += pc.getPreco()*pc.getQuantidade();
                    listaProdutos.add(pc);
                }
                venda.setTotal(total);
                venda.setListaProdutos(listaProdutos);

                // Fechar o ResultSet de itens antes do próximo loop
                rsItens.close();
                stmtItens.close();

                vendas.add(venda);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmtVenda, rsVenda);
            // stmtItens e rsItens já fechados dentro do loop
        }

        return vendas;
    }
    public ArrayList<Venda> readCompletosPorData(String inicio, String fim){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItens = null;
        ResultSet rsVenda = null;
        ResultSet rsItens = null;
        ArrayList<Venda> vendas = new ArrayList<>();

        try {
            // 1. Buscar todas as vendas que não estejam prontas
            String sqlVenda = "SELECT * FROM Venda WHERE statusVenda = 'Pronto'"
                    +"AND data BETWEEN ? AND ?";
            stmtVenda = con.prepareStatement(sqlVenda);
            stmtVenda = con.prepareStatement(sqlVenda);
            stmtVenda.setString(1, inicio);
            stmtVenda.setString(2,fim);
            rsVenda = stmtVenda.executeQuery();

            while (rsVenda.next()) {
                Venda venda = new Venda();

                venda.setId(rsVenda.getInt("id"));
                venda.setNomeCliente(rsVenda.getString("nome_cliente"));
                venda.setFormaPagamento(rsVenda.getString("forma_pagamento"));
                venda.setTipoVenda(rsVenda.getString("tipo_venda"));
                venda.setStatus(rsVenda.getString("statusVenda"));
                venda.setData(rsVenda.getTimestamp("data")); // Date ou Timestamp, dependendo
                // 2. Buscar os itens dessa venda
                String sqlItens = "SELECT p.id, p.nome, p.ingredientes, iv.quantidade, iv.total,p.imagem,,iv.observacao " +
                                  "FROM ItemVenda iv " +
                                  "JOIN Produto p ON iv.produto_id = p.id " +
                                  "WHERE iv.venda_id = ?";
                
                stmtItens = con.prepareStatement(sqlItens);
                stmtItens.setInt(1, venda.getId());
                rsItens = stmtItens.executeQuery();

                ArrayList<ProdutoCarrinho> listaProdutos = new ArrayList<>();
                double total = 0;
                while (rsItens.next()) {
                    ProdutoCarrinho pc = new ProdutoCarrinho();
                    pc.setId(rsItens.getInt("id"));
                    pc.setNome(rsItens.getString("nome"));
                    pc.setIngredientes(rsItens.getString("ingredientes"));
                    pc.setQuantidade(rsItens.getInt("quantidade"));
                    pc.setPreco(rsItens.getDouble("total") / pc.getQuantidade()); // calcula preço unitário
                    pc.setImagem(rsItens.getString("imagem"));
                    pc.setObservacao(rsItens.getString("observacao"));
                    total += pc.getPreco()*pc.getQuantidade();
                    listaProdutos.add(pc);
                }
                venda.setTotal(total);
                venda.setListaProdutos(listaProdutos);

                // Fechar o ResultSet de itens antes do próximo loop
                rsItens.close();
                stmtItens.close();

                vendas.add(venda);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmtVenda, rsVenda);
            // stmtItens e rsItens já fechados dentro do loop
        }

        return vendas;
    }
    //usar nas listas
    public ArrayList<Venda> readId(int id) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItens = null;
        ResultSet rsVenda = null;
        ResultSet rsItens = null;

        ArrayList<Venda> listaVendas = new ArrayList<>();

        try {
            String sqlVenda = "SELECT * FROM Venda WHERE id = %?%";
            stmtVenda = con.prepareStatement(sqlVenda);
            stmtVenda.setInt(1, id);
            rsVenda = stmtVenda.executeQuery();

            while (rsVenda.next()) {
                Venda venda = new Venda();
                venda.setId(rsVenda.getInt("id"));
                venda.setNomeCliente(rsVenda.getString("nome_cliente"));
                venda.setFormaPagamento(rsVenda.getString("forma_pagamento"));
                venda.setTipoVenda(rsVenda.getString("tipo_venda"));
                venda.setStatus(rsVenda.getString("statusVenda"));
                venda.setData(rsVenda.getTimestamp("data"));

                // Buscar produtos dessa venda
                String sqlItens = "SELECT p.id, p.nome, p.ingredientes, iv.quantidade, iv.total, p.imagem,iv.observacao " +
                                  "FROM ItemVenda iv " +
                                  "JOIN Produto p ON iv.produto_id = p.id " +
                                  "WHERE iv.venda_id = ?";
                stmtItens = con.prepareStatement(sqlItens);
                stmtItens.setInt(1, venda.getId());
                rsItens = stmtItens.executeQuery();

                ArrayList<ProdutoCarrinho> listaProdutos = new ArrayList<>();
                while (rsItens.next()) {
                    ProdutoCarrinho pc = new ProdutoCarrinho();
                    pc.setId(rsItens.getInt("id"));
                    pc.setNome(rsItens.getString("nome"));
                    pc.setIngredientes(rsItens.getString("ingredientes"));
                    pc.setQuantidade(rsItens.getInt("quantidade"));
                    pc.setPreco(rsItens.getDouble("total") / pc.getQuantidade());
                    pc.setImagem(rsItens.getString("imagem"));
                    pc.setObservacao(rsItens.getString("observacao"));

                    listaProdutos.add(pc);
                }

                venda.setListaProdutos(listaProdutos);
                listaVendas.add(venda);

                // Fechar ResultSet e Statement de itens após cada venda
                rsItens.close();
                stmtItens.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmtVenda, rsVenda);
            // stmtItens e rsItens já são fechados dentro do loop
        }

        return listaVendas;
    }
    //usar pra pegar uma específica
    public Venda readById(int id) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItens = null;
        ResultSet rsVenda = null;
        ResultSet rsItens = null;
        Venda venda = null;

        try {
            String sqlVenda = "SELECT * FROM Venda WHERE id = ?";
            stmtVenda = con.prepareStatement(sqlVenda);
            stmtVenda.setInt(1, id);
            rsVenda = stmtVenda.executeQuery();

            if (rsVenda.next()) {
                venda = new Venda();
                venda.setId(rsVenda.getInt("id"));
                venda.setNomeCliente(rsVenda.getString("nome_cliente"));
                venda.setFormaPagamento(rsVenda.getString("forma_pagamento"));
                venda.setTipoVenda(rsVenda.getString("tipo_venda"));
                venda.setStatus(rsVenda.getString("statusVenda"));
                venda.setData(rsVenda.getTimestamp("data"));

                String sqlItens = "SELECT p.id, p.nome, p.ingredientes, iv.quantidade, iv.total,p.imagem,iv.observacao " +
                                  "FROM ItemVenda iv " +
                                  "JOIN Produto p ON iv.produto_id = p.id " +
                                  "WHERE iv.venda_id = ?";

                stmtItens = con.prepareStatement(sqlItens);
                stmtItens.setInt(1, venda.getId());
                rsItens = stmtItens.executeQuery();

                ArrayList<ProdutoCarrinho> listaProdutos = new ArrayList<>();
                while (rsItens.next()) {
                    ProdutoCarrinho pc = new ProdutoCarrinho();
                    pc.setId(rsItens.getInt("id"));
                    pc.setNome(rsItens.getString("nome"));
                    pc.setIngredientes(rsItens.getString("ingredientes"));
                    pc.setQuantidade(rsItens.getInt("quantidade"));
                    pc.setPreco(rsItens.getDouble("total") / pc.getQuantidade());
                    pc.setImagem(rsItens.getString("imagem"));
                    pc.setObservacao(rsItens.getString("observacao"));

                    listaProdutos.add(pc);
                }

                venda.setListaProdutos(listaProdutos);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmtVenda, rsVenda);
            ConnectionFactory.closeConnection(null, stmtItens, rsItens);
        }

        return venda;
    }
    public ArrayList<Venda> read() {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmtVenda = null;
        PreparedStatement stmtItens = null;
        ResultSet rsVenda = null;
        ResultSet rsItens = null;

        ArrayList<Venda> listaVendas = new ArrayList<>();

        try {
            String sqlVenda = "SELECT * FROM Venda";
            stmtVenda = con.prepareStatement(sqlVenda);
            rsVenda = stmtVenda.executeQuery();

            while (rsVenda.next()) {
                Venda venda = new Venda();
                venda.setId(rsVenda.getInt("id"));
                venda.setNomeCliente(rsVenda.getString("nome_cliente"));
                venda.setFormaPagamento(rsVenda.getString("forma_pagamento"));
                venda.setTipoVenda(rsVenda.getString("tipo_venda"));
                venda.setStatus(rsVenda.getString("statusVenda"));
                venda.setData(rsVenda.getTimestamp("data"));

                // Buscar produtos dessa venda
                String sqlItens = "SELECT p.id, p.nome, p.ingredientes, iv.quantidade, iv.total, p.imagem,iv.observacao " +
                                  "FROM ItemVenda iv " +
                                  "JOIN Produto p ON iv.produto_id = p.id " +
                                  "WHERE iv.venda_id = ?";
                stmtItens = con.prepareStatement(sqlItens);
                stmtItens.setInt(1, venda.getId());
                rsItens = stmtItens.executeQuery();

                ArrayList<ProdutoCarrinho> listaProdutos = new ArrayList<>();
                while (rsItens.next()) {
                    ProdutoCarrinho pc = new ProdutoCarrinho();
                    pc.setId(rsItens.getInt("id"));
                    pc.setNome(rsItens.getString("nome"));
                    pc.setIngredientes(rsItens.getString("ingredientes"));
                    pc.setQuantidade(rsItens.getInt("quantidade"));
                    pc.setPreco(rsItens.getDouble("total") / pc.getQuantidade());
                    pc.setImagem(rsItens.getString("imagem"));
                    pc.setObservacao(rsItens.getString("observacao"));

                    listaProdutos.add(pc);
                }

                venda.setListaProdutos(listaProdutos);
                listaVendas.add(venda);

                // Fechar ResultSet e Statement de itens após cada venda
                rsItens.close();
                stmtItens.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con, stmtVenda, rsVenda);
            // stmtItens e rsItens já são fechados dentro do loop
        }

        return listaVendas;
    }
    public void updateStatus(int vendaId, String novoStatus) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement("UPDATE Venda SET statusVenda = ? WHERE id = ?");
            stmt.setString(1, novoStatus);
            stmt.setInt(2, vendaId);
            stmt.executeUpdate();
            new AlertSucesso(null, "Alterado com sucesso").setVisible(true);
        } catch (SQLException ex) {
            new AlertErro(null,"Erro ao atualizar status da venda: " + ex.getMessage()).setVisible(true);
            System.out.println("Erro ao atualizar status da venda: " + ex.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
    }



}

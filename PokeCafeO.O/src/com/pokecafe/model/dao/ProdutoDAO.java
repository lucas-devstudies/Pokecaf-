/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokecafe.model.dao;

import com.pokecafe.connection.ConnectionFactory;
import com.pokecafe.model.bean.Produto;
import com.pokecafe.view.AlertErro;
import com.pokecafe.view.AlertSucesso;
import java.awt.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.util.ArrayList;

/**
 *
 * @author lucas
 */
public class ProdutoDAO {
    public void create(Produto p){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement("INSERT INTO produto (nome,ingredientes,valor,imagem) VALUES (?,?,?,?)");
            
            stmt.setString(1,p.getNome());
            stmt.setString(2,p.getIngredientes());
            stmt.setDouble(3, p.getPreco());
            stmt.setString(4, p.getImagem());
            
            stmt.executeUpdate();
            
            new AlertSucesso(null,"Salvo com Sucesso").setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
            new AlertErro(null,"Erro ao Salvar: "+ex).setVisible(true);
        } finally{
            ConnectionFactory.closeConnection(con,stmt);
        }
    }
    public ArrayList<Produto> read(){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<Produto> produtos = new ArrayList<>();
        
        try {
            stmt = con.prepareStatement("SELECT *FROM produto");
            rs = stmt.executeQuery();
            
            while (rs.next()){
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setIngredientes(rs.getString("ingredientes"));
                produto.setPreco(rs.getDouble("valor"));
                produto.setImagem(rs.getString("imagem"));
                produtos.add(produto);       
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            ConnectionFactory.closeConnection(con,stmt,rs);
        }
        return produtos;
    }
    
    public void update(Produto p){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement("UPDATE produto set nome = ?, ingredientes=?, valor=?, imagem =? WHERE id = ?");
            
            stmt.setString(1,p.getNome());
            stmt.setString(2,p.getIngredientes());
            stmt.setDouble(3, p.getPreco());
            stmt.setString(4,p.getImagem());
            stmt.setInt(5,p.getId());
            
            stmt.executeUpdate();
            
            new AlertSucesso(null,"Atualizado com Sucesso").setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
            new AlertErro(null,"Erro ao Atualizar: "+ex).setVisible(true);
        } finally{
            ConnectionFactory.closeConnection(con,stmt);
        }
    }
    public Produto findById(int id){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        Produto p = new Produto();
        try {
            stmt = con.prepareStatement("select * from produto WHERE id = ?");
            stmt.setInt(1,id);            
            ResultSet produtoS = stmt.executeQuery();
             while (produtoS.next()) {
                    p.setId(produtoS.getInt("id"));
                    p.setNome(produtoS.getString("nome"));
                    p.setIngredientes(produtoS.getString("ingredientes"));
                    p.setPreco((double)Double.parseDouble(produtoS.getString("valor")));
                    p.setImagem(produtoS.getString("imagem"));
                }
             } 
        catch (SQLException ex) {
            new AlertErro(null,"Erro ao Atualizar: "+ex).setVisible(true);
        } finally{
            ConnectionFactory.closeConnection(con,stmt);
            return p;
        }
    }
    public void delete(Produto p){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement("DELETE FROM produto WHERE id = ?");
            stmt.setInt(1,p.getId());
            stmt.executeUpdate();
            
            new AlertSucesso(null,"Deletado com Sucesso").setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
            new AlertErro(null,"Erro ao Deletar: "+ex).setVisible(true);
        } finally{
            ConnectionFactory.closeConnection(con,stmt);
        }
    }
    public ArrayList<Produto> readForNome(String nome){
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<Produto> produtos = new ArrayList<>();
        
        try {
            stmt = con.prepareStatement("SELECT *FROM produto WHERE nome LIKE ?");
            stmt.setString(1, "%"+nome+"%");
            rs = stmt.executeQuery();
            
            while (rs.next()){
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setIngredientes(rs.getString("ingredientes"));
                produto.setPreco(rs.getDouble("valor"));
                produto.setImagem(rs.getString("imagem"));
                produtos.add(produto);       
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            ConnectionFactory.closeConnection(con,stmt,rs);
        }
        return produtos;
    }
}

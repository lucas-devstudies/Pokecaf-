/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokecafe.model.bean;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;

/**
 *
 * @author lucas
 */
public class Venda{
    
    private int id;
    private ArrayList<ProdutoCarrinho> listaProdutos;
    private String formaPagamento;
    private String tipoVenda;
    private String nomeCliente;
    private String status;
    private Timestamp data;
    private Double total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<ProdutoCarrinho> getListaProdutos() {
        return listaProdutos;
    }

    public void setListaProdutos(ArrayList<ProdutoCarrinho> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getTipoVenda() {
        return tipoVenda;
    }

    public void setTipoVenda(String tipoVenda) {
        this.tipoVenda = tipoVenda;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getData() {
        return data;
    }

    public void setData(Timestamp data) {
        this.data = data;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    
    
}

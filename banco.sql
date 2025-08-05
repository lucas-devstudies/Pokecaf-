create database pokecafe;
use pokecafe;

create Table Produto(
	id int auto_increment primary key,
    nome varchar(100) not null,
    ingredientes varchar(100) not null,
    valor double not null,
    imagem varchar (255) not null
);

create Table Usuarios(
	id int auto_increment primary key,
    nome varchar(100) not null,	
    senha varchar(100) not null 
);
CREATE TABLE Venda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_cliente VARCHAR(100) NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    tipo_venda VARCHAR(20) NOT NULL,
    statusVenda VARCHAR(20) NOT NULL,
    data DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE ItemVenda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    total DOUBLE NOT NULL,
    observacao VARCHAR (255),
    FOREIGN KEY (venda_id) REFERENCES Venda(id),
    FOREIGN KEY (produto_id) REFERENCES Produto(id)
);

CREATE DATABASE IF NOT EXISTS db_payMyBuddy;
USE db_payMyBuddy;

CREATE TABLE UserAccount (
    id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL
);

CREATE TABLE SenderRecipientConnection (
    id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    idSender INT NOT NULL,
    idRecipient INT NOT NULL,
    FOREIGN KEY (idSender) REFERENCES User(id),
    FOREIGN KEY (idRecipient) REFERENCES User(id)
);

CREATE TABLE Transaction (
    id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    idSender INT NOT NULL,
    idRecipient INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date DATETIME NOT NULL,
    description VARCHAR(100) NOT NULL,
    FOREIGN KEY (idSender) REFERENCES User(id),
    FOREIGN KEY (idRecipient) REFERENCES User(id)
);

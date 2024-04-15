CREATE DATABASE IF NOT EXISTS `payMyBuddy`;
USE `payMyBuddy`;

CREATE TABLE `user_account` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(50) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `first_name` VARCHAR(50) NOT NULL,
    `balance` DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `sender_recipient_connection` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `id_sender` INT NOT NULL,
    `id_recipient` INT NOT NULL,
    PRIMARY KEY (`ID`),
    CONSTRAINT `fk_connection_sender` FOREIGN KEY (`id_sender`) REFERENCES `user_account`(`id`),
    CONSTRAINT `fk_connection_recipient` FOREIGN KEY (`id_recipient`) REFERENCES `user_account`(`id`)
);

CREATE TABLE `transaction` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `id_sender` INT NOT NULL,
    `id_recipient` INT NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `date` DATETIME NOT NULL,
    `description` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_transaction_sender` FOREIGN KEY (`id_sender`) REFERENCES `user_account`(`id`),
    CONSTRAINT `fk_transaction_recipient` FOREIGN KEY (`id_recipient`) REFERENCES `user_account`(`id`)
);

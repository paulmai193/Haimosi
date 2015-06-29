DROP TABLE IF EXISTS `listtransview`;
CREATE VIEW `listtransview` AS SELECT `t`.`idtransaction` AS 'idtransaction', `t`.`quantity` AS 'quantity', `t`.`amount` AS 'amount', `t`.`time` AS 'time', `t`.`photo` AS 'photo', `t`.`status` AS 'status', `t`.`method` AS 'method', `i`.`name` AS 'name', `i`.`unit` AS 'unit' FROM `transaction` AS t LEFT JOIN `item` AS i ON `t`.`iditem` = `i`.`iditem`;
DROP TABLE IF EXISTS t_orders;
DROP TABLE IF EXISTS order_line_items;

CREATE TABLE `t_orders` (
                            `order_id` bigint NOT NULL AUTO_INCREMENT,
                            `order_number` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`order_id`)
);

CREATE TABLE `order_line_items` (
                                    `order_line_items_id` bigint NOT NULL AUTO_INCREMENT,
                                    `price` decimal(19,2) DEFAULT NULL,
                                    `quantity` int DEFAULT NULL,
                                    `sku_code` varchar(255) DEFAULT NULL,
                                    `order_id` bigint DEFAULT NULL,
                                    PRIMARY KEY (`order_line_items_id`),
                                    KEY `FK464467xtmx3fflaebpdd6tio` (`order_id`),
                                    CONSTRAINT `FK464467xtmx3fflaebpdd6tio` FOREIGN KEY (`order_id`) REFERENCES `t_orders` (`order_id`)
);
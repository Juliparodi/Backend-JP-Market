DROP TABLE IF EXISTS order_line_items;
DROP TABLE IF EXISTS t_orders;

CREATE TABLE t_orders (
                          order_id BIGINT NOT NULL AUTO_INCREMENT,
                          order_number VARCHAR(255) DEFAULT NULL,
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (order_id)
) ENGINE=InnoDB;

CREATE TABLE order_line_items (
                                  order_line_items_id BIGINT NOT NULL AUTO_INCREMENT,
                                  price DECIMAL(19,2) DEFAULT NULL,
                                  quantity INT DEFAULT NULL,
                                  sku_code VARCHAR(255) DEFAULT NULL,
                                  order_id BIGINT DEFAULT NULL,
                                  PRIMARY KEY (order_line_items_id),
                                  INDEX idx_order_id (order_id),
                                  CONSTRAINT fk_order_items_order
                                      FOREIGN KEY (order_id)
                                          REFERENCES t_orders (order_id)
                                          ON DELETE CASCADE
) ENGINE=InnoDB;
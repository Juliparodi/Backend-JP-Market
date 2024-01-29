package com.techmarket.productservice.db.migrations;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

@ChangeLog(order = "001")
public class DbChangeLog001 {

    @ChangeSet(order = "001", id = "hola", author = "hola")
    public void createAll() {

    }
}

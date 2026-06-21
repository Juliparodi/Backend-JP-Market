#!/bin/bash

mongosh <<EOF
use product-service

db.createUser({
  user: "julianparodi19",
  pwd: "$MONGO_JULIP_PASSWORD",
  roles: [
    { role: "readWrite", db: "product-service" }
  ]
})
EOF
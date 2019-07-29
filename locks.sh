#!/usr/bin/env bash

ls ~/db/accounting*
echo "H2 is locked , will attempt to remove it"
ls ~/db/accounting.lock.db
sudo chmod 777 $(ls ~/db/accounting.lock.db)
sudo rm $(ls ~/db/accounting.lock.db) --force
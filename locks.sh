#!/usr/bin/env bash

ls ~/anz*
echo "H2 is locked , will attempt to remove it"
ls ~/anzdb.lock.db
sudo chmod 777 $(ls ~/anzacc.lock.db)
sudo rm $(ls ~/~/anzacc.lock.db) --force
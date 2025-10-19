#!/bin/bash

pandoc -f markdown -t html -o ../privacy-notice.html privacy-notice.md --css=./style.css --embed-resources --standalone --metadata title="Privacy Notice"

pandoc -f markdown -t html -o ../terms-and-conditions.html terms-and-conditions.md --css=./style.css --embed-resources --standalone --metadata title="Terms & Conditions"

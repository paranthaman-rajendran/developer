#!/bin/bash

# File to encrypt
FILE="myfile.txt"

# Encrypted file
ENC_FILE="myfile.txt.enc"

# Password for encryption
PASSWORD="your_password"

# Encrypt the file using openssl
openssl enc -aes-256-cbc -salt -in $FILE -out $ENC_FILE -k $PASSWORD

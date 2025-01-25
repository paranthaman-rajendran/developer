#!/bin/bash

# Encrypted file
ENC_FILE="myfile.txt.enc"

# Decrypted file
DEC_FILE="myfile_decrypted.txt"

# Password for decryption
PASSWORD="your_password"

# Decrypt the file using openssl
openssl enc -d -aes-256-cbc -in $ENC_FILE -out $DEC_FILE -k $PASSWORD

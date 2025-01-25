Using gpg:

#!/bin/bash

# File to encrypt
FILE="myfile.txt"

# Encrypted file
ENC_FILE="myfile.txt.gpg"

# Encrypt the file using gpg
gpg --symmetric --cipher-algo AES256 --output $ENC_FILE $FILE

Decrypting a file:

#!/bin/bash

# Encrypted file
ENC_FILE="myfile.txt.gpg"

# Decrypted file
DEC_FILE="myfile_decrypted.txt"

# Decrypt the file using gpg
gpg --output $DEC_FILE --decrypt $ENC_FILE
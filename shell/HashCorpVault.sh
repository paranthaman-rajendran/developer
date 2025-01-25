################# Begining of shell script 
#!/bin/bash

# Vault address and token
VAULT_ADDR="http://127.0.0.1:8200"
VAULT_TOKEN="s.your_token_here"

# Path to the secret in Vault
SECRET_PATH="secret/data/myapp/config"

# Key to fetch from the secret
SECRET_KEY="my-secret-key"

# Fetch the secret from Vault using the API
response=$(curl --silent --header "X-Vault-Token: $VAULT_TOKEN" \
  --request GET \
  $VAULT_ADDR/v1/$SECRET_PATH)

# Extract the key value from the response
SECRET_VALUE=$(echo $response | jq -r .data.data.$SECRET_KEY)

# Output the secret value
echo "The value of $SECRET_KEY is: $SECRET_VALUE"

########## End of shell script
############### Python example starts
# Install a Python client for HashiCorp Vault)
#pip install hvac

#############
import hvac

# Vault address and token
vault_addr = 'http://127.0.0.1:8200'
vault_token = 's.your_token_here'

# Path to the secret in Vault
secret_path = 'secret/data/myapp/config'

# Key to fetch from the secret
secret_key = 'my-secret-key'

# Initialize the client
client = hvac.Client(url=vault_addr, token=vault_token)

# Fetch the secret from Vault
response = client.secrets.kv.v2.read_secret_version(path=secret_path)

# Extract the key value from the response
secret_value = response['data']['data'][secret_key]

# Output the secret value
print(f'The value of {secret_key} is: {secret_value}')


######################## Python Example Ends

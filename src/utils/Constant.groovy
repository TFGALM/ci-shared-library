package utils

class Constant implements Serializable {
  
  static final String errorInvalidBranch = "ERROR: The branch should start with 'feature/', 'bugfix/' or 'hotfix/'"
  static final String errorInvalidMergeRequestTitle = "ERROR: The branch title is not correct. The accepted values are: MAJOR, MINOR, PATCH"
  static final String errorMissingSourceUrl = "ERROR: Missing 'sourceRepoURL' parameter."
  static final String errorVersionNotSupported = "ERROR: The versionType is not supported."
  static final String errorNotFoundPatterTag = "ERROR: Not found pattern in actualTag."
  static final String errorSonarMaxRetries = "ERROR: Maximum retries reached while attempting to retrieve the SonarQube analysis."
  static final String errorInvalidTrivyArguments = "ERROR: Invalid arguments. Please provide 'repoUrl' and 'appVersion'."
  static final String errorInvalidQuality = "ERROR: Check the previous link in your logs and increase the quality of your code please."
  static final String errorWrongVersionTag = "ERROR: String should have the following pattern: 'x.y.z'."
  static final String errorBadEnvironmentVariables = "ERROR: Environment variables are not correct."
  static final String errorInvalidBranchType = "ERROR: Only 'feature' and 'bugfix' branches are allowed for creating personal tags."
  static final String errorInvalidTagName = "ERROR: '/' character is not allowed in tag name."
  static final String caCertificate = """
    mkdir -p /tmp/certificate/
    cat <<EOL > /tmp/certificate/ca-alopezpa-homelab-raiz.crt
-----BEGIN CERTIFICATE-----
MIIFFzCCAv+gAwIBAgIUM1gzB1HV0QkltgeSZkHHBSE3uS0wDQYJKoZIhvcNAQEL
BQAwGzEZMBcGA1UEAwwQYWxvcGV6cGEuaG9tZWxhYjAeFw0yNDA5MjkxNzIyMDRa
Fw0zNDA5MjcxNzIyMDRaMBsxGTAXBgNVBAMMEGFsb3BlenBhLmhvbWVsYWIwggIi
MA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQCUOV1vcEWWqsSM/5uF2Q+ypn+o
BSLgB5Vo8787Dlq7GyhIi0DD/Zv4zirBCOpDhsY1jLWBpSLWNP9Ugx2VvCUQFUeN
D0plCHkWYQw/ncqbfxm3R9+HZ+GBnXqnj6hqbB5+63/W2voTHAmAiZauzPpP0aBP
vMdpdKlwrdbtz9/TrLFMj938pQEiu6nEzp8L0hwHy9C7B36PsAsKQu4TbKPPzzPZ
AhUfc5awESwpKmsIPefiZTUHjyka8orwtdJZrq9xPhSi5m7x9BKxxe2W39SEE2yF
GTSzT83K+aFkDHff8991wR3zV4Of3ZqcCE7eQWSKQVaix9lezn7oUiFnO5IwHBg2
sAllOyKSjFZPcnFpEx6RTvFjfbWMWtyTKTzB7NuEbMZ20z0zgqPJwWNE8+CqBLik
oZUTwCuZqILVnWjUlxWxCmwWsN3dV5XDKNYR5uJ6dUCFhFptQFE2Ya+6uAy5Dk1Z
DiGGxZrndf1zxEye5RwgMQzOW/2TBC7/1uj7UUYYDgow6ViGN7Cn29VzzD17jNxp
THUCEN+wO9WtA3ydIEDmBDMv+jdKZLXuwXf5e2ebLFtRdJAofjgBrCdYzLf4/Gfl
YIcvsZatVYidEySDC+3m832x/XN3jNrXnymWrr8ZWqxnMlZv1K7MuOAYqOlaxhpm
UV4QzwA4sZauRnEaaQIDAQABo1MwUTAdBgNVHQ4EFgQUlpdoXpR4I1muUel+FzWg
WHNwvcowHwYDVR0jBBgwFoAUlpdoXpR4I1muUel+FzWgWHNwvcowDwYDVR0TAQH/
BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAgEAVPqgLeV9QrV5R7Fy61gugVrorIk5
uERjXPZxqVvbCYxW4Lo1jBf5WzALZhngbd+f0oaY2O7pOnhcB9DoczQqpHDfdO0k
mpth+sBDyn9dGPI3K+5z0ibl970zK+C8I01wUe5Anjf1y77ii1KfmsjvnAy+NcYx
UgCeFBE13M5Hm4qeIl30liWxLekXtTOIh00BKPsiJR7qelsFXIJb0tt3UQClPGeG
n2PU4fD18uZWWhCCcHo0Kr7Qy26xKQYhWX4JlY14BJW4PVJLW21nqNMKEXjt785i
41xx70pxrpal6M7raHM9+Kiuf0RYsaUfNEyOqVg8802+Xne/TBFe6vLZBQW9zxst
JrgwKm3KVTtK5nX6xenxrvJFgHSV15un36j7J/iypuDmifpDEmLk/ICAaRJSx8fL
BjXBuJkCR+aCIUJi5Gwea2E7y5C3YNPhzy4gBcPogJsRkRspAIAV9KtQybqN2bPN
GITVb/HhresAmwMBiLMF1TNCgczGtpPhh5jq7+h1Dy/BZE/E4vglocGbYUEKhkZy
zrpPKTrC67o/svpCJgT0mjAG047UP1gLpWBTDltkRiZPSYiVzlCpFvrojgTSnLXN
rObrVU6bpQlVgna2lRBnCUJuwfbPYMOHluY8FdyMywxVxHklo9dUn7U5O3+AHM8l
bT5JXvpAt9eFBuo=
-----END CERTIFICATE-----
EOL
"""
}

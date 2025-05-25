package service

class CraneService {
  private def script
  private String harborUser, harborPassword
  
  CraneService(script, String harborUser, String harborPassword) {
    this.script = script
    this.harborUser = harborUser
    this.harborPassword = harborPassword
  }

  void publishImageFromFile(String repoGroup, String repoName, String nextVersion) {
    
    String remoteLocation = "${script.pipelineConfig.services.harbor}/${repoGroup}/${repoName}:${nextVersion}"
    String file =  repoName + ".tar"
    setCertificate()
    script.sh("""
      export SSL_CERT_FILE='/tmp/certificate/harbor-core.crt'
      crane auth login -u '${harborUser}' -p '${harborPassword}' ${script.pipelineConfig.services.harbor}
      crane push ${file} ${remoteLocation}
      crane auth logout ${script.pipelineConfig.services.harbor}
    """)
  }
  
  //Certificado de ca harbor (secret harbor-all-internal-services-tls) y ca DOMINIO 
  private void setCertificate(){
    script.sh("""
        mkdir -p /tmp/certificate/
        cat <<EOL > /tmp/certificate/harbor-core.crt
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
-----BEGIN CERTIFICATE-----
MIIEqTCCApGgAwIBAgIUHsBZcZ3ZHlZTb9qD0/ozGiCoop0wDQYJKoZIhvcNAQEL
BQAwGzEZMBcGA1UEAwwQYWxvcGV6cGEuaG9tZWxhYjAeFw0yNDA5MjkxNzI0Mzda
Fw0yNTA5MjkxNzI0MzdaMF4xCzAJBgNVBAYTAkVTMQ8wDQYDVQQIDAZNYWRyaWQx
DzANBgNVBAcMBk1hZHJpZDEQMA4GA1UECgwHSG9tZWxhYjEbMBkGA1UEAwwSKi5h
bG9wZXpwYS5ob21lbGFiMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA
r7XbysycA9SD1MgE2B4weNPjwxaLXtjWQ8m9vLK/NKeKL/CtM1pS79AbwsHLLZdS
JiHmu07z/kXsoFMALNTiiKbT9eST8PjjTqKZ3eMjOnP8PgcUMwBupyB62zQnND09
qXVaoXsBAs9CkJEdPhfT4ymvemL3dxm9fygxtil0kEH8TtF3MrCl+wxsFMf6hEld
ch465Pk4Q0WTCvhZR8UmGLM4z4c1yUVVY5H2HxmF6plKn57ONpkFCXwTd87DOWYE
bn/eDunsO7Us/cHspDe56btNieXOlNVqNzrKPgjBTFwUa5zOtdtLLDFKml61iRJF
KU3ceZSibiUTF+39G+QOSQIDAQABo4GhMIGeMB8GA1UdIwQYMBaAFJaXaF6UeCNZ
rlHpfhc1oFhzcL3KMAkGA1UdEwQCMAAwCwYDVR0PBAQDAgTwMBMGA1UdJQQMMAoG
CCsGAQUFBwMBMC8GA1UdEQQoMCaCEiouYWxvcGV6cGEuaG9tZWxhYoIQYWxvcGV6
cGEuaG9tZWxhYjAdBgNVHQ4EFgQUsXRjJWQhAKg3XTb9pa/a2KWJSM8wDQYJKoZI
hvcNAQELBQADggIBAI8xqypuwv/rtWukx5mBYwlMyjJNLIAqoJvnxlNayIm1Bj+j
y7ay9HWRS30XKaZ/ZI6z8RCunasTCAZcnKVynmuXCt3RkLe0S7hH7pMPPKvmiZB2
SgAEVLYkk/dXkGJmxwe8s/BKgLuTgAoHOTLJ8+0rLX6oWkPDCHXg2L/xMIWBsqgE
KLIlIhGyX1Soh1z++lR+CttdvhCay9Fw+A/4Jgewy95fKJYQiqG1dhGVDFi/7yZL
vGjGFzyCgKfeI2Y0IdRtpkNIAfeRVzasW4soBT8+Lh+Eg25PQHID7wAFmK8TmseZ
K2h90mIaaZBxLj0rO8tNw+TQWyyqCsNb9rsyzkTM+5GUxqb2jRMPIdWS0L6Tg96E
gQUNw3NJoELCEXCjvoHOZdXd4h5XN6KXfZii1Z9/G9+BgYBVVufN1IVxHftOi1fL
FZXaL+RFWeiuStX3dVVPQMUUNSheFfBAxvNSBiX3a64KDHbUvROPAsz0hoTiNV5G
ZKNxM+RlyMzuprMrQkrBQuDrdpFCMS462H2CyUZq9FjS+8VUA/4GnVrZoEUEuVCu
OPcM5f07pNudaInz+GlcVKVdeKhcwtRuxZyeVABVz+qO/BQmbMwiekiZ+so7/DZx
cCI6D9UwLpIpRqClmVI6wunKRShiRPqtA+PXj4bjgrIq6m+ik7DOKh0iJ9BA
-----END CERTIFICATE-----
EOL
      """)
  }
}

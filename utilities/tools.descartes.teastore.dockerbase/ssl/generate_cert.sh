#!/bin/bash

openssl genrsa -out key.pem 2048

cat > config.conf<< EOF
[req]
distinguished_name=req
[SAN]
subjectAltName=DNS:teastore-registry, DNS:teastore-webui, DNS:teastore-auth, DNS:teastore-recommender, DNS:teastore-persistence, DNS:teastore-image, DNS:teastore-webui, DNS: localhost
EOF

openssl req -new -x509 -key key.pem -out cert.pem -days 3650 -subj /CN=teastore-registry -extensions SAN -config 'config.conf'

openssl x509 -in cert.pem -text -noout

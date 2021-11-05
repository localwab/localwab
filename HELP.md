# Getting Started

## Develop LocalWAB

### Reference Image

The [docker compose](https://github.com/WhatsApp/WhatsApp-Business-API-Setup-Scripts/blob/master/installation/docker-compose.yml)

Run:

    docker-compose up

Stop:

    docker-compose down --volumes

### TLS

[Create](https://stackoverflow.com/questions/10175812/how-to-generate-a-self-signed-ssl-certificate-using-openssl)
a self-signed SSL certificate using OpenSSL:

    openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes -subj '/CN=tls-terminator'

Copy the contents of private key (`key.pem` file) into `cert.pem` after the public key

Info:
[TLS termination](https://www.freecodecamp.org/news/docker-nginx-letsencrypt-easy-secure-reverse-proxy-40165ba3aee2/)

### Inspect image

https://stackoverflow.com/questions/29696656/finding-the-layers-and-layer-sizes-for-each-docker-image
https://github.com/wagoodman/dive

### Swagger

[Kotlin support](https://springdoc.org/#kotlin-support)

Migrating:

https://springdoc.org/index.html#migrating-from-springfox

UI:

http://localhost:8080/swagger-ui.html

API:

http://localhost:8091/v3/api-docs/
http://localhost:8080/v3/api-docs/


### Links

[Localtunnel](https://theboroer.github.io/localtunnel-www/) is a great tool for testing


### commands

#### Login

http --auth admin:secret --verify=no --print=HBhb POST https://localhost:9090/v1/users/login new_password=Pass123-qwerty

```
http --auth admin:secret --verify=no --print=HBhb POST https://localhost:9090/v1/users/login new_password=Pass123-qwerty
POST /v1/users/login HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Authorization: Basic YWRtaW46c2VjcmV0
Connection: keep-alive
Content-Length: 34
Content-Type: application/json
Host: localhost:9090
User-Agent: HTTPie/0.9.8

{
    "new_password": "Pass123-qwerty"
}

HTTP/1.1 200 OK
Cache-Control: no-cache, private
Content-Length: 331
Content-Type: application/json
Date: Sun, 18 Jul 2021 08:50:48 GMT
Server: Server
X-Request-ID: 326b386c761748af90231c8233eb42c2

{
    "meta": {
        "api_status": "stable",
        "version": "v2.33.4"
    },
    "users": [
        {
            "expires_after": "2021-07-25 08:50:48+00:00",
            "token": "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjY1OTgyNDgsImV4cCI6MTYyNzIwMzA0OCwid2E6cmFuZCI6IjQyMTIwMTQyY2JhNzFmZDA5NTZlYmFjNTEzZThkNDRiIn0.Lcsf-8KFlGwTLGImwz2HHtYr6UGNKUbVnyTEyw99Agk"
        }
    ]
}

```

#### Logout

http --verify=no --print=HBhb  POST https://localhost:9090/v1/users/logout 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjY3NzU2NzQsImV4cCI6MTYyNzM4MDQ3NCwid2E6cmFuZCI6Ijg4YjY0MzI3ZjMxMzg3YjgxYWJkOWI2ODgwMTlkYzk1In0.Q4JFYMLUQj94k7LI8ou0DrbG2chLOgmRHXMLfxciFYQ'

#### Create Account

http --verify=no --print=HBhb  --json POST https://localhost:9090/v1/account 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjY2ODI0MDgsImV4cCI6MTYyNzI4NzIwOCwid2E6cmFuZCI6IjAxYTVkY2ZjZTllN2NkOTczMDAyMGI0NTg3OWYyODQ2In0.1iu97hTd05V7sfyzoIdcmgx5sxtUQl3GLPH5T8F50Fs' cc=007 phone_number=9811453770 method=sms 'cert=CmcKIwjGvfOpuoTSAxIGZW50OndhIgpBbmRyZXlDb21wUIDVr4gGGkCh4oI3YCoRpaVKgqIf4fu3fqkcwWdk/CA6CRYt7rjVtcbIAv+AcC6v3xltkN/iCNKE4iYr3wFcosUB0JOA1cgPEi5tSQ2p0c6p8/BatbWRpGwokV/n4lvF2J2TYwlOrTwQHUVhv9kPCMa8CoKJmgtr'


```
HTTP/1.1 403 Forbidden
Cache-Control: no-cache, private
Content-Length: 167
Content-Type: application/json
Date: Mon, 19 Jul 2021 08:14:21 GMT
Server: Server
X-Internal-Request-IDS: 05014a01aad8488f852a95ddd7ce7303
X-Request-ID: 813b93f469654f76a511e78c25da2283

{
    "errors": [
        {
            "code": 1005,
            "details": "Please wait 6 hour(s) 33 minute(s) before trying again",
            "title": "Access denied"
        }
    ],
    "meta": {
        "api_status": "stable",
        "version": "2.33.4"
    }
}
```


Verify:

[info1](https://support.stellabot.com/portal/en/kb/articles/i-can-t-register-my-phone-number-for-whatsapp-business-api-what-should-i-do)

[info 2](https://docs.360dialog.com/submission-process/number-registration-issues)

```shell
http --verify=no --print=HBhb  --json POST https://localhost:9090/v1/account/verify 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgxODA0OTQsImV4cCI6MTYyODc4NTI5NCwid2E6cmFuZCI6ImFjZDY5ODBlNjlmYjUxNzBhY2EzNzE2M2VjMjllZGIxIn0.PIGjvzURmlqGlG4er5jTgC4FcT09_a-0xh4ljGHEQeI' code=316-089
POST /v1/account/verify HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgwNjUyMDksImV4cCI6MTYyODY3MDAwOSwid2E6cmFuZCI6ImUzNWYzM2YzY2JjNDFiYWFjZDc5NDUwYmUyYzRmYzY5In0.GQS8ET9c6kzNpZSBJmu8yocguyWQpYzF_TKO0rNvUhE
Connection: keep-alive
Content-Length: 19
Content-Type: application/json
Host: localhost:9090
User-Agent: HTTPie/0.9.8

{
    "code": "222-867"
}

HTTP/1.1 403 Forbidden
Cache-Control: no-cache, private
Content-Length: 186
Content-Type: application/json
Date: Wed, 04 Aug 2021 08:40:31 GMT
Server: Server
X-Internal-Request-IDS: 107b4910371249c29c7fce2e9f65d882
X-Request-ID: 093c177a55cf411782fa514f6a6d831f

{
    "errors": [
        {
            "code": 1005,
            "details": "Registration failed for reason 'biz_link_info_invalid_payload'.",
            "title": "Access denied"
        }
    ],
    "meta": {
        "api_status": "stable",
        "version": "2.33.4"
    }
}

```

#### Certificates

External

```
http --verify=no --print=HBhb  --json GET https://localhost:9090/v1/certificates/external/ca 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgwNjUyMDksImV4cCI6MTYyODY3MDAwOSwid2E6cmFuZCI6ImUzNWYzM2YzY2JjNDFiYWFjZDc5NDUwYmUyYzRmYzY5In0.GQS8ET9c6kzNpZSBJmu8yocguyWQpYzF_TKO0rNvUhE'
GET /v1/certificates/external/ca HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgwNjUyMDksImV4cCI6MTYyODY3MDAwOSwid2E6cmFuZCI6ImUzNWYzM2YzY2JjNDFiYWFjZDc5NDUwYmUyYzRmYzY5In0.GQS8ET9c6kzNpZSBJmu8yocguyWQpYzF_TKO0rNvUhE
Connection: keep-alive
Content-Type: application/json
Host: localhost:9090
User-Agent: HTTPie/0.9.8



HTTP/1.1 200 OK
Accept-Ranges: bytes
Cache-Control: public
Content-Disposition: attachment; filename="ca.pem"
Content-Length: 1464
Content-type: text/plain;charset=UTF-8
Date: Wed, 04 Aug 2021 08:29:33 GMT
Last-Modified: Wed, 04 Aug 2021 08:16:17 GMT
Server: Server
X-Request-ID: eac98473da464b0ab63804458f6204f6

-----BEGIN CERTIFICATE-----
MIIEDTCCAvWgAwIBAgIUWVGARfgyRaAQMXC4m7N2ywEcoJowDQYJKoZIhvcNAQEL
BQAwgZUxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDQTETMBEGA1UEBwwKTWVubG8g
UGFyazEVMBMGA1UECgwMV2hhdHNBcHAgSW5jMScwJQYDVQQLDB5FbnRlcnByaXNl
IENsaWVudCAtIEF1dG9HZW4gQ0ExJDAiBgkqhkiG9w0BCQEWFXN1ZGhhZ2FyQHdo
YXRzYXBwLmNvbTAeFw0yMTA4MDQwODE2MTdaFw0yMjA4MDQwODE2MTdaMIGVMQsw
CQYDVQQGEwJVUzELMAkGA1UECAwCQ0ExEzARBgNVBAcMCk1lbmxvIFBhcmsxFTAT
BgNVBAoMDFdoYXRzQXBwIEluYzEnMCUGA1UECwweRW50ZXJwcmlzZSBDbGllbnQg
LSBBdXRvR2VuIENBMSQwIgYJKoZIhvcNAQkBFhVzdWRoYWdhckB3aGF0c2FwcC5j
b20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC3zyr7STP4QtKOvaPj
geyRzNBlzzXbfqfw7Eu+NT3aJYhrI+jsWlByR4KiBv78wl0VNdWs2G91TJd0Lh9c
UXM3ZscyfkA0nWrUfDTP5ypx5NBQZHAZYTFN+6XOBoHmsb73mNbxB4zbJ+FNNtV5
mC4lQRNnNOgQ88PrumJITI3ZpHY7b0P7fvTX/1E4IAURp7jhjiso9Pwc5w3ahke7
2gigp1NrOlJu4bjKbgcLHqVakyzUW7rMdxSqSEhdiKVwso0qGkj3cctNYO28aRsV
FtVhiXdhRyntnYJ9cYIZqmHArB12mCHjbWhfw1F/41DMLgvo4Y8Ame8wEgtv8Nua
hRhZAgMBAAGjUzBRMB0GA1UdDgQWBBTCI/ygxFP84R3Lw6B0LfjyK/XBXTAfBgNV
HSMEGDAWgBTCI/ygxFP84R3Lw6B0LfjyK/XBXTAPBgNVHRMBAf8EBTADAQH/MA0G
CSqGSIb3DQEBCwUAA4IBAQCbR0Qr6g61/6RrbWJlmv1EudRNmo2apRWy0XuutSP/
jZs4uElx10H/Sd5fDGm6PULjY6/34f1BTIQUr2mEQI6hZVpOCkuPwSTgB4f+cy6e
+raw1eL7xgXi9aQg3pZjeruaWuouuQZm7QCkXOInrd2CK8nFUaJED7sD8zrKWRow
gLkIKLJeMFA+vVtrKewGRvrEMfRBlPJqlpTAkvnpA3Cgf6ES1aTqQPjcWVH9xWwR
VZ+Kio6kuahRvEWozyKQU++zF5p4eYh/hqFypRLt9CdbcEZPcDbKK8YwWWU4lE7A
si9Fdkvp2xRTL8/q88esYoZjvoKoZ1LSmEJ++y4hhguT
-----END CERTIFICATE-----

```

Webhooks:

```shell
http --verify=no --print=HBhb  --json GET https://localhost:9090/v1/certificates/webhooks/ca 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgwNjUyMDksImV4cCI6MTYyODY3MDAwOSwid2E6cmFuZCI6ImUzNWYzM2YzY2JjNDFiYWFjZDc5NDUwYmUyYzRmYzY5In0.GQS8ET9c6kzNpZSBJmu8yocguyWQpYzF_TKO0rNvUhE'
GET /v1/certificates/webhooks/ca HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgwNjUyMDksImV4cCI6MTYyODY3MDAwOSwid2E6cmFuZCI6ImUzNWYzM2YzY2JjNDFiYWFjZDc5NDUwYmUyYzRmYzY5In0.GQS8ET9c6kzNpZSBJmu8yocguyWQpYzF_TKO0rNvUhE
Connection: keep-alive
Content-Type: application/json
Host: localhost:9090
User-Agent: HTTPie/0.9.8



HTTP/1.1 404 Not Found
Cache-Control: no-cache, private
Content-Length: 150
Content-Type: application/json
Date: Wed, 04 Aug 2021 08:33:39 GMT
Server: Server
X-Internal-Request-IDS: 51f6068f81f14f10a97db6f5b06aabd8
X-Request-ID: 22d1cbbb181c4276ac2d8fc781ed1195

{
    "errors": [
        {
            "code": 1006,
            "details": "No webhook CA certificates found",
            "title": "Resource not found"
        }
    ],
    "meta": {
        "api_status": "stable",
        "version": "2.33.4"
    }
}

```

#### Settings

Get:

    http --verify=no --print=HBhb  --json GET https://localhost:9090/v1/settings/application 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2Mjk3OTI1NDAsImV4cCI6MTYzMDM5NzM0MCwid2E6cmFuZCI6ImFmNzBjZTkzYmM5ZGRkNzkyN2U1ODZmM2Y0NGYxYTNmIn0.aonB1kh67OADKxNouqj7bgC4T3OOQtFRpGIoz-XAsmc'

Update Webhook:

    http --verify=no --print=HBhb  --json PATCH https://localhost:9090/v1/settings/application 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2Mjk3OTI1NDAsImV4cCI6MTYzMDM5NzM0MCwid2E6cmFuZCI6ImFmNzBjZTkzYmM5ZGRkNzkyN2U1ODZmM2Y0NGYxYTNmIn0.aonB1kh67OADKxNouqj7bgC4T3OOQtFRpGIoz-XAsmc' webhooks:='{"url":"https://172.18.0.1:8443/mock/webhook"}'

#### Contacts

    http --verify=no --print=HBhb  --json POST https://localhost:9090/v1/contacts 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjkyODUwODksImV4cCI6MTYyOTg4OTg4OSwid2E6cmFuZCI6IjZkYjkzYzdhM2UzNjkzZDFjOWNiODBhMGRlYjQxMDkzIn0.U-Qcx8WpVamq3Lfm6UtEqMCd36KbXzZq5tBgtrY8Uzo' contacts:='["+79944067536"]' blocking=wait


#### Profile about
```
http --verify=no --print=HBhb  --json GET https://localhost:9090/v1/settings/profile/about 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgxODA0OTQsImV4cCI6MTYyODc4NTI5NCwid2E6cmFuZCI6ImFjZDY5ODBlNjlmYjUxNzBhY2EzNzE2M2VjMjllZGIxIn0.PIGjvzURmlqGlG4er5jTgC4FcT09_a-0xh4ljGHEQeI'
GET /v1/settings/profile/about HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgxODA0OTQsImV4cCI6MTYyODc4NTI5NCwid2E6cmFuZCI6ImFjZDY5ODBlNjlmYjUxNzBhY2EzNzE2M2VjMjllZGIxIn0.PIGjvzURmlqGlG4er5jTgC4FcT09_a-0xh4ljGHEQeI
Connection: keep-alive
Content-Type: application/json
Host: localhost:9090
User-Agent: HTTPie/0.9.8



HTTP/1.1 200 OK
Cache-Control: no-cache, private
Content-Length: 127
Content-Type: application/json
Date: Thu, 05 Aug 2021 17:05:01 GMT
Server: Server
X-Internal-Request-IDS: a0704013537c43f0bf0245ffa0704777
X-Request-ID: 3a97626e625147f4ae3575a656a151a5

{
    "meta": {
        "api_status": "stable",
        "version": "2.33.4"
    },
    "settings": {
        "profile": {
            "about": {
                "text": "Hey there! I am using WhatsApp."
            }
        }
    }
}
```

#### Profile photo

```shell
http --verify=no --print=HBhb  --json GET https://localhost:9090/v1/settings/profile/photo 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgxODA0OTQsImV4cCI6MTYyODc4NTI5NCwid2E6cmFuZCI6ImFjZDY5ODBlNjlmYjUxNzBhY2EzNzE2M2VjMjllZGIxIn0.PIGjvzURmlqGlG4er5jTgC4FcT09_a-0xh4ljGHEQeI'
GET /v1/settings/profile/photo HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgxODA0OTQsImV4cCI6MTYyODc4NTI5NCwid2E6cmFuZCI6ImFjZDY5ODBlNjlmYjUxNzBhY2EzNzE2M2VjMjllZGIxIn0.PIGjvzURmlqGlG4er5jTgC4FcT09_a-0xh4ljGHEQeI
Connection: keep-alive
Content-Type: application/json
Host: localhost:9090
User-Agent: HTTPie/0.9.8



HTTP/1.1 404 Not Found
Cache-Control: no-cache, private
Content-Length: 133
Content-Type: application/json
Date: Thu, 05 Aug 2021 17:07:29 GMT
Server: Server
X-Internal-Request-IDS: c29388953bf54fbdacdeb613e05b6b54
X-Request-ID: 996f153af60b4c4aa5e8c12aa2c53bcd

{
    "errors": [
        {
            "code": 1006,
            "details": "No picture set.",
            "title": "Resource not found"
        }
    ],
    "meta": {
        "api_status": "stable",
        "version": "2.33.4"
    }
}

```

#### Business profile

```shell
http --verify=no --print=HBhb  --json GET https://localhost:9090/v1/settings/business/profile 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgxODA0OTQsImV4cCI6MTYyODc4NTI5NCwid2E6cmFuZCI6ImFjZDY5ODBlNjlmYjUxNzBhY2EzNzE2M2VjMjllZGIxIn0.PIGjvzURmlqGlG4er5jTgC4FcT09_a-0xh4ljGHEQeI'
GET /v1/settings/business/profile HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgxODA0OTQsImV4cCI6MTYyODc4NTI5NCwid2E6cmFuZCI6ImFjZDY5ODBlNjlmYjUxNzBhY2EzNzE2M2VjMjllZGIxIn0.PIGjvzURmlqGlG4er5jTgC4FcT09_a-0xh4ljGHEQeI
Connection: keep-alive
Content-Type: application/json
Host: localhost:9090
User-Agent: HTTPie/0.9.8



HTTP/1.1 200 OK
Cache-Control: no-cache, private
Content-Length: 176
Content-Type: application/json
Date: Thu, 05 Aug 2021 17:08:50 GMT
Server: Server
X-Internal-Request-IDS: f2410202da8741f1bdb8051fdb1c7ae2
X-Request-ID: 75fa8ffc72b84038ae90ae433308c666

{
    "meta": {
        "api_status": "stable",
        "version": "2.33.4"
    },
    "settings": {
        "business": {
            "profile": {
                "address": "",
                "description": "This is my company",
                "email": "",
                "vertical": "",
                "websites": []
            }
        }
    }
}
```

#### Messages

```shell
http --verify=no --print=HBhb  --json POST https://localhost:9090/v1/messages 'Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjkyODUwODksImV4cCI6MTYyOTg4OTg4OSwid2E6cmFuZCI6IjZkYjkzYzdhM2UzNjkzZDFjOWNiODBhMGRlYjQxMDkzIn0.U-Qcx8WpVamq3Lfm6UtEqMCd36KbXzZq5tBgtrY8Uzo' text:='{"body": "Reply on 18.08.2021 at 14:44"}' to=79944067536 type=text
POST /v1/messages HTTP/1.1
Accept: application/json, */*
Accept-Encoding: gzip, deflate
Authorization: Bearer eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MjgzMzIwODIsImV4cCI6MTYyODkzNjg4Miwid2E6cmFuZCI6IjYzNjU3MTcyODU3YWUyZmVjNDQxYTAyZDliMjkxMTgyIn0.xZ2aydI9Ps7EzmocBWqHOfEz0c-EKTlIJI8uOoRVo_g
Connection: keep-alive
Content-Length: 67
Content-Type: application/json
Host: localhost:9090
User-Agent: HTTPie/0.9.8

{
    "text": {
        "body": "welcome!"
    },
    "to": "79944067536",
    "type": "text"
}

HTTP/1.1 201 Created
Cache-Control: no-cache, private
Content-Length: 101
Content-Type: application/json
Date: Sat, 07 Aug 2021 11:13:01 GMT
Server: Server
X-Internal-Request-IDS: 6b14fd19994c4b10b39d6fb7a2a37ba3|57E2A5B32E2C999E4C
X-Request-ID: 43039886b96e4870b143bf9b06348c31

{
    "messages": [
        {
            "id": "gBGGeZRAZ1NvAglX4qWzLiyZnkw"
        }
    ],
    "meta": {
        "api_status": "stable",
        "version": "2.33.4"
    }
}

```

Conversation:

```shell
Incoming webhook: {"statuses":[{"conversation":{"id":"01d767d4ae0d2a60facab34969798b5b"},"id":"gBGGeZRAZ1NvAgkuGvkubyIJdjs","pricing":{"billable":false,"pricing_model":"NBP"},"recipient_id":"79944067536","status":"delivered","timestamp":"1628771975"}]}

Incoming webhook: {"statuses":[{"conversation":{"id":"01d767d4ae0d2a60facab34969798b5b"},"id":"gBGGeZRAZ1NvAgkuGvkubyIJdjs","pricing":{"billable":false,"pricing_model":"NBP"},"recipient_id":"79944067536","status":"sent","timestamp":"1628771975"}]}

Incoming webhook: {"statuses":[{"id":"gBGGeZRAZ1NvAgkuGvkubyIJdjs","recipient_id":"79944067536","status":"read","timestamp":"1628771977"}]}
```



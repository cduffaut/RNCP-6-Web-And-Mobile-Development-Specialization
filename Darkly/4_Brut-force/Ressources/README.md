### Comment la faille a été trouvée:

1. On a la page sign in. Cette fois ci on va voir s'il est possible de réussir une attaque brutforce.

### Comment la faille a été exploitée:

2. On télécharge une wordlist de mots de passe: [RockYou.txt](https://github.com/brannondorsey/naive-hashcat/releases/download/data/rockyou.txt) (la plus célèbre)

Tests avec plusieurs usernames: `admin`, `root`.
Et on teste d'abord avec les 1000 premiers mots de passe, avec un script python simple.

3. On obtient un résultat avec le username `root` pour le password `shadow` (voir [resultat_brute_force](./resultat_brute_force.png))

### Comment la faille peut être corrigée:

- Mettre en place un Captcha
- Mot de passe fort exigé
- 2FA (authentification à deux facteurs)
- Limiter les tentatives de connexion: 5 tentatives par minute et par IP
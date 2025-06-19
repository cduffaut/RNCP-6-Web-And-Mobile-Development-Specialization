### Comment la faille a été trouvée:

1. Depuis la homepage, lorsque l'on clique sur "© BornToSec" on tombe sur cette page:
`http://127.0.0.1:8080/index.php?page=b7e44c7a40c5f80139f0a50f3650fb2bd8d00b0d24667c4c2ca32c88e13b758f`

2. En inspectant le code html de cette page en question on se rend compte si on descend après plein de retour à la ligne on trouve des commentaires laissés le long du code (voir [img 1](./1.png))

3. On trouve ce commentaire d'intéressant : `You must come from : "https://www.nsa.gov/".`
Et celui ci : `Let's use this browser : "ft_bornToSec". It will help you a lot.`

### Comment la faille a été exploitée:

4. Créons donc une requête à l'aide de curl, qui réponde à ces exigences:
```sh
curl.exe -A "ft_bornToSec" -e "https://www.nsa.gov/" "http://127.0.0.1:8080/index.php?page=b7e44c7a40c5f80139f0a50f3650fb2bd8d00b0d24667c4c2ca32c88e13b758f"
```

Explications: 
- `-A "ft_bornToSec"` : envoie un User-Agent personnalisé => Pour s'identifier auprès du serveur web.
- `-e "https://www.nsa.gov/"` : envoie un Referer modifié. => Indique la page d’origine d’où provient la requête actuelle.

5. La réponse que l'on obtient se trouve dans `result.txt`
On trouve dans cette réponse la ligne:
`The flag is : f2a29020ef3132e01dd61df97fd33ec8d7fcd1388cc9601e7db691d17d4d6188`

### Comment la faille peut être corrigée:

- Utiliser une authentification réelle: par session, token JWT, HTTP Basic, etc.

- Le serveur doit valider l’identité de l’utilisateur via des données sûres, pas des headers manipulables.

- Autoriser l’accès à la page sensible seulement si l’utilisateur est connecté ou possède un rôle/jeton spécifique.
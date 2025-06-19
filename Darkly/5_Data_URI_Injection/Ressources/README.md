### Comment la faille a été trouvée:

1. Sur la page d’accueil, tu vois une seule image cliquable.
Quand tu cliques dessus, tu es redirigé vers cette URL : `http://127.0.0.1:8080/?page=media&src=nsa`

2. Donc le site t’envoie sur une page spéciale qui utilise deux paramètres :
- `page=media` → indique au site de charger un certain type de contenu.
- `src=nsa` → indique quel fichier afficher.

3. En regardant le code HTML, on remarque quelque chose d'inhabituel, cette ligne:
`<object data="http://127.0.0.1/images/nsa_prism.jpg"></object>`

### Comment la faille a été exploitée:

4. Plutôt que d’utiliser une image classique (`<img>`), le site utilise `<object>`.
Et ce qui est important ici, c’est que la valeur du `data="..."` est directement contrôlée par l’URL (`src=nsa`), donc potentiellement modifiable par nous. 

5. On comprend que `src=...` est injecté tel quel dans `data="..."`.
Donc si on modifie `src=`, on modifie directement le contenu affiché dans la balise `<object>`.

6. Il existe une façon spéciale de charger du contenu dans le navigateur sans fichier : c’est l’URL data.
Ex: `data:text/html,<h1>Salut !</h1>`

7. Mais le site bloque les caractères spéciaux  (`<`, `>`, `!`) dans l’URL. Donc on encode en base64, sur le site: [https://www.base64encode.org/](https://www.base64encode.org/), ca nous donne:
`PGgxPlNhbHV0ICE8L2gxPg==`
L'URL pour notre test est donc: `http://127.0.0.1:8080/?page=media&src=data:text/html;base64,PGgxPlNhbHV0ICE8L2gxPg==`
Mais aucun flag, juste un message "Wrong answer".

8. Comportement typique en CTF : le flag est donné uniquement si tu prouves que tu as exécuté du JS.
Et comme `alert()` est le test visuel et simple par excellence, c’est souvent le déclencheur attendu.

Essayons donc avec alert: `<script>alert(TEST)</script>`
En base64 ca nous donne: `PHNjcmlwdD5hbGVydChURVNUKTwvc2NyaXB0Pgo=`
Cela nous donne comme URL final: `http://127.0.0.1:8080/?page=media&src=data:text/html;base64,PHNjcmlwdD5hbGVydChURVNUKTwvc2NyaXB0Pgo=`

Le flag apparait ! 

### Comment la faille peut être corrigée:

- Utiliser une liste blanche de fichiers autorisés (par exemple : nsa, prism, etc.)
- Empêcher l’injection de code HTML ou JavaScript via `data:`
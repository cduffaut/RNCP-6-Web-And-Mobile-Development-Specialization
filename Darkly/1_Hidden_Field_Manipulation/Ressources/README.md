### Comment la faille a été trouvée:

1. Tu tombe sur une page d'accueil avec un bouton `Sign In`
2. Tu as un lien cliquable `I forgot my password`
3. Une nouvelle page avec un bouton `Submit` et rien d'autre.
 Quand tu clique dessus, rien ne se passe. On inspecte du code source de la page pour comprendre.
4. En inspectant le code html de la page:
Un formulaire avec `action="#"` et `method="POST"` - Cela signifie que le formulaire soumet les données à la même page, en utilisant la méthode POST.
Le plus important : il y a un input de type hidden avec :
- `name="mail"`
- `value="webmaster@borntosec.com"`
- `maxlength="15"`
Ce champ caché contient l'adresse email à laquelle le mot de passe sera réinitialisé. Comme il s'agit d'un champ caché dans le HTML, l'application suppose probablement que l'utilisateur ne peut pas le modifier - ce qui est une erreur de sécurité.

### Comment la faille a été exploitée:

5. On a modifié la valeur du champ caché en double-cliquant sur la valeur `webmaster@borntosec.com` et en la remplaçant par une autre adresse email.
Cette vulnérabilité est liée à l'OWASP Top 10 sous "Broken Access Control" car elle permet de contourner les contrôles d'accès prévus par l'application.
6. En double cliquant sur le mail puis en changeant ce dernier par autre chose, la page se met à jour et nous affiche le flag.

### Comment la faille peut être corrigée:

7.  - Ne pas stocker de logique de sécurité dans les champs cachés.
- Pour toute donnée sensible à utiliser: le faire passer en back-end. (Assurer l'intégrité)
- Signer les données côté serveur avec un **HMAC** (=> technique cryptographique qui permet de vérifier l'intégrité d'un message & authentifier son origine).
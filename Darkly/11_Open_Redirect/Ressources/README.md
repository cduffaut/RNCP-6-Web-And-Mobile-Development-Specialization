### Comment la faille a été trouvée:

1. On démarre de la page d'accueil: `http://127.0.0.1:8080/`, et on examine de nouveau le code source.

2. Dans le HTML, on voit que le site propose des redirections comme ceci : `?page=redirect&site=facebook`

### Comment la faille a été exploitée:

3. Si le site ne vérifie pas ce qu'on lui donne comme destination, on peut changer cette valeur comme on veut.

4. On modifie dans le code html ceci `?page=redirect&site=facebook` par ceci `?page=redirect&site=google.com`
>>> Le flag apparait quand on clique sur le lien fraichement modifié.

### Comment la faille peut être corrigée:

- Supprimer complètement les redirections dynamiques: on passe par un lien direct sans redirection :
`<a href="https://facebook.com/lapagesouhaité" target="_blank">Facebook</a>`
- Utiliser une whitelist de destinations autorisées.
- Demander confirmation avant de rediriger, ex: "Vous allez quitter notre site pour visiter [sitelouchenepascliquer.com]. Voulez-vous continuer ?"
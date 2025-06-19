### Comment la faille a été trouvée:

1. Depuis la page d'accueil on clique sur le bouton `Survey`: qui nous conduit sur `http://127.0.0.1:8080/?page=survey`

2. On tombe sur un formulaire de vote. (voir [img 1](./1.png))

3. On valide une note sur le formulaire et on regarde ce qu'il se passe au niveau de la requête:
inspecter > network > on clique sur `page=survey` > payload. (voir [img 2](./2.png))

4. On observe nos deux valeurs dans le formulaire envoyé : le sujet évalué et la note.
La question est : est-ce que les valeurs envoyées sont vérifiées côté serveur ?

5. Pour vérifier cela: clic droit sur notre élément `?page=survey` > "edit and resend".
On change les valeurs par des nombres très grands. (voir [img 3](./3.png))

### Comment la faille a été exploitée:

6. On obtient une réponse html où si on descend un peu on voit le flag apparaitre ! (voir [img 4](./4.png))
Ce genre de comportement si mal anticipé, peut mener à faire crasher le serveur, ou contourner des règles établies... 

### Comment la faille peut être corrigée:

- Définir des valeurs de plage autorisées, et ne traiter que le type de valeur attendu. Gérer et prévoir les exceptions.

- Limiter la taille des entrées

- De manière générale ne jamais faire confiance aux données client.

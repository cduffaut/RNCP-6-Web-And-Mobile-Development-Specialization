### Comment la faille a été trouvée:

1. Les cookies du page web peuvent révéler de précieuses informations si mal configurés.
Depuis la page d'accueil: clic droit => inspecter => application => cookies. De là on regarde si des 
indices et/ou incohérences sont visibles.

### Comment la faille a été exploitée:

2. Le nom du cookie `I_am_admin` est suspect. Le contenu du cookie semble être un code chiffré:
`68934a3e9455fa72420237eb05902327` => ça ressemble à un hash. (voir [img 1](./1.png))

3. Pour vérifier cela on se rend sur [https://crackstation.net/](https://crackstation.net/)
Ce site permet de décrypter des hashs.

4. Après vérification le site nous donne ce résultat: `false`. Une réponse au nom du cookie `I_am_admin` ?
Donc si le hash donne `true` cela indiquerait que l'utilisateur est admin dans le cas contraire ? (voir [img 2](./2.png))

5. On fait donc l'étape inverse, on passe `true` sur le site [https://cyberchef.io/](https://cyberchef.io/) en MD5 (comme le hash précédent), pour en faire un hash. Ce qui nous donne `b326b5062b2f0e69046810717534cb09`

6. On remplace le hash d'origine par le nouveau (`true`).
On recharge la page et une pop up avec le flag apparait ! (voir [img 3](./3.png))

### Comment la faille peut être corrigée:

- Utiliser des cookies signés, en cas de modification le serveur en sera averti et le rejetera.
- Ne pas utiliser MD5 en guise de hash car comme on l'a vu, ceux-ci sont facilement cassables. Opter pour SHA-256.

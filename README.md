# Scaley

Application Java/Swing qui déduit la (ou les) **tonalité(s)** d'un morceau à partir des notes utilisées, et qui sait aussi afficher n'importe quelle gamme sur un clavier de piano.

## Fonctionnalités

- **Clavier de piano interactif** (un octave) : clic pour (dé)sélectionner une note, ou **glisser** pour en « peindre » plusieurs d'un coup.
- **Détection de tonalité** : compare les notes sélectionnées aux **37 gammes** (majeures, mineures, modes, gammes exotiques) sur les 12 toniques, et classe les résultats par **pourcentage de correspondance**.
- **Relative majeure/mineure** affichée pour les gammes concernées — ex. `Do Majeur (Ionien) (Relative de La mineur)`.
- **Construction de gamme** : deux menus (note + gamme) projettent directement la gamme choisie sur le piano et listent ses notes.
- **Bilingue** FR / EN (menu déroulant).
- **Notation des notes** au choix : solfège (`Do Ré Mi`) ou lettres (`C D E`) — indépendant de la langue.
- **Fenêtre redimensionnable** : le clavier s'adapte à la largeur, piano et résultats partagent un `JSplitPane`.

## Lancer le projet

Prérequis : un JDK (8+).

```bash
# compilation (l'encodage UTF-8 est nécessaire à cause des accents)
javac -encoding UTF-8 -d out Controleur.java ihm/VueScaley.java metier/*.java

# exécution
java -cp out Controleur
```

> Le `main` est dans `Controleur`.

## Structure (MVC)

```
Scaley/
├── Controleur.java              # point d'entrée + lien Vue <-> Modèle
├── ihm/
│   └── VueScaley.java           # fenêtre Swing (clavier, menus, résultats)
└── metier/
    ├── Note.java                # 12 notes, solfège <-> lettres
    ├── Gamme.java               # catalogue des 37 gammes + génération des notes
    ├── DetecteurTonalite.java   # calcul des pourcentages + tri
    ├── Resultat.java            # un résultat (nom + %)
    └── Langue.java              # drapeau de langue FR/EN
```

- **Modèle** (`metier`) : données et logique pure, ne connaît rien de l'affichage.
- **Vue** (`ihm`) : tout ce qui est Swing, aucune logique de calcul.
- **Contrôleur** : écoute les actions de la Vue, interroge le Modèle, met la Vue à jour.

## Comment marche la détection

Pour chaque gamme (12 toniques × 37 types), le pourcentage = part des notes sélectionnées présentes dans la gamme. Les résultats sont triés par pourcentage décroissant, puis par gamme la plus courte (la plus « serrée » d'abord).

Une tonalité majeure et sa **mineure relative** partagent exactement les mêmes notes : l'appli ne peut pas trancher à partir des seules notes et affiche donc les deux.

## Limite connue

Quelques gammes propriétaires (style Native Instruments) n'ont pas de définition publique fiable. Plusieurs ont été confirmées par recoupement (Arabe, Oriental, Piongio, Persan) ; trois gardent un intervalle de secours marqué `// à vérifier` dans `Gamme.java` :

- Major Bulgarian
- Major Polymode
- Minor Polymode

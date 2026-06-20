# Plan de développement — Scaley

**Scaley** est une application qui permet à un utilisateur de sélectionner les notes utilisées dans son morceau, et qui en déduit la (ou les) tonalité(s) correspondante(s).

---

## 1. Objectif du projet

- L'utilisateur sélectionne, parmi les 12 notes de la gamme chromatique (Do, Do#, Ré, Ré#, Mi, Fa, Fa#, Sol, Sol#, La, La#, Si), celles qu'il a utilisées dans sa composition.
- Le programme compare cet ensemble de notes aux gammes majeures et mineures connues.
- Le programme affiche la ou les tonalité(s) compatible(s) avec les notes sélectionnées.

Le projet doit rester réalisable avec uniquement les notions vues en **1ère année de BUT Informatique** : pas de notions avancées (lambdas, streams, généricité poussée, multithreading...).

---

## 2. Cahier des charges simplifié

| Élément | Description |
|---|---|
| Entrée | Liste des notes utilisées (sélection via cases à cocher) |
| Traitement | Comparaison des notes saisies avec les 24 gammes possibles (12 majeures + 12 mineures naturelles) |
| Sortie | Affichage de la/des tonalité(s) trouvée(s) |
| Cas particulier | Si plusieurs notes saisies appartiennent à plusieurs gammes (ex : Do majeur et La mineur partagent les mêmes notes), afficher toutes les possibilités |

---

## 3. Notions Java mobilisées (1ère année BUT Info)

- Variables et types primitifs (`int`, `boolean`, `String`)
- Tableaux (`String[]`) et/ou listes (`ArrayList<String>`)
- Boucles `for` / `while`
- Conditions `if / else`, éventuellement `switch`
- Méthodes (paramètres, valeur de retour)
- Classes et objets (attributs, constructeur, méthodes — POO de base)
- Collections simples (`ArrayList`, éventuellement `HashMap` pour associer une tonique à sa gamme)
- Bases de Swing pour l'interface graphique (`JFrame`, `JCheckBox`, `JButton`, `JLabel`, `JPanel`)
- Gestion d'événements Swing (`ActionListener`)
- Organisation du code selon le modèle **MVC** (Modèle / Vue / Contrôleur)

---

## 4. Architecture proposée (classes) — modèle MVC

Le projet est organisé en trois groupes de classes, selon le modèle **MVC** :

- **Modèle** → les données et la logique métier (les notes, les gammes, l'algorithme de détection). Ne sait rien de l'affichage.
- **Vue** → ce que l'utilisateur voit (la fenêtre Swing, les cases à cocher, le bouton, le résultat affiché). Ne contient aucune logique de calcul.
- **Contrôleur** → fait le lien entre les deux : récupère l'action de l'utilisateur dans la Vue, appelle le Modèle, puis demande à la Vue de se mettre à jour.

```
Scaley/
├── Main.java
│
├── modele/
│   ├── Note.java                → représente les 12 notes (tableau ou enum)
│   ├── Gamme.java                → représente une gamme (tonique + notes + type majeur/mineur)
│   └── DetecteurTonalite.java    → algorithme : comparer les notes saisies aux gammes
│
├── vue/
│   └── VueScaley.java            → fenêtre Swing (JFrame) : cases à cocher + bouton + label résultat
│
└── controleur/
    └── ControleurScaley.java     → écoute le bouton, interroge le Modèle, met à jour la Vue
```

### Modèle — `Note`
- Un tableau constant des 12 notes dans l'ordre chromatique :
  `{"Do","Do#","Ré","Ré#","Mi","Fa","Fa#","Sol","Sol#","La","La#","Si"}`

### Modèle — `Gamme`
- Attributs : `String tonique`, `boolean estMajeure`, `ArrayList<String> notes`
- Constructeur : à partir d'une tonique et d'un type (majeur/mineur), génère la liste des 7 notes de la gamme grâce à une formule d'intervalles
  - Gamme majeure : `[0, 2, 4, 5, 7, 9, 11]` demi-tons depuis la tonique
  - Gamme mineure naturelle : `[0, 2, 3, 5, 7, 8, 10]` demi-tons depuis la tonique

### Modèle — `DetecteurTonalite`
- Méthode `trouverTonalites(ArrayList<String> notesSaisies)` :
  - Génère les 24 gammes possibles (12 toniques × majeur/mineur)
  - Pour chaque gamme, vérifie si **toutes** les notes saisies appartiennent à la gamme
  - Retourne la liste des gammes compatibles
- Cette classe ne fait **que** du calcul : elle ne touche jamais à Swing ni à l'affichage.

### Vue — `VueScaley` (Swing)
- Hérite de `JFrame`
- Attributs : un tableau de `JCheckBox` (une par note), un `JButton` ("Trouver ma tonalité"), un `JLabel` pour afficher le résultat
- Constructeur : crée la fenêtre, place les 12 cases à cocher dans un `JPanel` (avec un `GridLayout` par exemple), ajoute le bouton et le label
- Méthode `getNotesCochees()` : renvoie la liste des notes dont la case est cochée (utilisée par le Contrôleur)
- Méthode `afficherResultat(String texte)` : met à jour le `JLabel`
- **Ne contient pas** la logique de calcul ni l'`ActionListener` métier — c'est le Contrôleur qui s'enregistre comme écouteur du bouton

### Contrôleur — `ControleurScaley`
- Attribut : une référence vers la `VueScaley` (la vue à mettre à jour)
- Implémente `ActionListener`, et s'enregistre sur le bouton de la Vue (`bouton.addActionListener(controleur)`)
- Méthode `actionPerformed(ActionEvent e)` :
  - Demande à la Vue les notes cochées (`vue.getNotesCochees()`)
  - Crée un `DetecteurTonalite` et appelle `trouverTonalites(...)` (le Modèle)
  - Transmet le résultat à la Vue via `vue.afficherResultat(...)`
- C'est la **seule** classe qui parle à la fois au Modèle et à la Vue

### `Main`
- Crée la `VueScaley`, crée le `ControleurScaley` en lui passant la vue, relie les deux, puis affiche la fenêtre (`vue.setVisible(true)`)

---

## 5. Algorithme de détection (pseudo-code)

```
Pour chaque tonique parmi les 12 notes :
    Pour type dans {majeur, mineur} :
        Construire la gamme correspondante (7 notes)
        Si toutes les notes saisies appartiennent à cette gamme :
            Ajouter "tonique + type" à la liste des résultats

Afficher la liste des résultats
```

C'est un algorithme simple, basé uniquement sur des boucles imbriquées et des tests d'appartenance dans un tableau/liste — donc tout à fait abordable en 1ère année.

---

## 6. Découpage en versions (jalons)

| Version | Contenu |
|---|---|
| **V0** | Structure du projet en packages `modele` / `vue` / `controleur`, classe `Note`, fenêtre Swing vide (`VueScaley` qui s'affiche) |
| **V1** | Classe `Gamme` (Modèle) : génération des notes d'une gamme majeure et mineure à partir d'une tonique |
| **V2** | `VueScaley` complète : 12 `JCheckBox` + bouton "Trouver ma tonalité" + `JLabel` résultat, sans aucune logique |
| **V3** | `DetecteurTonalite` (Modèle) : algorithme de comparaison, testé seul (sans interface) |
| **V4** | `ControleurScaley` : relie bouton ↔ Modèle ↔ Vue (récupère les cases cochées, appelle le détecteur, affiche le résultat) |
| **V5** *(option)* | Amélioration visuelle (icônes de notes, couleurs), gestion des erreurs (aucune case cochée) |

---

## 7. Exemple de jeu de test

| Notes saisies | Résultat attendu |
|---|---|
| Do, Ré, Mi, Fa, Sol, La, Si | Do majeur **et** La mineur (mêmes notes) |
| Do, Ré, Mi, Fa#, Sol, La, Si | Sol majeur **et** Mi mineur |
| Do, Mib, Sol | Plusieurs résultats possibles (notes insuffisantes pour trancher) |

---

## 8. Limite connue à expliquer à l'utilisateur

Une tonalité majeure et sa **mineure relative** (ex : Do majeur / La mineur) utilisent exactement les mêmes notes. L'application ne peut donc pas deviner, à partir des notes seules, s'il s'agit d'une tonalité majeure ou de sa mineure relative — elle doit afficher les deux possibilités plutôt que d'en choisir une au hasard.

---

## 9. Pistes d'amélioration (hors périmètre 1ère année)

- Détection automatique de la tonique probable selon la fréquence des notes (note la plus utilisée, note de fin, etc.)
- Prise en compte des gammes mineures harmonique/mélodique
- Interface graphique plus poussée (JavaFX)
- Sauvegarde de l'historique des morceaux analysés (fichier ou base de données)

---

---

## 10. Conventions de code à respecter

- **Indentation Allman** : l'accolade ouvrante `{` est placée seule sur sa propre ligne, alignée avec le mot-clé (et non collée à la fin de la ligne précédente comme dans le style K&R par défaut).
- **Tabulations**, pas d'espaces, pour l'indentation.

Exemple :

```java
public class Gamme
{
	private String tonique;
	private boolean estMajeure;

	public Gamme(String tonique, boolean estMajeure)
	{
		this.tonique = tonique;
		this.estMajeure = estMajeure;
	}

	public boolean contientNote(String note)
	{
		if (notes.contains(note))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
```

Je l'appliquerai pour tout le code Java qu'on écrira ensemble pour Scaley.

---

**Prochaine étape suggérée** : commencer par la V0 et V1 (`Note` et `Gamme` dans le package `modele`), qui posent les bases sans logique complexe, avant d'attaquer le `DetecteurTonalite` puis la connexion Vue/Contrôleur.


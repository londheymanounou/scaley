package metier;

import java.util.ArrayList;

public class Gamme
{
	// Catalogue des types de gammes : TYPES[i] correspond à INTERVALLES[i].
	// Intervalles = demi-tons depuis la tonique.
	public static final String[] TYPES =
	{
		"Majeur (Ionien)",
		"Majeur Bebop",
		"Majeur Bulgare",
		"Majeur Hexatonique",
		"Majeur Pentatonique",
		"Majeur Persan",
		"Majeur Polymode",
		"Mineur Harmonique",
		"Mineur Hongrois",
		"Mineur Mélodique",
		"Mineur Naturel (Éolien)",
		"Mineur Napolitain",
		"Mineur Pentatonique",
		"Mineur Polymode",
		"Mineur Roumain",
		"Autre Arabe",
		"Autre Bebop Dominante",
		"Autre Blues",
		"Autre Blues Nonatonique",
		"Autre Diminuée",
		"Autre Dorien",
		"Autre Oriental",
		"Autre Égyptien",
		"Autre Énigmatique",
		"Autre Hirajoshi",
		"Autre Iwato",
		"Autre Insen Japonais",
		"Autre Locrien",
		"Autre Super Locrien",
		"Autre Lydien",
		"Autre Mixolydien",
		"Autre Napolitain",
		"Autre Phrygien",
		"Autre Phrygien Dominant",
		"Autre Piongio",
		"Autre Prométhée",
		"Autre Ton Entier"
	};

	// Mêmes types, en anglais (même ordre que TYPES).
	public static final String[] TYPES_EN =
	{
		"Major (Ionian)",
		"Major Bebop",
		"Major Bulgarian",
		"Major Hexatonic",
		"Major Pentatonic",
		"Major Persian",
		"Major Polymode",
		"Minor Harmonic",
		"Minor Hungarian",
		"Minor Melodic",
		"Minor Natural (Aeolian)",
		"Minor Neapolitan",
		"Minor Pentatonic",
		"Minor Polymode",
		"Minor Romanian",
		"Other Arabic",
		"Other Bebop Dominant",
		"Other Blues",
		"Other Blues Nonatonic",
		"Other Diminished",
		"Other Dorian",
		"Other Eastern",
		"Other Egyptian",
		"Other Enigmatic",
		"Other Hirajoshi",
		"Other Iwato",
		"Other Japanese Insen",
		"Other Locrian",
		"Other Locrian Super",
		"Other Lydian",
		"Other Mixolydian",
		"Other Neapolitan",
		"Other Phrygian",
		"Other Phrygian Dominant",
		"Other Piongio",
		"Other Prometheus",
		"Other Whole Tone"
	};

	public static final int[][] INTERVALLES =
	{
		{0, 2, 4, 5, 7, 9, 11},            // Major (Ionian)
		{0, 2, 4, 5, 7, 8, 9, 11},         // Major Bebop
		{0, 2, 4, 6, 7, 9, 11},            // Major Bulgarian      // à vérifier
		{0, 2, 4, 5, 7, 9},                // Major Hexatonic
		{0, 2, 4, 7, 9},                   // Major Pentatonic
		{0, 1, 4, 5, 6, 8, 11},            // Major Persian
		{0, 2, 4, 5, 7, 9, 10, 11},        // Major Polymode       // à vérifier
		{0, 2, 3, 5, 7, 8, 11},            // Minor Harmonic
		{0, 2, 3, 6, 7, 8, 11},            // Minor Hungarian
		{0, 2, 3, 5, 7, 9, 11},            // Minor Melodic
		{0, 2, 3, 5, 7, 8, 10},            // Minor Natural (Aeolian)
		{0, 1, 3, 5, 7, 8, 11},            // Minor Neapolitan
		{0, 3, 5, 7, 10},                  // Minor Pentatonic
		{0, 2, 3, 5, 7, 8, 9, 10},         // Minor Polymode       // à vérifier
		{0, 2, 3, 6, 7, 9, 10},            // Minor Romanian
		{0, 2, 4, 5, 6, 8, 10},            // Other Arabic
		{0, 2, 4, 5, 7, 9, 10, 11},        // Other Bebop Dominant
		{0, 3, 5, 6, 7, 10},               // Other Blues
		{0, 2, 3, 4, 5, 7, 9, 10, 11},     // Other Blues Nonatonic
		{0, 2, 3, 5, 6, 8, 9, 11},         // Other Diminished
		{0, 2, 3, 5, 7, 9, 10},            // Other Dorian
		{0, 1, 4, 5, 6, 8, 10},            // Other Eastern        // = gamme Oriental, meilleure correspondance trouvée
		{0, 2, 5, 7, 10},                  // Other Egyptian
		{0, 1, 4, 6, 8, 10, 11},           // Other Enigmatic
		{0, 2, 3, 7, 8},                   // Other Hirajoshi
		{0, 1, 5, 6, 10},                  // Other Iwato
		{0, 1, 5, 7, 10},                  // Other Japanese Insen
		{0, 1, 3, 5, 6, 8, 10},            // Other Locrian
		{0, 1, 3, 4, 6, 8, 10},            // Other Locrian Super
		{0, 2, 4, 6, 7, 9, 11},            // Other Lydian
		{0, 2, 4, 5, 7, 9, 10},            // Other Mixolydian
		{0, 1, 3, 5, 7, 9, 11},            // Other Neapolitan
		{0, 1, 3, 5, 7, 8, 10},            // Other Phrygian
		{0, 1, 4, 5, 7, 8, 10},            // Other Phrygian Dominant
		{0, 2, 5, 7, 9, 10},               // Other Piongio
		{0, 2, 4, 6, 9, 10},               // Other Prometheus
		{0, 2, 4, 6, 8, 10}                // Other Whole Tone
	};

	// Noms de types selon la langue active.
	public static String[] nomsTypes()
	{
		return Langue.anglais ? TYPES_EN : TYPES;
	}

	private String tonique;
	private int indiceType;
	private ArrayList<String> notes;

	public Gamme(String tonique, int indiceType)
	{
		this.tonique = tonique;
		this.indiceType = indiceType;
		this.notes = new ArrayList<String>();

		int depart = Note.indiceDe(tonique);
		int[] intervalles = INTERVALLES[indiceType];

		for (int i = 0; i < intervalles.length; i++)
		{
			int indice = (depart + intervalles[i]) % 12;
			this.notes.add(Note.CHROMATIQUE[indice]);
		}
	}

	public boolean contientNote(String note)
	{
		return this.notes.contains(note);
	}

	public String getTonique()
	{
		return this.tonique;
	}

	public String getType()
	{
		return nomsTypes()[this.indiceType];
	}

	public ArrayList<String> getNotes()
	{
		return this.notes;
	}

	public String getNom()
	{
		return Note.affiche(this.tonique) + " " + getType();
	}

	// Nom + relative pour les deux gammes qui ont une relative au sens classique.
	// Détection par index (0 = Ionien, 10 = Éolien) pour rester indépendant de la langue.
	public String getNomComplet()
	{
		if (this.indiceType == 0)
		{
			int idx = (Note.indiceDe(this.tonique) + 9) % 12;   // relative mineure = +9 demi-tons
			String rel = Note.affiche(Note.CHROMATIQUE[idx]);
			return getNom() + (Langue.anglais
				? " (Relative of " + rel + " minor)"
				: " (Relative de " + rel + " mineur)");
		}
		if (this.indiceType == 10)
		{
			int idx = (Note.indiceDe(this.tonique) + 3) % 12;   // relative majeure = +3 demi-tons
			String rel = Note.affiche(Note.CHROMATIQUE[idx]);
			return getNom() + (Langue.anglais
				? " (Relative of " + rel + " major)"
				: " (Relative de " + rel + " majeur)");
		}
		return getNom();
	}
}

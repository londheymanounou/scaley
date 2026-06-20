package metier;

public class Note
{
	// Identité interne des notes (solfège) — ne change jamais.
	public static final String[] CHROMATIQUE =
	{
		"Do", "Do#", "Ré", "Ré#", "Mi", "Fa",
		"Fa#", "Sol", "Sol#", "La", "La#", "Si"
	};

	// Même ordre, en lettres anglo-saxonnes (affichage seulement).
	public static final String[] LETTRES =
	{
		"C", "C#", "D", "D#", "E", "F",
		"F#", "G", "G#", "A", "A#", "B"
	};

	// false = affichage solfège (Do, Ré…), true = lettres (C, D…). Indépendant de la langue.
	public static boolean lettres = false;

	// Convertit une note (solfège, identité interne) vers son affichage courant.
	public static String affiche(String noteSolfege)
	{
		if (!lettres)
		{
			return noteSolfege;
		}
		int i = indiceDe(noteSolfege);
		return i < 0 ? noteSolfege : LETTRES[i];
	}

	public static int indiceDe(String note)
	{
		for (int i = 0; i < CHROMATIQUE.length; i++)
		{
			if (CHROMATIQUE[i].equals(note))
			{
				return i;
			}
		}
		return -1;
	}
}

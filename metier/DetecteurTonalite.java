package metier;

import java.util.ArrayList;

public class DetecteurTonalite
{
	// Renvoie toutes les gammes, classées de la mieux représentée à la moins bonne.
	// Le pourcentage = part des notes saisies présentes dans la gamme.
	public ArrayList<Resultat> trouverTonalites(ArrayList<String> notesSaisies)
	{
		ArrayList<Resultat> resultats = new ArrayList<Resultat>();

		if (notesSaisies.isEmpty())
		{
			return resultats;
		}

		for (int i = 0; i < Note.CHROMATIQUE.length; i++)
		{
			String tonique = Note.CHROMATIQUE[i];

			for (int t = 0; t < Gamme.TYPES.length; t++)
			{
				Gamme gamme = new Gamme(tonique, t);
				int present = compterNotesPresentes(gamme, notesSaisies);
				int pourcentage = (present * 100 + notesSaisies.size() / 2) / notesSaisies.size();

				resultats.add(new Resultat(gamme.getNomComplet(), pourcentage, gamme.getNotes().size()));
			}
		}

		trier(resultats);
		return resultats;
	}

	private int compterNotesPresentes(Gamme gamme, ArrayList<String> notes)
	{
		int present = 0;
		for (int i = 0; i < notes.size(); i++)
		{
			if (gamme.contientNote(notes.get(i)))
			{
				present++;
			}
		}
		return present;
	}

	// Tri par sélection : pourcentage décroissant, puis gamme la plus courte d'abord.
	// O(n²) sur ~444 éléments, instantané.
	private void trier(ArrayList<Resultat> liste)
	{
		for (int i = 0; i < liste.size() - 1; i++)
		{
			int meilleur = i;
			for (int j = i + 1; j < liste.size(); j++)
			{
				if (estMeilleur(liste.get(j), liste.get(meilleur)))
				{
					meilleur = j;
				}
			}
			Resultat tmp = liste.get(i);
			liste.set(i, liste.get(meilleur));
			liste.set(meilleur, tmp);
		}
	}

	private boolean estMeilleur(Resultat a, Resultat b)
	{
		if (a.getPourcentage() != b.getPourcentage())
		{
			return a.getPourcentage() > b.getPourcentage();
		}
		return a.getNbNotes() < b.getNbNotes();
	}
}

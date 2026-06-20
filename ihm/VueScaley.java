package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import metier.Gamme;
import metier.Langue;
import metier.Note;

public class VueScaley extends JFrame
{
	private static final int LARGEUR_BLANCHE = 64;   // taille de référence (le clavier s'adapte ensuite)
	private static final int HAUTEUR_BLANCHE = 220;

	private static final Color FOND      = new Color(210, 210, 210);
	private static final Color SELECTION = new Color(150, 210, 240);

	private JButton[] touches;       // indexées comme Note.CHROMATIQUE
	private boolean[] selection;
	private Color couleurDefaut;     // fond JButton normal (selon le Look&Feel)
	private JButton boutonTrouver;
	private JComboBox<String> comboNote;
	private JComboBox<String> comboGamme;
	private JComboBox<String> comboLangue;
	private JComboBox<String> comboNotation;
	private JLabel lblLangue;
	private JLabel lblNotation;
	private JLabel lblNote;
	private JLabel lblGamme;
	private JTextArea resultat;

	public VueScaley()
	{
		super("Scaley — Détecteur de tonalité");

		this.touches = new JButton[Note.CHROMATIQUE.length];
		this.selection = new boolean[Note.CHROMATIQUE.length];

		this.boutonTrouver = new JButton("Trouver ma tonalité");
		this.couleurDefaut = this.boutonTrouver.getBackground();
		this.comboNote = new JComboBox<String>(nomsNotesAffichees());
		this.comboGamme = new JComboBox<String>(Gamme.nomsTypes());
		this.comboLangue = new JComboBox<String>(new String[] {"Français", "English"});
		this.comboNotation = new JComboBox<String>(new String[] {"Do Ré Mi", "C D E"});

		this.resultat = new JTextArea(10, 30);
		this.resultat.setEditable(false);
		this.resultat.setFont(new Font("Monospaced", Font.PLAIN, 12));

		this.lblLangue = new JLabel();
		this.lblNotation = new JLabel();
		this.lblNote = new JLabel();
		this.lblGamme = new JLabel();

		JPanel haut = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
		haut.add(this.lblLangue);
		haut.add(this.comboLangue);
		haut.add(this.lblNotation);
		haut.add(this.comboNotation);
		haut.add(this.lblNote);
		haut.add(this.comboNote);
		haut.add(this.lblGamme);
		haut.add(this.comboGamme);
		haut.add(this.boutonTrouver);

		this.comboLangue.addActionListener(new EcouteurLangue());
		this.comboNotation.addActionListener(new EcouteurNotation());
		appliquerLangue();   // remplit labels + texte par défaut selon la langue courante

		JSplitPane centre = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			construireClavier(), new JScrollPane(this.resultat));
		centre.setResizeWeight(0.5);          // le piano et les résultats grandissent ensemble
		centre.setContinuousLayout(true);

		this.setLayout(new BorderLayout(8, 8));
		this.add(haut, BorderLayout.NORTH);
		this.add(centre, BorderLayout.CENTER);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(760, 660);
		this.setMinimumSize(new Dimension(520, 440));
		this.setLocationRelativeTo(null);
	}

	private JLayeredPane construireClavier()
	{
		final JLayeredPane clavier = new JLayeredPane();
		clavier.setOpaque(true);
		clavier.setBackground(FOND);
		clavier.setPreferredSize(new Dimension(7 * LARGEUR_BLANCHE, HAUTEUR_BLANCHE));

		EcouteurSouris souris = new EcouteurSouris(clavier);
		Font police = new Font("SansSerif", Font.PLAIN, 11);

		for (int i = 0; i < Note.CHROMATIQUE.length; i++)
		{
			boolean noire = estNoire(i);

			JButton touche = new JButton(labelTouche(i));
			touche.setFont(police);
			touche.setMargin(new Insets(1, 1, 1, 1));
			touche.setFocusPainted(false);
			touche.setVerticalAlignment(JButton.BOTTOM);
			touche.addMouseListener(souris);
			touche.addMouseMotionListener(souris);
			this.touches[i] = touche;

			clavier.add(touche, noire ? JLayeredPane.PALETTE_LAYER : JLayeredPane.DEFAULT_LAYER);
			majCouleur(i);
		}

		// les positions absolues sont recalculées à chaque redimensionnement
		clavier.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				disposerTouches(clavier);
			}
		});

		return clavier;
	}

	// Place les touches proportionnellement à la taille courante du clavier.
	private void disposerTouches(JLayeredPane clavier)
	{
		int largeur = clavier.getWidth();
		int hauteur = clavier.getHeight();
		if (largeur == 0 || hauteur == 0)
		{
			return;
		}

		int largeurNoire = (int) (largeur / 7.0 * 0.62);
		int hauteurNoire = (int) (hauteur * 0.62);

		int blanche = 0;   // nombre de blanches déjà placées = position de la frontière
		for (int i = 0; i < Note.CHROMATIQUE.length; i++)
		{
			if (estNoire(i))
			{
				int frontiere = (int) Math.round(blanche * largeur / 7.0);
				this.touches[i].setBounds(frontiere - largeurNoire / 2, 0, largeurNoire, hauteurNoire);
			}
			else
			{
				int gauche = (int) Math.round(blanche * largeur / 7.0);
				int droite = (int) Math.round((blanche + 1) * largeur / 7.0);
				this.touches[i].setBounds(gauche, 0, droite - gauche, hauteur);
				blanche++;
			}
		}
	}

	private boolean estNoire(int i)
	{
		return Note.CHROMATIQUE[i].contains("#");
	}

	private void majCouleur(int i)
	{
		if (this.selection[i])
		{
			this.touches[i].setBackground(SELECTION);
		}
		else
		{
			this.touches[i].setBackground(this.couleurDefaut);
		}
	}

	public ArrayList<String> getNotesCochees()
	{
		ArrayList<String> cochees = new ArrayList<String>();
		for (int i = 0; i < this.touches.length; i++)
		{
			if (this.selection[i])
			{
				cochees.add(Note.CHROMATIQUE[i]);
			}
		}
		return cochees;
	}

	public void afficherResultat(String texte)
	{
		this.resultat.setText(texte);
	}

	// Met à jour tous les libellés de l'interface selon la langue active.
	private void appliquerLangue()
	{
		boolean en = Langue.anglais;

		this.lblLangue.setText(en ? "Language:" : "Langue :");
		this.lblNotation.setText(en ? "Notes:" : "Notes :");
		this.lblNote.setText(en ? "Key:" : "Note :");
		this.lblGamme.setText(en ? "Scale:" : "Gamme :");
		this.boutonTrouver.setText(en ? "Find my key" : "Trouver ma tonalité");
		this.resultat.setText(en
			? "Click the notes of your piece, then « Find my key »."
			: "Cliquez les notes de votre morceau, puis « Trouver ma tonalité ».");

		// recharge les noms de gammes dans la nouvelle langue, sélection conservée
		rechargerCombo(this.comboGamme, Gamme.nomsTypes());
	}

	// Met à jour les labels des touches et le menu Note dans la notation courante.
	private void majAffichageNotes()
	{
		for (int i = 0; i < this.touches.length; i++)
		{
			this.touches[i].setText(labelTouche(i));
		}
		rechargerCombo(this.comboNote, nomsNotesAffichees());
	}

	private String labelTouche(int i)
	{
		return Note.affiche(Note.CHROMATIQUE[i]);   // « Ré# » / « C# » tel quel
	}

	private String[] nomsNotesAffichees()
	{
		String[] noms = new String[Note.CHROMATIQUE.length];
		for (int i = 0; i < noms.length; i++)
		{
			noms[i] = Note.affiche(Note.CHROMATIQUE[i]);
		}
		return noms;
	}

	// Recharge le contenu d'un combo sans déclencher ses écouteurs (sinon le Controleur
	// reprojetterait la gamme sur le piano à chaque changement de langue/notation).
	private void rechargerCombo(JComboBox<String> combo, String[] items)
	{
		ActionListener[] ecouteurs = combo.getActionListeners();
		for (int i = 0; i < ecouteurs.length; i++)
		{
			combo.removeActionListener(ecouteurs[i]);
		}

		int sel = combo.getSelectedIndex();
		combo.setModel(new DefaultComboBoxModel<String>(items));
		if (sel >= 0 && sel < items.length)
		{
			combo.setSelectedIndex(sel);
		}

		for (int i = 0; i < ecouteurs.length; i++)
		{
			combo.addActionListener(ecouteurs[i]);
		}
	}

	private class EcouteurLangue implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Langue.anglais = (comboLangue.getSelectedIndex() == 1);
			appliquerLangue();
		}
	}

	private class EcouteurNotation implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Note.lettres = (comboNotation.getSelectedIndex() == 1);
			majAffichageNotes();
		}
	}

	public void ajouterEcouteur(ActionListener ecouteur)
	{
		this.boutonTrouver.addActionListener(ecouteur);
	}

	public void ajouterEcouteurGamme(ActionListener ecouteur)
	{
		this.comboNote.addActionListener(ecouteur);
		this.comboGamme.addActionListener(ecouteur);
	}

	public String getNoteChoisie()
	{
		// renvoie l'identité interne (solfège), quelle que soit la notation affichée
		return Note.CHROMATIQUE[this.comboNote.getSelectedIndex()];
	}

	public int getIndiceGammeChoisie()
	{
		return this.comboGamme.getSelectedIndex();
	}

	// Allume sur le piano exactement les notes passées (et éteint les autres).
	public void selectionnerNotes(ArrayList<String> notes)
	{
		for (int i = 0; i < this.touches.length; i++)
		{
			this.selection[i] = notes.contains(Note.CHROMATIQUE[i]);
			majCouleur(i);
		}
	}

	// Clic = bascule une touche ; glisser = « peint » le même état sur les touches survolées.
	private class EcouteurSouris extends MouseAdapter
	{
		private JLayeredPane clavier;
		private boolean peindre;   // état appliqué pendant le glissé (true = allume)
		private int dernier;       // dernière touche modifiée pendant ce glissé

		EcouteurSouris(JLayeredPane clavier)
		{
			this.clavier = clavier;
		}

		public void mousePressed(MouseEvent e)
		{
			int i = indiceDe(e.getComponent());
			if (i < 0)
			{
				return;
			}
			selection[i] = !selection[i];
			this.peindre = selection[i];
			this.dernier = i;
			majCouleur(i);
		}

		public void mouseDragged(MouseEvent e)
		{
			// la souris est captée par la touche de départ -> on retrouve celle sous le curseur
			Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), this.clavier);
			Component c = SwingUtilities.getDeepestComponentAt(this.clavier, p.x, p.y);
			int i = indiceDe(c);
			if (i < 0 || i == this.dernier)
			{
				return;
			}
			selection[i] = this.peindre;
			this.dernier = i;
			majCouleur(i);
		}

		private int indiceDe(Component c)
		{
			for (int k = 0; k < touches.length; k++)
			{
				if (touches[k] == c)
				{
					return k;
				}
			}
			return -1;
		}
	}
}

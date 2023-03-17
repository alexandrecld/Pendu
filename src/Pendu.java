import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class Pendu extends JFrame implements ActionListener {

    private JLabel motSecretLabel;
    private JLabel erreursLabel;
    private JTextField lettreField;
    private JButton propositionButton;
    private JButton recommencerButton;
    private ArrayList<String> mots;
    private String motSecret;
    private StringBuilder motCourant;
    private int nbErreurs;
    private String path = "/home/admindl/Documents/CDA2023/devjava/Pendu/src/dictionnaire.csv";

    private static final int MAX_ERREURS = 7;

    public Pendu() {
        super("Jeu du pendu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        // Labels pour le mot secret et le nombre d'erreurs
        motSecretLabel = new JLabel();
        add(motSecretLabel);

        erreursLabel = new JLabel();
        add(erreursLabel);

        // Champ de texte pour proposer une lettre
        lettreField = new JTextField();
        add(lettreField);

        // Boutons pour proposer une lettre et recommencer le jeu
        JPanel buttonPanel = new JPanel();
        propositionButton = new JButton("Proposer");
        propositionButton.addActionListener(this);
        buttonPanel.add(propositionButton);

        recommencerButton = new JButton("Recommencer");
        recommencerButton.addActionListener(this);
        buttonPanel.add(recommencerButton);

        add(buttonPanel);

        // Chargement du dictionnaire de mots
        mots = new ArrayList<String>();
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new FileNotFoundException("Le fichier n'existe pas");
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                mots.add(line);
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Erreur de chargement du dictionnaire : " + e.getMessage());
        }

        // Nouvelle partie
        nouvellePartie();
    }

    // Nouvelle partie
    private void nouvellePartie() {
        // Choix aléatoire du mot secret
        Random rand = new Random();
        motSecret = mots.get(rand.nextInt(mots.size())).toUpperCase();

        // Initialisation du mot courant avec des tirets
        motCourant = new StringBuilder();
        motCourant.append("-".repeat(motSecret.length()));

        // Réinitialisation du nombre d'erreurs
        nbErreurs = 0;

        // Mise à jour de l'interface
        updateMotSecret();
        updateErreurs();
        lettreField.setText("");
        propositionButton.setEnabled(true);
        lettreField.setEnabled(true);
        lettreField.requestFocus();
    }

    // Mise à jour du label pour le mot secret
    private void updateMotSecret() {
        motSecretLabel.setText(motCourant.toString());
    }

    // Mise à jour du label pour le nombre d'erreurs
    private void updateErreurs() {
        erreursLabel.setText("Erreurs : " + nbErreurs + "/" + MAX_ERREURS);
    }

    // Vérifie si le jeu est terminé (victoire ou défaite)
    private boolean isGameOver() {
        return (nbErreurs >= MAX_ERREURS) || (!motCourant.toString().contains("-"));
    }

    // Gestion des événements des boutons
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == propositionButton) {
            String lettre = lettreField.getText().toUpperCase();
            if (lettre.length() != 1) {
                JOptionPane.showMessageDialog(this, "Vous devez entrer une seule lettre", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else if (!Character.isLetter(lettre.charAt(0))) {
                JOptionPane.showMessageDialog(this, "Vous devez entrer une lettre", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                // Vérification de la proposition de lettre
                char c = lettre.charAt(0);
                boolean trouve = false;
                for (int i = 0; i < motSecret.length(); i++) {
                    if (motSecret.charAt(i) == c) {
                        motCourant.setCharAt(i, c);
                        trouve = true;
                    }
                }

                if (!trouve) {
                    nbErreurs++;
                }

                updateMotSecret();
                updateErreurs();

                if (isGameOver()) {
                    propositionButton.setEnabled(false);
                    lettreField.setEnabled(false);
                    lettreField.setText("");

                    if (motCourant.toString().equals(motSecret)) {
                        JOptionPane.showMessageDialog(this, "Bravo, vous avez gagné !", "Victoire", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Dommage, vous avez perdu. Le mot était " + motSecret, "Défaite", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    lettreField.setText("");
                    lettreField.requestFocus();
                }
            }
        } else if (source == recommencerButton) {
            nouvellePartie();
        }
    }

    public static void main(String[] args) {
        Pendu pendu = new Pendu();
        pendu.setVisible(true);
    }
}


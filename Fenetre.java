import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Color;

public class Fenetre {
	JFrame fenetre ;
	JTextArea jtf1;
	JTextArea jtf2;
	JPanel p = new JPanel();
	public Fenetre(String titre) {
		//création de la fenetre
		fenetre = new JFrame(titre);
		fenetre.setSize(2600, 200);
		p.setSize(2600, 200);
		jtf1 = new JTextArea();
		jtf1.setSize(fenetre.getWidth(), 40);
		jtf1.setBackground(Color.black);
		jtf1.setForeground(Color.white);
		jtf1.setFont(new Font(Font.MONOSPACED, Font.PLAIN,2*p.getWidth()/200));
		jtf2 = new JTextArea(); 
		jtf2.setSize(fenetre.getWidth(), 40);
		jtf2.setBackground(Color.black);
		jtf2.setForeground(Color.white);
		jtf2.setFont(new Font(Font.MONOSPACED, Font.PLAIN,2*p.getWidth()/200));
		p.setLayout(new GridLayout(2,1));
		p.add(jtf1);
		p.add(jtf2);
		fenetre.setContentPane(p);
		fenetre.setResizable(false);
		fenetre.setVisible(true);
	}
}

import java.util.HashMap;
import java.util.Map;


public class Test {
	
	public static void main(String[] args) {
	    Generateur g = new Generateur();
		g.start();
		Moniteur m = new Moniteur();
		m.start();
	}
}

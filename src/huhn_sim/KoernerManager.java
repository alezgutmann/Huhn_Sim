package huhn_sim;

public class KoernerManager {
	Korn[] koernerArray;
	int koernercount = 0;

	
	public KoernerManager() {
		koernerArray = new Korn[10];
	}
	
	public void addKorn(Korn korn) {
		 if (koernercount == koernerArray.length) {
	            // Resize array if full
	            Korn[] newArray = new Korn[koernerArray.length + 10];
	            System.arraycopy(koernerArray, 0, newArray, 0, koernerArray.length);
	            koernerArray = newArray;
	        }
	        koernerArray[koernercount++] = korn;
	}
	
	public void removeKorn(int index) {
		if (index < 0 || index >= koernercount) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
		// Shift elements to the left
		for (int i = index; i < koernercount - 1; i++) {
			koernerArray[i] = koernerArray[i + 1];
		}
		koernerArray[--koernercount] = null; // Clear the last element	
	}
	
	public int getKoernerCount() {
		return koernercount;
	}
	
	public Korn getKorn(int index) {
		return koernerArray[index];
	}
	
	public void render() {
		 for (int i = 0; i < koernercount; i++) {
	            if (koernerArray[i] != null) {
	                koernerArray[i].render();
	            }
	        }
	}
}

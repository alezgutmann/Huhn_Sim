package huhn_sim;

import libs.Vektor2D;

public abstract class BasisObjekt {
   public int id;
   public Vektor2D position;
   
   public BasisObjekt() {
      this(new Vektor2D(0,0));
   }
   
   public BasisObjekt(Vektor2D position) {
      this.position = new Vektor2D(position);
   }
   
   public Vektor2D getPosition() {
	   return position;
   }
   
   public void setPosition(Vektor2D pos) {
	   position = new Vektor2D(pos);
   }
  
   public abstract void render();
}

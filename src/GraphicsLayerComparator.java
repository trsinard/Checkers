
import java.util.Comparator;
public class GraphicsLayerComparator<E> implements Comparator<E> {
	
	
    public int compare(E d1, E d2) {
    	if(d1 instanceof Drawable && d1 instanceof Drawable){
    		return ((Drawable)d1).getZ() - ((Drawable)d2).getZ();
    	}
		return 0;
	}
}

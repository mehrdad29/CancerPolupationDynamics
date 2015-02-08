import java.util.Comparator;


public class QComparator implements Comparator<MyCore>{

	@Override
	public int compare(MyCore q0, MyCore q1) {
		// TODO Auto-generated method stub
		if (q0.time == q1.time)
			return 0;
		else if (q0.time > q1.time)
			return 1;
		else
			return -1;
	}
	
}

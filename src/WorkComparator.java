import java.util.Comparator;


public class WorkComparator implements Comparator<Work>{

	@Override
	public int compare(Work w0, Work w1) {
		// TODO Auto-generated method stub
		if (w0.type == w1.type) {
			if (w0.workNumber > w1.workNumber)
				return 1;
			else
				return -1;
		}
		else if (w0.type > w1.type)
			return 1;
		else
			return -1;
	}

}

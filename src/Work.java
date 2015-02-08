
public class Work implements Comparable<Work> {
	int type;
	int workNumber;
	double deadline;
	double startTime;
	double timeInQueue = 0;
	double timeStartAQueue = 0;
	
	public Work(int type, int workNumber, double deadline, double startTime, double timeInQueue, double timeStartAQueue) {
		super();
		this.type = type;
		this.workNumber = workNumber;
		this.deadline = deadline;
		this.startTime = startTime;
		this.timeInQueue = timeInQueue;
		this.timeStartAQueue = timeStartAQueue;
	}

	@Override
	public int compareTo(Work w1) {
		if (this.type == w1.type) {
			if (this.workNumber > w1.workNumber)
				return 1;
			else
				return -1;
		}
		else if (this.type > w1.type)
			return 1;
		else
			return -1;
	}
}

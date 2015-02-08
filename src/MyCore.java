
public class MyCore implements Comparable<MyCore> {
	int serverNumber;
	int coreNumber;
	double time;
	int workNumber;
	int type;
	double deadline;
	double startTime;
	double timeInQueue = 0;;
	
	public MyCore(int serverNumber, int coreNumber, double time, int workNumber, int type, double deadline, double startTime, double timeInQueue) {
		super();
		this.serverNumber = serverNumber;
		this.coreNumber = coreNumber;
		this.time = time;
		this.workNumber = workNumber;
		this.type = type;
		this.deadline = deadline;
		this.startTime = startTime;
		this.timeInQueue = timeInQueue;
	}

	@Override
	public int compareTo(MyCore q1) {
		if (this.time == q1.time)
			return 0;
		else if (this.time > q1.time)
			return 1;
		else
			return -1;
	}	
}

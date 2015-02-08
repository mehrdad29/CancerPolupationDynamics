import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class Main {

	static Comparator<Work> workComparator = null;
	static Comparator<MyCore> qComparator = null;

	static int numServers;
	static int[] numCores;

	static double inputRate;
	static double meanDeadline;
	static double serviceRateTimer;
	static double[][] serviceRateCores;

	static boolean isTimerBusy;
	static boolean[][] isBusyCores;

	static int[] queuesLength;

	static double time;
	static int workNumber;

	static int numberOfExits;
	static int numberOfExits1;
	static int numberOfExits2;

	static Random rand;
	static PriorityQueue[] queues;

	static PriorityQueue<MyCore> myCores;

	static double sumTimeSpentSystem = 0;
	static double sumTimeSpentSystem1 = 0;
	static double sumTimeSpentSystem2 = 0;
	static Vector<Double> timeSpentSystemV;
	static Vector<Double> timeSpentSystemV1;
	static Vector<Double> timeSpentSystemV2;

	static double sumTimeSpentQueue = 0;
	static double sumTimeSpentQueue1 = 0;
	static double sumTimeSpentQueue2 = 0;
	static Vector<Double> timeSpentQueueV;
	static Vector<Double> timeSpentQueueV1;
	static Vector<Double> timeSpentQueueV2;

	static Vector<Double> timeSpentInQueueTimer;

	static Vector[] timeSpentInQueueServers;

	static int numDeadlinePassed = 0;
	static int numDeadlinePassed1 = 0;
	static int numDeadlinePassed2 = 0;

	static double sumTimerQueueLength = 0;
	static double[] sumServerQueueLength;

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);

		qComparator = new QComparator();

		myCores = new PriorityQueue<MyCore>(1, qComparator);

		numServers = s.nextInt();
		inputRate = s.nextDouble();
		meanDeadline = s.nextDouble();
		serviceRateTimer = s.nextDouble();

		numCores = new int[numServers];
		serviceRateCores = new double[numServers][];

		isBusyCores = new boolean[numServers][];
		isTimerBusy = false;

		queues = new PriorityQueue[numServers + 1];

		numberOfExits = 0;
		numberOfExits1 = 0;
		numberOfExits2 = 0;
		sumServerQueueLength = new double[numServers];

		for (int i = 0; i < numServers + 1; i++)
			queues[i] = new PriorityQueue<Work>(1, workComparator);

		for (int i = 0; i < numServers; i++) {
			numCores[i] = s.nextInt();
			serviceRateCores[i] = new double[numCores[i]];
			isBusyCores[i] = new boolean[numCores[i]];
			for (int j = 0; j < numCores[i]; j++) {
				serviceRateCores[i][j] = s.nextDouble();
			}
		}

		time = 0;
		workNumber = 0;
		workComparator = new WorkComparator();

		rand = new Random();

		timeSpentQueueV = new Vector<>();
		timeSpentQueueV1 = new Vector<>();
		timeSpentQueueV2 = new Vector<>();
		timeSpentSystemV = new Vector<>();
		timeSpentSystemV1 = new Vector<>();
		timeSpentSystemV2 = new Vector<>();

		timeSpentInQueueTimer = new Vector<>();
		timeSpentInQueueServers = new Vector[numServers];

		for (int i = 0; i < numServers; i++)
			timeSpentInQueueServers[i] = new Vector<Double>();

		MyCore tempCore = null;

		outest: while (true) {
			tempCore = myCores.poll();
			// System.out.println(numberOfExits);
			if (tempCore != null) {
				time = tempCore.time;
			}
			System.out.println(numberOfExits);
			if (tempCore == null || tempCore.serverNumber == -2) {
				if (tempCore != null) {
					if (queues[0].isEmpty() && isTimerBusy == false) {
						myCores.add(new MyCore(-1, -1, time
								+ getRandomExp(serviceRateTimer),
								tempCore.workNumber, tempCore.type,
								getDeadline(meanDeadline), time, 0));
						isTimerBusy = true;
					} else {
						queues[0].add(new Work(tempCore.type,
								tempCore.workNumber, getDeadline(meanDeadline),
								time, -time, time));

						if (numberOfExits > 20000 || isEnded()) {
							break outest;
						} else if (numberOfExits > 5000) {
							sumTimeSpentQueue -= time;
							sumTimerQueueLength -= time;
							if (tempCore.type == 1) {
								sumTimeSpentQueue1 -= time;
							} else {
								sumTimeSpentQueue2 -= time;
							}
						}

					}
				}
				myCores.add(new MyCore(-2, -1, time + getRandomExp(inputRate),
						workNumber, getType(), 0, 0, 0));
				workNumber++;
			} else if (tempCore.serverNumber == -1) {
				/*
				 * if (!queues[0].isEmpty()) { Work tempWork = null; while
				 * (!queues[0].isEmpty()) { tempWork = (Work) queues[0].poll();
				 * if (tempWork.deadline > time) break; else { tempWork = null;
				 * numberOfExits++; } } if (tempWork != null) { myCores.add(new
				 * MyCore(-1, -1, time + getRandomExp(serviceRateTimer),
				 * tempWork.workNumber, tempWork.type, tempWork.deadline,
				 * tempWork.startTime)); isTimerBusy = true; } else {
				 * isTimerBusy = false; } } else { isTimerBusy = false; }
				 */

				Work tempWork;
				for (int i = 0; i < numServers + 1; i++) {
					tempWork = null;
					PriorityQueue<Work> tempQueue = new PriorityQueue<Work>(1,
							workComparator);
					tempQueue.clear();
					while (!queues[i].isEmpty()) {
						tempWork = (Work) queues[i].poll();
						if (tempWork.deadline > time) {
							tempQueue.add(tempWork);
						} else {

							if (numberOfExits > 20000 || isEnded()) {
								break outest;
							} else if (numberOfExits > 5000) {
								sumTimeSpentSystem += tempWork.deadline
										- tempWork.startTime;
								numDeadlinePassed++;
								sumTimeSpentQueue += time;
								timeSpentSystemV.add(tempWork.deadline
										- tempWork.startTime);
								timeSpentQueueV.add(tempWork.timeInQueue
										+ tempWork.deadline);
								if (i == 0)
									timeSpentInQueueTimer.add(tempWork.deadline
											- tempWork.timeStartAQueue);
								else
									timeSpentInQueueServers[i - 1]
											.add(tempWork.deadline
													- tempWork.timeStartAQueue);
								if (tempCore.type == 1) {
									sumTimeSpentSystem1 += tempWork.deadline
											- tempWork.startTime;
									numDeadlinePassed1++;
									sumTimeSpentQueue1 += time;
									timeSpentSystemV1.add(tempWork.deadline
											- tempWork.startTime);
									timeSpentQueueV1.add(tempWork.timeInQueue
											+ tempWork.deadline);
									numberOfExits1++;
								} else {
									sumTimeSpentSystem2 += tempWork.deadline
											- tempWork.startTime;
									numDeadlinePassed2++;
									sumTimeSpentQueue2 += time;
									timeSpentSystemV2.add(tempWork.deadline
											- tempWork.startTime);
									timeSpentQueueV2.add(tempWork.timeInQueue
											+ tempWork.deadline);
									numberOfExits2++;
								}
								if (i == 0) {
									sumTimerQueueLength += time;
								} else {
									sumServerQueueLength[i - 1] += time;
								}

							}

							tempWork = null;
							numberOfExits++;
						}
					}
					queues[i] = new PriorityQueue<Work>(tempQueue);
				}
				if (!queues[0].isEmpty()) {
					tempWork = (Work) queues[0].poll();
					myCores.add(new MyCore(-1, -1, time
							+ getRandomExp(serviceRateTimer),
							tempWork.workNumber, tempWork.type,
							tempWork.deadline, tempWork.startTime,
							tempWork.timeInQueue + time));

					timeSpentInQueueTimer.add(time - tempWork.timeStartAQueue);
					sumTimeSpentQueue += time;
					if (tempCore.type == 1) {
						sumTimeSpentQueue1 += time;
					} else {
						sumTimeSpentQueue2 += time;
					}
					sumTimerQueueLength += time;

					isTimerBusy = true;
				} else {
					isTimerBusy = false;
				}

				int sumTemp = 0;
				int min = Integer.MAX_VALUE;
				for (int i = 0; i < numServers; i++) {
					if (queues[i + 1].size() < min) {
						min = queues[i + 1].size();
						sumTemp = 1;
					} else if (queues[i + 1].size() == min) {
						sumTemp++;
					}
				}
				int randomQueue = rand.nextInt(sumTemp);
				for (int i = 0; i < numServers; i++) {
					if (queues[i + 1].size() == min && randomQueue == 0) {
						if (queues[i + 1].size() != 0 && tempCore.deadline > time) {
							queues[i + 1].add(new Work(tempCore.type,
									tempCore.workNumber, tempCore.deadline,
									tempCore.startTime, tempCore.timeInQueue
											- time, time));

							sumTimeSpentQueue -= time;
							if (tempCore.type == 1) {
								sumTimeSpentQueue1 -= time;
							} else {
								sumTimeSpentQueue2 -= time;
							}
							sumServerQueueLength[i] -= time;

						} else {
							boolean flag = false;
							for (int j = 0; j < numCores[i]; j++) {
								if (isBusyCores[i][j] == false) {
									myCores.add(new MyCore(
											i,
											j,
											time
													+ getRandomExp(serviceRateCores[i][j]),
											tempCore.workNumber, tempCore.type,
											tempCore.deadline,
											tempCore.startTime,
											tempCore.timeInQueue));
									isBusyCores[i][j] = true;
									flag = true;
								}
							}
							if (flag == false && tempCore.deadline > time) {
								queues[i + 1].add(new Work(tempCore.type,
										tempCore.workNumber, tempCore.deadline,
										tempCore.startTime,
										tempCore.timeInQueue - time, time));

								sumTimeSpentQueue -= time;
								if (tempCore.type == 1) {
									sumTimeSpentQueue1 -= time;
								} else {
									sumTimeSpentQueue2 -= time;
								}
								sumServerQueueLength[i] -= time;

							}
						}
						break;
					} else if (queues[i + 1].size() == min) {
						randomQueue--;
					}
				}
			} else {
				/*
				 * if (!queues[tempCore.serverNumber + 1].isEmpty()) { Work
				 * tempWork = null; while (!queues[tempCore.serverNumber +
				 * 1].isEmpty()) { tempWork = (Work)
				 * queues[tempCore.serverNumber + 1].poll(); if
				 * (tempWork.deadline > time) break; else { tempWork = null;
				 * numberOfExits++; } } if (tempWork != null) { myCores.add(new
				 * MyCore( tempCore.serverNumber, tempCore.coreNumber, time +
				 * getRandomExp
				 * (serviceRateCores[tempCore.serverNumber][tempCore.
				 * coreNumber]), tempWork.workNumber, tempWork.type,
				 * tempWork.deadline, tempWork.startTime));
				 * isBusyCores[tempCore.serverNumber][tempCore.coreNumber] =
				 * true; } else {
				 * isBusyCores[tempCore.serverNumber][tempCore.coreNumber] =
				 * false; } } else {
				 * isBusyCores[tempCore.serverNumber][tempCore.coreNumber] =
				 * false; }
				 */

				Work tempWork;
				for (int i = 0; i < numServers + 1; i++) {
					tempWork = null;
					PriorityQueue<Work> tempQueue = new PriorityQueue<Work>(1,
							workComparator);
					tempQueue.clear();
					while (!queues[i].isEmpty()) {
						tempWork = (Work) queues[i].poll();
						if (tempWork.deadline > time) {
							tempQueue.add(tempWork);
						} else {
							if (numberOfExits > 20000 || isEnded()) {
								break outest;
							} else if (numberOfExits > 5000) {
								sumTimeSpentSystem += tempWork.deadline
										- tempWork.startTime;
								numDeadlinePassed++;
								sumTimeSpentQueue += time;
								timeSpentSystemV.add(tempWork.deadline
										- tempWork.startTime);
								timeSpentQueueV.add(tempWork.timeInQueue
										+ tempWork.deadline);
								if (i == 0)
									timeSpentInQueueTimer.add(tempWork.deadline
											- tempWork.timeStartAQueue);
								else
									timeSpentInQueueServers[i - 1]
											.add(tempWork.deadline
													- tempWork.timeStartAQueue);
								if (tempCore.type == 1) {
									sumTimeSpentSystem1 += tempWork.deadline
											- tempWork.startTime;
									numDeadlinePassed1++;
									sumTimeSpentQueue1 += time;
									timeSpentSystemV1.add(tempWork.deadline
											- tempWork.startTime);
									timeSpentQueueV1.add(tempWork.timeInQueue
											+ tempWork.deadline);
									numberOfExits1++;
								} else {
									sumTimeSpentSystem2 += tempWork.deadline
											- tempWork.startTime;
									numDeadlinePassed2++;
									sumTimeSpentQueue2 += time;
									timeSpentSystemV2.add(tempWork.deadline
											- tempWork.startTime);
									timeSpentQueueV2.add(tempWork.timeInQueue
											+ tempWork.deadline);
									numberOfExits2++;
								}

								if (i == 0) {
									sumTimerQueueLength += time;
								} else {
									sumServerQueueLength[i - 1] += time;
								}
							}

							tempWork = null;
							numberOfExits++;
						}
					}
					queues[i] = new PriorityQueue<Work>(tempQueue);
				}
				if (!queues[tempCore.serverNumber + 1].isEmpty()) {
					tempWork = (Work) queues[tempCore.serverNumber + 1].poll();
					myCores.add(new MyCore(
							tempCore.serverNumber,
							tempCore.coreNumber,
							time
									+ getRandomExp(serviceRateCores[tempCore.serverNumber][tempCore.coreNumber]),
							tempWork.workNumber, tempWork.type,
							tempWork.deadline, tempWork.startTime,
							tempWork.timeInQueue + time));
					isBusyCores[tempCore.serverNumber][tempCore.coreNumber] = true;

					timeSpentInQueueServers[tempCore.serverNumber].add(time
							- tempWork.timeStartAQueue);

					sumTimeSpentQueue += time;
					if (tempCore.type == 1) {
						sumTimeSpentQueue1 += time;
					} else {
						sumTimeSpentQueue2 += time;
					}
					sumServerQueueLength[tempCore.serverNumber] += time;

				} else {
					isBusyCores[tempCore.serverNumber][tempCore.coreNumber] = false;
				}

				numberOfExits++;

				if (numberOfExits > 20000 || isEnded()) {
					break outest;
				} else if (numberOfExits > 5000) {
					sumTimeSpentSystem += time - tempCore.startTime;
					timeSpentSystemV.add(time - tempCore.startTime);
					timeSpentQueueV.add(tempCore.timeInQueue);
					if (tempCore.type == 1) {
						sumTimeSpentSystem1 += time - tempCore.startTime;
						timeSpentSystemV1.add(time - tempCore.startTime);
						timeSpentQueueV1.add(tempCore.timeInQueue);
						numberOfExits1++;
					} else {
						sumTimeSpentSystem2 += time - tempCore.startTime;
						timeSpentSystemV2.add(time - tempCore.startTime);
						timeSpentQueueV2.add(tempCore.timeInQueue);
						numberOfExits2++;
					}
				}
			}
		}

		System.out.println("Average Time Spent in System: "
				+ (getSum(timeSpentSystemV) / timeSpentSystemV.size()));
		System.out.println("Average Time Spent in System (Type 1): "
				+ (getSum(timeSpentSystemV1) / timeSpentSystemV1.size()));
		System.out.println("Average Time Spent in System (Type 2): "
				+ (getSum(timeSpentSystemV2) / timeSpentSystemV2.size()));

		System.out.println("Average Time Spent in Queue: "
				+ (getSum(timeSpentQueueV) / timeSpentQueueV.size()));
		System.out.println("Average Time Spent in Queue (Type 1): "
				+ (getSum(timeSpentQueueV1) / timeSpentQueueV1.size()));
		System.out.println("Average Time Spent in Queue (Type 2): "
				+ (getSum(timeSpentQueueV2) / timeSpentQueueV2.size()));

		System.out.println("Average Dedline Passed: "
				+ (numDeadlinePassed / timeSpentSystemV.size()));
		System.out.println("Average Dedline Passed (Type 1): "
				+ (numDeadlinePassed1 / timeSpentSystemV1.size()));
		System.out.println("Average Dedline Passed (Type 2): "
				+ (numDeadlinePassed2 / timeSpentSystemV2.size()));

		System.out.println("Average Queue Length (Timer): "
				+ (getSum(timeSpentInQueueTimer) / time));

		for (int i = 0; i < numServers; i++) {
			System.out.println("Average Queue Length (Server " + (i + 1)
					+ "): " + (getSum(timeSpentInQueueServers[i]) / time));
		}

		System.out.println("Number of Required Work for Simulation to Finish: "
				+ numberOfExits);
	}

	public static boolean isEnded() {
		return isEnough(timeSpentQueueV) & isEnough(timeSpentQueueV1)
				& isEnough(timeSpentQueueV2) & isEnough(timeSpentSystemV)
				& isEnough(timeSpentSystemV1) & isEnough(timeSpentSystemV2);
	}

	public static double getSum(Vector<Double> inputs) {
		double output = 0;
		for (int i = 0; i < inputs.size(); i++)
			output += inputs.elementAt(i);
		return output;
	}

	static boolean isEnough(Vector<Double> inputs) {
		int n = inputs.size();
		double avg = AVG(inputs);
		double var = 0;
		for (int i = 0; i < inputs.size(); i++)
			var = var + Math.pow(inputs.elementAt(i) - avg, 2);
		var = var / n;
		double sigma = Math.sqrt(var);

		double accur = (1.96 * sigma) / (Math.sqrt(n) * avg);

		return false;
	}

	static double AVG(Vector<Double> inputs) {
		double avg = 0;
		for (int i = 0; i < inputs.size(); i++)
			avg = avg + inputs.elementAt(i);

		return avg / inputs.size();
	}

	public static double getRandomExp(double rate) {
		return -rate * Math.log(rand.nextDouble());
	}

	public static int getType() {
		if (rand.nextDouble() < 0.1)
			return 0;
		else
			return 1;
	}

	public static double getDeadline(double mean) {
		return (-1 / mean) * Math.log(rand.nextDouble()) + time;
	}

}

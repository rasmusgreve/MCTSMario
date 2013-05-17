package itu.ejuuragr;
import java.io.*;
import java.util.*;
public class WilcoxonAnalysis {

	StatsResult[] statsResults;
	StatsResult UCT, Winning;
	
	/**
	 * Class for doing a Wilcoxon signed rank analysis
	 * Expects a semicolon (;) separated file with first row being names, and all next rows being samples
	 * Each column is expected to be a different test
	 */
	public WilcoxonAnalysis(String filename, int samples, int tests) throws Exception
	{
		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new FileReader(f));

		statsResults = new StatsResult[tests];
		
		String line = reader.readLine();
		String[] names = line.split(";");
		if (tests != names.length) throw new IllegalArgumentException("tests != names.length");
		for (int i = 0; i < names.length; i++)
		{
			statsResults[i] = new StatsResult(names[i], samples);
		}
	
		line = reader.readLine();
		int i = 0;
		while (line != null)
		{
			String[] values = line.split(";");
			if (tests != values.length) throw new IllegalArgumentException("tests != values.length");
			for (int j = 0; j < values.length; j++)
			{
				statsResults[j].values[i] = Double.parseDouble(values[j]);
			}
			i++;
			line = reader.readLine();
		}
		reader.close();
		System.out.println("Data loaded!");
		
	}
	
	public void FindUCTWinning()
	{
		UCT = new StatsResult("UCT", statsResults[0].values.length);
		for (int i = 0; i < statsResults[0].values.length; i++)
		{
			UCT.values[i] = statsResults[0].values[i];
		}
		Winning = new StatsResult("Winning", statsResults[0].values.length);
		for (int sample = 0; sample < statsResults[0].values.length; sample++)
		{
			double maxValue = 0;
			for (int test = 0; test < statsResults.length; test++)
			{
				if (statsResults[test].values[sample] > maxValue) maxValue = statsResults[test].values[sample];
			}
			Winning.values[sample] = maxValue;
		}
		System.out.println("UCT and Winning determined");
	}
	
	private enum Comparison {UCT, Winning}
	
	public void WilcoxonSignedRankTest(Comparison comp, int resultIndex)
	{
		StatsResult first = (comp == Comparison.UCT) ? UCT : Winning;
		StatsResult second = statsResults[resultIndex];
		int samples = first.values.length;
		AbsDifAndSign[] primaryDifs = new AbsDifAndSign[samples];
		//Step 1 - calc absdif and sign
		for (int sample = 0; sample < samples; sample++)
		{
			primaryDifs[sample] = new AbsDifAndSign(first.values[sample], second.values[sample]);
		}		
		//Step 2 - remove 0 absdifs
		ArrayList<AbsDifAndSign> reducedDifsList = new ArrayList<AbsDifAndSign>();
		for (AbsDifAndSign abs : primaryDifs)
		{
			if (abs.absDif <= 0.01) continue; //Ignore 0 difs
			reducedDifsList.add(abs);
		}
		AbsDifAndSign[] reducedDifs = reducedDifsList.toArray(new AbsDifAndSign[reducedDifsList.size()]);
		//Step 3 - order
		Arrays.sort(reducedDifs);

		//Step 4 - rank
		int curRank = 1;
		for (int i = 0; i < reducedDifs.length; i++)
		{
			int rank = curRank;
			//Count how many have the same dif
			int count = 1;
			for (int j = i+1; j < reducedDifs.length; j++)
			{
				if (reducedDifs[j].absDif == reducedDifs[i].absDif) count++;
				else break;
			}
			//Adjust rank
			rank = rank / count;
			//Update ranks
			for (int j = 0; j < count; j++)
			{
				reducedDifs[i+j].rank = rank;
			}
			i += count-1;
			curRank += count;
		}
		
		//Step 5 - calculate W ( abs(sum(sign*rank)) )
		int W = 0;
		for (AbsDifAndSign dif : reducedDifs)
		{
			W += dif.rank * dif.sign;
		}
		W = Math.abs(W);
		
		//Step 6 - calculate z
		int Nr = reducedDifs.length;
		double sigma = Math.sqrt((Nr * (Nr + 1) * (2 * Nr + 1))/6);
		double z = (W - .5) / sigma;
		
		
		//Extra step - display p interval
		System.out.print(second.title + " same as " + first.title + " : ");
		if (z < 1.645)
		{
			System.out.print("p > 0.05");
		}
		else if (z < 1.96)
		{
			System.out.print("0.05 > p > 0.025");
		}
		else if (z < 2.326)
		{
			System.out.print("0.025 > p > 0.01");
		}
		else if (z < 2.576)
		{
			System.out.print("0.01 > p > 0.005");
		}
		else if (z < 3.291)
		{
			System.out.print("0.005 > p > 0.0005");
		}
		else
		{
			System.out.print("p < 0.0005");
		}
		System.out.println();
	}
	
	private class AbsDifAndSign implements Comparable<AbsDifAndSign>
	{
		public double absDif;
		public double sign;
		public int rank;
		public AbsDifAndSign(double a, double b)
		{
			absDif = Math.abs(a-b);
			sign = Math.signum(a-b);
		}
		@Override
		public int compareTo(AbsDifAndSign other) {
			return (this.absDif < other.absDif) ? -1 : ((this.absDif > other.absDif) ? 1 : 0);
		}
	}
	
	private class StatsResult
	{
		public String title;
		public double[] values;
		public StatsResult(String title, int samples)
		{
			this.title = title;
			this.values = new double[samples];
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		WilcoxonAnalysis x = new WilcoxonAnalysis("Mario stats data2.csv", 100, 64);
		x.FindUCTWinning();
		for (int i = 0; i < 64; i++)
			x.WilcoxonSignedRankTest(Comparison.UCT, i);

		for (int i = 0; i < 64; i++)
			x.WilcoxonSignedRankTest(Comparison.Winning, i);
	}

}

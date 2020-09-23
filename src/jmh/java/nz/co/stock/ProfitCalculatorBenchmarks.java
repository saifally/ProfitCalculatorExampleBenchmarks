package nz.co.stock;

import static java.util.Comparator.comparingInt;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class ProfitCalculatorBenchmarks {

	/*
	 * Measures the performance of Brute force logic
	 */

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public void measureCalculateMaxProfitForStockPricesBruteForce() throws InterruptedException {
		
		List<Integer> dataForBenchmark = IntStream.rangeClosed(0, 1000)
			    .boxed().collect(Collectors.toList());
		
		calculateMaxProfitForStockPricesBruteForce(dataForBenchmark);
	}

	/*
	 * Measures the performance of Brute force logic
	 */

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public void measureCalculateMaxProfitForStockPricesUsingStream() throws InterruptedException {
		List<Integer> dataForBenchmark = IntStream.rangeClosed(0, 1000)
			    .boxed().collect(Collectors.toList());
		
		calculateMaxProfitForStockPricesUsingStream(dataForBenchmark);
	}

	/*
	 * This benchmark type measures the performance of the logic
	 */

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public void measureCalculateMaxProfitForStockPricesInOnePass() throws InterruptedException {
		
		List<Integer> dataForBenchmark = IntStream.rangeClosed(0, 1000)
			    .boxed().collect(Collectors.toList());
		
		calculateMaxProfitForStockPricesInOnePass(dataForBenchmark);
	}

	// Brute force , a bit inefficient but good for TDD , Benchmark and readablility
	public Integer calculateMaxProfitForStockPricesBruteForce(List<Integer> stockPrices) {

		// We need minimum two prices
		if (stockPrices.size() < 2) {
			return 0;
		}
		
		// First pass , get the position of the lowest stock price
		// Also ignore the last one , we have to sell before that
		int minIndex = IntStream.range(0, stockPrices.size() - 1)
				.boxed()
				.min(comparingInt(stockPrices::get))
				.get();

		// Second and smaller pass, get Maximum price after minimum has happened
		int maxIndex = IntStream.range(minIndex, stockPrices.size())
				.boxed()
				.max(comparingInt(stockPrices::get))
				.get();

		// 3rd and 4th fetch
		return stockPrices.get(maxIndex) - stockPrices.get(minIndex);
	}

	// Java 8 code , a bit more efficient
	public Integer calculateMaxProfitForStockPricesUsingStream(List<Integer> stockPrices) {

		// We need minimum two prices
		if (stockPrices.size() < 2) {
			return 0;
		}

		// First pass , get the position of the lowest stock price, ignore the last one
		// , we have to sell before that
		int minStockPriceIndex = IntStream.range(0, stockPrices.size() - 1)
				.boxed()
				.min(comparingInt(stockPrices::get))
				.get();

		// Second operation , fetch the lowest value
		int mimimumStockPrice = stockPrices.get(minStockPriceIndex);

		// Third and smaller pass, get Maximum price after Minimum has happened
		Integer maximumProfit = stockPrices
				.subList(minStockPriceIndex, stockPrices.size())
				.stream()
				.map(stockPrice -> (stockPrice - mimimumStockPrice))
				.max(Comparator.comparing(Integer::valueOf))
				.get();

		return maximumProfit;
	}

	// One Pass using Pre Java 8 - Old school
	// Can not be done in Java 8 Stream because lambdas can not use global variable
	public Integer calculateMaxProfitForStockPricesInOnePass(List<Integer> stockPrices) {

		// Initialize the values
		Integer lowestStockPrice = stockPrices.get(0);
		Integer maxProfit = 0;
		Integer profit = 0;

		for (Integer stockPrice : stockPrices) {
			// Keep assigning the lowest stock prices
			if (lowestStockPrice > stockPrice) {
				lowestStockPrice = stockPrice;
			} else {
				profit = stockPrice - lowestStockPrice;
			}

			// Keep checking if there is a bigger profit
			if (profit > maxProfit) {
				maxProfit = profit;
			}
		}
		return maxProfit;
	}

	/*
	 *
	 * Run the Java API of JMH
	 */

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + ProfitCalculatorBenchmarks.class.getSimpleName() + ".*")
				.warmupIterations(1)
				.measurementIterations(1)
				.forks(1)
				.build();

		new Runner(opt).run();
	}

}

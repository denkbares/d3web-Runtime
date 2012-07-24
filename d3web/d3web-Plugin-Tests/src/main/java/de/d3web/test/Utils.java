package de.d3web.test;

import de.d3web.testing.ArgsCheckResult;


public class Utils {

	public static ArgsCheckResult testArgNumber(String [] args, int numberOfArgumentsExpected, String testname) {
			if (args.length == numberOfArgumentsExpected) return new ArgsCheckResult(args);

			if (args.length > numberOfArgumentsExpected) {
				ArgsCheckResult result = new ArgsCheckResult(args);
				result.setWarning(args.length - 1,
						"Too many arguments passend for test '" + testname
								+ "': Expected number of arguments: "
								+ numberOfArgumentsExpected + " - found: " + args.length);
				return result;
			}

			ArgsCheckResult result = new ArgsCheckResult(args);
			result.setError(0,
					"Not enough arguments for execution of test '" + testname
							+ "'. Expected number of arguments: "
							+ numberOfArgumentsExpected + " - found: " + args.length);
			return result;
		}
}

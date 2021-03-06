package org.signalml.math;

import org.apache.commons.math.complex.Complex;

/**
 * This class contains various methods operating on arrays for
 * convolution, reversing the order of elements in array etc.
 *
 * @author Piotr Szachewicz
 */
public class ArrayOperations {

	/**
	 * Returns the array extended to a new size. New elements are filled with
	 * zeros. New array size must be greater or equal to the original
	 * array size.
	 * @param array the array to be resized
	 * @param newSize the new size for the array
	 * @return the resized array
	 */
	public static double[] padArrayWithZerosToSize(double[] array, int newSize) {

		assert(newSize >= array.length);

		double[] paddedArray = new double[newSize];

		System.arraycopy(array, 0, paddedArray, 0, array.length);

		for (int i = array.length; i < paddedArray.length; i++) {
			paddedArray[i] = 0;
		}

		return paddedArray;

	}

	/**
	 * Returns the convolution of the two arrays.
	 *
	 * @param array1 first input array
	 * @param array2 second input array
	 * @return an array containing the discrete convolution of array1
	 * and array2
	 */
	public static double[] convolve(double[] array1, double[] array2) {

		int n = array1.length + array2.length - 1;
		double[] result = new double[n];
		double[] f = padArrayWithZerosToSize(array1, n);
		double[] g = padArrayWithZerosToSize(array2, n);

		int i;
		int fpos, gpos;

		for (i = 0; i < result.length; i++) {

			fpos = 0;
			gpos = i;
			for (; fpos <= i && gpos >= 0; fpos++, gpos--) {
				result[i] += f[fpos] * g[gpos];
			}

		}

		return result;
	}

	/**
	 * Returns an reversed copy of an array.
	 *
	 * @param array an array to be reversed
	 * @return the copy of the array having the order of its elements
	 * reversed
	 */
	public static double[] reverse(double[] array) {

		double[] newArray = new double[array.length];

		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[array.length - 1 - i];
		}

		return newArray;

	}

	/**
	 * Returns a trimmed copy of a given array.
	 *
	 * @param array an array to be trimmed
	 * @param size the size to which the array should be trimmed (must be
	 * less or equal to the original size of the array)
	 * @return a trimmed copy of the given array
	 */
	public static double[] trimArrayToSize(double[] array, int size) {

		assert(array.length >= size);

		double[] trimmedArray = new double[size];
		System.arraycopy(array, 0, trimmedArray, 0, size);
		return trimmedArray;

	}

	/**
	 * Returns a trimmed copy of a given array with the first numberOfElements
	 * removed.
	 *
	 * @param array an array to be trimmed
	 * @param numberOfElements the number of elements to be removed from
	 * array
	 * @return a trimmed copy of the given array
	 */
	public static double[] removeFirstElements(double[] array, int numberOfElements) {

		assert(array.length > numberOfElements);

		double[] trimmedArray = new double[array.length - numberOfElements];

		for (int i = 0; i < trimmedArray.length; i++)
			trimmedArray[i] = array[i + numberOfElements];

		return trimmedArray;

	}

	public static Complex[] convertDoubleArrayToComplex(double[] input) {
		Complex[] result = new Complex[input.length];

		for (int i = 0; i < result.length; i++)
			result[i] = new Complex(input[i], 0.0);

		return result;
	}

	/**
	 * Fills the arrray with a given value from the startIndex (incl.) to the endIndex (excl.).
	 * @param array the array to be filled with the value
	 * @param value the value that will be written to given range of the array
	 * @param startIndex first index to which the value will be written
	 * @param endIndex first index to which the value will not be written
	 */
	public static void fillArrayWithValue(double[] array, double value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			array[i] = value;
		}
	}

}

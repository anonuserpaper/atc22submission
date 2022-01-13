package Anon.controller.service.utils;

import jersey.repackaged.com.google.common.base.Joiner;
import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * Output wrappers and utility functions.
 * <p>
 */
class Output {

    /**
     * whether to output the time stamp before each output line.
     */
    private static boolean outputTime = true;
    private static BufferedWriter bufferedWriter = null;

    /**
     * Print out messages on both the stdout and the output file.
     *
     * @param str the message to be outputted.
     */
    public static void pl(String str) {
        print(str);
    }

    /**
     * Print out message
     *
     * @param str a array of strings
     */
    @SuppressWarnings("Since15")
    public static void pl(String[] str) {
        print(String.join(" ", str));
    }

    /**
     * Print out the message with more complex arguments.
     *
     * @param str  the message string with formatting symbols.
     * @param args the arguments for the formatting symbols.
     */
    public static void pl(String str, Object... args) {
        String display = String.format(str, args);
        print(display);
    }

    /**
     * Print a list of Integers
     *
     * @param numbers the list of integers
     */
    public static void pl(List<Integer> numbers) {
        String s = Joiner.on(",").join(numbers);
        print(s);
    }


    /**
     * Print out the message with more complex arguments plus padding in the front
     *
     * @param str  the message string with formatting symbols.
     * @param args the arguments for the formatting symbols.
     */
    public static void pl(int padding, String str, Object... args) {

        StringBuilder strBuilder = new StringBuilder(str);
        for (int i = 0; i < padding; i++) {
            strBuilder.insert(0, "\t");
        }
        str = strBuilder.toString();
        String display = String.format(str, args);

        print(display);
    }

    /**
     * a generic print function
     *
     * @param display the string to be displayed
     */
    private static void print(String display) {
        DateTime dt = TimeUtil.getLocalTime();

        if (outputTime) {
            // if outputTime is true, add the current time stamp at the beginning of each line.
            display = dt.toString() + " : " + display;
        }

        System.out.println(display);
        System.out.flush();

        if (bufferedWriter != null) {
            try {
                bufferedWriter.write(display + "\n");
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

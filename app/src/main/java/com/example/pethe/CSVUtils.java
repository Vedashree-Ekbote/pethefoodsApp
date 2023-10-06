package com.example.pethe;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVUtils {

        private static final char DEFAULT_SEPARATOR = ',';

        public static void writeLine(Writer writer, List<String> values) throws IOException {
            writeLine(writer, values, DEFAULT_SEPARATOR, ' ');
        }

        public static void writeLine(Writer writer, List<String> values, char separators) throws IOException {
            writeLine(writer, values, separators, ' ');
        }

        public static void writeLine(Writer writer, List<String> values, char separators, char customQuote) throws IOException {
            // (The implementation of the writeLine method remains the same as provided in the previous response)
        }

        private static String followCSVFormat(String value) {
            // (The implementation of the followCSVFormat method remains the same as provided in the previous response)
            return value;
        }
    }

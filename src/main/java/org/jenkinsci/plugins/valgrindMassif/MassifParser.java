package org.jenkinsci.plugins.valgrindMassif;
import java.io.*;
import java.math.BigInteger;
import java.security.KeyException;
import java.text.ParseException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

import org.jenkinsci.plugins.valgrindMassif.report.MassifReportHeapNode;
import org.jenkinsci.plugins.valgrindMassif.report.MassifReportSnapshot;

/**
 * Created by frakafra on 2014. 2. 17
 * Valgrind Massif output parser
 */
public class MassifParser {
    public static final Pattern commentPattern = compile("\\s*(\\#|\\$)");
    public static final Pattern fieldDescriptionPattern = compile("desc:\\s(.*)$");
    public static final Pattern fieldCommandPattern = compile("cmd:\\s(.*)$");
    public static final Pattern fieldTimeUnitPattern = compile("time_unit:\\s(ms|B|i)$");
    public static final Pattern fieldSnapshotPattern = compile("snapshot=(\\d+)");
    public static final Pattern fieldTimePattern = compile("time=(\\d+)");
    public static final Pattern fieldMemoryExtraPattern = compile("mem_heap_extra_B=(\\d+)");
    public static final Pattern fieldMemoryHeapPattern = compile("mem_heap_B=(\\d+)");
    public static final Pattern fieldMemoryStackPattern = compile("mem_stacks_B=(\\d+)");
    public static final Pattern fieldMemoryHeapTreePattern = compile("heap_tree=(\\w+)");
    public static final Pattern heapEntryPattern = compile(
            "\\s*n" +                    // skip zero or more spaces, then 'n'
                    "(\\d+)" +   // match number of children, 1 or more digits
                    ":\\s" +                     // skip ':' and one space
                    "(\\d+)" +     // match the number of bytes, 1 or more digits
                    "\\s" +                      // skip one space
                    "(.*)"         // match the details
    );
    public static final Pattern heapBelowThresholdPattern = compile("in.*places?.*");
    public static final Pattern heapDetailsPattern = compile(
            "([a-fA-F0-9x]+)" +  // match the hexadecimal address
                    ":\\s" +                        // skip ': '
                    "(.+?)" +          // match the function's name, non-greedy
                    "(" +                         // don't capture fname/line group
                    "\\s" +
                    "\\(" +
                    "(in\\s)?" +               // skip 'in ' if present
                    "([^:]+)" +        // match the file name
                    ":?" +                      // skip ':', if present
                    "(\\d+)?" +          // match the line number, if present
                    "\\)" +
                    ")?" +                          // fname/line group is optional
                    "$"                           // should have reached the EOL
    );

    private InputStream inputStream;
    private Integer lineNumber = 0;

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public MassifReport parse(File file) throws IOException, ParseException, KeyException {
        MassifReport report = new MassifReport();

        BufferedReader reader = new BufferedReader(new FileReader(file));

        parseHeader(reader, report);
        parseSnapshots(reader, report);

        return report;
    }

    public MassifReport parse() throws IOException, ParseException, KeyException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        MassifReport report = new MassifReport();
        parseHeader(reader, report);
        parseSnapshots(reader, report);

        return report;
    }

    private String getNextLine(BufferedReader reader, Boolean eofAble) throws IOException {

        String line = reader.readLine();
        if (line != null && line.length() != 0) {
            lineNumber++;
            return line.replace("\n", "").replace("\r", "");
        }
        else if (eofAble) {
            return null;
        }
        else {
            throw new IOException();
        }
    }

    private MatchResult matchUnconditional(Pattern pattern, String string) throws ParseException{
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find()) {
            throw new ParseException("can't match '" + string + "' against '" + pattern.toString() + "'", lineNumber);
        }
        return matcher.toMatchResult();
    }

    private String getNextField(BufferedReader reader, Pattern pattern, boolean eofAble) throws IOException, ParseException {

        String line = getNextLine(reader, eofAble);
        while(line != null) {
            if (commentPattern.matcher(line).find()) {
                line = getNextLine(reader, eofAble);
            }
            if (line == null) {
                return null;
            }
            return matchUnconditional(pattern, line).group(1);
        }

        return null;
    }

    private void parseHeader(BufferedReader reader, MassifReport report) throws IOException, ParseException {
        report.setDescription(getNextField(reader, fieldDescriptionPattern, true));
        report.setCommand(getNextField(reader, fieldCommandPattern, true));
        report.setTimeUnit(getNextField(reader, fieldTimeUnitPattern, true));
    }

    private MassifReportHeapNode parseHeapTree(BufferedReader reader) throws ParseException, IOException{
        String line = getNextLine(reader, true);

        MatchResult entryMatch = matchUnconditional(heapEntryPattern, line);
        String detailsGroup = entryMatch.group(3);

        Matcher detailsMatch = heapDetailsPattern.matcher(detailsGroup);

        MassifReportHeapNode heapNode = new MassifReportHeapNode();

        if (detailsMatch.find()) {
            String lineNumberStr = detailsMatch.group(6);

            heapNode.setFunctionName(detailsMatch.group(2));
            heapNode.setFileName(detailsMatch.group(5));
            heapNode.setAddress(detailsMatch.group(1));
            if (lineNumberStr != null) {
                heapNode.setLineNumber(Integer.parseInt(lineNumberStr));
            }
        }

        Integer childCount = Integer.parseInt(entryMatch.group(1));
        for (Integer i = 0; i < childCount; ++i) {
            heapNode.appendChild(parseHeapTree(reader));
        }

        heapNode.setBytes(Integer.parseInt(entryMatch.group(2)));

        return heapNode;
    }

    private MassifReportSnapshot parseSnapshot(BufferedReader reader) throws ParseException, IOException {
        String snapshotIDstr = getNextField(reader, fieldSnapshotPattern, true);
        if (snapshotIDstr == null) {
            return null;
        }

        MassifReportSnapshot snapshot = new MassifReportSnapshot();
        snapshot.setSnapshotID(Integer.parseInt(snapshotIDstr));
        snapshot.setTime(new BigInteger(getNextField(reader, fieldTimePattern, true)));
        snapshot.setMemoryHeap(Integer.parseInt(getNextField(reader, fieldMemoryHeapPattern, true)));
        snapshot.setMemoryHeapExtra(Integer.parseInt(getNextField(reader, fieldMemoryExtraPattern, true)));
        snapshot.setMemoryStacks(Integer.parseInt(getNextField(reader, fieldMemoryStackPattern, true)));
        String heapTreeField = getNextField(reader, fieldMemoryHeapTreePattern, true);

        if (heapTreeField.equals("empty")) {
            snapshot.setIsDetailed(false);
            snapshot.setIsPeak(false);
            return snapshot;
        }
        snapshot.setIsPeak(heapTreeField.equals("peak"));
        snapshot.setIsDetailed(true);

        snapshot.setRoot(parseHeapTree(reader));

        return snapshot;
    }

    private void parseSnapshots(BufferedReader reader, MassifReport report) throws ParseException, IOException {
        int index = 0;

        MassifReportSnapshot snapshot = parseSnapshot(reader);

        while(snapshot != null) {
            if (snapshot.getIsDetailed().booleanValue()) {
                report.appendDetailedSnapshotIndex(index);
            }
            if (snapshot.getIsPeak().booleanValue()) {
                report.setPeakSnapshotIndex(index);
            }
            report.appendSnapshot(snapshot);
            snapshot = parseSnapshot(reader);
            index += 1;
        }

    }
}

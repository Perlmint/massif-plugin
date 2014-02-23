package org.jenkinsci.plugins.valgrindMassif.report;


import java.util.List;
import java.util.Vector;

/**
 * Created by frakafra on 2014. 2. 22..
 */
public class MassifReportHeapNode {
    private Integer lineNumber;
    private String functionName;
    private String fileName;
    private String address;
    private Integer bytes;
    private List<MassifReportHeapNode> children;

    public String toStirng() {
        return toString(0);
    }

    public String toString(Integer level) {
        String stringfiedChildren = "";
        if (children != null) {
            stringfiedChildren = "[";
            for (MassifReportHeapNode child : children) {
                stringfiedChildren = stringfiedChildren + "\n" + child.toString(level + 1);
            }
            stringfiedChildren = stringfiedChildren + "\n]";
        }
        String outAddress = "";
        if (address != null) {
            outAddress = "\nAddress : " + address.toString();
        }
        String outLineNumber = "";
        if (lineNumber != null) {
            outLineNumber = "\nLine : " + lineNumber.toString();
        }
        String outFileName = "";
        if (fileName != null) {
            outFileName = "\nFile : " + fileName.toString();
        }
        String outFunctionName = "";
        if (functionName != null) {
            outFunctionName = "\nFunction : " + functionName.toString();
        }
        return "HeapNode {" + outAddress + outLineNumber + outFileName +
                outFunctionName + "\nBytes : " + bytes.toString() + "\nChildren : " + stringfiedChildren +
                "\n}";
    }

    public MassifReportHeapNode() {
        this.children = null;
    }

    public void appendChild(MassifReportHeapNode child) {
        if (this.children == null) {
            this.children = new Vector<MassifReportHeapNode>();
        }

        this.children.add(child);
    }

    public List<MassifReportHeapNode> getChildren() {
        return children;
    }

    public MassifReportHeapNode getChild(Integer index) {
        if (this.children == null) {
            return null;
        }
        return this.children.get(index);
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getBytes() {
        return bytes;
    }

    public void setBytes(Integer bytes) {
        this.bytes = bytes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle.report;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public final class ReportBuilder {
    
    /**
     * The StringBuilder object underpinning this ReportBuilder.
     */
    private final StringBuilder sb;
    
    /**
     * The new line separator.
     */
    private final static String NLS = System.lineSeparator();;
    
    /**
     * The indent character.
     */
    private final static String IDT = "\t";;
    
    /**
     * Report header ascii art;
     */
    private static final String title =
            "  _______ _                                _____           _      " + NLS +
            " |__   __| |                              / ____|         | |     " + NLS +
            "    | |  | |__   ___ _ __ _ __ ___   ___ | |    _   _  ___| | ___ " + NLS +
            "    | |  | '_ \\ / _ \\ '__| '_ ` _ \\ / _ \\| |   | | | |/ __| |/ _ \\" + NLS +
            "    | |  | | | |  __/ |  | | | | | | (_) | |___| |_| | (__| |  __/" + NLS +
            "    |_|  |_| |_|\\___|_|  |_| |_| |_|\\___/ \\_____\\__, |\\___|_|\\___|" + NLS +
            "                                                 __/ |            " + NLS +
            "                                                |___/              ";
    
    public static String generateReport(ReportDataBlock rdb) {
        ReportBuilder rb = new ReportBuilder();
        rb.appendLine(-1,title);
        rb.addReportBlock(rdb, 0);
        return rb.toString();
    }
    
    /**
     * The constructor
     */
    private ReportBuilder() {
        sb = new StringBuilder();
    }
    
    /**
     * Appends a new line to the report at the indent level.
     * @param indent the indent level.
     * @param line the line to append to the report.
     */
    private void appendLine(int indent, String line) {
        if (indent > -1 && indent < 2) {
            sb.append(NLS);
        }
        for (int i = 1; i<indent; i++) {
            sb.append(IDT);
        }
        sb.append(line);
        sb.append(NLS);
        // Add divider
        if (line.length() > 0) {
            String div;
            switch (indent) {
                case -1:
                    div = "";
                    break;
                case 0:
                    div = "=";
                    break;
                case 1:
                    div = "-";
                    break;
                default:
                    div = "";
                    break;
            }
            if (indent > -1 && indent < 2) {
                for (int i = 0; i<line.length(); i++) {
                    sb.append(div);
                }
                sb.append(NLS);
            }
        }
    }
    
    @Override
    public String toString() {
        return sb.toString();
    }
    
    private void addReportBlock(ReportDataBlock rdb, int indent) {
        appendLine(indent, rdb.name);
        rdb.getData().keySet().stream().forEach(k -> {
            appendLine(indent+1,k + ": " + rdb.getData().get(k));
        });
        rdb.getSubBlocks().stream().forEach(s -> {
            addReportBlock(s, indent+1);
        });
    }
        
    
    
    
    
}

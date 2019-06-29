/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thermocycle.report;

import thermocycle.report.ReportDataBlock;

/**
 *
 * @author Chris Ward <christheward@gmail.com>
 */
public interface Reportable {
    
    /**
     * Gets the report data for this object.
     * @return the report data block.
     */
    public ReportDataBlock getReportData();
    
}
